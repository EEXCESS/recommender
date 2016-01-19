package eu.eexcess.federatedrecommender;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
import org.junit.Test;

import java.util.List;

public class FederatedRecommenderDocDetailsTest {

    /**
     * This test is just for debugging (disfunctional) since partner can't be
     * tested that way
     */
    @Test
    public void test() {
        FederatedRecommenderConfiguration federatedRecommenderConfiguration = new FederatedRecommenderConfiguration();
        federatedRecommenderConfiguration.setNumRecommenderThreads(20);
        federatedRecommenderConfiguration.setPartnersTimeout(1000);
        federatedRecommenderConfiguration.setSolrServerUri("");
        FederatedRecommenderCore fRC = null;
        try {
            fRC = FederatedRecommenderCore.getInstance(federatedRecommenderConfiguration);
        } catch (FederatedRecommenderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PartnerBadge p1 = new PartnerBadge();
        p1.setSystemId("p1");
        PartnerBadge p2 = new PartnerBadge();
        p2.setSystemId("p2");
        fRC.registerPartner(p1);
        fRC.registerPartner(p2);
        DocumentBadgeList documents = getDocs(fRC.getPartnerRegister().getPartners());

        DocumentBadgeList results = fRC.getDocumentDetails(documents);
        System.out.println(results);

    }

    private DocumentBadgeList getDocs(List<PartnerBadge> partners) {
        DocumentBadgeList documents = new DocumentBadgeList();
        for (PartnerBadge partnerBadge : partners) {
            for (int i = 0; i < 30; i++) {
                DocumentBadge e = new DocumentBadge();
                e.id = i + " " + partnerBadge.getSystemId();
                e.provider = partnerBadge.getSystemId();
                documents.documentBadges.add(e);
            }
        }
        return documents;
    }

}
