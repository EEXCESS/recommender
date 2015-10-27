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
    	LOGGER.info("Selecting sources by given Age");
        if (userProfile.getPartnerList().isEmpty()){
        	LOGGER.info("1");
            selectPartners(userProfile, partners);
        }
        else {
        	LOGGER.info("2");
            ArrayList<PartnerBadge> tmpPartnerList = new ArrayList<PartnerBadge>(userProfile.getPartnerList());
            userProfile.setPartnerList(new ArrayList<PartnerBadge>());
            selectPartners(userProfile, tmpPartnerList);
        }

        return userProfile;
    }

    private void selectPartners(SecureUserProfile userProfile, List<PartnerBadge> partners) {
    	LOGGER.info("Partners: "+partners);
        if (partners != null)
            partners.forEach((badge) -> {
            	LOGGER.info("Selecting sources by given Age");
            	LOGGER.info(userProfile.getAgeRange()+ " " +badge.getAgeRange() +" " + badge.getSystemId());
                	if(userProfile.getAgeRange() == badge.getAgeRange())
                		userProfile.getPartnerList().add(badge);                
            });
    }

}
