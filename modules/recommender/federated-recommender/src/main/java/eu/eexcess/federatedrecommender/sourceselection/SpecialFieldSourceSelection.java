package eu.eexcess.federatedrecommender.sourceselection;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hziak on 26.11.15.
 */
public class SpecialFieldSourceSelection implements PartnerSelector {

    @Override public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {

        List<PartnerBadge> partnersReturned = new ArrayList<>(partners);
        if (userProfile.getPartnerList() != null && !userProfile.getPartnerList().isEmpty()) {
            partnersReturned.clear();
            partnersReturned.addAll(userProfile.getPartnerList());
        }
        boolean hasKeyWordSpecialType = false;
        for (ContextKeyword keyword : userProfile.getContextKeywords()) {
            if (keyword.getType() != null) {
                hasKeyWordSpecialType = true;
            }
        }

        if (userProfile.getTimeRange() != null && (userProfile.getTimeRange().getStart() != null || userProfile.getTimeRange().getEnd() != null || hasKeyWordSpecialType)) {
            final List<PartnerBadge> tmpPartners = new ArrayList<PartnerBadge>();

            partnersReturned.forEach(badge -> {
                if (badge.getSpecialFieldQueryGeneratorClass() != null) {
                    tmpPartners.add(badge);
                }
            });
            partnersReturned = tmpPartners;

        }

        if (userProfile.getPartnerList() != null && !userProfile.getPartnerList().isEmpty()) {
            userProfile.getPartnerList().clear();
            userProfile.setPartnerList(partnersReturned);
        }
        return userProfile;
    }
}
