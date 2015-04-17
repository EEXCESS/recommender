/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.diversityasurement.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.eexcess.diversityasurement.evaluation.config.Settings;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance.CategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Queries;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.QueryJsonIO;
import eu.eexcess.logger.PianoLogger;

public class IASelectVSQueryExpansionEvaluation {

	private static Logger logger = PianoLogger.getLogger(IASelectVSQueryExpansionEvaluation.class);

	private static Map<Integer, String> categoryIdToName;
	private static Map<String, Integer> categoryNameToId;
	// Map<subCategoryId, HashMap<topCategoryId, relevance>>
	private static HashMap<Integer, HashMap<Integer, Double>> estimatedRelevances;

	public static class DataStatistics {
		// Map<numberTopCategories, numberCategoriesHavingNumberTopCategories>
		Map<Integer, Integer> topCategoriesPerCategoryDistibution = new HashMap<>();

		HashSet<Integer> withNumtopCategories = new HashSet<>(Arrays.asList(30, 31, 32, 33, 34, 35, 36, 37));
		// Map<withNTopCategories, Map<section[%]{0-10,11-20,...91-100},
		// numOccurences>>
		Map<Integer, HashMap<Integer, Integer>> probabilityDistribution = new HashMap<>();

		Queries inQueries = new Queries();
		Queries outQueries = new Queries();

		Map<Query, ArrayList<CategoryToTopCategoryRelevance>> queryRelevances;
		// accumulated and normalized query relevances
		Map<Query, ArrayList<CategoryRelevance>> queryRelevancesNormalized = new HashMap<>();
	}

	private static DataStatistics stats = new DataStatistics();

	public static void main(String[] args) throws IOException, ParseException {
		restoreCache();

		collectCategoryDistribution();
		System.out.println("categories to topCategories distribution:");
		System.out.println("#cat, #topcat");
		for (Map.Entry<Integer, Integer> entry : stats.topCategoriesPerCategoryDistibution.entrySet()) {
			System.out.println(entry.getValue() + ", " + entry.getKey());
		}
		System.out.println();

		collectProbabilityDistribution(10, stats.withNumtopCategories);
		System.out.println("probability distribution:");
		for (Map.Entry<Integer, HashMap<Integer, Integer>> entry : stats.probabilityDistribution.entrySet()) {
			Integer numTopCategories = entry.getKey();
			System.out.println("probability distribution of categories having exact [" + numTopCategories
							+ "] topCategories:");
			System.out.println("withNTopC, #range, #count");
			for (Map.Entry<Integer, Integer> probabilityEntry : entry.getValue().entrySet()) {
				System.out.println(numTopCategories + ", " + probabilityEntry.getKey() + ", "
								+ probabilityEntry.getValue());
			}
		}
		System.out.println();
		System.out.println("query relevances");
		filterQueryProbabilities();

	}

	private static void filterQueryProbabilities() throws FileNotFoundException, IOException, ParseException {

		stats.inQueries = QueryJsonIO.readQueries(new File(Settings.Queries.PATH));
		QueryCategoryKShortestPathsEvaluation.openInIndex();

		// for each query fetch all top docs categories relevance
		stats.queryRelevances = collectTopDocsCategoryRelevances(stats.inQueries,
						Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider);

		// sum up total probability and normalize for each top category
		System.out.println();
		for (Map.Entry<Query, ArrayList<CategoryToTopCategoryRelevance>> entry : stats.queryRelevances.entrySet()) {
			ArrayList<CategoryRelevance> queryTopCategoryRelevances = accumulateTopCategoryRelevance(entry.getValue());
			stats.queryRelevancesNormalized.put(entry.getKey(), queryTopCategoryRelevances);
			System.out.println("query [" + entry.getKey().query + "] relevances [" + queryTopCategoryRelevances.size()
							+ "]:");
			System.out.println("id, p, name");
			for (CategoryRelevance queryRelevance : queryTopCategoryRelevances) {
				System.out.println(queryRelevance.categoryId + ", " + queryRelevance.categoryRelevance + ", "
								+ queryRelevance.categoryName);
			}
		}

		// take best 7 categories out of all per query
		Map<Query, ArrayList<CategoryRelevance>> shortenedCategoryRelevances = new HashMap<>();
		int numTopCategories = 7;
		for (Map.Entry<Query, ArrayList<CategoryRelevance>> entry : stats.queryRelevancesNormalized.entrySet()) {
			ArrayList<CategoryRelevance> sortedRelevances = new ArrayList<>(entry.getValue());
			Collections.sort(sortedRelevances, new CategoryRelevance.DescRelevanceComparator());
			int maxIdx = (numTopCategories > sortedRelevances.size()) ? sortedRelevances.size() : numTopCategories;
			shortenedCategoryRelevances.put(entry.getKey(),
							new ArrayList<CategoryRelevance>(sortedRelevances.subList(0, maxIdx)));
		}

		// re-normalize shortened relevances
		for (Map.Entry<Query, ArrayList<CategoryRelevance>> entry : shortenedCategoryRelevances.entrySet()) {
			ArrayList<CategoryRelevance> normalized = toNormalizedRelevance(entry.getValue());
			stats.queryRelevancesNormalized.put(entry.getKey(), normalized);
			
			System.out.println("normalized shortened relevance list query [" + entry.getKey().query + "] size ["
							+ normalized.size() + "]:");
			System.out.println("id, p, name");
			for (CategoryRelevance relevance : normalized) {
				System.out.println(relevance.categoryId + "," + relevance.categoryRelevance + ","
								+ relevance.categoryName);
			}
		}

		
		
		
			FileWriter writer = new FileWriter("/home/rrubien/Desktop/foobar.json");
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
			gson.toJson(stats.queryRelevancesNormalized, writer);
			writer.close();
		
		
		// write as JSON
		// process queries manually!!

		QueryCategoryKShortestPathsEvaluation.closeInIndex();
	}

