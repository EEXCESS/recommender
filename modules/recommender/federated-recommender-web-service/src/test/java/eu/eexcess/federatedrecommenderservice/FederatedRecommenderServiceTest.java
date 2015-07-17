package eu.eexcess.federatedrecommenderservice;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

public class FederatedRecommenderServiceTest {

    @Test
    public void serviceStartWorksReadFromResource() {
        @SuppressWarnings("unused")
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {

            e.printStackTrace();
            assertFalse("Environment Variable or config file could not be read", true);
            return;
        }

        assert (true); // at least the service could start
    }
}
