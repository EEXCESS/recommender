package eu.eexcess.federatedrecommender.sourceselection

import eu.eexcess.dataformats.PartnerBadge
import eu.eexcess.dataformats.userprofile.ContextKeyword
import eu.eexcess.dataformats.userprofile.SecureUserProfile
import eu.eexcess.dataformats.userprofile.SpecialFieldsEum
import eu.eexcess.dataformats.userprofile.TimeRange
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * Created by hziak on 26.11.15.
 */
class SpecialFieldSourceSelectionTest {

    @Test fun SpecialFieldSourceSelectionTestAll() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner3.specialFieldQueryGeneratorClass = "this.is.an.test.class";

        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);

        userp.timeRange = TimeRange();
        userp.timeRange.end = "1900";
        userp.timeRange.start = "2000";

        var returnedSUP = sFSS.sourceSelect(userp, partners);
        userp.partnerList = ArrayList<PartnerBadge>();
        userp.partnerList.addAll(partners);
        assertTrue(returnedSUP.partnerList.equals(partners));
    }

    @Test fun SpecialFieldSourceSelectionTestKeywordSpecialType() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner3.specialFieldQueryGeneratorClass = "this.is.an.test.class";

        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);
        var keyword = ContextKeyword("test")
        keyword.type = SpecialFieldsEum.Location;
        userp.contextKeywords.add(keyword);

        var returnedSUP = sFSS.sourceSelect(userp, partners);
        userp.partnerList = ArrayList<PartnerBadge>();
        userp.partnerList.addAll(partners);
        assertFalse(returnedSUP.partnerList == null);
        assertTrue(returnedSUP.partnerList.equals(partners));

    }


    @Test fun SpecialFieldSourceSelectionTestNone() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();


        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);

        userp.timeRange = TimeRange();
        userp.timeRange.end = "1900";
        userp.timeRange.start = "2000";

        var returnedSUP = sFSS.sourceSelect(userp, partners);
        assertTrue(returnedSUP.partnerList == null);

    }

    @Test fun SpecialFieldSourceSelectionTestTwoOf3() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";


        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);

        userp.timeRange = TimeRange();
        userp.timeRange.end = "1900";
        userp.timeRange.start = "2000";

        var returnedSUP = sFSS.sourceSelect(userp, partners);
        userp.partnerList = ArrayList<PartnerBadge>();
        userp.partnerList.add(partner1);
        userp.partnerList.add(partner2);
        assertTrue(returnedSUP.partnerList.size == 2);

    }

    @Test fun SpecialFieldSourceSelectionTestAllreadySet() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";


        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);

        userp.timeRange = TimeRange();
        userp.timeRange.end = "1900";
        userp.timeRange.start = "2000";

        userp.partnerList = ArrayList<PartnerBadge>();
        userp.partnerList.add(partner1);

        var returnedSUP = sFSS.sourceSelect(userp, partners);

        assertTrue(returnedSUP.partnerList.size == 1);


    }


    @Test fun SpecialFieldSourceSelectionTestNoTimeRangeSetPartnerListNull() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";


        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);

        var returnedSUP = sFSS.sourceSelect(userp, partners);
        assertTrue(returnedSUP.partnerList == null);


    }

    @Test fun SpecialFieldSourceSelectionTestNoTimeRangeSetPartnerListSet() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";


        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);
        userp.partnerList = ArrayList<PartnerBadge>();
        userp.partnerList.add(partner2);

        var returnedSUP = sFSS.sourceSelect(userp, partners);
        assertTrue(returnedSUP.partnerList.size == 1);
        assertFalse(returnedSUP.partnerList.equals(partners));

    }


    @Test fun SpecialFieldSourceSelectionTestNoEndOrStartSet() {
        var sFSS = SpecialFieldSourceSelection() ;
        var partners = ArrayList<PartnerBadge>();
        var userp = SecureUserProfile();
        var partner1 = PartnerBadge();
        var partner2 = PartnerBadge();
        var partner3 = PartnerBadge();
        partner1.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner2.specialFieldQueryGeneratorClass = "this.is.an.test.class";
        partner3.specialFieldQueryGeneratorClass = "this.is.an.test.class";

        partners.add(partner1);
        partners.add(partner2);
        partners.add(partner3);

        userp.timeRange = TimeRange();


        var returnedSUP = sFSS.sourceSelect(userp, partners);

        assertTrue(returnedSUP.partnerList == null);


    }

}


