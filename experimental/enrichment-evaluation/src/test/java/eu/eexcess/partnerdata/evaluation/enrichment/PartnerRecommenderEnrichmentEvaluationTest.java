package eu.eexcess.partnerdata.evaluation.enrichment;
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



public class PartnerRecommenderEnrichmentEvaluationTest {
	
	public  PartnerRecommenderEnrichmentEvaluationTest() {
		
	}
	
	public void testService(String serviceName){
		long start = System.currentTimeMillis();
		System.out.println(serviceName + " started...");
		PartnerRecommenderEvaluationTestHelper.testService(serviceName);
		System.out.println(serviceName + " finished " + (System.currentTimeMillis() - start) +"ms");
		
	}
	
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		PartnerRecommenderEnrichmentEvaluationTest tester = new PartnerRecommenderEnrichmentEvaluationTest();

		tester.testService("kimportal");
		
		tester.testService("europeana");

		tester.testService("ddb");
		
		tester.testService("mendeley");

//		tester.testService("wissenmedia");

		tester.testService("zbw");
		
		System.out.println(" finished " + (System.currentTimeMillis() - start) +"ms");

	}


}