	/**
	 * Accumulate and normalize relevance values of each list entry.
	 * 
	 * @param topCategoryRelevance
	 *            list of relevances; one relevance has n relevances to n top
	 *            categories
	 * @return
	 */
	private static ArrayList<CategoryRelevance> accumulateTopCategoryRelevance(
					ArrayList<CategoryToTopCategoryRelevance> topCategoryRelevance) {

		HashMap<Integer, CategoryRelevance> accumulatedTopCategoryRelevances = new HashMap<>();

		// accumulate probabilities
		// for each category
		for (CategoryToTopCategoryRelevance category : topCategoryRelevance) {
			// for each category relevance
			for (CategoryRelevance relevance : category.topCategoryRelevances) {
				Double topCatRelevance = relevance.categoryRelevance;

				if (!topCatRelevance.isNaN() && topCatRelevance > 0) {
					Integer topCatId = relevance.categoryId;

					CategoryRelevance accumulatedTopCategoryRelevance = accumulatedTopCategoryRelevances.get(topCatId);
					if (null == accumulatedTopCategoryRelevance) {
						accumulatedTopCategoryRelevance = new CategoryRelevance();
						accumulatedTopCategoryRelevance.categoryId = topCatId;
						accumulatedTopCategoryRelevance.categoryName = relevance.categoryName;
						accumulatedTopCategoryRelevance.categoryRelevance = 0.0;
					}
					accumulatedTopCategoryRelevance.categoryRelevance += topCatRelevance;
					accumulatedTopCategoryRelevances.put(topCatId, accumulatedTopCategoryRelevance);
				}
			}
		}

		// normalize probabilities
		double sum = 0.0;
		for (HashMap.Entry<Integer, CategoryRelevance> entry : accumulatedTopCategoryRelevances.entrySet()) {
			Double relevance = entry.getValue().categoryRelevance;
			if (!relevance.isNaN() && relevance > 0) {
				sum += relevance;
			}
		}

		for (HashMap.Entry<Integer, CategoryRelevance> entry : accumulatedTopCategoryRelevances.entrySet()) {
			Double relevance = entry.getValue().categoryRelevance;
			if (!relevance.isNaN() && relevance > 0) {
				entry.getValue().categoryRelevance = relevance / sum;
			}
		}

		return new ArrayList<CategoryRelevance>(accumulatedTopCategoryRelevances.values());
	}

	private static Map<Integer, Double> toNormalizedRelevance(Map<Integer, Double> relevances) {
		double sum = 0;
		for (Map.Entry<Integer, Double> entry : relevances.entrySet()) {
			Double relevance = entry.getValue();
			if (!relevance.isNaN() && relevance > 0) {
				sum += relevance;
			}
		}
	
		HashMap<Integer, Double> normalized = new HashMap<>(relevances.size());
		for (Map.Entry<Integer, Double> entry : relevances.entrySet()) {
			normalized.put(entry.getKey(), entry.getValue() / sum);
		}
	
		return normalized;
	}

