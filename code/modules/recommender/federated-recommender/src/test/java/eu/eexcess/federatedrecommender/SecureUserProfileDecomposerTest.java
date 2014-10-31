package eu.eexcess.federatedrecommender;

import org.junit.Test;


public class SecureUserProfileDecomposerTest {
	
	@Test
	public void testSecureUserProfileDecomposer(){
		//TODO: rewrite
//		FederatedRecommenderConfiguration fConfiguration = new FederatedRecommenderConfiguration();
//    	fConfiguration.solrServerUri ="http://localhost:8983/solr/";
//    	fConfiguration.graphHitsLimitPerQuery=10;
//    	fConfiguration.graphMaxPathLength=5;
//    	fConfiguration.graphQueryDepthLimit=4;
//    	
//    	DbPediaSolrIndex dbPediaSolrIndex = new DbPediaSolrIndex(fConfiguration);
//    	DbPediaGraph dbPediaGraph = new DbPediaGraph(dbPediaSolrIndex); 
//		SecureUserProfileDecomposer sUPD = new SecureUserProfileDecomposer(fConfiguration, dbPediaSolrIndex);
//		SecureUserProfile secureUserProfile = new SecureUserProfile();
//		secureUserProfile.contextList.add("Michelle Obama");
//		secureUserProfile.contextList.add("Hillary Clinton");
//		secureUserProfile.contextList.add("george w. bush");
//		secureUserProfile.contextList.add("Barack Obama");
//		secureUserProfile.contextList.add("Bill Clinton");
//		secureUserProfile.contextList.add("China");
//		secureUserProfile.contextList.add("Alfred Gusenbauer");
//		secureUserProfile.contextList.add("Austria");
//		secureUserProfile.contextList.add("Germany");
//		secureUserProfile.contextList.add("Mercedes");
//		secureUserProfile.contextList.add("Volkswage");
//		secureUserProfile.contextList.add("Audi");
//		secureUserProfile.contextList.add("woman");
//		secureUserProfile.contextList.add("europe");
//		secureUserProfile.contextList.add("labor");  
//		
//		secureUserProfile.contextList.add("Argentina");
//		int SEMANTIC_DISTANCE_THRESHOLD = 20;
//		sUPD.decompose(secureUserProfile, SEMANTIC_DISTANCE_THRESHOLD );
	}


	
	@Test
	public void testQueryWomenLaborEurope(){
		//TODO: rewrite
		/**FederatedRecommenderConfiguration fConfiguration = new FederatedRecommenderConfiguration();
    	fConfiguration.solrServerUri ="http://localhost:8983/solr/";
    	fConfiguration.graphHitsLimitPerQuery=10;
    	fConfiguration.graphMaxPathLength=5;
    	fConfiguration.graphQueryDepthLimit=4;
    	
    	DbPediaSolrIndex dbPediaSolrIndex = new DbPediaSolrIndex(fConfiguration);
    	 
		SecureUserProfileDecomposer sUPD = new SecureUserProfileDecomposer(fConfiguration, dbPediaSolrIndex);
		
		SecureUserProfile secureUserProfile = new SecureUserProfile();
		secureUserProfile.contextList.add("women");
		secureUserProfile.contextList.add("labor");
		secureUserProfile.contextList.add("europe");
		
		int SEMANTIC_DISTANCE_THRESHOLD = 20;
		List<SecureUserProfile> candidateProfiles = sUPD.decompose(secureUserProfile, SEMANTIC_DISTANCE_THRESHOLD );
		
		System.out.println("Returned profiles: " + candidateProfiles.size() + "\n");
		
		for(SecureUserProfile profile: candidateProfiles) 
		{
			System.out.println(profile.toString());
		}
		*/
	}
	
}
