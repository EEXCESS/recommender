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
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.diversityasurement.evaluation.config.Settings;
import eu.eexcess.diversityasurement.iaselect.Category;
import eu.eexcess.diversityasurement.iaselect.Document;
import eu.eexcess.diversityasurement.iaselect.IASelect;
import eu.eexcess.diversityasurement.iaselect.Query;
import eu.eexcess.diversityasurement.iaselect.ScoreBasedDocumentQualityValueV;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance.CategoryRelevance;
import eu.eexcess.logger.PianoLogger;

public class IASelectVSQueryExpansionEvaluation {

	private static Logger logger = PianoLogger.getLogger(IASelectVSQueryExpansionEvaluation.class);

	Map<Document, HashSet<Category>> documentQualities = new HashMap<Document, HashSet<Category>>();

	public static void main(String[] args) throws Exception {
		long startTimestamp = System.currentTimeMillis();
		Set<Query> queries = getNormalizedManuallyProcessedQueries();
		System.out.println("read #queries [" + queries.size() + "]");

		QueryEvaluation.restoreCache();
		open();

		// run ia-select
		int k = 10;
		Map<Query, LinkedHashSet<Document>> iasResult = iaSelect(queries, k);
		Map<Query, LinkedHashSet<Document>> qexpResult = queryExpansion(queries);

		int[] at = { 3, 4, 5 };
		ndcgSummary(iasResult, qexpResult, at);

		close();
		System.out.println("total duration [" + (System.currentTimeMillis() - startTimestamp) + "]ms");
	}

	/**
	 * compare results using ndcg for values in at
	 * 
	 * @param iasResult
	 *            IA-Select results
	 * @param qexpResult
	 *            query expansion results
	 * @param at
	 *            array of values to calculate NDCG@at
	 */
	private static void ndcgSummary(Map<Query, LinkedHashSet<Document>> iasResult,
					Map<Query, LinkedHashSet<Document>> qexpResult, int[] at) {
		
		// get categories of docs
		// add relevance to categories

		// calculate rank of docs from ideally sorted list

		// run ndcd for each i in at

	}

	private static Map<Query, LinkedHashSet<Document>> iaSelect(Set<Query> queries, int k) throws Exception {

		Map<Query, LinkedHashSet<Document>> result = new LinkedHashMap<>();
		int qIdx = 0;

		for (Query q : queries) {
			System.out.println("\n");
			LinkedHashSet<Document> documents = new LinkedHashSet<>();
			R(q, documents);
			if (documents.size() <= 0) {
				logger.severe("query [" + q + "] [" + qIdx + "/" + queries.size() + "] generates no documents");
				continue;
			}

			System.out.println("run IA-Select for query [" + q.query + "] query# [" + qIdx + "/" + queries.size()
							+ "] and #documents [" + documents.size() + "] ");
			result.put(q, iaSelect(k, q, documents));

			for (Document d : iaSelect(k, q, documents)) {
				System.out.println("ia-select: " + d);
			}

			System.out.println("ia-select vs. default R(q):");
			int maxIdx = k;
			for (Document d : documents) {
				if (maxIdx-- <= 0) {
					break;
				}
				System.out.println("R(q):      " + d);
			}

			qIdx++;
		}
		return result;
	}

	/**
	 * select top k documents from a set of documents acording to the ia-select
	 * implementation: {@link IASelect}
	 * 
	 * @param k
	 *            num docs to select
	 * @param q
	 *            the query
	 * @param Rq
	 *            list of documents of which to select k docs
	 * @return
	 * @throws Exception
	 */
	private static LinkedHashSet<Document> iaSelect(int k, Query q, Set<Document> Rq) throws Exception {
		ScoreBasedDocumentQualityValueV V = new ScoreBasedDocumentQualityValueV();
		IASelect diversifyer = new IASelect();
		LinkedHashSet<Document> diverdified = diversifyer.iaSelect(k, q, Rq, V);
		return diverdified;
	}

	private static Map<Query, LinkedHashSet<Document>> queryExpansion(Set<Query> queries) throws IOException,
					ParseException {

		Map<Query, LinkedHashSet<Document>> result = new HashMap<>();
		for (Query q : queries) {
			result.put(q, queryExpansion(q));
		}
		return result;
	}

	/**
	 * run lucene searcher with expanded queries
	 * 
	 * @param q
	 *            the query
	 * @return a more diverse result list according to a usual search with non
	 *         expanded q
	 * @throws IOException
	 * @throws ParseException
	 */
	private static LinkedHashSet<Document> queryExpansion(Query q) throws IOException, ParseException {

		SecureUserProfile expandedSPQuery = QueryExpansionEvaluation.expandQuery(q,
						Settings.QueryExpansionEvaluation.MAX_TERMS_TO_EXPAND_QUERY);
		Query expandedQuery = new Query(QueryExpansionEvaluation.toQuery(expandedSPQuery));

		LinkedHashSet<Document> documentCollector = new LinkedHashSet<>();
		R(expandedQuery, documentCollector);
		return documentCollector;
	}

