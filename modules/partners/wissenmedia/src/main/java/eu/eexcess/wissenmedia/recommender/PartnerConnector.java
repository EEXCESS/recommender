/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.wissenmedia.recommender;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;
import org.apache.commons.lang.text.StrSubstitutor;
import org.w3c.dom.Document;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Query generator for Wissenmedia.
 * 
 * @author plopez@know-center.at
 */

public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {
	
	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException {
		
		// Configure
	try {	
	    
	    
//        ClientConfig config = new DefaultClientConfig();
        Client client = new Client(PartnerConfigurationCache.CONFIG.getClientDefault());

        client.addFilter(new HTTPBasicAuthFilter(partnerConfiguration.getUserName(), partnerConfiguration.getPassword()));

        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.getQueryGeneratorClass());
		
        String query = getQueryGenerator().toQuery(userProfile);
        
        Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("query", query);
		int numResults = 10;
        if (userProfile.getNumResults()!=null && userProfile.getNumResults() != 0)
        	numResults  = userProfile.getNumResults();
        valuesMap.put("numResults", numResults+"");

        String searchRequest = StrSubstitutor.replace(partnerConfiguration.getSearchEndpoint(), valuesMap);
        
        WebResource service = client.resource(searchRequest);
        Builder builder = service.accept(MediaType.APPLICATION_XML);
        client.destroy();
        return builder.get(Document.class);
		}
		catch (Exception e) {
			throw new IOException("Cannot query partner REST API!", e);
		}
        
	}

	@Override
	public Document queryPartnerDetails(PartnerConfiguration partnerConfiguration,
			DocumentBadge document, PartnerdataLogger logger)
			throws IOException {
		// Configure
		try {	
	        Client client = new Client(PartnerConfigurationCache.CONFIG.getClientDefault());
	
	        client.addFilter(new HTTPBasicAuthFilter(partnerConfiguration.getUserName(), partnerConfiguration.getPassword()));

			queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.getQueryGeneratorClass());

			String detailQuery = getQueryGenerator().toDetailQuery(document);

			Map<String, String> valuesMap = new HashMap<String, String>();
	        valuesMap.put("detailQuery", detailQuery);
	        
	        String searchRequest = StrSubstitutor.replace(partnerConfiguration.getDetailEndpoint(), valuesMap);
	        
	        WebResource service = client.resource(searchRequest);
	       
	        Builder builder = service.accept(MediaType.APPLICATION_XML);
	        client.destroy();
	        return builder.get(Document.class);
		}
		catch (Exception e) {
				throw new IOException("Cannot query partner REST API!", e);
		}
	}


}
