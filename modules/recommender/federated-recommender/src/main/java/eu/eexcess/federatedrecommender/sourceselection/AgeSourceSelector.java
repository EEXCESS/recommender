package eu.eexcess.federatedrecommender.sourceselection;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;

/**
 * selects the sources according to the age of the user
 * 
 * @author hziak
 *
 */
public class AgeSourceSelector implements PartnerSelector {

    @Override
    public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
        Integer userAge = calcUserAge(userProfile.getBirthDate());
        if (userProfile.getPartnerList().isEmpty())
            selectPartners(userProfile, partners, userAge);
        else {
            ArrayList<PartnerBadge> tmpPartnerList = new ArrayList<PartnerBadge>(userProfile.getPartnerList());
            userProfile.setPartnerList(new ArrayList<PartnerBadge>());
            selectPartners(userProfile, tmpPartnerList, userAge);
        }

        return userProfile;
    }

    private void selectPartners(SecureUserProfile userProfile, List<PartnerBadge> partners, Integer userAge) {
        if (partners != null)
            partners.forEach((badge) -> {
                if (badge.getLowerAgeLimit() == null) {
                    badge.setLowerAgeLimit(18);
                }
                if (badge.getUpperAgeLimit() == null) {
                    badge.setUpperAgeLimit(150);
                }
                if (badge.getLowerAgeLimit() <= userAge && badge.getUpperAgeLimit() >= userAge) {
                    userProfile.getPartnerList().add(badge);
                }
            });
    }

    private Integer calcUserAge(Date birthDate) {
        LocalDate userLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate = LocalDate.now();
        Period p = Period.between(userLocalDate, localDate);
        return p.getYears();
    }
}
