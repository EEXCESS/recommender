package eu.eexcess.federatedrecommenderservice.sourceselection;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
import eu.eexcess.federatedrecommenderservice.FederatedRecommenderService;

public class SourceSelectionAlgorithmLoadingTest {

	@Test
	public void testClassLoading() {
		   FederatedRecommenderService service = null;
	        
	            try {
					service = new FederatedRecommenderService();
				} catch (FederatedRecommenderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	       
	}

}
