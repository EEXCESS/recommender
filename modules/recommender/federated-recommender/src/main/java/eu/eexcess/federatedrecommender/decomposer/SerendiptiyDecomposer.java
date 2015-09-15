package eu.eexcess.federatedrecommender.decomposer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.History;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
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
		List<History> historyList= inputSecureUserProfile.history;
		for (History history : historyList) {
			inputSecureUserProfile.contextKeywords.add(new ContextKeyword(history.title,0.2,ExpansionType.SERENDIPITY));
		}
		java.util.List<Interest> interestList= inputSecureUserProfile.interestList;
		for (Interest interest : interestList) {
			inputSecureUserProfile.contextKeywords.add(new ContextKeyword(interest.text,0.4,ExpansionType.SERENDIPITY));
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
