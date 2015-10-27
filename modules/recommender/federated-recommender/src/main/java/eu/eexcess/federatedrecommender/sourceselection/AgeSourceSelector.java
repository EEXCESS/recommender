package eu.eexcess.federatedrecommender.sourceselection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(AgeSourceSelector.class.getCanonicalName());
    public AgeSourceSelector(FederatedRecommenderConfiguration configuration) {
    }
	
    @Override
    public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
    	if(userProfile.getAgeRange()==null)
    		userProfile.setAgeRange(2); //if no age given we think its ageRange 2
        if (userProfile.getPartnerList().isEmpty()){
            selectPartners(userProfile, partners);
        }
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
            	if(badge.getAgeRange()==null)
            		badge.setAgeRange(2); //if no age given we think its ageRange 2
            	if(userProfile.getAgeRange() == badge.getAgeRange())
                		userProfile.getPartnerList().add(badge);                
            });
    }

}
