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
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import eu.eexcess.diversityasurement.evaluation.config.Settings;
import eu.eexcess.diversityasurement.wikipedia.GrphTupleCollector;
import eu.eexcess.diversityasurement.wikipedia.MainCategoryRelevanceEstimator;
import eu.eexcess.diversityasurement.wikipedia.RDFCategoryExtractor;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance.CategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Queries;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.Query;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.QueryJsonIO;

import grph.Grph;

/**
 * evaluate category relevance of sub categories to top categories (wikipedia:
 * main topic classification)
 * 
 * @author Raoul Rubien
 *
 */
public class QueryCategoryKShortestPathsEvaluation {

    public static class ConcurrentSettigns {
        public int totalThreads = 1;
        // number of categories a thread calculates sequentially
        public int numCategoriesToCalculateBundled = 1;
    }

    private static final Logger LOGGER = Logger.getLogger(QueryCategoryKShortestPathsEvaluation.class.getName());
    static Grph grph;
    // CategoryRelevanceDetails queriesRelevances = new
    // CategoryRelevanceDetails();
    static MainCategoryRelevanceEstimator estimator;

    private static int[] topCategories;
    private static Map<String, Integer> categoryNameToId;
    static Map<Integer, String> categoryIdToName;
    private static Map<String, Integer> topCategoryIds;
    // public IOFIles fileResource = new IOFIles();
    private static ConcurrentSettigns concurrentSettings = new ConcurrentSettigns();
    // private static EstimationArguments estimationArguments = new
    // EstimationArguments();
    static IndexReader indexReader;
    private static IndexWriter indexWriter;

    private static Map<Integer, Map<Integer, Double>> estimatedRelevances;
    private static int chunkStartIdx = 0;

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