	private static ArrayList<CategoryRelevance> toNormalizedRelevance(ArrayList<CategoryRelevance> relevances) {
		double sum = 0.0;
		ArrayList<CategoryRelevance> normalized = new ArrayList<>(relevances);
		for (CategoryRelevance relevance : normalized) {
			if (!relevance.categoryRelevance.isNaN() && relevance.categoryRelevance > 0) {
				sum += relevance.categoryRelevance;
			}
		}
	
		for (CategoryRelevance relevance : normalized) {
			if (!relevance.categoryRelevance.isNaN() && relevance.categoryRelevance > 0) {
				relevance.categoryRelevance = relevance.categoryRelevance / sum;
			}
		}
		return normalized;
	}

	private static Map<Query, ArrayList<CategoryToTopCategoryRelevance>> collectTopDocsCategoryRelevances(
					Queries queries, int numTopDocumentsToConsider) throws ParseException, IOException {
		ArrayList<Integer> seenCategoryIds = new ArrayList<>();
		IndexSearcher indexSearcher = new IndexSearcher(QueryCategoryKShortestPathsEvaluation.indexReader);
		QueryParser queryParser = new QueryParser(QueryCategoryKShortestPathsEvaluation.SEARCH_FIELD_SECTIONTEXT,
						new EnglishAnalyzer());
		int totalMissedCategories = 0;
		int totalFoundCategories = 0;
		int totalDuplicateCollisions = 0;

		// for each query
		HashMap<Query, ArrayList<CategoryToTopCategoryRelevance>> result = new HashMap<>();
		for (Query q : queries.queries) {

			org.apache.lucene.search.Query query = queryParser.parse(q.query);
			TopDocs topDocs = indexSearcher.search(query, numTopDocumentsToConsider);

			int missedCategories = 0;
			int foundCategories = 0;
			int duplicateCollisions = 0;
			ArrayList<CategoryToTopCategoryRelevance> categoryRelevancesDetails = new ArrayList<>();

			// for all top docs
			for (ScoreDoc sDoc : topDocs.scoreDocs) {
				org.apache.lucene.document.Document doc = indexSearcher.doc(sDoc.doc);
				IndexableField[] categories = doc.getFields("category");

				// for all top doc categories
				for (IndexableField category : categories) {
					String categoryName = category.stringValue().replace(" ", "_");
					Integer categoryId = categoryNameToId.get(categoryName);
					if (categoryId == null) {
						missedCategories++;
					} else {

						// if category not seen so far
						if (!seenCategoryIds.contains(categoryId)) {
							seenCategoryIds.add(categoryId);
							foundCategories++;

							CategoryToTopCategoryRelevance categoryRelevanceDetail = new CategoryToTopCategoryRelevance();
							categoryRelevanceDetail.categoryId = categoryId;
							categoryRelevanceDetail.categoryName = categoryIdToName.get(categoryId);
							categoryRelevanceDetail.query = q.query;

							// for all category to top category relations
							HashMap<Integer, Double> topCategories = estimatedRelevances.get(categoryId);
							ArrayList<CategoryRelevance> queryToTopCategoryRelevances = new ArrayList<>();
							for (Map.Entry<Integer, Double> entry : topCategories.entrySet()) {
								CategoryRelevance tr = new CategoryRelevance();
								tr.categoryId = entry.getKey();
								tr.categoryName = categoryIdToName.get(tr.categoryId);
								tr.categoryRelevance = entry.getValue();
								queryToTopCategoryRelevances.add(tr);
							}
							categoryRelevanceDetail.topCategoryRelevances = queryToTopCategoryRelevances
											.toArray(new CategoryRelevance[0]);
							categoryRelevancesDetails.add(categoryRelevanceDetail);
						} else {
							duplicateCollisions++;
						}
					}
				}
			}

			result.put(q, categoryRelevancesDetails);

			logger.info("found [" + foundCategories + "] missed [" + missedCategories
							+ "] categories duplicate collisions [" + duplicateCollisions + "] out of ["
							+ (foundCategories + missedCategories + duplicateCollisions) + "] categories for query ["
							+ q.query + "] ");
			totalMissedCategories += missedCategories;
			totalFoundCategories += foundCategories;
			totalDuplicateCollisions += duplicateCollisions;
		}
		logger.info("totals: found [" + totalFoundCategories + "] missed [" + totalMissedCategories
						+ "] categories duplicate collisions [" + totalDuplicateCollisions + "] out of ["
						+ (totalFoundCategories + totalMissedCategories + totalDuplicateCollisions)
						+ "] categories for [" + queries.queries.length + "] queries");
		return result;
	}

