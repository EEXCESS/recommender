package eu.eexcess.federatedrecommender.decomposer;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.userprofile.*;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Generates an extended secure user profile by taken the history of the user into account
 * @author hziak
 *
 */
public class SerendiptiyDecomposer implements
		SecureUserProfileDecomposer<SecureUserProfileEvaluation, SecureUserProfileEvaluation> {
	private static final Logger logger = Logger
			.getLogger(SerendiptiyDecomposer.class.getName());
	@Override
	public SecureUserProfileEvaluation decompose(
			SecureUserProfileEvaluation inputSecureUserProfile) {
		List<History> historyList= inputSecureUserProfile.getHistory();
		for (History history : historyList) {
			inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(history.title,0.2,ExpansionType.SERENDIPITY));
		}
		java.util.List<Interest> interestList= inputSecureUserProfile.getInterestList();
		for (Interest interest : interestList) {
			inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(interest.getText(),0.4,ExpansionType.SERENDIPITY));
			//inputSecureUserProfile.contextKeywords.add(new ContextKeyword(interest.text,interest.competenceLevel,true));
		}
		return inputSecureUserProfile;
	}

	@Override
	public void setConfiguration(FederatedRecommenderConfiguration fedRecConfig)
			throws FederatedRecommenderException {
		logger.log(Level.INFO,"Nothing todo with FederatedRecommenderConfiguration, not needed.");
	}

}
