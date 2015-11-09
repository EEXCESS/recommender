package eu.eexcess.federatedrecommender.evaluation.blockranking;

import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.decomposer.SerendiptiyDecomposer;

public class SerendiptiyDecomposerTest {

	@Test
	public void test() {
		SerendiptiyDecomposer serendiptiyDecomposer = new SerendiptiyDecomposer();
		SecureUserProfileEvaluation inputSecureUserProfile = new SecureUserProfileEvaluation();
		ContextKeyword keyword1 = new ContextKeyword("keyword1");
		inputSecureUserProfile.getContextKeywords().add(keyword1);
		ContextKeyword keyword2 = new ContextKeyword("keyword1");
		inputSecureUserProfile.getContextKeywords().add(keyword2);
		String interest1 = "Interest1";
		inputSecureUserProfile.getInterestList().add(new Interest(interest1));
		String interest2 = "Interest2";
		inputSecureUserProfile.getInterestList().add(new Interest(interest2));
		SecureUserProfileEvaluation resultProfile =serendiptiyDecomposer.decompose(inputSecureUserProfile);
		int foundInterests = 0;
		for (ContextKeyword keyword : resultProfile.getContextKeywords()) {
			if(keyword.getText().equals(interest1) || keyword.getText().equals(interest2)){
				if(keyword.getExpansion()==ExpansionType.SERENDIPITY){
					foundInterests++;
				}else assert(false);
			}
			
		}
		assert(foundInterests== inputSecureUserProfile.getInterestList().size());
		
	
	}

}