	/**
	 * Read manualy processed queries from
	 * {@link Settings.RelevanceEvaluation.IOFiles.inManuallySelectedWeightedNotNormalizedQueries}
	 * and normalize relevances
	 * 
	 * @return normalized queries
	 * @throws IOException
	 */
	private static Set<Query> getNormalizedManuallyProcessedQueries() throws IOException {

		Set<Query> result = new HashSet<Query>();
		File queries = Settings.RelevanceEvaluation.IOFiles.inManuallySelectedWeightedNotNormalizedQueries;
		Map<eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query, ArrayList<CategoryRelevance>> inQueries = readQueries(queries);

		for (Map.Entry<eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query, ArrayList<CategoryRelevance>> entry : inQueries
						.entrySet()) {
			ArrayList<CategoryRelevance> catetoryRelevances = entry.getValue();

			double sum = 0.0;
			for (CategoryRelevance cr : catetoryRelevances) {
				sum += cr.categoryRelevance;
			}

			Query q = new Query(entry.getKey().query);
			for (CategoryRelevance cr : catetoryRelevances) {
				q.addCategory(new Category(cr.categoryName, cr.categoryRelevance / sum));
			}

			result.add(q);
		}
		return result;
	}

	/**
	 * reads unnormalized query map
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static Map<eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query, ArrayList<CategoryRelevance>> readQueries(
					File file) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(file));
		Type type = new TypeToken<Map<eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query, ArrayList<CategoryRelevance>>>() {
		}.getType();
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		Map<eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query, ArrayList<CategoryRelevance>> inQueries = gson
						.fromJson(reader, type);
		reader.close();
		return inQueries;
	}

	/**
	 * fetch top documents for query q and determine category and document
	 * probabilities to all top categories
	 * 
	 * @param q
	 *            query
	 * @param documentCollector
	 *            set where documents will be stored to
	 * @param documentToCategoryCollector
	 *            map where document category sets will be stored to
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void R(Query q, LinkedHashSet<Document> documentCollector) throws IOException, ParseException {

		IndexSearcher indexSearcher = new IndexSearcher(QueryCategoryKShortestPathsEvaluation.indexReader);
		QueryParser queryParser = new QueryParser(Settings.RelevanceEvaluation.Lucene.SEARCH_FIELD_SECTIONTEXT,
						new EnglishAnalyzer());
		org.apache.lucene.search.Query query = queryParser.parse(q.query);

		// all docs of query
		TopDocs docs = indexSearcher.search(query,
						Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider);
		Document.maxDocumentScore = docs.getMaxScore();

		eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query iaQuery = new eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query();
		iaQuery.query = q.query;

		// docs top category relevances
		for (ScoreDoc sDoc : docs.scoreDocs) {
			org.apache.lucene.document.Document doc = indexSearcher.doc(sDoc.doc);

			ArrayList<Integer> seenCategoryIds = new ArrayList<>();
			ArrayList<CategoryToTopCategoryRelevance> categoriesToTopCategoriesRelevanceCollector = new ArrayList<>();

			QueryEvaluation.collectDocTopCategoryRelevances(doc, iaQuery, seenCategoryIds,
							categoriesToTopCategoriesRelevanceCollector);

			ArrayList<CategoryRelevance> collapsedNormalizedTopCategoriesRelevance = QueryEvaluation
							.collapseTopCategoryRelevances(categoriesToTopCategoriesRelevanceCollector);

			// store all top categories relevances of a document
			Set<Category> iaCategories = new HashSet<>(collapsedNormalizedTopCategoriesRelevance.size());
			for (CategoryRelevance cRelevance : collapsedNormalizedTopCategoriesRelevance) {
				Category iaCategory = new Category(cRelevance.categoryName, cRelevance.categoryRelevance);
				iaCategories.add(iaCategory);
			}
			Document iaDocument = new Document(doc.getField(Settings.RelevanceEvaluation.Lucene.SEARCH_FIELD_TITLE)
							.stringValue(), iaCategories, sDoc.doc);
			iaDocument.documentScore = sDoc.score;

			documentCollector.add(iaDocument);
		}

		// // query top categories relevance
		// ArrayList<CategoryToTopCategoryRelevance>
		// queryCategoriesToTopCategoriesRelevance = QueryEvaluation
		// .collectTopDocsCategoryRelevances(iaQuery,
		// Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider);
		//
		// ArrayList<CategoryRelevance>
		// collapsedNormalizedQueryTopCategoriesRelevance = QueryEvaluation
		// .collapseTopCategoryRelevances(queryCategoriesToTopCategoriesRelevance);
		//
		// for (CategoryRelevance cRelevance :
		// collapsedNormalizedQueryTopCategoriesRelevance) {
		// Category iaCategory = new Category(cRelevance.categoryName,
		// cRelevance.categoryRelevance);
		// q.addCategory(iaCategory);
		// }
	}

	private static void close() throws IOException {
		QueryEvaluation.close();
	}

	private static void open() throws IOException {
		QueryEvaluation.open();
	}

}
