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
package eu.eexcess.mendeley.recommender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.text.StrSubstitutor;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.mendeley.recommender.dataformat.MendeleyDocDetails;
import eu.eexcess.mendeley.recommender.dataformat.MendeleyDocs;
import eu.eexcess.mendeley.recommender.dataformat.MendeleyResponse;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;
import eu.eexcess.utils.URLParamEncoder;

/**
 * Query generator for Mendeley.
 *
 * To exercise this, start the standalone server on say port 8000
 * and then make a GET request to localhost:8000/eexcess/partner/debugDumpProfile
 * to get a sample "profile" i.e. a query. You can then make a POST request
 * to localhost:8000/eexcess/partner/recommend, sending the query as the payload.
 * 
 * @author mark.levy@mendeley.com
 * 
 * @author hziak@know-center.at
 *
 */

public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {
	private static final Logger logger = Logger.getLogger(PartnerConnector.class.getName());
	private static final String DOCUMENT_DETAILS_ENDPOINT = "https://api-oauth2.mendeley.com/oapi/documents/details/${canonicalId}";
	private static final String TOKEN_URL = "https://api-oauth2.mendeley.com/oauth/token";

	
    public PartnerConnector(){
    	
    	
    }
	
	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile) throws IOException {

		// Configure
			ExecutorService threadPool = Executors.newCachedThreadPool();
			ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(JacksonJsonProvider.class);
			Client client = Client.create(config);

			AccessTokenResponse accessTokenResponse = getAccessToken(client, partnerConfiguration);
			
			MendeleyResponse mR;
			try {
				mR = fetchSearchResults(client, userProfile, accessTokenResponse, partnerConfiguration);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e1) {
				logger.log(Level.SEVERE,"Could not get results from partner for query: "+userProfile +"\n with accestoken:"+accessTokenResponse+"\n and configuration:"+partnerConfiguration ,e1);
				throw new IOException(e1);
			} 
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			
			long start = System.currentTimeMillis();
			HashMap<MendeleyDocs, Future<MendeleyDocs>> futures = new HashMap<MendeleyDocs, Future<MendeleyDocs>>();
			if(mR !=null && mR.documents!=null){
				 
				for (MendeleyDocs doc : mR.documents) {
					final Client tmpClient =client;
					final AccessTokenResponse tmpAccessTokenResponse = accessTokenResponse;	
					final String canonicalId = doc.uuid;
					
			    	Future<MendeleyDocs> future = threadPool.submit(new Callable<MendeleyDocs>() {
						@Override
						public MendeleyDocs call() throws Exception {
							MendeleyDocDetails details= fetchDocumentDetails(tmpClient, canonicalId, tmpAccessTokenResponse);
							MendeleyDocs docF = new MendeleyDocs();
							docF.description =details.detailsAbstract;
							docF.publicationOutlet=details.publicationoutlet;
							docF.website = details.website;
							details=null;
							return docF;
						}
					});
					futures.put(doc, future);
				}
			}
			else{
				mR = new MendeleyResponse();
				mR.total_results=0;
				mR.total_pages=0;
				mR.items_per_page=0;
				logger.log(Level.INFO,"Partner did return zero documents for \n query: "+userProfile +"\n with accestoken:"+accessTokenResponse+"\n and configuration:"+partnerConfiguration);
			}
			
			for (MendeleyDocs docFK : futures.keySet()) {
				try {
					
					MendeleyDocs detailedDoc= futures.get(docFK).get(start + 15 * 500 - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
							
					int index =mR.documents.indexOf(docFK);
					MendeleyDocs doc= mR.documents.get(index);
					doc.description =detailedDoc.description;
					doc.publicationOutlet=detailedDoc.publicationOutlet;
					doc.website = detailedDoc.mendeley_url;
					futures.get(docFK).cancel(true);
				} catch (InterruptedException | ExecutionException
						| TimeoutException e) {
					futures.get(docFK).cancel(true);
					
					logger.log(Level.WARNING,"Detail thread for "+docFK.doi+" did not responses in time",e);
				}
				futures.put(docFK,null);
			}
			futures=null;
			client=null;

			Document newResponse;
			try {
				newResponse = transformJSON2XML(mapper.writeValueAsString(mR));
			} catch (EEXCESSDataTransformationException e) {
				logger.log(Level.SEVERE,"Partners response could not be transformed to xml for query: "+userProfile +"\n with accestoken:"+accessTokenResponse+"\n configuration:"+partnerConfiguration+"\n and repsonse:"+mR,e);
				throw new IOException(e);
			}
			threadPool.shutdownNow();
			return newResponse;
		}
		
	

	protected MendeleyResponse fetchSearchResults(Client client, SecureUserProfile userProfile, AccessTokenResponse accessTokenResponse,
			PartnerConfiguration partnerConfiguration) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		QueryGeneratorApi queryGenerator = (QueryGeneratorApi)Class.forName(partnerConfiguration.queryGeneratorClass).newInstance();		
		String query = queryGenerator.toQuery(userProfile);

		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("query", URLParamEncoder.encode(query));       
		
		String searchRequest = StrSubstitutor.replace(partnerConfiguration.searchEndpoint, valuesMap);
		MendeleyResponse jsonResponse = getJSONResponse(client, accessTokenResponse, searchRequest);
		int numResults=10000;
		if(userProfile.numResults!=null)
			numResults=userProfile.numResults;
		if(jsonResponse.documents.size()<numResults)
			numResults=jsonResponse.documents.size()-1;
		if(numResults>0)
		jsonResponse.documents = jsonResponse.documents.subList(0, numResults);
		
			
		return jsonResponse;
	}

	protected MendeleyDocDetails fetchDocumentDetails(Client client, String canonicalId, AccessTokenResponse accessTokenResponse) {
		
			Map<String, String> valuesMap = new HashMap<String, String>();
			valuesMap.put("canonicalId", canonicalId);
			String request = StrSubstitutor.replace(DOCUMENT_DETAILS_ENDPOINT, valuesMap);
			MendeleyDocDetails returnValue=new MendeleyDocDetails();
			try {
		
				returnValue = client.resource(request)
						.header("Authorization", "Bearer " + accessTokenResponse.getAccessToken()).accept(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE)
						.get(MendeleyDocDetails.class);
			} catch (Exception e) {
				logger.log(Level.INFO,"No Detailed information on document " +canonicalId+ " found" );
			}
			valuesMap=null;
			request=null;
			client.destroy();
			return returnValue; 	
	}

	private MendeleyResponse getJSONResponse(Client client, AccessTokenResponse accessTokenResponse, String request) {
		MendeleyResponse result =null;
		try {
		
			client.addFilter(new LoggingFilter(logger));
			result =client.resource(request)
					.header("Authorization", "Bearer " + accessTokenResponse.getAccessToken()).accept(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE)
					.get(MendeleyResponse.class);	
		} catch (UniformInterfaceException e) {
			String resultString = e.getResponse().getEntity(String.class);
			ClientResponse header = client.resource(request).header("Authorization", "Bearer " + accessTokenResponse.getAccessToken()).head();
						
			logger.log(Level.WARNING,"Server returned equal or above 300 \nResponse as String:\n"+resultString+"\n Header: "+header+
					"\n Request: "+request+
					"\n AccessTokenResponse: "+accessTokenResponse.toString(),e);

		}
		return result; 
	}

	protected AccessTokenResponse getAccessToken(Client client, PartnerConfiguration partnerConfiguration) throws UnsupportedEncodingException {
		String tokenParams = String.format("grant_type=client_credentials&scope=all&client_id=%s&client_secret=%s", 
				partnerConfiguration.userName, partnerConfiguration.password);
		
		ClientResponse postResponse = client.resource(TOKEN_URL)
				.entity(tokenParams, MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.header("Authorization", basic(partnerConfiguration.userName, partnerConfiguration.password))
				.post(ClientResponse.class);
		
		String json = postResponse.getEntity(String.class);		
		JSONObject accessToken = (JSONObject) JSONSerializer.toJSON(json);	
		
		AccessTokenResponse response = new AccessTokenResponse();
		response.setAccessToken(accessToken.getString("access_token"));
		response.setExpiresIn(accessToken.getLong("expires_in"));
		response.setRefreshToken(accessToken.getString("refresh_token"));
		response.setTokenType(accessToken.getString("token_type"));
		
		return response;
	}

	private String basic(String username, String password) throws UnsupportedEncodingException {
		String credentials = username + ":" + password;        
		return "Basic " + Base64.encodeBase64String(credentials.getBytes(CharEncoding.UTF_8));
	}
	
//	private String getAuthorsString(List<MendeleyAuthors> authors) {
//		String authorsString = "";
//		if(authors!=null)
//		for (MendeleyAuthors mendeleyAuthors : authors) {
//			if (authorsString.length() > 0) 
//				authorsString += ", ";
//			authorsString += mendeleyAuthors.forename + " " + mendeleyAuthors.surname;
//		}
//		
//		return authorsString;
//	}
}