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
package eu.eexcess.zbw.webservice.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;

public class ZBWPartnerRecommenderTest {

    private static final String DEPLOYMENT_CONTEXT = "eexcess-partner-zbw-1.0-SNAPSHOT";
    private static final String DATAPROVIDER = "ZBW";
    private static int port = 8812;
    private static PartnerStandaloneServer server;

    public ZBWPartnerRecommenderTest() {

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
        ids.add("10010480352");
        uris.add("http://www.econbiz.de/Record/10010480352");
        ids.add("10010432231");
        uris.add("http://www.econbiz.de/Record/10010432231");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(2, documentDetails.documentBadges.size());

    }

    @Test
    public void detailCall8Objects() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("10010480352");
        uris.add("http://www.econbiz.de/Record/10010480352");
        ids.add("10010432231");
        uris.add("http://www.econbiz.de/Record/10010432231");
        ids.add("10010480352");
        uris.add("http://www.econbiz.de/Record/10010480352");
        ids.add("10010432231");
        uris.add("http://www.econbiz.de/Record/10010432231");
        ids.add("10010480352");
        uris.add("http://www.econbiz.de/Record/10010480352");
        ids.add("10010432231");
        uris.add("http://www.econbiz.de/Record/10010432231");
        ids.add("10010480352");
        uris.add("http://www.econbiz.de/Record/10010480352");
        ids.add("10010432231");
        uris.add("http://www.econbiz.de/Record/10010432231");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(8, documentDetails.documentBadges.size());

    }

    @Test
    public void detailCallCreator() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("10010330023");
        uris.add("http://www.econbiz.de/Record/10010330023");
        ids.add("10010775499");
        uris.add("http://www.econbiz.de/Record/10010775499");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(2, documentDetails.documentBadges.size());

    }

    @Test
    public void singleQueryWomenWorkforceChina() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("women workforce china");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(7, resultList.results.size());

    }

    @Test
    public void singleQueryWomenWorkforceChinaWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("women workforce china");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(7, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                    PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0);
            assertEquals(1, documentDetails.documentBadges.size());
        }
    }

    @Test
    public void singleQueryFrauenarbeit() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("frauenarbeit");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());
    }

    @Test
    public void singleQueryFrauenarbeitWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("frauenarbeit");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                    PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0);
            assertEquals(1, documentDetails.documentBadges.size());
        }
    }

    @Test
    public void singleQueryserendipity() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("serendipity");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test
    public void singleQueryserendipityWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("serendipity");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                    PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0);
            assertEquals(1, documentDetails.documentBadges.size());
        }
    }

    @Test
    public void singleQueryFrauenarbeitChinaArbeitsbedingungenGeschlechterrolle() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("frauenarbeit");
        keywords.add("china");
        keywords.add("Arbeitsbedingungen");
        keywords.add("Geschlechterrolle");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test
    public void singleQueryFrauenarbeitChinaArbeitsbedingungenGeschlechterrolleWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("frauenarbeit");
        keywords.add("china");
        keywords.add("Arbeitsbedingungen");
        keywords.add("Geschlechterrolle");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                    PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0);
            assertEquals(1, documentDetails.documentBadges.size());
        }
    }

}
