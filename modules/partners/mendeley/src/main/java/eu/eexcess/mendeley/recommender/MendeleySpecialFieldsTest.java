package eu.eexcess.mendeley.recommender;

/**
 * Created by hziak on 23.11.15.
 */

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SpecialFieldsEum;
import eu.eexcess.dataformats.userprofile.TimeRange;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MendeleySpecialFieldsTest {

    public MendeleySpecialFields mendeleySpecialFields;

    @Before public void prepareConfig() {
        mendeleySpecialFields = new MendeleySpecialFields();

    }

    @Test public void toQueryTestWHOWHAT() {
        SecureUserProfile secureUserProfile = new SecureUserProfile();
        ContextKeyword ck1 = new ContextKeyword();
        ContextKeyword ck3 = new ContextKeyword();
        ck1.setType(SpecialFieldsEum.Person);
        ck1.setText("SEPP FORCHER");
        ck3.setType(SpecialFieldsEum.Person);
        ck3.setText("Ada Lovelace");
        secureUserProfile.getContextKeywords().add(ck1);
        ContextKeyword ck2 = new ContextKeyword();
        ck2.setType(SpecialFieldsEum.Organization);
        ck2.setText("KLINGENDES ÖSTERREICH");
        secureUserProfile.getContextKeywords().add(ck2);
        secureUserProfile.getContextKeywords().add(ck3);
        String returnString = mendeleySpecialFields.toQuery(secureUserProfile);
        System.out.println(returnString);
        assertTrue(returnString.equals("&author=SEPP+FORCHER+OR+Ada+Lovelace&title=KLINGENDES+%C3%96STERREICH"));

    }

    @Test public void toQueryTestFROMTO() {
        SecureUserProfile secureUserProfile = new SecureUserProfile();
        secureUserProfile.setTimeRange(new TimeRange());
        secureUserProfile.getTimeRange().setStart("1970");
        secureUserProfile.getTimeRange().setEnd("2000");

        String returnString = mendeleySpecialFields.toQuery(secureUserProfile);
        System.out.println(returnString);
        assertTrue(returnString.equals("&min_year=1970&max_year=2000"));

    }
}
