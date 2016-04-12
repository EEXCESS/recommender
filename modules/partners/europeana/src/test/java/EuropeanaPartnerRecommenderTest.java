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
import eu.eexcess.europeana.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;


public class EuropeanaPartnerRecommenderTest {

	private static final String DEPLOYMENT_CONTEXT = "eexcess-partner-europeana-1.0-SNAPSHOT";
	private static final String DATAPROVIDER = "europeana";
	private static int port = 8812;
	private static PartnerStandaloneServer server;
	
	public  EuropeanaPartnerRecommenderTest() {
		
	}
	
	@SuppressWarnings("static-access")
	@BeforeClass
    static public void startJetty() throws Exception {
		server = new PartnerStandaloneServer();
		server.start(port);
    }
	
	@Test
	public void detailCall() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(2, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallGitHubIssueViz54() {
		// https://github.com/EEXCESS/visualization-widgets/issues/54
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("/2058620/e0064797_4543_e64b_3b36_e78553f58411");
        uris.add("http://europeana.eu/resolve/record/2058620/e0064797_4543_e64b_3b36_e78553f58411");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(2, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCall10Objects() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(10, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallRegressionTestRecordWithoutImage() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("/2058805/PLUS__49ae49a23f67c759bf4fc791ba842aa2__artifact__cho");
        uris.add("http://europeana.eu/resolve/record/2058805/PLUS__49ae49a23f67c759bf4fc791ba842aa2__artifact__cho");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallForEnrichment() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("/92070/BibliographicResource_1000126223479");
        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(2, documentDetails.documentBadges.size());

	}

	@Test
	public void detailCallForEnrichmentSingleObject() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
//        ids.add("/92070/BibliographicResource_1000126223479");
//        uris.add("http://europeana.eu/resolve/record/92070/BibliographicResource_1000126223479");
        ids.add("/9200290/BibliographicResource_3000073520496");
        uris.add("http://europeana.eu/resolve/record/9200290/BibliographicResource_3000073520496");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());

	}

	/*
	 * UnitTest for https://github.com/EEXCESS/recommender/issues/27
	 */
	@Test
	public void detailCallProblemDCdateParachute() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("/2022343/6EB096FC2BCC725C2340A59F32774D8CC3086F53");
        uris.add("http://europeana.eu/resolve/record/2022343/6EB096FC2BCC725C2340A59F32774D8CC3086F53");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());
        assertTrue(!documentDetails.documentBadges.get(0).details.contains("18371838"));
        assertTrue(documentDetails.documentBadges.get(0).details.contains("\"dctermsdate\":1837,"));
        assertTrue(!documentDetails.documentBadges.get(0).details.contains("\"dctermsdate\":1838,"));
	}

	/*
	 *  Unittest for https://github.com/EEXCESS/PartnerWizard/issues/1
	 */
	@Test
	public void detailCallProblemDCdateArmenerziehervereins() {
        ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> uris = new ArrayList<String>();
        ids.add("2022041/10848_C5081F31_FD9A_4E63_8213_2DA9CA1578FD");
        uris.add("http://europeana.eu/resolve/record/2022041/10848_C5081F31_FD9A_4E63_8213_2DA9CA1578FD");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));
	    
        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0 );
        assertEquals(1, documentDetails.documentBadges.size());
        assertTrue(documentDetails.documentBadges.get(0).details.contains("\"dctermsdate\":1911,"));
	}
	
	@Test
	public void singleQueryGraz() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("graz");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());

	}
	
	@Test
	public void singleQueryGrazWithDetails() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("graz");
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
	public void singleQueryMonaLisa() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("mona lisa");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(16, resultList.results.size());

	}

	
	@Test
	public void singleQueryMonaLisaWithDetails() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("mona lisa");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(16, resultList.results.size());
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
	public void singleQuery() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("Kriegsflieger");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());

	}

	@Test
	public void singleQueryAugustaLeighWithDetails() {
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("Augusta Leigh");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT,	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,"Augusta Leigh", keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(16, resultList.results.size());
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
