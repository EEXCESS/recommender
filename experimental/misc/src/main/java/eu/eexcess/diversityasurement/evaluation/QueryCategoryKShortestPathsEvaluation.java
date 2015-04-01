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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math.estimation.EstimatedParameter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import eu.eexcess.diversityasurement.wikipedia.GrphTupleCollector;
import eu.eexcess.diversityasurement.wikipedia.MainCategoryRelevanceEstimator;
import eu.eexcess.diversityasurement.wikipedia.RDFCategoryExtractor;
import eu.eexcess.diversityasurement.wikipedia.config.Settings;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Queries;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.QueriesCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.QueryCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.QueryJsonIO;
import eu.eexcess.logger.PianoLogger;
import eu.eexcess.sourceselection.redde.Redde.QueryRelevance;
import grph.Grph;

public class QueryCategoryKShortestPathsEvaluation {

	Logger logger = PianoLogger.getLogger(QueryCategoryKShortestPathsEvaluation.class);

	private int[] topCategories;
	private int[] kShortestPaths = new int[] { 3, 5, 10, 30, 100 };

	Grph grph;
	private Map<String, Integer> categoryIds;
	private Map<String, Integer> topCategoryIds;
	private final String[] SEARCH_FIELD = { "sectionText" };
	private File luceneIndexDirectory = new File("/opt/data/wikipedia/eexcess/enwiki-big/");
	private File jsonResult = new File("/opt/iaselect/results/category-estimation-" + System.currentTimeMillis()
					+ ".json");
	private int numTopDocumentsToConsider = 1;
	QueryCategoryRelevance queriesRelevances = new QueryCategoryRelevance();
	MainCategoryRelevanceEstimator estimator;

	public static void main(String[] args) {
		QueryCategoryKShortestPathsEvaluation self = new QueryCategoryKShortestPathsEvaluation();

		switch (args[0]) {
		case "--complete":
			try {
				self.inflateCategoryTree();
				ArrayList<QueryCategoryRelevance> qcrList = new ArrayList<>();
				int queryIdx = 0;
				int numQueries = self.getQueries().size();
				for (String query : self.getQueries()) {
					int[] relevantCategories = self.getQueryCategories(query, self.numTopDocumentsToConsider);
					System.out.println("relevance of query [" + queryIdx++ + "/" + numQueries++ + "] [" + query
									+ "] with [" + relevantCategories.length + "] categories: ");
					QueryCategoryRelevance qrel = self.evaluateKSortestPaths(relevantCategories, self.kShortestPaths);
					qrel.query = query;
					qcrList.add(qrel);
					
				}
				QueriesCategoryRelevance qscr = new QueriesCategoryRelevance();
				qscr.queries = qcrList.toArray(new QueryCategoryRelevance[qcrList.size()]);
				QueryJsonIO.writeQueries(self.jsonResult, qscr);
				System.out.println("cache statistics: " + self.estimator.getStatistics());
			} catch (IOException | ParseException e) {
				self.logger.log(Level.SEVERE, "failed estimating query to category relevance [" + e.getMessage() + "]");
			}
			break;
		case "--one-query": // deprecated, testing only
			try {
				self.inflateCategoryTree();
				ArrayList<QueryCategoryRelevance> qcrList = new ArrayList<>();
				for (String query : new String[] { args[1] }) {
					int[] relevantCategories = self.getQueryCategories(query, self.numTopDocumentsToConsider);
					qcrList.add(self.evaluateKSortestPaths(relevantCategories, self.kShortestPaths));
				}
				QueriesCategoryRelevance qscr = new QueriesCategoryRelevance();
				qscr.queries = qcrList.toArray(new QueryCategoryRelevance[qcrList.size()]);
				QueryJsonIO.writeQueries(self.jsonResult, qscr);
			} catch (IOException | ParseException e) {
				self.logger.log(Level.SEVERE, "failed estimating query to category relevance [" + e.getMessage() + "]");
			}
			break;
		}
		System.out.println("\n\nfinished");
	}

	/**
	 * read queries from file
	 */
	private ArrayList<String> getQueries() throws IOException {
		Queries queries = QueryJsonIO.readQueries(new File(Settings.Queries.PATH));

		ArrayList<String> queryList = new ArrayList<>(queries.queries.length);
		for (Query query : queries.queries) {
			queryList.add(query.query);
		}
		return queryList;
	}

