package eu.eexcess.federatedrecommender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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

    // @Test
    // public void test() {
    //
    // SecureUserProfile secureUserProfile = new SecureUserProfile();
    // secureUserProfile.contextList.add("Obama");
    // secureUserProfile.contextList.add("Clinton");
    // secureUserProfile.contextList.add("U.S.A");
    // secureUserProfile.contextList.add("Europa");
    // secureUserProfile.contextList.add("Eggenberg");
    // secureUserProfile.contextList.add("Vienna");
    // secureUserProfile.contextList.add("Car");
    // secureUserProfile.contextList.add("Fiat");
    // secureUserProfile.contextList.add("Ferrari");
    // secureUserProfile.contextList.add("Volkswagen");
    // secureUserProfile.contextList.add("Lamborghini");
    // secureUserProfile.contextList.add("Audi");
    // secureUserProfile.contextList.add("Opel");
    // secureUserProfile.contextList.add("Porsche");
    //
    //
    // FederatedRecommenderCore dCore = FederatedRecommenderCore.getInstance();
    // PartnerRecommender partnerRecommenderApi = new PartnerRecommender();
    // dCore.addPartner((PartnerRecommenderApi) partnerRecommenderApi);
    // try {
    // System.out.println(dCore.generateFederatedRecommendation(secureUserProfile));
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

    @Test
    public void sourceSelection_givenSet_expectCorrectInvocationOrder() throws FederatedRecommenderException {
        FederatedRecommenderConfiguration frcc = new FederatedRecommenderConfiguration();
        frcc.setNumRecommenderThreads(20);
        frcc.setPartnersTimeout(1000);
        frcc.setSolrServerUri("");
        frcc.setSourceSelectors(new String[] { "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA",
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB" });
        FederatedRecommenderCore frc = FederatedRecommenderCore.getInstance(frcc);

        ArrayList<String> selectorsClassNames = new ArrayList<>(3);
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA");
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB");

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
                "eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB" });
        FederatedRecommenderCore frc = FederatedRecommenderCore.getInstance(frcc);

        ArrayList<String> selectorsClassNames = new ArrayList<>(3);
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA");
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB");
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA");
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorA");
        selectorsClassNames.add("eu.eexcess.federatedrecommender.FederatedRecommenderCoreTest$TestableSourceSelectorB");

        SecureUserProfileEvaluation userProfile = new SecureUserProfileEvaluation();

        frc.sourceSelection(userProfile, selectorsClassNames);

        assertEquals(3, TestableSourceSelectorA.invocationCount);
        assertEquals(2, TestableSourceSelectorB.invocationCount);

        assertEquals(1, TestableSourceSelectorA.instantiationCount);
        assertEquals(1, TestableSourceSelectorB.instantiationCount);
    }

}
