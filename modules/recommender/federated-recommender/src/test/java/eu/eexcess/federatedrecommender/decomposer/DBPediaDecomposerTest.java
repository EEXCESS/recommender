package eu.eexcess.federatedrecommender.decomposer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

public class DBPediaDecomposerTest {

//	private static final String OBAMA = "obama";
//	private static final String CLINTON = "Clinton";
	private static final String SCHWARZENEGGER = "schwarzenegger";
	private static final String TERMINATOR = "terminator";
	private static final String CONAN = "conan";
	private static final String CHINA = "china";
	private static final String FRANCE = "france";
	private static final String BOMB = "bomb";
	private static final String GIBRALTAR = "gibraltar";
	private Logger logger = Logger.getLogger(DBPediaDecomposerTest.class.getName());

	@Test
	public void test() {
		ObjectMapper mapper = new ObjectMapper();
		DBPediaDecomposer dbPediaDecomposer = new DBPediaDecomposer();
		FederatedRecommenderConfiguration fedRecConfig = null;
		try {
			fedRecConfig = mapper.readValue(new File("/home/hziak/workspaces/eexcess/recommender/modules/evaluation/src/main/resources/federatedRecommenderConfig.json"), FederatedRecommenderConfiguration.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dbPediaDecomposer.setConfiguration(fedRecConfig);
		} catch (FederatedRecommenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SecureUserProfileEvaluation inputSecureUserProfile = new SecureUserProfileEvaluation();
//		inputSecureUserProfile.contextKeywords.add(new ContextKeyword(OBAMA));
//		inputSecureUserProfile.contextKeywords.add(new ContextKeyword(CLINTON));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(SCHWARZENEGGER));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(TERMINATOR));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(CONAN));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(FRANCE));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(CHINA));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(BOMB));
		inputSecureUserProfile.getContextKeywords().add(new ContextKeyword(GIBRALTAR));
		
		SecureUserProfileEvaluation eval =dbPediaDecomposer.decompose(inputSecureUserProfile);
		for (ArrayList<ContextKeyword> iterable_element : eval.getContextKeywordsGroups()) {
			
			logger.log(Level.INFO,iterable_element.toArray().toString());
		}
		
	}

}