	/**
	 * get top categories for query from lucene index
	 */
	private int[] getQueryCategories(String queryString, int numDocumentsToConsider) throws ParseException, IOException {

		HashSet<Integer> categoryLabels = new HashSet<>();
		Directory directory = FSDirectory.open(luceneIndexDirectory);
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryParser = new QueryParser(SEARCH_FIELD[0], new EnglishAnalyzer());
		// queryParser.setDefaultOperator(Operator.OR); // TODO
		org.apache.lucene.search.Query query = null;
		query = queryParser.parse(queryString);

		TopDocs topDocs = indexSearcher.search(query, numDocumentsToConsider);

		int totalFoundCategories = 0;
		int totalMissedCategories = 0;

		for (ScoreDoc sDocs : topDocs.scoreDocs) {
			org.apache.lucene.document.Document doc = indexSearcher.doc(sDocs.doc);
			IndexableField[] categories = doc.getFields("category");
			int foundCategories = 0;
			int missedCategories = 0;
			// collect all docs categories
			for (IndexableField category : categories) {
				Integer categoryId = categoryIds.get(category.stringValue().replace(" ", "_"));
				if (categoryId == null) {
					// logger.warning("failed to lookup category [" +
					// category.stringValue().replace(" ", "_") + "]");
					missedCategories++;
				} else {
					categoryLabels.add(categoryId);
					foundCategories++;
				}
			}
			totalFoundCategories += foundCategories;
			totalMissedCategories += missedCategories;
		}
		logger.info("found [" + totalFoundCategories + "] missed [" + totalMissedCategories + "] categories out of ["
						+ (totalFoundCategories + totalMissedCategories) + "] categories");

		int[] categoryIds = new int[categoryLabels.size()];
		int idx = 0;
		for (Integer id : categoryLabels) {
			categoryIds[idx++] = id;
		}
		return categoryIds;
	}

	private void inflateCategoryTree() throws IOException {
		logger.info("inflating tree from [" + Settings.RDFCategories.PATH + "]");
		GrphTupleCollector collector = new GrphTupleCollector(350000);
		RDFCategoryExtractor extractor = new RDFCategoryExtractor(new File(Settings.RDFCategories.PATH), collector);
		extractor.extract();
		grph = collector.getGraph();
		logger.info(extractor.getStatistics().toString());
		logger.info(collector.getStatistics().toString());

		// get category id's of top categories
		categoryIds = collector.getCategoryMap();
		String[] topCategoryLabels = { "Agriculture", "Architecture", "Arts", "Behavior", "Chronology", "Concepts",
						"Creativity", "Culture", /* "Disciplines", */"Education", "Environment", "Geography",
						"Government", "Health", "History", "Humanities", "Humans", "Industry", "Information",
						"Knowledge", "Language", "Law", "Life", "Mathematics", "Matter", "Medicine", "Mind", "Nature",
						"Objects", "People", "Politics", "Science", "Society", "Sports", "Structure", "Systems",
						"Technology", "Universe", "World" };

		topCategoryIds = new HashMap<>();
		topCategoryIds.values().toArray();
		int idx = 0;
		topCategories = new int[topCategoryLabels.length];
		for (String topCategoryLabel : topCategoryLabels) {
			Integer id = categoryIds.get(topCategoryLabel);
			if (null == id) {
				logger.severe("category [" + topCategoryLabel + "] not found!");
				continue;
			}
			topCategoryIds.put(topCategoryLabel, id);
			topCategories[idx++] = id;
		}
		System.out.println();
		estimator = new MainCategoryRelevanceEstimator(grph, topCategories);
	}

	/**
	 * calculate category relevance for all startCategoryIds and different
	 * maxKShortestPaths
	 */
	private QueryCategoryRelevance evaluateKSortestPaths(int[] startCategoryIds, int[] maxKShortestPaths /* TODO */) {

		estimator.setNumCategoriesToCalculateBundled(1);
		// initialize a relevance map
		HashMap<Integer, Double> relevanceDistribution = new HashMap<>(topCategories.length);
		for (Integer topCategory : topCategories) {
			relevanceDistribution.put(topCategory, 0.0);
		}

		logger.info("start estimation....");
		long startTimestamp = System.currentTimeMillis();
		// merge every's start topic relevance to one relevance map
		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : estimator.estimateRelevancesConcurrent(
						startCategoryIds, kShortestPaths[0], 7, 7).entrySet()) {
			logger.info("estimation done in [ " + (System.currentTimeMillis() - startTimestamp) + "] ms");
			HashMap<Integer, Double> mapTocollect = entry.getValue();
			for (Map.Entry<Integer, Double> entryTocollect : mapTocollect.entrySet()) {
				Integer category = entryTocollect.getKey();
				Double relevance = entryTocollect.getValue();
				if (!(relevance == null) && !(relevance.isNaN())) {
					Double oldRelevance = relevanceDistribution.get(category);
					relevanceDistribution.put(category, oldRelevance + relevance);
				}
			}
		}

		// convert map to QueryCategoryRelevance
		QueryCategoryRelevance qcr = new QueryCategoryRelevance();
		qcr.relevances = new QueryCategoryRelevance.Relevance[relevanceDistribution.size()];
		int idx = 0;
		for (Map.Entry<Integer, Double> entry : relevanceDistribution.entrySet()) {
			QueryCategoryRelevance.Relevance qcrr = new QueryCategoryRelevance.Relevance();
			qcrr.categoryId = entry.getKey();
			qcrr.categoryName = getCategoryName(qcrr.categoryId);
			qcrr.categoryrelevance = entry.getValue();
			qcr.relevances[idx++] = qcrr;
		}
		return qcr;
	}

	private String getCategoryName(Integer id) {
		for (Map.Entry<String, Integer> entry : categoryIds.entrySet()) {
			if (entry.getValue().compareTo(id) == 0) {
				return entry.getKey();
			}
		}
		return null;
	}
}
