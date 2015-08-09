package eu.eexcess.dbpedia;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.decomposer.DBPediaDecomposer;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * test the decomposer with the dataset from
 * "Learning Noun Phrase Query Segmentation" Bergsma, Wang
 * 
 * @author hziak
 *
 */
public class DBPediaDecomposerEvaluation {

	private static final Logger LOGGER = Logger
			.getLogger(DBPediaDecomposerEvaluation.class.getName());

	private static final List<SecureUserProfileEvaluation> PROFILES = new ArrayList<SecureUserProfileEvaluation>();

	private DBPediaDecomposerEvaluation() {
	}

	private void readProfilesFromFile(String fileName) {

		BufferedReader bR = null;
		try {
			bR = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e1) {
			LOGGER.log(Level.WARNING,"Could not read File ",e1);
		}
		try {
			String line = bR.readLine();
			while (line != null) {
				line = bR.readLine();
				parseAndAddLineToProfiles(line);
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Could not read line from file", e);
		}

	}

	private void parseAndAddLineToProfiles(String line) {
		if (line != null) {
			String tmpLine = line.replaceAll("http.*", "");
			String contextKeywordsOneLine = tmpLine.replaceAll("\"", "");
			SecureUserProfileEvaluation profile = new SecureUserProfileEvaluation();
			for (String contextKeyword : contextKeywordsOneLine.split(" ")) {
				if(!contextKeyword.trim().isEmpty())
					profile.contextKeywords.add(new ContextKeyword(contextKeyword.trim()));
			}
			String[] contextKeywordsOptimalSplit = tmpLine.split("\"");
			for (String string : contextKeywordsOptimalSplit) {
				ArrayList<ContextKeyword> list = new ArrayList<ContextKeyword>();
				for (String contextKeyword : string.split(" ")) {
					if(!contextKeyword.trim().isEmpty())
						list.add(new ContextKeyword(contextKeyword.trim()));
				}
				if(!list.isEmpty())
					profile.getContextKeywordsGroups().add(list);
			}
			PROFILES.add(profile);
		}
	}

	public static void main(String[] args) {
		DBPediaDecomposerEvaluation dbPediaDecomposerEvaluation = new DBPediaDecomposerEvaluation();
		DBPediaDecomposer decomposer = new DBPediaDecomposer();
		ObjectMapper mapper = new ObjectMapper();
		FederatedRecommenderConfiguration fedRecConfig = null;
		try {
			fedRecConfig = mapper.readValue(new File("/home/hziak/workspaces/eexcess/recommender/modules/evaluation/src/main/resources/federatedRecommenderConfig.json"), FederatedRecommenderConfiguration.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			decomposer.setConfiguration(fedRecConfig);
		} catch (FederatedRecommenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String fileName = "/home/hziak/Dokumente/QuerySegmentation/segments.greater4.train";
		dbPediaDecomposerEvaluation.readProfilesFromFile(fileName);
		
		for (SecureUserProfileEvaluation profileEvaluation : dbPediaDecomposerEvaluation.getProfiles()) {
			SecureUserProfileEvaluation tmpProfile = new SecureUserProfileEvaluation();
			tmpProfile.contextKeywords = profileEvaluation.contextKeywords;
		
			SecureUserProfileEvaluation returnedProfile = decomposer.decompose(tmpProfile);
			for (ArrayList<ContextKeyword> string : returnedProfile.getContextKeywordsGroups()) {
				for (ContextKeyword contextKeyword : string) {
					System.out.print(contextKeyword.text);
				}
				System.out.println("#");
			}
			System.out.println();
			System.out.println("Keywords:");
			for (ContextKeyword string : profileEvaluation.contextKeywords) {
				System.out.print(string.text +" ");
			}
			System.out.println();
			System.out.println("KeywordGroups:");
			for (ArrayList<ContextKeyword> string : profileEvaluation.getContextKeywordsGroups()) {
				for (ContextKeyword contextKeyword : string) {
					
					System.out.print(contextKeyword.text +" ");	
				}
				System.out.print(" # ");
				
			}
			
		}
	}

	public static List<SecureUserProfileEvaluation> getProfiles() {
		return PROFILES;
	}

}
