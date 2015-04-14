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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import eu.eexcess.diversityasurement.wikipedia.GrphTupleCollector;
import eu.eexcess.diversityasurement.wikipedia.MainCategoryRelevanceEstimator;
import eu.eexcess.diversityasurement.wikipedia.RDFCategoryExtractor;
import eu.eexcess.diversityasurement.wikipedia.config.Settings;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryRelevanceDetails;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryRelevanceDetails.TopCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Queries;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.QueryJsonIO;
import eu.eexcess.logger.PianoLogger;
import grph.Grph;

public class QueryCategoryKShortestPathsEvaluation {

	public static class IOFIles {
		public File inLuceneIndexDirectory = new File("/opt/data/wikipedia/eexcess/enwiki-big/");
		public File outLuceneIndexDirectory = new File("/opt/iaselect/results/category-relevance-index-big/");
		public File outCachedNodes = new File("/opt/iaselect/results/cache/cached-nodes.bin");
		public File outCachedPaths = new File("/opt/iaselect/results/cache/cached-paths.bin");
		public File outCategoryIdToName = new File("/opt/iaselect/results/cache/category-id-to-name.bin");
		public File outCategoryNameToId = new File("/opt/iaselect/results/cache/category-name-to-id.bin");
	}

	public static class ConcurrentSettigns {
		public int totalThreads = 1;
		// number of categories a thread calculates sequentially
		public int numCategoriesToCalculateBundled = 1;
	}

	public static class EstimationArguments {
		private int numTopDocumentsToConsider = 60;
		private int numKClosestCategoryNeighborsToConsider = 1000;
		private int kShortestPaths = 1;
		private int nodesPerChunk = 2000;
		private boolean isDistributionStartedAtSiblings = false;
	}

	Logger logger = PianoLogger.getLogger(QueryCategoryKShortestPathsEvaluation.class);
	Grph grph;
	CategoryRelevanceDetails queriesRelevances = new CategoryRelevanceDetails();
	MainCategoryRelevanceEstimator estimator;
	private static final String SEARCH_FIELD_SECTIONTEXT = "sectionText";
	private int[] topCategories;
	private Map<String, Integer> categoryNameToId;
	private Map<Integer, String> categoryIdToName;
	private Map<String, Integer> topCategoryIds;
	public IOFIles fileResource = new IOFIles();
	private ConcurrentSettigns concurrentSettings = new ConcurrentSettigns();
	private EstimationArguments estimationArguments = new EstimationArguments();
	private IndexReader indexReader;
	private IndexWriter indexWriter;

	private int chunkStartIdx = 0;

