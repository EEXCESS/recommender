#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

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
package ${package}.recommender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import ${groupId}.config.PartnerConfiguration;
import ${groupId}.dataformats.result.DocumentBadge;
import ${groupId}.dataformats.result.ResultList;
import ${groupId}.dataformats.userprofile.SecureUserProfile;
import ${groupId}.partnerdata.reference.PartnerdataLogger;
import ${groupId}.partnerrecommender.api.PartnerConfigurationCache;
import ${groupId}.partnerrecommender.api.PartnerConnectorApi;
import ${groupId}.partnerrecommender.api.QueryGeneratorApi;

/**
 * Query generator for KIM.Collect.
 * 
 * @author thomas.orgel@joanneum.at
 */

public class PartnerConnector implements PartnerConnectorApi {
	
    private QueryGeneratorApi queryGenerator;

    /**
     * Returns the query generator for the partner search engine.
     * @return the query generator
     */
    protected QueryGeneratorApi getQueryGenerator() {
        return queryGenerator;
    }
    
    @Override
    public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger)
    				throws IOException {
    	return null;
    }
    
	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException {
		// Configure
		try {	
	        Client client = new Client(PartnerConfigurationCache.CONFIG.getClientDefault());
	
	        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass);
			
	        String query = getQueryGenerator().toQuery(userProfile);
	        
	        Map<String, String> valuesMap = new HashMap<String, String>();
	        valuesMap.put("query", query);
	        int numResults = 10;
	        if (userProfile.numResults!=null && userProfile.numResults != 0)
	        	numResults  = userProfile.numResults;
	        valuesMap.put("numResults", numResults+"");
	        
	        String searchRequest = StrSubstitutor.replace(partnerConfiguration.searchEndpoint, valuesMap);
	        
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
	
	        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass);
			
	        String detailQuery = getQueryGenerator().toDetailQuery(document);
	        
	        Map<String, String> valuesMap = new HashMap<String, String>();
	        valuesMap.put("detailQuery", detailQuery);
	        
	        String searchRequest = StrSubstitutor.replace(partnerConfiguration.detailEndpoint, valuesMap);
	        
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
