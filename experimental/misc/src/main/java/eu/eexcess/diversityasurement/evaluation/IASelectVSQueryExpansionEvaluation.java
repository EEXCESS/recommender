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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
import eu.eexcess.diversityasurement.ndcg.NDCG;
import eu.eexcess.diversityasurement.ndcg.NDCGIA;
import eu.eexcess.diversityasurement.ndcg.NDCGIACategory;
import eu.eexcess.diversityasurement.ndcg.NDCGResult;
import eu.eexcess.diversityasurement.ndcg.NDCGResultList;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance.CategoryRelevance;
import eu.eexcess.logger.PianoLogger;

public class IASelectVSQueryExpansionEvaluation {

	private static Logger logger = PianoLogger.getLogger(IASelectVSQueryExpansionEvaluation.class);

	// Map<Document, HashSet<Category>> documentQualities = new
	// HashMap<Document, HashSet<Category>>();

	private static class EvaluationResult {

		Set<Query> queries = new LinkedHashSet<>();

		Map<Query, LinkedHashSet<Document>> luceneSearchResultList = new HashMap<>();
		Map<Query, LinkedHashSet<Document>> iaSelectResultList = new HashMap<>();
		Map<Query, LinkedHashSet<Document>> queryExpansionResultList = new HashMap<>();

		// Map<query, Map<k, NDCGResultList>> for ndcg of iaselect (as "ideal"
		// resutl) and query expansion (as "other" result)
		// Map<Query, Map<Integer, NDCGResultList>> NDCGArguments_IaQe = new
		// HashMap<>();

		Map<Query, Map<Integer, Double>> ndcg_LuQe = new HashMap<>();
		Map<Query, Map<Integer, Double>> ndcgIa_LuQe = new HashMap<>();
		// Map<Query, Map<Integer, Double>> spearmanRho_IaQe = new HashMap<>();
		// Map<Query, Map<Integer, Double>> kendallTao_IaQe = new HashMap<>();

		// Map<query, Map<k, NDCGResultList>> for ndcg of iaselect (as "ideal"
		// resutl) and lucene result (as "other" result)
		// Map<Query, Map<Integer, NDCGResultList>> NDCGArguments_IaL = new
		// HashMap<>();

		Map<Query, Map<Integer, Double>> ndcg_LuIa = new HashMap<>();
		Map<Query, Map<Integer, Double>> ndcgIa_LuIa = new HashMap<>();
		// Map<Query, Map<Integer, Double>> spearmanRho_IaL = new HashMap<>();
		// Map<Query, Map<Integer, Double>> kendallTao_IaL = new HashMap<>();

		int[] atKs = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	}

	public static void main(String[] args) throws Exception {
		long startTimestamp = System.currentTimeMillis();
		Set<Query> queries = getNormalizedManuallyProcessedQueries();
		System.out.println("read #queries [" + queries.size() + "]");

		EvaluationResult result = new EvaluationResult();

		QueryEvaluation.restoreCache();
		open();

		evaluateNDCG(queries, result);
		printResult(result);

		close();
		System.out.println("total duration [" + (System.currentTimeMillis() - startTimestamp) + "]ms");
	}

