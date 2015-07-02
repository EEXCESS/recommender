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

import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.kimportal.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;


public class KIMPortalPartnerRecommenderTest {

	private static final String DATAPROVIDER = "KIM.Portal";
	private static final String DEPLOYMENT_CONTEXT = "eexcess-partner-kimportal-1.0-SNAPSHOT";
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
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
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
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());

	}
	
	@Test
	public void singleQueryHuelftenschanz() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("Hülftenschanz");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(6, resultList.results.size());

	}
	
	@Test
	public void singleQueryBierglasOneResult() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
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
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertNotNull(resultList.results);
        assertTrue(resultList.results.size() == 0 );

	}

	@Test
	public void singleQueryBierglas() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());

	}

	@Test
	public void singleQueryBierglasZiegelhofKalenderbild() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
		keywords.add("ziegelhof");
		keywords.add("Kalenderbild");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());
	}
	
	@Test
	public void detailCall() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("E1.6882");
        uris.add("http://www.kim.bl.openinteractive.ch/sammlungen#f19e71ca-4dc6-48b8-858c-60a1710066f0");
        ids.add("E1.6880");
        uris.add("http://www.kim.bl.openinteractive.ch/sammlungen#f04ae6c5-45fd-ff40-333c-f3b50dffbe3d");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(2, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallWithCountry() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("E1.6889");
        uris.add("http://www.kim.bl.openinteractive.ch/sammlungen#7c627767-4511-144e-8c29-c8a475aca2ac");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallForEnrichmentSingleObject() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("Mz 000068");
        uris.add("http://www.kim.bl.openinteractive.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallForEnrichmentSingleObjectJSON() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("Mz 000068");
        uris.add("http://www.kim.bl.openinteractive.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetailsJSON(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCallJSON(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());

	}

	@Test
	public void singleQueryBierglasZiegelhofKalenderbildWithDetails() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("bierglas");
		keywords.add("ziegelhof");
		keywords.add("Kalenderbild");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
    		ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
            		port, 
            		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
    	    
            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0 );
            assertEquals(1, documentDetails.documentBadges.size());
		}
	}

	@Test
	public void singleQuerySpWithDetails() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("sp");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
    		ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
            		port, 
            		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
    	    
            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0 );
            assertEquals(1, documentDetails.documentBadges.size());
		}
	}
}
