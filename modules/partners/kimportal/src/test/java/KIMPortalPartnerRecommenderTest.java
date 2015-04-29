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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.kimportal.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;


public class KIMPortalPartnerRecommenderTest {

	private static int port = 8812;
	private static PartnerStandaloneServer server;
	
	public  KIMPortalPartnerRecommenderTest() {
		
	}
	
	@SuppressWarnings("static-access")
	@BeforeClass
    static public void startJetty() throws Exception {
		server = new PartnerStandaloneServer();
		server.start(port);
    }
	
	@Test
	public void singleQuerySp() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("sp");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-kimportal-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());

	}

	@Test
	public void singleQueryZiegelhof() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("ziegelhof");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-kimportal-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(12, resultList.results.size());

	}

	@Test
	public void singleQueryBierglasOneResult() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-kimportal-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(1,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(1, resultList.results.size());

	}

	@Test
	public void singleQueryZiegelhofXXXXXXNoResults() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("ziegelhofNoResults");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-kimportal-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(12, resultList.results.size());

	}

	@Test
	public void singleQueryBierglas() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-kimportal-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(7, resultList.results.size());

	}

	@Test
	public void singleQueryBierglasZiegelhofKalenderbild() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
		keywords.add("ziegelhof");
		keywords.add("Kalenderbild");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-kimportal-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(1, resultList.results.size());

	}
}
