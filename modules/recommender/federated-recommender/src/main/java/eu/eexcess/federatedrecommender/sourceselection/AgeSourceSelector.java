package eu.eexcess.federatedrecommender.sourceselection;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.config.FederatedRecommenderConfiguration;
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

    public AgeSourceSelector(FederatedRecommenderConfiguration configuration) {
    }
	
    @Override
    public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
        if (userProfile.getPartnerList().isEmpty())
            selectPartners(userProfile, partners);
        else {
            ArrayList<PartnerBadge> tmpPartnerList = new ArrayList<PartnerBadge>(userProfile.getPartnerList());
            userProfile.setPartnerList(new ArrayList<PartnerBadge>());
            selectPartners(userProfile, tmpPartnerList);
        }

        return userProfile;
    }

    private void selectPartners(SecureUserProfile userProfile, List<PartnerBadge> partners) {
        if (partners != null)
            partners.forEach((badge) -> {
                	if(userProfile.getAgeRange() == badge.getAgeRange())
                		userProfile.getPartnerList().add(badge);                
            });
    }

}