	private static void printResult(EvaluationResult result) {
		int maxResultsToPrint = Settings.QueryExpansionEvaluation.NUM_TOP_DOCS_TO_CONSIDER;
		//
		int queryNumbering = 0;
		System.out.println("qeuryNumber, ndcg, ndcgIA, k, query, idealName, otherName");
		for (Query q : result.queries) {
			printNDCGs(result.ndcg_LuIa, result.ndcgIa_LuIa, q, queryNumbering, "lucene", "ia-select", false, null);

		}
		queryNumbering = 0;
		System.out.println("qeuryNumber, ndcg, ndcgIA, k, query, idealName, otherName");
		for (Query q : result.queries) {
			printNDCGs(result.ndcg_LuQe, result.ndcgIa_LuQe, q, queryNumbering, "lucene", "qeury-exp", false, null);
			queryNumbering++;
		}

		//
		for (Integer k : result.atKs) {
			queryNumbering = 0;
			System.out.println("NDCG-IA for k [" + k + "]");
			System.out.println("qeuryNumber, ndcg, ndcgIA, k, query, idealName, otherName");
			for (Query q : result.queries) {
				printNDCGs(result.ndcg_LuQe, result.ndcgIa_LuIa, q, queryNumbering, "lucene", "ia-select", false, k);
				queryNumbering++;
			}
		}
		for (Integer k : result.atKs) {
			queryNumbering = 0;
			System.out.println("NDCG for k ["+k+"]");
			System.out.println("qeuryNumber, ndcg, ndcgIA, k, query, idealName, otherName");
			for (Query q : result.queries) {
				printNDCGs(result.ndcg_LuQe, result.ndcgIa_LuQe, q, queryNumbering, "lucene", "qeury-exp", false, k);
				queryNumbering++;
			}
		}
		
		//
		queryNumbering = 0;
		for (Query q : result.queries) {
			System.out.println();
			System.out.println("query [" + q.query + "]");

			printNDCGs(result.ndcg_LuIa, result.ndcgIa_LuIa, q, queryNumbering, "lucene", "ia-select", true, null);
			printNDCGs(result.ndcg_LuQe, result.ndcgIa_LuQe, q, queryNumbering, "lucene", "qeury-exp", true, null);
			queryNumbering++;

			// print ia selected results
			if (null == result.iaSelectResultList.get(q)) {
				System.err.println("no iaselected documents for query [" + q.query + "] available");
			} else {
				int printed = 0;
				for (Document d : result.iaSelectResultList.get(q)) {
					if (printed++ > maxResultsToPrint) {
						break;
					}
					System.out.println("ias: " + d);
				}
			}
			// print query expanded results
			if (null == result.queryExpansionResultList.get(q)) {
				System.err.println("no query expanded documents for query [" + q.query + "] available");
			} else {
				int printed = 0;
				for (Document d : result.queryExpansionResultList.get(q)) {
					if (printed++ > maxResultsToPrint) {
						break;
					}
					System.out.println("qex: " + d);
				}
			}
			// print lucene results
			if (null == result.luceneSearchResultList.get(q)) {
				System.err.println("no standard lucene documents for query [" + q.query + "] available");
			} else {
				int printed = 0;
				for (Document d : result.luceneSearchResultList.get(q)) {
					if (printed++ > maxResultsToPrint) {
						break;
					}
					System.out.println("luc: " + d);
				}
			}
		}
	}

	private static void printNDCGs(Map<Query, Map<Integer, Double>> ndcg, Map<Query, Map<Integer, Double>> ndcgIa,
					Query q, int queryNumber, String idealName, String otherName, boolean withTitle, Integer onlyK) {
		if (null == ndcg.get(q)) {
			System.err.println("no ndcg: [" + idealName + "(=ideal)]-[" + otherName + "(=other)] found for query ["
							+ q.query + "]");
		} else {
			if (null == ndcgIa.get(q)) {
				System.err.println("no ndcgIA: [" + idealName + "(=ideal)]-[" + otherName
								+ "(=other)] found for query [" + q.query + "]");
			} else {
				if (withTitle) {
					System.out.println("NDCG: [" + idealName + "(=ideal)]-[" + otherName + "(=other)] for query ["
									+ q.query + "]");
					System.out.println("qeuryNumber, ndcg, ndcgIA, k, query, idealName, otherName");
				}
				for (Map.Entry<Integer, Double> entry : ndcg.get(q).entrySet()) {
					int k = entry.getKey();
					if (onlyK != null) {
						if (k == onlyK) {
							double ndcgValue = entry.getValue();
							double ndcgIaValue = ndcgIa.get(q).get(k);
							System.out.println(queryNumber + "," + ndcgValue + ", " + ndcgIaValue + ", " + k + ", "
											+ q.query + ", " + idealName + ", " + otherName);
						}
					} else {
						double ndcgValue = entry.getValue();
						double ndcgIaValue = ndcgIa.get(q).get(k);
						System.out.println(queryNumber + "," + ndcgValue + ", " + ndcgIaValue + ", " + k + ", "
										+ q.query + ", " + idealName + ", " + otherName);
					}
				}
			}
		}
	}

