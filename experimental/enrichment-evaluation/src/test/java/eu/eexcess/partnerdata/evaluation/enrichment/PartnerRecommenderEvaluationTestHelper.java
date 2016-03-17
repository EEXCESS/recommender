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

package eu.eexcess.partnerdata.evaluation.enrichment;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;

public class PartnerRecommenderEvaluationTestHelper {
	
//	private static final String FILENAME_KEYWORDS = ".\\src\\test\\data\\review-demo\\"+ "keywords_review_demo.txt";
	private static final String FILENAME_KEYWORDS = ".\\src\\test\\data\\testbed-1\\"+ "keywords_random_1.txt";
//	private static final String FILENAME_KEYWORDS = ".\\src\\test\\data\\testbed-1\\"+ "keywords_random_10.txt";
//	private static final String FILENAME_KEYWORDS = ".\\src\\test\\data\\testbed-1\\"+ "keywords_random_20.txt";
//	private static final String FILENAME_KEYWORDS = ".\\src\\test\\data\\testbed-1\\"+ "keywords_random_100.txt";

	private static int port = 8090;

	static public ArrayList<String> readKeywords()
	{
		KeywordsList keywordsHelper = new KeywordsList();
		ArrayList<String> keywords = keywordsHelper.readFile(FILENAME_KEYWORDS);
		return keywords;
	}
	
	
	
	static public StringEntity createParamsForPartnerRecommender(int records, ArrayList<String> keywords)
	{
		StringEntity params = null;
		try {
			String userprofile = "<eexcess-secure-user-profile numResults=\""+
					records+
					"\" firstName=\"Hugo\" lastName=\"Boss\" birthDate=\"2013-10-14T05:06:44.550+02:00\">   <contextKeywords>      ";
			for (int i = 0; i < keywords.size(); i++) {
				userprofile +="<contextKeywords><text>"+keywords.get(i)+"</text></contextKeywords>";
			}
			userprofile +=" </contextKeywords></eexcess-secure-user-profile>";
			params = new StringEntity(userprofile);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return params;
	}

	static public ResultList parseResponse(String responseString) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(ResultList.class);

		StringReader reader = new StringReader(responseString);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object resultListObject = unmarshaller.unmarshal(reader);

		if(resultListObject instanceof ResultList) {
			return (ResultList) resultListObject;
			
		}
		return null;
	}
	
	static public ResultList getRecommendations(String deploymentContext, int port,StringEntity params) {
		HttpClient httpClient = new DefaultHttpClient();
		ResultList resultList = null;
	    try {

			HttpPost request = new HttpPost("http://localhost:" +port + "/"+deploymentContext+"/partner/recommend/");
	        request.addHeader("content-type", "application/xml");
            request.addHeader("accept", "application/xml");

	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity());
            
            resultList = PartnerRecommenderEvaluationTestHelper.parseResponse(responseString);
            
            
            if (resultList!= null && resultList.results.size() > 0 )
            {
	            for (int i = 0; i < resultList.results.size(); i++) {
	                ArrayList<String> ids = new ArrayList<String>();
	                ArrayList<String> uris = new ArrayList<String>();
	                ids.add(resultList.results.get(i).documentBadge.id);
	                uris.add(resultList.results.get(i).documentBadge.uri);
	                DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
	                        .getDetails(deploymentContext, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris,resultList.results.get(i).documentBadge.provider));
	
	            }
            }
	    }catch (Exception ex) {
	    	System.out.println(ex);
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
		return resultList;
	}

	static public void testService(String serviceName) {
		ArrayList<String> keywords = PartnerRecommenderEvaluationTestHelper.readKeywords();
		for (int i = 0; i < keywords.size(); i++) {
			ArrayList<String> keyword = new ArrayList<String>();
			keyword.add(keywords.get(i));
	        ResultList resultList = PartnerRecommenderEvaluationTestHelper.getRecommendations("eexcess-partner-"+serviceName+"-1.0-SNAPSHOT",	
    		port , 
    		PartnerRecommenderEvaluationTestHelper.createParamsForPartnerRecommender(20,keyword ));
			System.out.print(" " + i);
		}

	}
	

}
