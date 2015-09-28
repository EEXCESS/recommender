package eu.eexcess.federatedrecommender.sourceselection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

/**
 * test for the age source selector algorithm
 * 
 * @author hziak
 *
 */
public class AgeSourceSelectorTest {

    @Test
    public void testSourceSelectionAge15LastThreeLeftOut() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setLowerAgeLimit(15);
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p1.setSystemId("P2");
        p2.setLowerAgeLimit(18);
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p1.setSystemId("P3");
        p3.setLowerAgeLimit(22);
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p1.setSystemId("P4");
        p4.setLowerAgeLimit(25);
        p4.setUpperAgeLimit(30);
        partners.add(p4);

        SecureUserProfile userProfile = new SecureUserProfile();
        Date userBirthDay = new Date();
        userBirthDay.setYear(100);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && !userProfile.getPartnerList().contains(p2) && !userProfile.getPartnerList().contains(p3)
                && !userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionAge25AllIn() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setLowerAgeLimit(15);
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setLowerAgeLimit(18);
        p2.setSystemId("P2");
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setLowerAgeLimit(22);
        p3.setSystemId("P3");
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setSystemId("P4");
        p4.setLowerAgeLimit(25);
        p4.setUpperAgeLimit(30);
        partners.add(p4);

        SecureUserProfile userProfile = new SecureUserProfile();
        Date userBirthDay = new Date();
        userBirthDay.setYear(90);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionAge65LastLeftOut() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setLowerAgeLimit(15);
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setLowerAgeLimit(18);
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setLowerAgeLimit(22);
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setLowerAgeLimit(25);
        p4.setUpperAgeLimit(30);
        partners.add(p4);

        SecureUserProfile userProfile = new SecureUserProfile();
        Date userBirthDay = new Date();
        userBirthDay.setYear(50);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionAge65All() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setLowerAgeLimit(15);
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setLowerAgeLimit(18);
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setLowerAgeLimit(22);
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setLowerAgeLimit(25);
        partners.add(p4);

        SecureUserProfile userProfile = new SecureUserProfile();
        Date userBirthDay = new Date();
        userBirthDay.setYear(50);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionPartnersNull() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = null;

        SecureUserProfile userProfile = new SecureUserProfile();
        Date userBirthDay = new Date();
        userBirthDay.setYear(50);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().isEmpty()) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionPartnersEmpty() throws Exception {
        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();

        SecureUserProfile userProfile = new SecureUserProfile();
        Date userBirthDay = new Date();
        userBirthDay.setYear(50);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().isEmpty()) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionAllreadySelectedSources() throws Exception {

        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge p1 = new PartnerBadge();
        p1.setLowerAgeLimit(15);
        p1.setSystemId("P1");
        partners.add(p1);
        PartnerBadge p2 = new PartnerBadge();
        p2.setLowerAgeLimit(18);
        p2.setSystemId("P2");
        partners.add(p2);
        PartnerBadge p3 = new PartnerBadge();
        p3.setLowerAgeLimit(22);
        p3.setSystemId("P3");
        partners.add(p3);
        PartnerBadge p4 = new PartnerBadge();
        p4.setLowerAgeLimit(25);
        p4.setUpperAgeLimit(30);
        p4.setSystemId("P4");
        partners.add(p4);
        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.getPartnerList().add(p1);
        userProfile.getPartnerList().add(p2);
        userProfile.getPartnerList().add(p4);
        Date userBirthDay = new Date();
        userBirthDay.setYear(50);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && !userProfile.getPartnerList().contains(p3)
                && !userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }

    @Test
    public void testSourceSelectionBadgesHaveNoAgeGiven() throws Exception {

        AgeSourceSelector ageSourceSelector = new AgeSourceSelector();
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
        Date userBirthDay = new Date();
        userBirthDay.setYear(50);
        userProfile.setBirthDate(userBirthDay);
        ageSourceSelector.sourceSelect(userProfile, partners);
        if (userProfile.getPartnerList().contains(p1) && userProfile.getPartnerList().contains(p2) && userProfile.getPartnerList().contains(p3)
                && userProfile.getPartnerList().contains(p4)) {
            assert (true);
        } else
            throw new AssertionError();
    }
}
