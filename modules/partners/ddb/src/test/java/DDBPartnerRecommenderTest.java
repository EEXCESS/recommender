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
import eu.eexcess.ddb.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;


public class DDBPartnerRecommenderTest {

	private static final String DEPLOYMENT_CONTEXT = "eexcess-partner-ddb-1.0-SNAPSHOT";
	private static final String DATAPROVIDER = "Deutsche Digitale Bibliothek";
	private static int port = 8812;
	private static PartnerStandaloneServer server;
	
	public  DDBPartnerRecommenderTest() {
		
	}
	
	@SuppressWarnings("static-access")
	@BeforeClass
    static public void startJetty() throws Exception {
		server = new PartnerStandaloneServer();
		server.start(port);
    }
	
	@Test
	public void singleQueryGoethe() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("goethe");
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
        ids.add("WV2X3T5D7ZIS4RJHRH3IDR367ERXT54X");
        uris.add("https://www.deutsche-digitale-bibliothek.de/item/WV2X3T5D7ZIS4RJHRH3IDR367ERXT54X");
        ids.add("LDXEDWTZB35GHAXYDNVTZBK2JDHIROPP");
        uris.add("https://www.deutsche-digitale-bibliothek.de/item/LDXEDWTZB35GHAXYDNVTZBK2JDHIROPP");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(2, documentDetails.documentBadges.size());

	}
	
	@Test
	public void detailCallrdfRDFProblem() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("EHTG6PFMAXH6MU35PP6S44ODZ56DLT2F");
        uris.add("https://www.deutsche-digitale-bibliothek.de/item/EHTG6PFMAXH6MU35PP6S44ODZ56DLT2F");
        ids.add("FQOUN2TQHRXHXG2LKASNZ3VD432AFUIJ");
        uris.add("https://www.deutsche-digitale-bibliothek.de/item/FQOUN2TQHRXHXG2LKASNZ3VD432AFUIJ");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(2, documentDetails.documentBadges.size());

	}
	@Test
	public void singleGoetheWithDetails() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("goethe");
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
            uris.add("");//resultList.results.get(i).documentBadge.uri
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
            		port, 
            		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
    	    
            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0 );
            assertEquals(1, documentDetails.documentBadges.size());
		}
	}

}