	/**
	 * Reads category relations (parent<-child) from RDF {@link #settings.RDFCategories.PATH} and
	 * estimates their relation to top categories. Estimates are done only for
	 * categories found by {@link #settings.Queries.PATH} looked up in {@link #fileResource.inLuceneIndexDirectory}
	 * taking all categories of all top
	 * {@link estimationArguments.numTopDocumentsToConsider} documents. Results
	 * are stored after every chunk with {@link #estimationArguments.nodesPerChunk} nodes is
	 * processed.
	 * <p>
	 * Results:<br>
	 * relevances are written to lucene index in
	 * {@link fileResource.outLuceneIndexDirectory} <br>
	 * cache (shortest paths, known nodes) is stored to {@link #fileResource.outCachedNodes}
	 * and {@link #fileResource.outCachedPaths}
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		long startTimestamp = System.currentTimeMillis();
		QueryCategoryKShortestPathsEvaluation self = new QueryCategoryKShortestPathsEvaluation();

		try {
			self.openInIndex();
			self.openOutIndex();
			self.inflateCategoryTree();
			self.tryRestoreCache();
			ArrayList<String> queries = self.getQueries();

			// collect categories from index
			ArrayList<Integer> categoryIds = self.collectTopDocsCategories(queries,
							self.estimationArguments.numTopDocumentsToConsider, SEARCH_FIELD_SECTIONTEXT,
							self.estimationArguments.numTopDocumentsToConsider);

			// evaluate and store to result-index in chunks:
			int chunkCount = 0;
			ArrayList<Integer> chunk = self.nextChunk(categoryIds);
			while (chunk.size() > 0) {
				self.logger.info("chunk count [" + chunkCount++ + "] " + chunk);
				self.logger.info(chunk.toString());

				self.evaluateKSortestPaths(chunk, self.estimationArguments.kShortestPaths,
								self.estimationArguments.isDistributionStartedAtSiblings);
				self.indexWriter.commit();
				self.trySaveCache();
				chunk = self.nextChunk(categoryIds);
			}

			self.closeInIndex();
			self.closeOutIndex();
			self.trySaveCache();

			self.logger.info("total duration: [" + (System.currentTimeMillis() - startTimestamp) + "]ms");

		} catch (IOException | ParseException e) {
			self.logger.severe("ERROR: " + e.getMessage());
		}
	}

	private void trySaveCache() {
		try {
			fileResource.outCachedNodes.renameTo(new File(fileResource.outCachedNodes.getAbsolutePath()
							+ System.currentTimeMillis()));
			estimator.writeCachedNodes(fileResource.outCachedNodes);
		} catch (Exception e) {
			logger.severe("failed to store node cache");
		}

		try {
			fileResource.outCachedPaths.renameTo(new File(fileResource.outCachedPaths.getAbsolutePath()
							+ System.currentTimeMillis()));
			estimator.writeCachedPaths(fileResource.outCachedPaths);
		} catch (Exception e) {
			logger.severe("failed to store paths cache");
		}

		try {
			if (categoryIdToName != null) {
				FileOutputStream fos = new FileOutputStream(fileResource.outCategoryIdToName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(categoryIdToName);
				oos.close();
				fos.close();
				logger.info("stored category-id-to-name [" + categoryIdToName.size() + "] to ["
								+ fileResource.outCategoryIdToName.getAbsolutePath() + "]");

			}
		} catch (Exception e) {
			logger.severe("failed to store category-id-to-name");
		}

		try {
			if (categoryNameToId != null) {
				FileOutputStream fos = new FileOutputStream(fileResource.outCategoryNameToId);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(categoryNameToId);
				oos.close();
				fos.close();
				logger.info("stored category-name-to-id [" + categoryNameToId.size() + "] to ["
								+ fileResource.outCategoryNameToId.getAbsolutePath() + "]");

			}
		} catch (Exception e) {
			logger.severe("failed to store category-name-to-id");
		}
	}

	private void tryRestoreCache() {
		try {
			estimator.readCachedNodes(fileResource.outCachedNodes);
		} catch (ClassNotFoundException | IOException e) {
			logger.severe("failed to restore nodes cache " + e.getMessage());
		}

		try {
			estimator.readCachedPaths(fileResource.outCachedPaths);
		} catch (ClassNotFoundException | IOException e) {
			logger.severe("failed to restore paths cache " + e.getMessage());
		}
	}

	private ArrayList<Integer> nextChunk(ArrayList<Integer> categoryIds) {

		if (chunkStartIdx > categoryIds.size()) {
			return new ArrayList<Integer>();
		}

		int chunkEndIdx = ((chunkStartIdx + estimationArguments.nodesPerChunk) > categoryIds.size()) ? categoryIds
						.size() : chunkStartIdx + estimationArguments.nodesPerChunk;
		ArrayList<Integer> chunk = new ArrayList<Integer>(categoryIds.subList(chunkStartIdx, chunkEndIdx));
		chunkStartIdx += estimationArguments.nodesPerChunk;

		return chunk;
	}

	void writeToIndex(CategoryRelevanceDetails crd) throws IOException {

		// if (null == crd.topCategoryRelevances ||
		// crd.topCategoryRelevances.length <= 0) {
		// return;
		// }

		Document doc = new Document();

		if (null != crd.documenId) {
			doc.add(new StringField("documentID", Integer.toString(crd.documenId), Field.Store.YES));
		}

		if (null != crd.categoryName) {
			doc.add(new StringField("categoryName", crd.categoryName, Field.Store.YES));
		}

		if (null != crd.queryNumber) {
			doc.add(new StringField("queryNumber", Integer.toString(crd.queryNumber), Field.Store.YES));
		}

		if (null != crd.queryDescription) {
			doc.add(new StringField("queryDescription", crd.queryDescription, Field.Store.YES));
		}

		if (null != crd.query) {
			doc.add(new StringField("query", crd.query, Field.Store.YES));
		}

		if (null != crd.topCategoryRelevances) {
			for (CategoryRelevanceDetails.TopCategoryRelevance tcr : crd.topCategoryRelevances) {

				if (null != tcr.categoryRelevance && !Double.isNaN(tcr.categoryRelevance)) {
					// try {
					// ByteArrayOutputStream bos = new ByteArrayOutputStream();
					// ObjectOutputStream oos = new ObjectOutputStream(bos);
					// oos.writeObject(tcr);
					// doc.add(new StoredField("TopCategoryRelevance",
					// bos.toByteArray()));

					doc.add(new StringField("TopCategoryRelevance-hr", tcr.categoryRelevance.toString() + "|"
									+ tcr.categoryId.toString() + "|" + tcr.categoryName, Field.Store.YES));
					// doc.add(new StringField("TopCategoryRelevance-id",
					// tcr.categoryId.toString(), Field.Store.YES));
					// doc.add(new StringField("TopCategoryRelevance-name",
					// tcr.categoryName, Field.Store.YES));
					// } catch (IOException e) {
					// logger.severe("failed storing binary field [TopCategoryRelevance] to index");
					// }
				}
			}
		}

		doc.add(new StringField("categoryId", Integer.toString(crd.categoryId), Field.Store.YES));
		indexWriter.addDocument(doc);
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

	private void openInIndex() throws IOException {
		Directory directory = FSDirectory.open(fileResource.inLuceneIndexDirectory);
		indexReader = DirectoryReader.open(directory);
	}

	private void closeInIndex() throws IOException {
		try {
			indexReader.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "index reader closed erroneous", e);
		} catch (NullPointerException npe) {
			logger.log(Level.SEVERE, "index reader already closed");
		}
		indexReader = null;

	}

	void openOutIndex() throws IOException {
		try {
			Directory indexDirectory = FSDirectory.open(fileResource.outLuceneIndexDirectory);
			Analyzer analyzer = new EnglishAnalyzer();
			IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
			writerConfig.setOpenMode(OpenMode.CREATE);
			// writerConfig.setRAMBufferSizeMB(ramBufferSizeMB);
			indexWriter = new IndexWriter(indexDirectory, writerConfig);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "unable to open/create index at [" + fileResource.outLuceneIndexDirectory + "]", e);
			throw e;
		}
	}

	void closeOutIndex() throws IOException {
		try {
			indexWriter.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "index writer closed erroneous", e);
		} catch (NullPointerException npe) {
			logger.log(Level.SEVERE, "index writer already closed");
		}
		indexWriter = null;
	}

	private ArrayList<Integer> collectTopDocsCategories(ArrayList<String> queryStrings, int numTopDocumentsToConsider,
					String searchField,/*
										 * HashMap<Integer, ArrayList<Integer>>
										 * docCatgoryCollector, HashMap<Integer,
										 * String> categoryQueryStringCollector,
										 */int numTopDocs) throws ParseException, IOException {

		ArrayList<Integer> listOfCategoryIds = new ArrayList<>();
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryParser = new QueryParser(searchField, new EnglishAnalyzer());
		int totalMissedCategories = 0;
		int totalFoundCategories = 0;
		int totalDuplicateCollisions = 0;

		for (String queryString : queryStrings) {

			org.apache.lucene.search.Query query = queryParser.parse(queryString);
			TopDocs topDocs = indexSearcher.search(query, numTopDocs);

			int missedCategories = 0;
			int foundCategories = 0;
			int duplicateCollisions = 0;
			for (ScoreDoc sDoc : topDocs.scoreDocs) {
				org.apache.lucene.document.Document doc = indexSearcher.doc(sDoc.doc);
				IndexableField[] categories = doc.getFields("category");
				// collect all top doc categories
				for (IndexableField category : categories) {
					Integer categoryId = categoryNameToId.get(category.stringValue().replace(" ", "_"));
					if (categoryId == null) {
						missedCategories++;
					} else {

						if (!listOfCategoryIds.contains(categoryId)) {
							listOfCategoryIds.add(categoryId);
							foundCategories++;
						} else {
							duplicateCollisions++;
						}
					}
				}
				// docCatgoryCollector.put(sDoc.doc, listOfCategoryIds);
				// categoryQueryStringCollector.put(sDoc.doc, queryString);
			}
			logger.info("found [" + foundCategories + "] missed [" + missedCategories
							+ "] categories duplicate collisions [" + duplicateCollisions + "] out of ["
							+ (foundCategories + missedCategories + duplicateCollisions) + "] categories for query ["
							+ queryString + "] ");
			totalMissedCategories += missedCategories;
			totalFoundCategories += foundCategories;
			totalDuplicateCollisions += duplicateCollisions;
		}
		logger.info("totals: found [" + totalFoundCategories + "] missed [" + totalMissedCategories
						+ "] categories duplicate collisions [" + totalDuplicateCollisions + "] out of ["
						+ (totalFoundCategories + totalMissedCategories + totalDuplicateCollisions)
						+ "] categories for [" + queryStrings.size() + "] queries");
		return listOfCategoryIds;
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
		categoryNameToId = collector.getCategoryMap();
		String[] topCategoryLabels = { "Agriculture", "Architecture", "Arts", "Behavior", "Chronology", "Concepts",
						"Creativity", "Culture", /* "Disciplines", */"Education", "Environment", "Geography",
						"Government", "Health", "History", "Humanities", "Humans", "Industry", "Information",
						"Knowledge", "Language", "Law", "Life", "Mathematics", "Matter", "Medicine", "Mind", "Nature",
						"Objects", "People", "Politics", "Science", "Society", "Sports", "Structure", "Systems",
						"Technology", "Universe", "World" };

		// create inverted id lookup table
		if (categoryIdToName == null) {
			categoryIdToName = new HashMap<>();
			for (Map.Entry<String, Integer> entry : categoryNameToId.entrySet()) {
				categoryIdToName.put(entry.getValue(), entry.getKey());
			}
		}

		topCategoryIds = new HashMap<>();
		int idx = 0;
		topCategories = new int[topCategoryLabels.length];
		for (String topCategoryLabel : topCategoryLabels) {
			Integer id = categoryNameToId.get(topCategoryLabel);
			if (null == id) {
				logger.severe("category [" + topCategoryLabel + "] not found!");
				continue;
			}
			topCategoryIds.put(topCategoryLabel, id);
			topCategories[idx++] = id;
		}

		// try re-read estimator's cache
		estimator = new MainCategoryRelevanceEstimator(grph, topCategories);
		estimator.setKClosestNeighborsSubgraph(estimationArguments.numKClosestCategoryNeighborsToConsider);
	}

	/**
	 * calculate relevance distribution for each start node to top categories
	 * 
	 * @param startCategoryIds
	 *            see
	 *            {@link MainCategoryRelevanceEstimator#estimateRelevancesConcurrent(int[], int, int, int, boolean)}
	 * @param kShortestPaths
	 *            see
	 *            {@link MainCategoryRelevanceEstimator#estimateRelevancesConcurrent(int[], int, int, int, boolean)}
	 * @param distributeOverSiblingCategories
	 *            see
	 *            {@link MainCategoryRelevanceEstimator#estimateRelevancesConcurrent(int[], int, int, int, boolean)}
	 * @param categoryId2QeryString
	 *            query string that produced startCategoryIds
	 * @param documentId2CategoryId
	 *            document IDs that produced startCategoryIds
	 */
	private void evaluateKSortestPaths(ArrayList<Integer> startCategoryIds, int kShortestPaths,
					boolean distributeOverSiblingCategories) {

		MainCategoryRelevanceEstimator
						.setNumCategoriesToCalculateBundled(concurrentSettings.numCategoriesToCalculateBundled);
		// single node calculation of relevance
		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : estimator.estimateRelevancesConcurrent(
						startCategoryIds, kShortestPaths, concurrentSettings.totalThreads,
						distributeOverSiblingCategories).entrySet()) {

			HashMap<Integer, Double> relevances = entry.getValue();
			Integer startCategoryId = entry.getKey();

			CategoryRelevanceDetails category = new CategoryRelevanceDetails();
			category.categoryId = startCategoryId;
			category.categoryName = getCategoryName(category.categoryId);
			category.topCategoryRelevances = new TopCategoryRelevance[relevances.size()];

			// category relevances
			int idx = 0;
			for (HashMap.Entry<Integer, Double> relEntry : relevances.entrySet()) {
				CategoryRelevanceDetails.TopCategoryRelevance topCategory = new CategoryRelevanceDetails.TopCategoryRelevance();
				Integer topCategoryId = relEntry.getKey();
				topCategory.categoryId = topCategoryId;

				topCategory.categoryName = getCategoryName(topCategory.categoryId);
				topCategory.categoryRelevance = relEntry.getValue();
				category.topCategoryRelevances[idx++] = topCategory;

				// // if (!Double.isNaN(topCategory.categoryRelevance)) {
				// logger.info("[" + startCategoryId + "] estimates to [" +
				// topCategoryId + "] with ["
				// + topCategory.categoryRelevance + "]");
				// // }
			}

			try {
				// logger.info("write document [n=" + category.categoryName +
				// " id=" + category.categoryId
				// + "] results to index");
				writeToIndex(category);
			} catch (IOException e) {
				logger.severe("failed to write to index");
			}
		}
	}

	private String getCategoryName(Integer id) {
		return categoryIdToName.get(id);
	}
}
