package eu.eexcess.federatedrecommender;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class FederatedRecommenderConfigCreator {

	public static void main(String[] args) {
		FederatedRecommenderConfiguration fConfiguration = new FederatedRecommenderConfiguration();
		fConfiguration.setSolrServerUri("http://localhost:8983/solr/");
    	fConfiguration.setGraphHitsLimitPerQuery(10);
    	fConfiguration.setGraphMaxPathLength(5);
    	fConfiguration.setGraphQueryDepthLimit(4);
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
