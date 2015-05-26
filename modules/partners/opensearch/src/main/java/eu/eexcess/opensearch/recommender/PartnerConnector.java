/**
 * Copyright (C) 2014
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.eexcess.opensearch.recommender;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.json.JSONArray;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.opensearch.opensearchDescriptionDocument.OpensearchDescription;
import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Url;
import eu.eexcess.opensearch.opensearchDescriptionDocument.parse.OpenSearchDocumentParser;
import eu.eexcess.opensearch.recommender.dataformat.JsonOpensearchResultListBuilder;
import eu.eexcess.opensearch.recommender.dataformat.OpensearchResultListBuilder;
import eu.eexcess.opensearch.recommender.searchLink.SearchLinkFilter;
import eu.eexcess.opensearch.recommender.searchLink.SearchLinkSelector;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;

/**
 * Requests search results for a given SecureUserProfile from an OpenSearch end
 * point.
 * 
 * @author Raoul Rubien
 */
public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {

	private static final Logger logger = Logger.getLogger(PartnerConnector.class.getName());

	private static final String searchTermsVariableName = "searchTerms";
	private static final String substitutorPrefix = "{";
	private static final String substitutorSuffix = "}";

	private static final String searchLinkType = "application/x-suggestions+json";

	private PartnerConfiguration partnerConfig = null;
	private OpensearchDescription descriptionDocument = null;

	public PartnerConnector() {
	}

	@Override
	public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger)
					throws IOException {
		partnerConfig = partnerConfiguration;

		if (PartnerConfigurationCache.CONFIG.getIntializedFlag() == false) {
			descriptionDocument = readOpensearchDescriptionDocument(partnerConfiguration.searchEndpoint);
			PartnerConfigurationCache.CONFIG.setIntializedFlag(bootstrapSearchEndpoint(descriptionDocument));
		}
		
		String query = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass).toQuery(userProfile);
		return fetchSearchResults(query, descriptionDocument);
	}

	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger)
					throws IOException {

		return null;
	}

	/**
	 * Reads OpenSearch Description document from {@code searchEndpoint} and
	 * replaces {@code searchEndpoint} by a new link (search link).
	 * 
	 * @param descriptionDocument
	 *            description document that describes the OpenSearch api
	 * @return true if exactly one valid link was found
	 */
	private boolean bootstrapSearchEndpoint(OpensearchDescription descriptionDocument) {

		SearchLinkFilter linkFilter = new SearchLinkFilter();
		linkFilter.setType(searchLinkType);
		SearchLinkSelector linkSelector = new SearchLinkSelector();
		List<Url> selection = linkSelector.select(descriptionDocument.searchLinks, linkFilter);

		if (selection.size() <= 0) {
			logger.log(Level.WARNING, "no search link found in [" + partnerConfig.searchEndpoint + "]");
			return false;
		} else if (selection.size() > 1) {
			logger.log(Level.WARNING, "ambiguous search links found in [" + partnerConfig.searchEndpoint + "] - take ["
							+ selection.get(0).template + "]");
			return false;
		}

		partnerConfig.searchEndpoint = selection.get(0).template;
		return true;
	}

	/**
	 * Performs a search with {@code query}.
	 * 
	 * @param query
	 *            query to be used for fetching results
	 * @param descriptionDocument
	 *            description document that describes the open search api
	 * @return the search results for that {@code query} or null on error
	 */
	private ResultList fetchSearchResults(String query, OpensearchDescription descriptionDocument) {

		Client client = new Client(PartnerConfigurationCache.CONFIG.getClientJacksonJson());
		String searchRequestUrl = injectSearchQuery(partnerConfig.searchEndpoint, query);

		WebResource documentResource = client.resource(searchRequestUrl);

		ClientResponse searchResult = documentResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		if (searchResult.getStatus() != 200) {
			logger.log(Level.WARNING, "failed receiving search result for [" + query + "]");
			return null;
		}

		String jsonResponseString = searchResult.getEntity(String.class);
		JSONArray jsonResponse = new org.json.JSONArray(jsonResponseString);

		OpensearchResultListBuilder osResponseBuilder = new JsonOpensearchResultListBuilder(jsonResponse,
						descriptionDocument);
		return osResponseBuilder.build();
	}

	/**
	 * Replace the "{searchTerm}" in {@code searchEndpointTemplate} with
	 * {@code searchQuery}.
	 * 
	 * @param searchEndpointTemplate
	 *            the search end point link containing searchTerm placeholder
	 * @param searchQuery
	 *            the search term
	 * @return the substituted link or null on error
	 */
	private String injectSearchQuery(String searchEndpointTemplate, String searchQuery) {

		try {
			Map<String, String> valuesMap = new HashMap<String, String>();
			valuesMap.put(searchTermsVariableName, searchQuery);
			StrSubstitutor substitutor = new StrSubstitutor(valuesMap, substitutorPrefix, substitutorSuffix);
			return substitutor.replace(partnerConfig.searchEndpoint);
		} catch (Exception e) {
		}

		logger.log(Level.WARNING, "failed to prepare search request url [" + searchEndpointTemplate + "] with query ["
						+ searchQuery + "]");
		return null;
	}

	/**
	 * Fetch and parse the OpenSearch document found at
	 * {@code PartnerConfigurationEnum.CONFIG.getPartnerConfiguration().searchEndpoint}
	 * 
	 * @return parsed OpenSearch description document or null on error
	 */
	private OpensearchDescription readOpensearchDescriptionDocument(String searchEndpoint) {

		OpensearchDescription document = null;

		try {
			Client client = PartnerConfigurationCache.CONFIG.getClientDefault();
			WebResource documentResource = client.resource(searchEndpoint);
			ClientResponse response = documentResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);

			if (response.getStatus() != 200) {

				logger.log(Level.WARNING, "failed receiving document [" + searchEndpoint + "]");
				return null;
			}
			String xmlResponse = response.getEntity(String.class);

			OpenSearchDocumentParser osParser = new OpenSearchDocumentParser();
			document = osParser.toDescriptionDocument(xmlResponse);
			if (null == document) {
				logger.log(Level.WARNING, "failed parsing document from xml");
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "failed reading description document [" + searchEndpoint + "]", e);
		}

		if (document == null) {
			logger.log(Level.WARNING, "failed creating description document [=NULL]");
			return null;
		}
		return document;
	}

	@Override
	public Document queryPartnerDetails(
			PartnerConfiguration partnerConfiguration,
			DocumentBadge document, PartnerdataLogger logger)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}