	/**
	 * run evaluation
	 * 
	 * @param queries
	 * @param resultCollector
	 * @throws Exception
	 */
	private static void evaluateNDCG(Set<Query> queries, EvaluationResult resultCollector) throws Exception {

		// have an overview of queries in result
		for (Query q : queries) {
			resultCollector.queries.add(q);
		}

		int k = 10;
		resultCollector.iaSelectResultList = iaSelect(queries, k, resultCollector.luceneSearchResultList);
		resultCollector.queryExpansionResultList = queryExpansion(queries);

		Map<Query, LinkedHashSet<Document>> otherResultIa = resultCollector.iaSelectResultList;
		Map<Query, LinkedHashSet<Document>> otherResultQE = resultCollector.queryExpansionResultList;
		Map<Query, LinkedHashSet<Document>> idealResultLu = resultCollector.luceneSearchResultList;
		Map<Query, LinkedHashSet<Document>> idealResult = idealResultLu;

		// run ndcg against iaselect
		for (Query q : queries) {

			LinkedHashSet<Document> idealResultList = idealResult.get(q);
			LinkedHashSet<Document> otherResultList = otherResultIa.get(q);

			if (null == idealResultList) {
				logger.severe("ndcg-ia-lucene failed find query [" + q.query + "]: not in optimal result list");
				continue;
			}
			if (null == otherResultList) {
				logger.severe("ndcg-ia-lucene failed find query [" + q.query + "]: not in other result list");
				continue;
			}

			ArrayList<NDCGIACategory> queryCategories = new ArrayList<NDCGIACategory>();
			for (Category cat : q.categories()) {
				queryCategories.add(new NDCGIACategory(cat.name, cat.probability));
			}

			System.out.println("ndcgSummary idealResultLu(=ideal)-otherResultIa(=other) at query [" + q + "]");
			Map<Integer, List<Double>> ndcgIaQe_AtK_NCDG_NDCGIA = ndcgSummary(idealResultList, otherResultList,
							queryCategories, resultCollector.atKs);
			Map<Integer, Double> ndcgMap = new HashMap<>();
			Map<Integer, Double> ndcgIaMap = new HashMap<>();

			for (Map.Entry<Integer, List<Double>> entry : ndcgIaQe_AtK_NCDG_NDCGIA.entrySet()) {
				Iterator<Double> iter = entry.getValue().iterator();
				double ndcg = iter.next();
				double ndcgIa = iter.next();
				Integer atK = entry.getKey();
				ndcgMap.put(atK, ndcg);
				ndcgIaMap.put(atK, ndcgIa);
			}
			resultCollector.ndcg_LuIa.put(q, ndcgMap);
			resultCollector.ndcgIa_LuIa.put(q, ndcgIaMap);
		}

		// run ndcg against expansion
		for (Query q : queries) {

			LinkedHashSet<Document> idealResultList = idealResult.get(q);
			LinkedHashSet<Document> otherResultList = otherResultQE.get(q);

			if (null == idealResultList) {
				logger.severe("ndcg-ia-qe failed find query [" + q.query + "]: not in optimal result list");
				continue;
			}
			if (null == otherResultList) {
				logger.severe("ndcg-ia-qe failed find query [" + q.query + "]: not in other result list");
				continue;
			}

			ArrayList<NDCGIACategory> queryCategories = new ArrayList<NDCGIACategory>();
			for (Category cat : q.categories()) {
				queryCategories.add(new NDCGIACategory(cat.name, cat.probability));
			}

			System.out.println("ndcgSummary idealResultLu(ideal)-otherResultQE(other) at query [" + q + "]");
			Map<Integer, List<Double>> ndcg_AtK_NCDG_NDCGIA = ndcgSummary(idealResultList, otherResultList,
							queryCategories, resultCollector.atKs);
			Map<Integer, Double> ndcgMap = new HashMap<>();
			Map<Integer, Double> ndcgIaMap = new HashMap<>();

			for (Map.Entry<Integer, List<Double>> entry : ndcg_AtK_NCDG_NDCGIA.entrySet()) {
				Iterator<Double> iter = entry.getValue().iterator();
				double ndcg = iter.next();
				double ndcgIa = iter.next();
				Integer atK = entry.getKey();
				ndcgMap.put(atK, ndcg);
				ndcgIaMap.put(atK, ndcgIa);
			}
			resultCollector.ndcg_LuQe.put(q, ndcgMap);
			resultCollector.ndcgIa_LuQe.put(q, ndcgIaMap);
		}
	}

