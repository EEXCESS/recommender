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
package eu.eexcess.partnerdata.reference.enrichment;


import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.PartnerdataTracer.FILETYPE;


public class FreeBase extends EnrichmentServiceBase {
	
	
	public FreeBase(PartnerConfiguration config) {
		super(config);
	}

	public ArrayList<FreebaseResult> getEntitiesFreeBase(String word, PartnerdataLogger logger)
	{
		ArrayList<FreebaseResult> resultSet=new ArrayList<FreebaseResult>();
        int results = 0;

		try {
	    	int resultsLimit=1;
	 
	        HttpTransport httpTransport = new NetHttpTransport();
	        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	        GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
	        url.put("query", word);
	        url.put("limit", resultsLimit);
	        url.put("indent", "true");
	        
	        // old key: AIzaSyDMg3oA-nJH9fIniL5Psgc_joybSNgn2LA
	        url.put("key", "AIzaSyC2rWB9EejkKsdwdWEr3rsqFuuOfB_876g");
	        HttpRequest request = requestFactory.buildGetRequest(url);
	        HttpResponse httpResponse = request.execute();
	        String responseString = httpResponse.parseAsString();
	        
	        PartnerdataTracer.dumpFile(FreeBase.class, this.partnerConfig, responseString, "freebase-response", FILETYPE.JSON);
	        
	        JSONObject response = (JSONObject)JSONSerializer.toJSON(responseString);
	        JSONArray resultsResponse = (JSONArray)response.get("result");
	        for (Object result : resultsResponse) {
	        	results++;
	        	JSONObject jsonResult=(JSONObject)result;
	        	FreebaseResult freebaseResult = new FreebaseResult();
	        	if (jsonResult.has("mid"))
	        		freebaseResult.setId(jsonResult.getString("mid"));
	        	if (jsonResult.has("name"))
	        		freebaseResult.setName(jsonResult.getString("name"));
	        	if (jsonResult.has("notable")){
		        	JSONObject notableResult=(JSONObject) jsonResult.get("notable");
		        	if (notableResult.has("name"))
		        		freebaseResult.setNotableName(notableResult.getString("name"));
		        	if (notableResult.has("id"))
		        		freebaseResult.setNotableId(notableResult.getString("id"));
	        	}
	        	if (jsonResult.has("lang"))
	        		freebaseResult.setLanguage(jsonResult.getString("lang"));
	        	resultSet.add(freebaseResult);
	        	/*
	        	String[] splittedResults= jsonResult.get("name").toString().toLowerCase().split(" ");
	        	for (String s: splittedResults)
	        	{
					s=StringNormalizer.removeStopMarks(s);
					
					if (s.length()>0)
					{
						resultSet.add(s);
					}
	          	}
	        	*/
	        	/*
	        	if (notableResult!=null)
	        	{
		        	resultSet.add(notableResult.get("name").toString());
		        	splittedResults= notableResult.get("name").toString().toLowerCase().split("[/[ ]]");
		        	for (String s: splittedResults)
		        	{
		        		// TODO
						//s=StringNormalizer.removeStopMarks(s);
						
						if (s.length()>0)
						{
							resultSet.add(s);
						}
		        	}
	        	}
		        	*/
	        	
	        }

	      } catch (Exception ex) {
	        ex.printStackTrace();
	      }
		
		logger.getActLogEntry().addEnrichmentFreebaseResults(results);
		logger.getActLogEntry().addEnrichmentFreebaseServiceCalls(1);
		
		return resultSet;
	}

}


