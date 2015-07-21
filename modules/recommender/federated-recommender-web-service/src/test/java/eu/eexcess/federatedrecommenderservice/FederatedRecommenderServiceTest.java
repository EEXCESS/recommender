package eu.eexcess.federatedrecommenderservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

public class FederatedRecommenderServiceTest {
    public static final Logger LOGGER = Logger.getLogger(FederatedRecommenderServiceTest.class.getName());

    @Test
    public void serviceStartWorksReadFromResource() {
        @SuppressWarnings("unused")
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.WARNING, "Service could not be initiated", e);
            assertFalse("Environment Variable or config file could not be read", true);
            return;
        }

        assert (true); // at least the service could start
    }

    @Test
    public void testGetPreviewImage() {
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.WARNING, "Service could not be initiated", e);
        }
        String type = "other";
        Response resp = null;
        try {
            resp = service.getPreviewImage(type);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Service could not read preview image", e);
        }
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            assert (resp.getEntity().equals(this.getClass().getResourceAsStream("/Thumbnails_EECXESS_unknown.png")));
        } else
            assert (false);
    }

    @Test
    public void testRegisterPartnerBadgeOk() {
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.WARNING, "Service could not be initiated", e);
        }
        PartnerBadge badge = new PartnerBadge();
        badge.systemId = "TEST 1234fksaldsan";
        Response resp = null;
        try {
            resp = service.registerPartner(badge);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Service could not register partner", e);
        }
        assert (resp.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    public void testRegisterPartnerKeyOk() {
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.WARNING, "Service could not be initiated", e);
        }
        PartnerBadge badge = new PartnerBadge();
        badge.systemId = "TEST 1234fksaldsan";
        badge.partnerKey = "asjkjasdjaslkdjaskldjlaskjdaslkdj";
        Response resp = null;
        try {
            resp = service.registerPartner(badge);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Service could not register partner", e);
        }
        assert (resp.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    public void testRegisterPartnerKeyToShort() {
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.WARNING, "Service could not be initiated", e);
        }
        PartnerBadge badge = new PartnerBadge();
        badge.systemId = "TEST 1234fksaldsan123";
        badge.partnerKey = "asjkjasdj";
        Response resp = null;
        try {
            resp = service.registerPartner(badge);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Service could not register partner", e);
        }
        assertTrue(resp.getStatus() == Response.Status.NOT_MODIFIED.getStatusCode());
    }

    @Test
    public void testGetRegisteredPartners() throws Exception {
        FederatedRecommenderService service = null;
        try {
            service = new FederatedRecommenderService();
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.WARNING, "Service could not be initiated", e);
        }
        PartnerBadge badge = new PartnerBadge();
        badge.systemId = "TEST 1234fksaldsan123";
        Response resp = null;
        try {
            resp = service.registerPartner(badge);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Service could not register partner", e);
        }
        assertTrue(resp.getStatus() == Response.Status.OK.getStatusCode());
        assertTrue(service.getRegisteredPartners().partners.contains(badge));
    }

}