	/**
	 * calculate NDCG for all k ∈ at
	 * 
	 * @param idealResultList
	 *            ideal ordered result list
	 * @param otherResultList
	 *            an other result list
	 * @param at
	 *            array of values to calculate NDCG@k
	 * @throws IOException
	 * @return Map<k, List<Double>> with ap value 1st=ndcg 2nd=ndcgia
	 */
	private static Map<Integer, List<Double>> ndcgSummary(LinkedHashSet<Document> idealResultList,
					LinkedHashSet<Document> otherResultList, ArrayList<NDCGIACategory> queryCategory, int[] at)
					throws IOException {

		int maxK = 0;
		for (int k : at) {
			maxK = (k > maxK) ? k : maxK;
		}

		Map<Integer, ArrayList<CategoryRelevance>> documentToCategoriesRelevance = new HashMap<>();

		// for each ideal sorted document: collect categories and relevances
		for (Document document : idealResultList) {
			if (!documentToCategoriesRelevance.containsKey(document.documentId)) {
				documentToCategoriesRelevance.put(document.documentId, getDocumentRelevances(document));
				// System.out.println("ideal: " + document.name);
			}
		}
		// for each other document: collect categories and relevances
		for (Document document : otherResultList) {
			if (!documentToCategoriesRelevance.containsKey(document.documentId)) {
				documentToCategoriesRelevance.put(document.documentId, getDocumentRelevances(document));
				// System.out.println("other: " + document.name);
			}
		}

		// int volatileDocumentOrderRank =
		int maxCategoryRank = getMaxCategoryOverlap(queryCategory, idealResultList);
		System.out.println("CatCount for all :" + maxCategoryRank);

		// calculate document rank based on ideally sorted result list
		NDCG.RankToJudgementMapper rankMapper = new NDCG.RankToJudgementMapper(maxCategoryRank);
		Map<Integer, Integer> documentRankMapping = new HashMap<>(idealResultList.size());
		for (Document document : idealResultList) {
			int section = rankMapper.r(document.priority);
			document.priority = section;
			documentRankMapping.put(document.documentId, section);
		}

		// build an ndcg result list in same order as otherResultList (not
		// ideally sorted list)
		NDCGResultList ndcgList = new NDCGResultList();
		System.out.println("ndcg argument list (NDCGResultList):");
		int takenDocs = 0;
		for (Document document : otherResultList) {
			if (takenDocs++ >= maxK) {
				break;
			}

			NDCGResult ndcgDoc = new NDCGResult();

			// add order dependent relevance
			Integer rank = documentRankMapping.get(document.documentId);
			if (null == rank) {
				ndcgDoc.nDCGRelevance = 0;
			} else {
				ndcgDoc.nDCGRelevance = rank;
			}

			// add categories if available
			List<CategoryRelevance> categories = documentToCategoriesRelevance.get(document.documentId);
			if (null != categories) {
				for (CategoryRelevance docCatgory : documentToCategoriesRelevance.get(document.documentId)) {
					ndcgDoc.categories.add(new NDCGIACategory(docCatgory.categoryName, docCatgory.categoryRelevance));
				}
			}
			ndcgList.results.add(ndcgDoc);
		}

		System.out.println("--->");
		System.out.println("ndcg args:");
		for (NDCGResult r : ndcgList.results) {
			System.out.println("ndcg-arg:" + r);
		}

		int printed = 0;
		System.out.println("ndcg args ideal list:");
		for (Document d : idealResultList) {
			if (printed++ >= maxK) {
				break;
			}
			System.out.println("ideal-doc:" + d);
		}

		printed = 0;
		System.out.println("ndcg args other list:");
		for (Document d : otherResultList) {
			if (printed++ >= maxK) {
				break;
			}
			System.out.println("other-doc:" + d);
		}

		// run ndcg for each i in at
		Map<Integer, List<Double>> ndcgATKResults = new HashMap<>();
		for (Integer k : at) {
			List<Double> results = new LinkedList<>();
			// 1st ndcg
			NDCG ndcgCalc = new NDCG();
			double ndcg = ndcgCalc.calcNDCG(ndcgList, null, k);
			results.add(ndcg);
			// 2nd ndcgia
			NDCGIA ndcgIaCalc = new NDCGIA();
			double ndcgIa = ndcgIaCalc.calcNDCGIA(ndcgList, queryCategory, k);
			results.add(ndcgIa);

			ndcgATKResults.put(k, results);

			System.out.println("ndcg@" + k + "=" + ndcg);
			System.out.println("ndcgIa@" + k + "=" + ndcgIa);
		}
		System.out.println("<---");

		return ndcgATKResults;
	}