	/**
	 * Increment counter for each probability within a section for categories
	 * having exact number of top categories.
	 * 
	 * @param numSections
	 *            number of sections the probability should be classified to
	 * @param withNumtopCategories
	 *            consider only categories having exact number of top categories
	 *            enumerated in the set
	 */
	private static void collectProbabilityDistribution(int numSections, Set<Integer> withNumtopCategories) {
		for (Map.Entry<Integer, HashMap<Integer, Double>> relevance : estimatedRelevances.entrySet()) {
			HashMap<Integer, Double> relevances = relevance.getValue();
			int numProbabilities = getNumProbabilitiesGTZero(relevances);

			if (withNumtopCategories.contains(numProbabilities)) {
				Map<Integer, Double> normalized = toNormalizedRelevance(relevances);
				for (Map.Entry<Integer, Double> normalizedEntry : normalized.entrySet()) {
					int section = getSection(numSections, normalizedEntry.getValue());
					incrementNormalizedRelevance(numProbabilities, section, 1);
				}
			}
		}
	}

	private static void incrementNormalizedRelevance(int numProbabilities, int section, int increment) {
		HashMap<Integer, Integer> sectionHits = stats.probabilityDistribution.get(numProbabilities);

		if (null == sectionHits) {
			sectionHits = new HashMap<Integer, Integer>();
			stats.probabilityDistribution.put(numProbabilities, sectionHits);
		}

		Integer hits = sectionHits.get(section);

		if (null == hits) {
			hits = new Integer(0);
			sectionHits.put(section, hits);
		}

		sectionHits.put(section, hits + increment);
	}

	private static int getSection(int numSections, double normalizedValue) {
		return new Double(Math.floor(numSections * normalizedValue)).intValue();
	}

	private static int getNumProbabilitiesGTZero(HashMap<Integer, Double> relevances) {
		int numEntries = 0;
		for (HashMap.Entry<Integer, Double> relevance : relevances.entrySet()) {
			Double probability = relevance.getValue();
			if (!probability.isNaN() && probability > 0) {
				numEntries++;
			}
		}
		return numEntries;
	}

	private static void collectCategoryDistribution() {
		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : estimatedRelevances.entrySet()) {
			HashMap<Integer, Double> relevances = entry.getValue();

			int nCategories = getNumProbabilitiesGTZero(relevances);

			Integer numNodesHavingNCategories = stats.topCategoriesPerCategoryDistibution.get(nCategories);
			if (null == numNodesHavingNCategories) {
				stats.topCategoriesPerCategoryDistibution.put(nCategories, 1);
			} else {
				stats.topCategoriesPerCategoryDistibution.put(nCategories, numNodesHavingNCategories + 1);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void restoreCache() {
		try {
			FileInputStream fis = new FileInputStream(Settings.ContrastEvaluation.IOFIles.inRelevances);
			ObjectInputStream ois = new ObjectInputStream(fis);
			estimatedRelevances = (HashMap<Integer, HashMap<Integer, Double>>) ois.readObject();
			ois.close();
			fis.close();
			logger.info("read [" + estimatedRelevances.size() + "] relevances from file ["
							+ Settings.ContrastEvaluation.IOFIles.inRelevances.getAbsolutePath() + "]");
		} catch (Exception e) {
			logger.severe("failed loading relevances from file ["
							+ Settings.ContrastEvaluation.IOFIles.inRelevances.getAbsolutePath() + "]");
		}

		try {
			FileInputStream fis = new FileInputStream(Settings.ContrastEvaluation.IOFIles.inCategoryIdToName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			categoryIdToName = (Map<Integer, String>) ois.readObject();
			ois.close();
			fis.close();
			logger.info("read [" + categoryIdToName.size() + "] category IDs from file ["
							+ Settings.ContrastEvaluation.IOFIles.inCategoryIdToName.getAbsolutePath() + "]");
		} catch (Exception e) {
			logger.severe("failed loading category-id-to-name from file ["
							+ Settings.ContrastEvaluation.IOFIles.inCategoryIdToName.getAbsolutePath() + "]");
		}

		try {
			FileInputStream fis = new FileInputStream(Settings.ContrastEvaluation.IOFIles.inCategoryNameToId);
			ObjectInputStream ois = new ObjectInputStream(fis);
			categoryNameToId = (Map<String, Integer>) ois.readObject();
			ois.close();
			fis.close();
			logger.info("read [" + categoryNameToId.size() + "] category names from file ["
							+ Settings.ContrastEvaluation.IOFIles.inCategoryNameToId.getAbsolutePath() + "]");
		} catch (Exception e) {
			logger.severe("failed loading category-name-to-id from file ["
							+ Settings.ContrastEvaluation.IOFIles.inCategoryNameToId.getAbsolutePath() + "]");
		}
	}
}
