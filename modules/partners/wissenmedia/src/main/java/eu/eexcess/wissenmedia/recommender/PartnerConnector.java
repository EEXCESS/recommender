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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
import eu.eexcess.utils.URLParamEncoder;

/**
 * Query generator for Wissenmedia.
 * 
 * @author plopez@know-center.at
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
	    
	    
//        ClientConfig config = new DefaultClientConfig();
        Client client = new Client(PartnerConfigurationCache.CONFIG.getClientDefault());

        client.addFilter(new HTTPBasicAuthFilter(partnerConfiguration.userName, partnerConfiguration.password));

        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass);
		
        String query = getQueryGenerator().toQuery(userProfile);
        
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("query", URLParamEncoder.encode(query));
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
	public Document queryPartnerDetails(
			PartnerConfiguration partnerConfiguration,
			List<DocumentBadge> documents, PartnerdataLogger logger)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