	private static int getMaxCategoryOverlap(ArrayList<NDCGIACategory> queryCategory,
					LinkedHashSet<Document> idealResultList) {
		int maxCategoryRank = 0;

		for (Document document : idealResultList) {
			int catCount = 0;
			for (Category c : document.getTopCategories(queryCategory.size() * 5)) {
				blub: for (NDCGIACategory ndcgiaCategory : queryCategory) {
					if (ndcgiaCategory.getCategoryName().equals(c.name)) {
						catCount++;
						break blub;
					}
				}
			}

			System.out.println("CatCount found Cat " + document.name + " :" + catCount);
			document.priority = catCount;
			if (maxCategoryRank < catCount)
				maxCategoryRank = catCount;
		}
		return maxCategoryRank;
	}

	/**
	 * return a list of category relevances for this document to all top
	 * categories
	 * 
	 * @param document
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<CategoryRelevance> getDocumentRelevances(Document document) throws IOException {

		IndexSearcher indexSearcher = new IndexSearcher(QueryCategoryKShortestPathsEvaluation.indexReader);
		org.apache.lucene.document.Document doc = indexSearcher.doc(document.documentId);

		eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query empty = new eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query();
		empty.query = "-";

		ArrayList<Integer> seenCategoryIds = new ArrayList<>();
		ArrayList<CategoryToTopCategoryRelevance> categoriesToTopCategoriesRelevanceCollector = new ArrayList<>();

		QueryEvaluation.collectDocTopCategoryRelevances(doc, empty, seenCategoryIds,
						categoriesToTopCategoriesRelevanceCollector);
		return QueryEvaluation.collapseTopCategoryRelevances(categoriesToTopCategoriesRelevanceCollector);
	}

	/**
	 * run iaselect for each query @k
	 * 
	 * @param queries
	 * @param k
	 * @param luceneResultDocumentCollector
	 * @return
	 * @throws Exception
	 */
	private static Map<Query, LinkedHashSet<Document>> iaSelect(Set<Query> queries, int k,
					Map<Query, LinkedHashSet<Document>> luceneResultDocumentCollector) throws Exception {

		Map<Query, LinkedHashSet<Document>> result = new LinkedHashMap<>();
		int qIdx = 0;

		for (Query q : queries) {
			LinkedHashSet<Document> documents = new LinkedHashSet<>();
			R(q, documents, Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider);
			if (documents.size() <= 0) {
				logger.severe("query [" + q + "] [" + qIdx + "/" + queries.size() + "] generates no documents");
				continue;
			}
			if (k > documents.size()) {
				logger.severe("query [" + q + "] [" + qIdx + "/" + queries.size() + "] generates too less documents ["
								+ documents.size() + "]");
				continue;
			}

			LinkedHashSet<Document> documentsToCollect = new LinkedHashSet<>();
			for (Document d : documents) {
				documentsToCollect.add(d);
			}

			luceneResultDocumentCollector.put(q, documentsToCollect);
			result.put(q, iaSelect(k, q, documents));
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
		R(expandedQuery, documentCollector, Settings.QueryExpansionEvaluation.NUM_TOP_DOCS_TO_CONSIDER);
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

		Set<Query> result = new LinkedHashSet<Query>();
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
	private static void R(Query q, LinkedHashSet<Document> documentCollector, int numTopDocsToconsider)
					throws IOException, ParseException {

		IndexSearcher indexSearcher = new IndexSearcher(QueryCategoryKShortestPathsEvaluation.indexReader);
		QueryParser queryParser = new QueryParser(Settings.RelevanceEvaluation.Lucene.SEARCH_FIELD_SECTIONTEXT,
						new EnglishAnalyzer());
		org.apache.lucene.search.Query query = queryParser.parse(q.query);

		// / NUM_TOP_DOCS_TO_CONSIDER

		// all docs of query
		TopDocs docs = indexSearcher.search(query, numTopDocsToconsider);

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
			iaDocument.documentScore = (double) sDoc.score / (double) docs.getMaxScore();

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
