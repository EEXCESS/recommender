package eu.eexcess.federatedrecommender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

public class FederatedRecommenderCoreTest {

    public static class TestableSourceSelectorA implements PartnerSelector {
        public static int instantiationCount = 0;
        public static int invocationCount = 0;
        public static long lastInvocationTime = 0;

        public TestableSourceSelectorA(FederatedRecommenderConfiguration configuration) {
            instantiationCount++;
        }

        @Override
        public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
            invocationCount++;
            lastInvocationTime = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            return null;
        }

    }

    public static class TestableSourceSelectorB implements PartnerSelector {
        public static int instantiationCount = 0;
        public static int invocationCount = 0;
        public static long lastInvocationTime = 0;

        public TestableSourceSelectorB(FederatedRecommenderConfiguration configuration) {
            instantiationCount++;
        }

        @Override
        public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
            invocationCount++;
            lastInvocationTime = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            return null;
        }

    }

    @Test
    public void sourceSelection_givenSet_expectCorrectInvocationOrder() throws FederatedRecommenderException {
        FederatedRecommenderConfiguration frcc = new FederatedRecommenderConfiguration();
        frcc.setNumRecommenderThreads(20);
        frcc.setPartnersTimeout(1000);
        frcc.setSolrServerUri("");
        frcc.setSourceSelectors(new String[] { "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA",
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB" });
        FederatedRecommenderCore frc = FederatedRecommenderCore.getInstance(frcc);

        ArrayList<String> selectorsClassNames = new ArrayList<String>(Arrays.asList(frcc.getSourceSelectors()));
        SecureUserProfileEvaluation userProfile = new SecureUserProfileEvaluation();
        frc.sourceSelection(userProfile, selectorsClassNames);

        assertEquals(1, TestableSourceSelectorA.invocationCount);
        assertEquals(1, TestableSourceSelectorB.invocationCount);
        assertTrue(TestableSourceSelectorA.lastInvocationTime < TestableSourceSelectorB.lastInvocationTime);
    }

    @Before
    public void resetCounters() {
        TestableSourceSelectorA.invocationCount = 0;
        TestableSourceSelectorB.invocationCount = 0;
    }

    @Test
    public void sourceSelection_givenSetWithDuplicates_expectSingleInstantiation() throws FederatedRecommenderException {

        FederatedRecommenderConfiguration frcc = new FederatedRecommenderConfiguration();
        frcc.setNumRecommenderThreads(20);
        frcc.setPartnersTimeout(1000);
        frcc.setSolrServerUri("");
        frcc.setSourceSelectors(new String[] { "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA",
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB",
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA",
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA",
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB" });
        FederatedRecommenderCore frc = FederatedRecommenderCore.getInstance(frcc);

        ArrayList<String> selectorsClassNames = new ArrayList<String>(Arrays.asList(frcc.getSourceSelectors()));
        SecureUserProfileEvaluation userProfile = new SecureUserProfileEvaluation();
        frc.sourceSelection(userProfile, selectorsClassNames);

        assertEquals(3, TestableSourceSelectorA.invocationCount);
        assertEquals(2, TestableSourceSelectorB.invocationCount);

        assertEquals(1, TestableSourceSelectorA.instantiationCount);
        assertEquals(1, TestableSourceSelectorB.instantiationCount);
    }

}