        try {
            openInIndex();
            openOutIndex();
            init();
            tryRestoreCache();
            ArrayList<String> queries = getQueries();

            // collect categories from index
            ArrayList<Integer> categoryIds = collectTopDocsCategories(queries, Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider);
            appendExternallyDefinedCategories(categoryIds);

            // evaluate and store to result-index in chunks:
            int chunkCount = 0;
            ArrayList<Integer> chunk = nextChunk(categoryIds);
            while (chunk.size() > 0) {
                LOGGER.info("chunk count [" + chunkCount++ + "] " + chunk);
                LOGGER.info(chunk.toString());

                evaluateKSortestPaths(chunk, Settings.RelevanceEvaluation.EstimationArguments.kShortestPaths,
                        Settings.RelevanceEvaluation.EstimationArguments.isDistributionStartedAtSiblings);
                indexWriter.commit();
                trySaveCache();
                chunk = nextChunk(categoryIds);
            }

            closeInIndex();
            closeOutIndex();
            trySaveCache();

            LOGGER.info("total duration: [" + (System.currentTimeMillis() - startTimestamp) + "]ms");

        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, "ERROR", e);
        }
    }

    private static void trySaveCache() {
        try {
            Settings.RelevanceEvaluation.IOFiles.outCachedNodes.renameTo(new File(Settings.RelevanceEvaluation.IOFiles.outCachedNodes.getAbsolutePath()
                    + System.currentTimeMillis()));
            estimator.writeCachedNodes(Settings.RelevanceEvaluation.IOFiles.outCachedNodes);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to store node cache", e);
        }

        try {
            Settings.RelevanceEvaluation.IOFiles.outCachedPaths.renameTo(new File(Settings.RelevanceEvaluation.IOFiles.outCachedPaths.getAbsolutePath()
                    + System.currentTimeMillis()));
            estimator.writeCachedPaths(Settings.RelevanceEvaluation.IOFiles.outCachedPaths);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to store paths cache", e);
        }

        try {
            if (categoryIdToName != null) {
                Settings.RelevanceEvaluation.IOFiles.outCategoryIdToName.delete();
                FileOutputStream fos = new FileOutputStream(Settings.RelevanceEvaluation.IOFiles.outCategoryIdToName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(categoryIdToName);
                oos.close();
                fos.close();
                LOGGER.info("stored category-id-to-name [" + categoryIdToName.size() + "] to ["
                        + Settings.RelevanceEvaluation.IOFiles.outCategoryIdToName.getAbsolutePath() + "]");

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to store category-id-to-name", e);
        }

        try {
            if (categoryNameToId != null) {
                Settings.RelevanceEvaluation.IOFiles.outCategoryNameToId.delete();
                FileOutputStream fos = new FileOutputStream(Settings.RelevanceEvaluation.IOFiles.outCategoryNameToId);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(categoryNameToId);
                oos.close();
                fos.close();
                LOGGER.info("stored category-name-to-id [" + categoryNameToId.size() + "] to ["
                        + Settings.RelevanceEvaluation.IOFiles.outCategoryNameToId.getAbsolutePath() + "]");

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to store category-name-to-id", e);
        }

        try {
            if (estimatedRelevances != null) {
                Settings.RelevanceEvaluation.IOFiles.outRelevances.delete();
                FileOutputStream fos = new FileOutputStream(Settings.RelevanceEvaluation.IOFiles.outRelevances);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(estimatedRelevances);
                oos.close();
                fos.close();
                LOGGER.info("stored estimated relevances [" + estimatedRelevances.size() + "] to ["
                        + Settings.RelevanceEvaluation.IOFiles.outRelevances.getAbsolutePath() + "]");

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to store estimated relevances", e);
        }
    }

    /**
     * see {@link #restoreCache()}
     */
    private static void tryRestoreCache() {
        try {
            restoreCache();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to restore cache", e);
        }
    }

    /**
     * restore cached nodes and paths
     * 
     * @throws Exception
     */
    static void restoreCache() throws Exception {
        try {
            estimator.readCachedNodes(Settings.RelevanceEvaluation.IOFiles.outCachedNodes);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.log(Level.SEVERE, "failed to restore nodes cache ", e);
            throw e;
        }

        try {
            estimator.readCachedPaths(Settings.RelevanceEvaluation.IOFiles.outCachedPaths);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.log(Level.SEVERE, "failed to restore paths cache ", e);
            throw e;
        }
    }

    private static ArrayList<Integer> nextChunk(ArrayList<Integer> categoryIds) {

        if (chunkStartIdx > categoryIds.size()) {
            return new ArrayList<Integer>();
        }

        int chunkEndIdx = ((chunkStartIdx + Settings.RelevanceEvaluation.EstimationArguments.nodesPerChunk) > categoryIds.size()) ? categoryIds.size()
                : chunkStartIdx + Settings.RelevanceEvaluation.EstimationArguments.nodesPerChunk;
        ArrayList<Integer> chunk = new ArrayList<Integer>(categoryIds.subList(chunkStartIdx, chunkEndIdx));
        chunkStartIdx += Settings.RelevanceEvaluation.EstimationArguments.nodesPerChunk;

        return chunk;
    }

    static void writeToIndex(CategoryToTopCategoryRelevance crd) throws IOException {

        // if (null == crd.topCategoryRelevances ||
        // crd.topCategoryRelevances.length <= 0) {
        // return;
        // }

        Document doc = new Document();

        // if (null != crd.documenId) {
        // doc.add(new StringField("documentID",
        // Integer.toString(crd.documenId), Field.Store.YES));
        // }

        if (null != crd.categoryName) {
            doc.add(new TextField("categoryName", crd.categoryName, Field.Store.YES));
        }

        // if (null != crd.queryNumber) {
        // doc.add(new StringField("queryNumber",
        // Integer.toString(crd.queryNumber), Field.Store.YES));
        // }

        if (null != crd.queryDescription) {
            doc.add(new TextField("queryDescription", crd.queryDescription, Field.Store.YES));
        }

        if (null != crd.query) {
            doc.add(new TextField("query", crd.query, Field.Store.YES));
        }

        if (null != crd.topCategoryRelevances) {
            for (CategoryToTopCategoryRelevance.CategoryRelevance tcr : crd.topCategoryRelevances) {

                if (null != tcr.categoryRelevance && !Double.isNaN(tcr.categoryRelevance)) {
                    // try {
                    // ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    // ObjectOutputStream oos = new ObjectOutputStream(bos);
                    // oos.writeObject(tcr);
                    // doc.add(new StoredField("TopCategoryRelevance",
                    // bos.toByteArray()));

                    doc.add(new TextField("TopCategoryRelevance-hr", tcr.categoryRelevance.toString() + "|" + tcr.categoryId.toString() + "|"
                            + tcr.categoryName, Field.Store.YES));
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

        doc.add(new TextField("categoryId", Integer.toString(crd.categoryId), Field.Store.YES));
        indexWriter.addDocument(doc);
    }

    /**
     * read queries from file
     */
    public static ArrayList<String> getQueries() throws IOException {
        Queries queries = QueryJsonIO.readQueries(new File(Settings.Queries.PATH));

        ArrayList<String> queryList = new ArrayList<>(queries.queries.length);
        for (Query query : queries.queries) {
            queryList.add(query.query);
        }
        return queryList;
    }

    static void openInIndex() throws IOException {
        Directory directory = FSDirectory.open(Settings.RelevanceEvaluation.IOFiles.inLuceneIndexDirectory);
        indexReader = DirectoryReader.open(directory);
    }

    static void closeInIndex() throws IOException {
        try {
            indexReader.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "index reader closed erroneous", e);
        } catch (NullPointerException npe) {
            LOGGER.log(Level.SEVERE, "index reader already closed", npe);
        }
        indexReader = null;

    }

    static void openOutIndex() throws IOException {
        try {
            Directory indexDirectory = FSDirectory.open(Settings.RelevanceEvaluation.IOFiles.outLuceneIndexDirectory);
            Analyzer analyzer = new EnglishAnalyzer();
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
            writerConfig.setOpenMode(OpenMode.CREATE);
            // writerConfig.setRAMBufferSizeMB(ramBufferSizeMB);
            indexWriter = new IndexWriter(indexDirectory, writerConfig);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to open/create index at [" + Settings.RelevanceEvaluation.IOFiles.outLuceneIndexDirectory + "]", e);
            throw e;
        }
    }

    static void closeOutIndex() throws IOException {
        try {
            indexWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "index writer closed erroneous", e);
        } catch (NullPointerException npe) {
            LOGGER.log(Level.SEVERE, "index writer already closed", npe);
        }
        indexWriter = null;
    }

    /**
     * append additional externally defined categories
     */
    private static void appendExternallyDefinedCategories(ArrayList<Integer> categoryCollector) {

        int numAdded = 0;
        Set<Integer> externalDefined = getExternallyDefinedCategories();
        for (Integer id : externalDefined) {
            if (id == null) {
                LOGGER.severe("ignoring category with id=NULL while processing externally defined catgories");
            } else {
                if (!categoryCollector.contains(id)) {
                    categoryCollector.add(id);
                    numAdded++;
                }
            }
        }

        LOGGER.info("added [" + numAdded + "] out of [" + externalDefined.size() + "] new external defined categories");
    }

    /**
     * Reads externally defined Map<String, Integer> of additional categories to
     * be calculated.
     * 
     * @return set of integer or an empty set if any error occurs
     */
    private static Set<Integer> getExternallyDefinedCategories() {
        try {
            JsonReader reader = new JsonReader(new FileReader(Settings.RelevanceEvaluation.IOFiles.additionalDefinedCategoryIDs));
            Type type = new TypeToken<Map<String, Integer>>() {
            }.getType();
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            Map<String, Integer> inQueries = gson.fromJson(reader, type);
            reader.close();
            return new LinkedHashSet<>(inQueries.values());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "failed reading additional defined categories from [" + Settings.RelevanceEvaluation.IOFiles.additionalDefinedCategoryIDs.getAbsolutePath()
                            + "]", e);
        }
        return new LinkedHashSet<>();
    }

    /**
     * collect all category IDs involved on documents for all given queries
     * 
     * @param queryStrings
     * @param numTopDocumentsToConsider
     * @return
     * @throws ParseException
     * @throws IOException
     */
    static ArrayList<Integer> collectTopDocsCategories(ArrayList<String> queryStrings, int numTopDocumentsToConsider) throws ParseException, IOException {

        ArrayList<Integer> listOfCategoryIds = new ArrayList<>();
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser queryParser = new QueryParser(Settings.RelevanceEvaluation.Lucene.SEARCH_FIELD_SECTIONTEXT, new EnglishAnalyzer());
        int totalMissedCategories = 0;
        int totalFoundCategories = 0;
        int totalDuplicateCollisions = 0;

        for (String queryString : queryStrings) {

            org.apache.lucene.search.Query query = queryParser.parse(queryString);
            TopDocs topDocs = indexSearcher.search(query, numTopDocumentsToConsider);

            int missedCategories = 0;
            int foundCategories = 0;
            int duplicateCollisions = 0;
            for (ScoreDoc sDoc : topDocs.scoreDocs) {
                org.apache.lucene.document.Document doc = indexSearcher.doc(sDoc.doc);
                IndexableField[] categories = doc.getFields(Settings.RelevanceEvaluation.Lucene.SEARCH_FIELD_CATEGORY);
                // collect all top doc categories
                for (IndexableField category : categories) {
                    Integer categoryId = categoryNameToId.get(replaceCategoryWhitespaceChars(category.stringValue()));
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
            }
            LOGGER.info("found [" + foundCategories + "] missed [" + missedCategories + "] categories duplicate collisions [" + duplicateCollisions
                    + "] out of [" + (foundCategories + missedCategories + duplicateCollisions) + "] categories for query [" + queryString + "] ");
            totalMissedCategories += missedCategories;
            totalFoundCategories += foundCategories;
            totalDuplicateCollisions += duplicateCollisions;
        }
        LOGGER.info("totals: found [" + totalFoundCategories + "] missed [" + totalMissedCategories + "] categories duplicate collisions ["
                + totalDuplicateCollisions + "] out of [" + (totalFoundCategories + totalMissedCategories + totalDuplicateCollisions) + "] categories for ["
                + queryStrings.size() + "] queries");
        return listOfCategoryIds;
    }

    public static String replaceCategoryWhitespaceChars(String stringValue) {
        return stringValue.replace(" ", "_");
    }

    static void init() throws IOException {
        // build graph from rdf file
        LOGGER.info("inflating tree from [" + eu.eexcess.diversityasurement.wikipedia.config.Settings.RDFCategories.PATH + "]");
        GrphTupleCollector collector = new GrphTupleCollector(350000);
        RDFCategoryExtractor extractor = new RDFCategoryExtractor(new File(eu.eexcess.diversityasurement.wikipedia.config.Settings.RDFCategories.PATH),
                collector);
        extractor.extract();
        grph = collector.getGraph();
        LOGGER.info(extractor.getStatistics().toString());
        LOGGER.info(collector.getStatistics().toString());

        // get category id's of top categories
        categoryNameToId = collector.getCategoryMap();
        String[] topCategoryLabels = { "Agriculture", "Architecture", "Arts", "Behavior", "Chronology", "Concepts", "Creativity", "Culture", /*
                                                                                                                                              * "Disciplines"
                                                                                                                                              * ,
                                                                                                                                              */"Education",
                "Environment", "Geography", "Government", "Health", "History", "Humanities", "Humans", "Industry", "Information", "Knowledge", "Language",
                "Law", "Life", "Mathematics", "Matter", "Medicine", "Mind", "Nature", "Objects", "People", "Politics", "Science", "Society", "Sports",
                "Structure", "Systems", "Technology", "Universe", "World" };

        // create inverted id lookup table
        if (categoryIdToName == null) {
            categoryIdToName = new HashMap<>();
            for (Map.Entry<String, Integer> entry : categoryNameToId.entrySet()) {
                categoryIdToName.put(entry.getValue(), entry.getKey());
            }
        }

        // construct top category string-id map
        topCategoryIds = new HashMap<>();
        int idx = 0;
        topCategories = new int[topCategoryLabels.length];
        for (String topCategoryLabel : topCategoryLabels) {
            Integer id = categoryNameToId.get(topCategoryLabel);
            if (null == id) {
                LOGGER.severe("category [" + topCategoryLabel + "] not found!");
                continue;
            }
            topCategoryIds.put(topCategoryLabel, id);
            topCategories[idx++] = id;
        }

        // try re-read estimator's cache
        estimator = new MainCategoryRelevanceEstimator(grph, topCategories);
        estimator.setKClosestNeighborsSubgraph(Settings.RelevanceEvaluation.EstimationArguments.numKClosestCategoryNeighborsToConsider);

        estimatedRelevances = new HashMap<>();
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
    private static void evaluateKSortestPaths(ArrayList<Integer> startCategoryIds, int kShortestPaths, boolean distributeOverSiblingCategories) {

        MainCategoryRelevanceEstimator.setNumCategoriesToCalculateBundled(concurrentSettings.numCategoriesToCalculateBundled);
        // single node calculation of relevance
        for (Map.Entry<Integer, Map<Integer, Double>> entry : estimator.estimateRelevancesConcurrent(startCategoryIds, kShortestPaths,
                concurrentSettings.totalThreads, distributeOverSiblingCategories).entrySet()) {

            Integer startCategoryId = entry.getKey();
            Map<Integer, Double> relevances = entry.getValue();

            if (estimatedRelevances.containsKey(startCategoryId)) {
                LOGGER.severe("failed to enrich relevances: found result for duplicate category id [id=" + startCategoryId + " n="
                        + getCategoryName(startCategoryId) + "]");
            } else {
                estimatedRelevances.put(startCategoryId, relevances);
            }

            CategoryToTopCategoryRelevance category = new CategoryToTopCategoryRelevance();
            category.categoryId = startCategoryId;
            category.categoryName = getCategoryName(category.categoryId);
            category.topCategoryRelevances = new CategoryRelevance[relevances.size()];

            // category relevances
            int idx = 0;
            for (HashMap.Entry<Integer, Double> relEntry : relevances.entrySet()) {
                CategoryToTopCategoryRelevance.CategoryRelevance topCategory = new CategoryToTopCategoryRelevance.CategoryRelevance();
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
                LOGGER.log(Level.SEVERE, "failed to write to index", e);
            }
        }
    }

    private static String getCategoryName(Integer id) {
        return categoryIdToName.get(id);
    }
}
