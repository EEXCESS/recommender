package eu.eexcess.federatedrecommender;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.eexcess.config.FederatedRecommenderConfiguration;

public class FederatedRecommenderConfigCreator {

	public static void main(String[] args) {
		FederatedRecommenderConfiguration fConfiguration = new FederatedRecommenderConfiguration();
		fConfiguration.solrServerUri ="http://localhost:8983/solr/";
    	fConfiguration.graphHitsLimitPerQuery=10;
    	fConfiguration.graphMaxPathLength=5;
    	fConfiguration.graphQueryDepthLimit=4;
    	ObjectMapper mapper = new ObjectMapper(); 
    	  
    	
		try {
			mapper.writeValue(new File("src/main/resources/federatedRecommenderConfig.json"), fConfiguration);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
     
	}

}
