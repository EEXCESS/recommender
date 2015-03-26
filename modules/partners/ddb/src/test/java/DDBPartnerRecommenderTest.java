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
import eu.eexcess.ddb.webservice.tool.PartnerStandaloneServer;
import eu.eexcess.partnerrecommender.test.PartnerRecommenderTestHelper;


public class DDBPartnerRecommenderTest {

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
        ResultList resultList = PartnerRecommenderTestHelper.getRecommendations("eexcess-partner-ddb-1.0-SNAPSHOT",	
        		port, 
        		PartnerRecommenderTestHelper.createParamsForPartnerRecommender(20,keywords ));
	    
        assertNotNull(resultList);
        assertTrue(resultList.results.size() > 0 );
        assertEquals(20, resultList.results.size());

	}

}
