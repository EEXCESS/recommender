package eu.eexcess.federatedrecommender.sourceselection;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * test for the age source selector algorithm
 * 
 * @author hziak
 *
 */
public class AgeSourceSelectorTest {

    @Test
    public void testSourceSelectionAge1LastThreeLeftOut() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector(null);
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setAgeRange(0);
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setSystemId("P2");
        p2.setAgeRange(1);
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setSystemId("P3");
        p3.setAgeRange(2);
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setSystemId("P4");
        p4.setAgeRange(2);
        partners.add(p4);

        SecureUserProfile userProfile = new SecureUserProfile();
        
        userProfile.setAgeRange(1);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (!userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && !userProfile.getPartnerList().contains(p3)
                && !userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionAgeLastLeftOut() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector(null);
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setAgeRange(0);
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setAgeRange(1);
        p2.setSystemId("P2");
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setAgeRange(2);
        p3.setSystemId("P3");
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setSystemId("P4");
        p4.setAgeRange(2);
        partners.add(p4);

        SecureUserProfile userProfile = new SecureUserProfile();
        
        userProfile.setAgeRange(2);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (!userProfile.getPartnerList().contains(p1) && !userProfile.getPartnerList().contains(p2) && userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

  
    @Test
    public void testSourceSelectionPartnersNull() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector(null);
        List<PartnerBadge> partners = null;

        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.setAgeRange(2);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().isEmpty()) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionPartnersEmpty() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector(null);
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();

        SecureUserProfile userProfile = new SecureUserProfile();
        
        userProfile.setAgeRange(2);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().isEmpty()) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionAllreadySelectedSources() throws Exception {

        AgeSourceSelector ageSourceSelector = new AgeSourceSelector(null);
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
 
        p1.setAgeRange(0);
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setAgeRange(1);
        p2.setSystemId("P2");
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setAgeRange(2);
        p3.setSystemId("P3");
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setSystemId("P4");
        p4.setAgeRange(2);
        partners.add(p4);
        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.getPartnerList().add(p1);
        userProfile.getPartnerList().add(p2);
        userProfile.getPartnerList().add(p4);
        
        userProfile.setAgeRange(2);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (!userProfile.getPartnerList().contains(p1) && !userProfile.getPartnerList().contains(p2) && !userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionBadgesHaveNoAgeGiven() throws Exception {

        AgeSourceSelector ageSourceSelector = new AgeSourceSelector(null);
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setSystemId("P2");
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setSystemId("P3");
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setSystemId("P4");
        partners.add(p4);
        SecureUserProfile userProfile = new SecureUserProfile();
    
        userProfile.setAgeRange(2);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }
}
