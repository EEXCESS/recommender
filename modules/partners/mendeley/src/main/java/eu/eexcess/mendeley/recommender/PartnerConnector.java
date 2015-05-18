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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.mendeley.recommender.dataformat.MendeleyAuthors;
import eu.eexcess.mendeley.recommender.dataformat.MendeleyDocs;
import eu.eexcess.mendeley.recommender.dataformat.MendeleyResponse;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationEnum;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;
import eu.eexcess.utils.URLParamEncoder;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.text.StrSubstitutor;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;

import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Query generator for Mendeley.
 *
 * To exercise this, start the standalone server on say port 8000
 * and then make a GET request to http://localhost:8000/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/debugDumpProfile
 * to get a sample "profile" i.e. a query. You can then make a POST request
 * to http://localhost:8000/eexcess/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend, sending the query as the payload.
 * 
 * @author mark.levy@mendeley.com
 * 
 * @author hziak@know-center.at
 *
 */

public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {
	private static final Logger log = Logger.getLogger(PartnerConnector.class.getName());
	private static final String TOKEN_URL = "https://api.mendeley.com/oauth/token";
    public static final MediaType APPLICATION_MENDELEY_TYPE = new MediaType("application", "vnd.mendeley-document.1+json");

    public PartnerConnector(){}
    
	@Override
	public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger)
					throws IOException {
		return null;
	}
	
	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(clientConfig);
		AccessTokenResponse accessTokenResponse = getAccessToken(client, partnerConfiguration);

		MendeleyResponse mR;
        try {
            mR = fetchSearchResults(client, userProfile, accessTokenResponse, partnerConfiguration);
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e1) {
            log.log(Level.SEVERE,"Could not get results from partner for query: "+userProfile +"\n with accestoken:"+accessTokenResponse+"\n and configuration:"+partnerConfiguration ,e1);
            throw new IOException(e1);
        }
        client.destroy();
        ObjectMapper mapper = PartnerConfigurationEnum.CONFIG.getObjectMapper();// can reuse, share globally
        for (MendeleyDocs doc : mR.getDocuments()) {
            doc.authorsString  = getAuthorsString(doc.authors);
        }

        Document newResponse;
        try {
            newResponse = transformJSON2XML(mapper.writeValueAsString(mR));
        } catch (EEXCESSDataTransformationException e) {
            log.log(Level.SEVERE,"Partners response could not be transformed to xml for query: "+userProfile +"\n with accestoken:"+accessTokenResponse+"\n configuration:"+partnerConfiguration+"\n and repsonse:"+mR,e);
            throw new IOException(e);
        }
        return newResponse;
    }

	

	protected MendeleyResponse fetchSearchResults(Client client, SecureUserProfile userProfile, AccessTokenResponse accessTokenResponse,
			PartnerConfiguration partnerConfiguration) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String query = PartnerConfigurationEnum.CONFIG.getQueryGenerator().toQuery(userProfile);
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("query", URLParamEncoder.encode(query));       
		String searchRequest = StrSubstitutor.replace(partnerConfiguration.searchEndpoint, valuesMap);
		MendeleyResponse jsonResponse = getJSONResponse(client, accessTokenResponse, searchRequest);

		if(jsonResponse==null || jsonResponse.getDocuments()==null )
			log.log(Level.WARNING,"Mendeley returned an empty result list");

        int numResults=100;
        if(userProfile.numResults!=null)
            numResults=userProfile.numResults;

        jsonResponse.limitNumDocuments(numResults);

		return jsonResponse;
	}

	private MendeleyResponse getJSONResponse(Client client, AccessTokenResponse accessTokenResponse, String request) {
		MendeleyResponse result =null;
		try {
			client.addFilter(new LoggingFilter(log));
			MendeleyDocs[] docs = client.resource(request)
					.header("Authorization", "Bearer " + accessTokenResponse.getAccessToken()).accept(APPLICATION_MENDELEY_TYPE)
					.get(MendeleyDocs[].class);
            result = new MendeleyResponse(Arrays.asList(docs));
		} catch (UniformInterfaceException e) {
			String resultString = e.getResponse().getEntity(String.class);
			ClientResponse header = client.resource(request).header("Authorization", "Bearer " + accessTokenResponse.getAccessToken()).head();
						
			log.log(Level.WARNING,"Server returned equal or above 300 \nResponse as String:\n"+resultString+"\n Header: "+header+
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
	
	private String getAuthorsString(List<MendeleyAuthors> authors) {
		String authorsString = "";
		if(authors != null)
            for (MendeleyAuthors mendeleyAuthors : authors) {
                if (authorsString.length() > 0)
                    authorsString += ", ";
                mendeleyAuthors.first_name = mendeleyAuthors.first_name != null ? mendeleyAuthors.first_name.replaceAll("\\r\\n|\\r|\\n", " ") : "";
                mendeleyAuthors.last_name = mendeleyAuthors.last_name.replaceAll("\\r\\n|\\r|\\n", " ");
                authorsString += mendeleyAuthors.first_name + " " + mendeleyAuthors.last_name;
            }
		
		return authorsString;
	}

	@Override
	public Document queryPartnerDetails(
			PartnerConfiguration partnerConfiguration,
			List<DocumentBadge> documents, PartnerdataLogger logger)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}