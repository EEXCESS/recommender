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
package eu.eexcess.mendeley.webservice.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.mendeley.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;

public class MendeleyPartnerRecommenderTest {

    private static final String DEPLOYMENT_CONTEXT = "eexcess-partner-mendeley-1.0-SNAPSHOT";
    private static final String DATAPROVIDER = "Mendeley";
    private static int port = 8812;
    private static PartnerStandaloneServer server;

    public MendeleyPartnerRecommenderTest() {

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
        ids.add("9b71cb2e-78ff-3ae0-b1dc-af0db029b508");
        uris.add("http://www.mendeley.com/research/work-256");
        ids.add("0d530ec8-8f31-304c-b340-1056277df01b");
        uris.add("http://www.mendeley.com/research/public-concern-work-website");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(2, documentDetails.documentBadges.size());

    }

    @Test
    public void detailCallSingleItemWithOneAuthor() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("0d530ec8-8f31-304c-b340-1056277df01b");
        uris.add("http://www.mendeley.com/research/public-concern-work-website");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(1, documentDetails.documentBadges.size());

    }

    @Test
    public void detailCallSingleItemWithmoreAuthors() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("9b71cb2e-78ff-3ae0-b1dc-af0db029b508");
        uris.add("http://www.mendeley.com/research/work-256");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper.getDetails(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(1, documentDetails.documentBadges.size());

    }

    @Test
    public void singleQueryWork() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("work");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test
    public void singleQueryWorkWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("work");
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
    public void singleQueryMonaLisa() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("mona lisa");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test
    public void singleQueryMonaLisaWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("mona lisa");
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
    public void singleQueryAdaLovelaceLordByron() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("Ada Lovelace");
        keywords.add("Lord Byron");
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations(DEPLOYMENT_CONTEXT, port,
                PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test
    public void singleQueryAdaLovelaceLordByronWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("Ada Lovelace");
        keywords.add("Lord Byron");
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
    public void singleQueryMonaLisaThreaded() throws InterruptedException, ExecutionException {
        final int threadCount = 3;
        final MendeleyPartnerRecommenderTest domainObject = new MendeleyPartnerRecommenderTest();
        Callable<Long> task = new Callable<Long>() {
            @Override
            public Long call() {
                domainObject.singleQueryMonaLisa();
                return new Long(0);
            }
        };
        List<Callable<Long>> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Long>> futures = executorService.invokeAll(tasks);
        List<Long> resultList = new ArrayList<Long>(futures.size());
        // Check for exceptions
        for (Future<Long> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get());
        }
    }
}
