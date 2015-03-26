package eu.eexcess.federatedrecommender.decomposer;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.History;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
/**
 * Generates an extended secure user profile by taken the history of the user into account
 * @author hziak
 *
 */
public class SerendiptiyDecomposer implements
		SecureUserProfileDecomposer<SecureUserProfileEvaluation, SecureUserProfileEvaluation> {

	@Override
	public SecureUserProfileEvaluation decompose(
			SecureUserProfileEvaluation inputSecureUserProfile) {
		java.util.List<History> historyList= inputSecureUserProfile.history;
		for (History history : historyList) {
			inputSecureUserProfile.contextKeywords.add(new ContextKeyword(history.title,0.2,ExpansionType.EXPANSION));
		}
		java.util.List<Interest> interestList= inputSecureUserProfile.interestList;
		for (Interest interest : interestList) {
			inputSecureUserProfile.contextKeywords.add(new ContextKeyword(interest.text,0.4,ExpansionType.EXPANSION));
			//inputSecureUserProfile.contextKeywords.add(new ContextKeyword(interest.text,interest.competenceLevel,true));
		}
		return inputSecureUserProfile;
	}

}
