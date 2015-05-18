/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.eexcess.federatedrecommender.decomposer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaGraph;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaSolrIndex;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * Transforms the secure user profile in multiple profiles with different
 * interests combinations
 * @author hziak
 */

public class DBPediaDecomposer implements SecureUserProfileDecomposer<SecureUserProfileEvaluation, SecureUserProfileEvaluation> {

	private static final Logger logger = Logger.getLogger(SecureUserProfileDecomposer.class.getName());

	/*
	 * Parameters to build the DBpedia graph -> to be tuned up
	 */

	private int hitsLimit; // Number of hits per node
	private int depthLimit; // Depth traversal for graph creation
	private int semanticDistanceThreshold;
	private DbPediaSolrIndex dbPediaSolrIndex;

	/**
	 * 
	 * Generates a list of secureUserProfiles consisting of pairs of
	 * semantically related keywords from the original user profile. Two
	 * keywords are semantically related if a path in DBPedia exists between
	 * them and the path length is < "semanticDistanceThreshold"
	 * 
	 * It always includes the inputSecureUserProfile. In case of error building
	 * a DBPedia graph, it only returns the inputSecureUserProfile
	 * 
	 * 
	 * E.g. a user profile consisting of
	 * 
	 * InputProfile: "Obama", "USA", "President"
	 * 
	 * will generate the following user profiles, considering that all pairs of
	 * keywords are linked together in DBpedia by a path whose length is <
	 * MAX_PATH_LENGTH.
	 * 
	 * OutputProfile 1: "Obama", "USA" OutputProfile 2: "Obama", "President"
	 * OutputProfile 3: "USA", "President"
	 * 
	 * See "SecureUserProfileDecomposerTest.java"
	 * 
	 * These profiles are generated to re-query the recommender with them and
	 * improve results.
	 * 
	 * 
	 * @param inputSecureUserProfile
	 * @param semanticDistanceThreshold
	 * @return
	 */
	@Override
	public SecureUserProfileEvaluation decompose(SecureUserProfileEvaluation inputSecureUserProfile) {


		List<ContextKeyword> profileKeywords = inputSecureUserProfile.contextKeywords; // Keywords
		// consist (for now) in context keywords keywordList.addAll(inputSecureUserProfile.interestList);
		// Return inputSecureUserProfile if no further combinations are
		// possible.
		if (profileKeywords.size() < 3) {
			logger.log(Level.WARNING, "Input Secure User Profile contains less than 3 keywords, returning input profile directly");
			return (SecureUserProfileEvaluation) inputSecureUserProfile;
		}

		List<String> dbPediaEntityNames = new ArrayList<String>(); // DBpedia
																	// entities
																	// representing
																	// keywords
																	// from the
																	// inputProfile
		DbPediaGraph dbPediaGraph = new DbPediaGraph(dbPediaSolrIndex);
		SimpleWeightedGraph<String, DefaultEdge> semanticGraph;

		/*
		 * Get a graph from DBpedia which contains the profileKeywords.
		 */

		try {
			semanticGraph = dbPediaGraph.getFromKeywords(profileKeywords, dbPediaEntityNames, hitsLimit, depthLimit);
			// WARNING: dbPediaKeywords is an i/o parameter, now contains
			// DBpedia entities representing keywords from the inputProfile
		} catch (FederatedRecommenderException e) {
			logger.log(Level.SEVERE, "Graph could not be build out of DBPedia, perhaps server is not running or reachable,returning input profile directly");
			return (SecureUserProfileEvaluation) inputSecureUserProfile;
		}

		/*
		 * Try all possible pairs of keywords from the input profile to generate
		 * candidate profiles consisting on 2 semantically related keywords.
		 * 
		 * Two keywords are semantically related if a path with length <
		 * "semanticDistanceThreshold" exist in the DBpedia graph.
		 */

		List<String> restOfdbPediaEntityNames = new ArrayList<String>(dbPediaEntityNames);

		for (String dbPediaEntityName : dbPediaEntityNames) // keyNodes =
															// DBpedia node
															// names
															// representing
															// keywords from the
															// inputProfile
		{
			restOfdbPediaEntityNames.remove(dbPediaEntityName);
			for (String dbPediaEntityName2 : restOfdbPediaEntityNames) {
				DijkstraShortestPath<String, DefaultEdge> path = new DijkstraShortestPath<String, DefaultEdge>(semanticGraph, dbPediaEntityName, dbPediaEntityName2);
				System.out.println("[" + dbPediaEntityName + ", " + dbPediaEntityName2 + "] - path length: " + path.getPathLength());
				if (path.getPathLength() < semanticDistanceThreshold) {
					ArrayList<ContextKeyword> contextKeywordGroup = new ArrayList<ContextKeyword>();
					contextKeywordGroup.add(new ContextKeyword(dbPediaEntityName));
					contextKeywordGroup.add(new ContextKeyword(dbPediaEntityName2));
				}
			}

		}
		return (SecureUserProfileEvaluation) inputSecureUserProfile;
	}

	public int getSemanticDistanceThreshold() {
		return semanticDistanceThreshold;
	}

	public void setSemanticDistanceThreshold(int semanticDistanceThreshold) {
		this.semanticDistanceThreshold = semanticDistanceThreshold;
	}

	@Override
	public void setConfiguration(FederatedRecommenderConfiguration fedRecConfig)
			throws FederatedRecommenderException {
		this.hitsLimit = fedRecConfig.graphHitsLimitPerQuery;
		this.depthLimit = fedRecConfig.graphMaxPathLength;
		this.dbPediaSolrIndex = new DbPediaSolrIndex(fedRecConfig);
		this.semanticDistanceThreshold = fedRecConfig.graphQueryDepthLimit;
		
	}

}
