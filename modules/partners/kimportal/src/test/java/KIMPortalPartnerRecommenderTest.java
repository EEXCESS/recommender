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

import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.kimportal.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class KIMPortalPartnerRecommenderTest {

    private static final String DATAPROVIDER = "KIM.Portal";
    private static final String DEPLOYMENT_CONTEXT = "eexcess-partner-kimportal-1.0-SNAPSHOT";
    private static int port = 8812;
    private static PartnerStandaloneServer server;

    public KIMPortalPartnerRecommenderTest() {

    }

    @SuppressWarnings("static-access") @BeforeClass static public void startJetty() throws Exception {
        server = new PartnerStandaloneServer();
        server.start(port);
    }

    @Test public void singleQuerySp() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("sp");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test public void singleQueryBasel() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("basel");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }
    
    
    @Test public void singleQueryAsteraceae() {
        ArrayList<String> keywords = new ArrayList<String>();
//        keywords.add("Asteraceae");
        String mainTopic = "Asteraceae";
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, mainTopic, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test public void singleQueryZiegelhof() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("ziegelhof");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test public void singleQueryNapoleon() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("Napoleon");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test public void singleQueryHuelftenschanz() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("Hülftenschanz");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(6, resultList.results.size());

    }

    @Test public void singleQueryBierglasOneResult() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("bierglas");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(1, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(1, resultList.results.size());

    }

    @Test public void singleQueryZiegelhofXXXXXXNoResults() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("ziegelhofNoResults");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertNotNull(resultList.results);
        assertTrue(resultList.results.size() == 0);

    }

    @Test public void singleQueryBierglas() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("bierglas");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());

    }

    @Test public void singleQueryBierglasZiegelhofKalenderbild() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("bierglas");
        keywords.add("ziegelhof");
        keywords.add("Kalenderbild");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());
    }

    @Test public void detailCall() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("f19e71ca-4dc6-48b8-858c-60a1710066f0");
        uris.add("https://www.kgportal.bl.ch/sammlungen#f19e71ca-4dc6-48b8-858c-60a1710066f0");
        ids.add("f04ae6c5-45fd-ff40-333c-f3b50dffbe3d");
        uris.add("https://www.kgportal.bl.ch/sammlungen#f04ae6c5-45fd-ff40-333c-f3b50dffbe3d");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(2, documentDetails.documentBadges.size());

    }

    @Test public void detailCallBaselGeo() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("70e1531b-4ce5-33cb-8ba7-91b2dcd033f8");
        uris.add("https://www.kgportal.bl.ch/sammlungen#70e1531b-4ce5-33cb-8ba7-91b2dcd033f8");
        ids.add("e5cc7b15-49c6-ae2e-c292-b0b4ffbf7276");
        uris.add("https://www.kgportal.bl.ch/sammlungen#e5cc7b15-49c6-ae2e-c292-b0b4ffbf7276");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(2, documentDetails.documentBadges.size());

    }

    @Test public void detailCallWithCountry() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("7c627767-4511-144e-8c29-c8a475aca2ac");
        uris.add("https://www.kgportal.bl.ch/sammlungen#7c627767-4511-144e-8c29-c8a475aca2ac");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(1, documentDetails.documentBadges.size());

    }

    @Test public void detailCallForEnrichmentSingleObject() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(1, documentDetails.documentBadges.size());

    }

    @Test public void detailCallForEnrichmentListOfObject11() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        ids.add("d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        uris.add("https://www.kgportal.bl.ch/sammlungen#d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        ids.add("44a001e9-12dc-47f3-9f1f-ce43ab901651");
        uris.add("https://www.kgportal.bl.ch/sammlungen#44a001e9-12dc-47f3-9f1f-ce43ab901651");
        ids.add("61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        uris.add("https://www.kgportal.bl.ch/sammlungen#61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        ids.add("c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        ids.add("29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        uris.add("https://www.kgportal.bl.ch/sammlungen#29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        ids.add("27fea24b-066b-4975-bc31-181071f7ddb6");
        uris.add("https://www.kgportal.bl.ch/sammlungen#27fea24b-066b-4975-bc31-181071f7ddb6");
        ids.add("de48080b-b88e-4a03-a3da-be242ea28401");
        uris.add("https://www.kgportal.bl.ch/sammlungen#de48080b-b88e-4a03-a3da-be242ea28401");
        ids.add("897d230e-6258-4086-804d-e555bc94d231");
        uris.add("https://www.kgportal.bl.ch/sammlungen#897d230e-6258-4086-804d-e555bc94d231");
        ids.add("60791aa4-ce66-4217-bf73-53ec6ad07102");
        uris.add("https://www.kgportal.bl.ch/sammlungen#60791aa4-ce66-4217-bf73-53ec6ad07102");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(11, documentDetails.documentBadges.size());

    }

    @Test public void detailCallForEnrichmentListOfObject20() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        ids.add("d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        uris.add("https://www.kgportal.bl.ch/sammlungen#d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        ids.add("44a001e9-12dc-47f3-9f1f-ce43ab901651");
        uris.add("https://www.kgportal.bl.ch/sammlungen#44a001e9-12dc-47f3-9f1f-ce43ab901651");
        ids.add("61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        uris.add("https://www.kgportal.bl.ch/sammlungen#61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        ids.add("c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        ids.add("29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        uris.add("https://www.kgportal.bl.ch/sammlungen#29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        ids.add("27fea24b-066b-4975-bc31-181071f7ddb6");
        uris.add("https://www.kgportal.bl.ch/sammlungen#27fea24b-066b-4975-bc31-181071f7ddb6");
        ids.add("de48080b-b88e-4a03-a3da-be242ea28401");
        uris.add("https://www.kgportal.bl.ch/sammlungen#de48080b-b88e-4a03-a3da-be242ea28401");
        ids.add("897d230e-6258-4086-804d-e555bc94d231");
        uris.add("https://www.kgportal.bl.ch/sammlungen#897d230e-6258-4086-804d-e555bc94d231");
        ids.add("60791aa4-ce66-4217-bf73-53ec6ad07102");
        uris.add("https://www.kgportal.bl.ch/sammlungen#60791aa4-ce66-4217-bf73-53ec6ad07102");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(20, documentDetails.documentBadges.size());

    }

    @Test public void detailCallForEnrichmentListOfObject40() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        ids.add("d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        uris.add("https://www.kgportal.bl.ch/sammlungen#d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        ids.add("44a001e9-12dc-47f3-9f1f-ce43ab901651");
        uris.add("https://www.kgportal.bl.ch/sammlungen#44a001e9-12dc-47f3-9f1f-ce43ab901651");
        ids.add("61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        uris.add("https://www.kgportal.bl.ch/sammlungen#61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        ids.add("c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        ids.add("29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        uris.add("https://www.kgportal.bl.ch/sammlungen#29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        ids.add("27fea24b-066b-4975-bc31-181071f7ddb6");
        uris.add("https://www.kgportal.bl.ch/sammlungen#27fea24b-066b-4975-bc31-181071f7ddb6");
        ids.add("de48080b-b88e-4a03-a3da-be242ea28401");
        uris.add("https://www.kgportal.bl.ch/sammlungen#de48080b-b88e-4a03-a3da-be242ea28401");
        ids.add("897d230e-6258-4086-804d-e555bc94d231");
        uris.add("https://www.kgportal.bl.ch/sammlungen#897d230e-6258-4086-804d-e555bc94d231");
        ids.add("60791aa4-ce66-4217-bf73-53ec6ad07102");
        uris.add("https://www.kgportal.bl.ch/sammlungen#60791aa4-ce66-4217-bf73-53ec6ad07102");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c14af242-2ed0-4f92-bae6-f4bde57ebcd7");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#d0b4b108-c929-49f7-9fc7-94f6fc1d35fa");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#44a001e9-12dc-47f3-9f1f-ce43ab901651");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#61873d85-6ec9-4a1d-8b94-90ab53d53aa3");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#c7fd7ff6-ccf8-47c6-b39d-208d833546da");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#29f65eab-e8a6-4417-86cf-5d7a28b150f5");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#27fea24b-066b-4975-bc31-181071f7ddb6");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#de48080b-b88e-4a03-a3da-be242ea28401");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#897d230e-6258-4086-804d-e555bc94d231");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#60791aa4-ce66-4217-bf73-53ec6ad07102");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(40, documentDetails.documentBadges.size());

    }

    @Test public void detailCallForEnrichmentSingleObjectJSON() {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> uris = new ArrayList<String>();
        ids.add("aa0b5559-6e86-46db-9785-0329ab800956");
        uris.add("https://www.kgportal.bl.ch/sammlungen#aa0b5559-6e86-46db-9785-0329ab800956");
        DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                .getDetailsJSON(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCallJSON(ids, uris, DATAPROVIDER));

        assertNotNull(documentDetails);
        assertTrue(documentDetails.documentBadges.size() > 0);
        assertEquals(1, documentDetails.documentBadges.size());

    }

    @Test public void singleQueryBierglasZiegelhofKalenderbildWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("bierglas");
        keywords.add("ziegelhof");
        keywords.add("Kalenderbild");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(10, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                    .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0);
            assertEquals(1, documentDetails.documentBadges.size());
        }
    }

    @Test public void singleQuerySpWithDetails() {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("sp");
        ResultList resultList = PartnerRecommenderTestHelper
                .getRecommendations(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20, keywords));

        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0);
        assertEquals(20, resultList.results.size());
        for (int i = 0; i < resultList.results.size(); i++) {
            ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> uris = new ArrayList<String>();
            ids.add(resultList.results.get(i).documentBadge.id);
            uris.add(resultList.results.get(i).documentBadge.uri);
            DocumentBadgeList documentDetails = PartnerRecommenderTestHelper
                    .getDetails(DEPLOYMENT_CONTEXT, port, PartnerRecommenderTestHelper.createParamsForPartnerRecommenderDetailCall(ids, uris, DATAPROVIDER));

            assertNotNull(documentDetails);
            assertTrue(documentDetails.documentBadges.size() > 0);
            assertEquals(1, documentDetails.documentBadges.size());
        }
    }
}
