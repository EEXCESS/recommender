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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.queryparser.classic.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.diversityasurement.evaluation.config.Settings;
import eu.eexcess.diversityasurement.iaselect.Query;
import eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceWikipediaDecomposer;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;


public class QueryExpansionEvaluation {

    private static final Logger LOGGER = Logger.getLogger(QueryExpansionEvaluation.class.getName());
    private static long totalTime = 0;
    private static long decomposeCounts = 0;
    private static PseudoRelevanceWikipediaDecomposer decomposer = newDecomposer();

    private QueryExpansionEvaluation() {
    }

    /**
     * expand queries, fetch all categories in every query's top documents and
     * store to file
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        int[] termExpansions = { 5, 10, 15, 20 };
        Set<SecureUserProfile> expandedQueries = getExpandedQueries(termExpansions);

        QueryCategoryKShortestPathsEvaluation.openInIndex();
        QueryCategoryKShortestPathsEvaluation.init();
        QueryCategoryKShortestPathsEvaluation.restoreCache();

        Set<Integer> newExpandedcategories = new HashSet<>();

        // get all cat. for expanded queries
        getAllCategories(expandedQueries, newExpandedcategories, Settings.QueryExpansionEvaluation.NUM_TOP_DOCS_TO_CONSIDER, false);
        // get all cat. for expanded queries wit removed brackets
        getAllCategories(expandedQueries, newExpandedcategories, Settings.QueryExpansionEvaluation.NUM_TOP_DOCS_TO_CONSIDER, true);

        storeCategories(newExpandedcategories);
        QueryCategoryKShortestPathsEvaluation.closeInIndex();
    }

    private static void storeCategories(Set<Integer> categories) throws /*IllegalStateException,*/ IOException {

        Map<String, Integer> mapping = new LinkedHashMap<>();
        for (Integer id : categories) {
            String name = QueryCategoryKShortestPathsEvaluation.categoryIdToName.get(id);
            if (null == name) {
                throw new IllegalStateException("failed to fetch name of category [" + id + "]");
            }
            mapping.put(name, id);
        }

        LOGGER.info("storing [" + categories.size() + "] categories to file ["
                + Settings.QueryExpansionEvaluation.IOFiles.outExpandedCategoryIDs.getAbsoluteFile() + "]");
        FileWriter writer = new FileWriter(Settings.QueryExpansionEvaluation.IOFiles.outExpandedCategoryIDs.getAbsoluteFile());
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        gson.toJson(mapping, writer);
        writer.close();
    }

    private static Set<SecureUserProfile> getExpandedQueries(int[] termExpansions) throws IOException {
        Set<SecureUserProfile> expandedQueries = new LinkedHashSet<>();
        for (String q : QueryCategoryKShortestPathsEvaluation.getQueries()) {
            LOGGER.info("expanding query [" + q + "]");
            Query iasQuery = new Query(q);

            for (int k : termExpansions) {
                expandedQueries.add(expandQuery(iasQuery, k));
            }
        }
        return expandedQueries;
    }

    /**
     * get all categories of all queries generated out of
     * {@link SecureUserProfile} that appear in top documents
     * 
     * @param queries
     */
    private static void getAllCategories(Set<SecureUserProfile> queries, Set<Integer> categoryCollector, int numTopDocumentsToConsider, boolean doRemoveBracket)
            throws IOException, ParseException {
        ArrayList<String> queryStrings = new ArrayList<>(queries.size());

        for (SecureUserProfile q : queries) {
            String queryString = toQuery(q);
            if (doRemoveBracket) {
                queryString = queryString.replace(")", " ").replace("(", " ");
            }

            LOGGER.info("query generator: [" + queryString + "] doReplaceBracket=[" + doRemoveBracket + "]");
            queryStrings.add(queryString);
        }

        for (Integer categoryID : QueryCategoryKShortestPathsEvaluation.collectTopDocsCategories(queryStrings, numTopDocumentsToConsider)) {
            if (!categoryCollector.contains(categoryID)) {
                categoryCollector.add(categoryID);
            }
        }
    }

    /**
     * generate lucene query string out of user profile distinguishing expanded
     * and original keywords
     * 
     * @param userProfile
     * @return
     */
    static String toQuery(SecureUserProfile userProfile) {
        StringBuilder result = new StringBuilder();
        boolean expansion = false;
        for (ContextKeyword key : userProfile.getContextKeywords()) {

            if (key.getExpansion() != null && (key.getExpansion() == ExpansionType.PSEUDORELEVANCEWP || key.getExpansion() == ExpansionType.SERENDIPITY)) {
                if (!expansion) {
                    expansion = true;
                    if (result.length() > 0) {
                        if (key.getExpansion() == ExpansionType.PSEUDORELEVANCEWP)
                            result.append(" OR (\"" + key.getText() + "\"");
                        else
                            result.append(" AND (\"" + key.getText() + "\"");
                    } else
                        result.append("(\"" + key.getText() + "\"");
                } else {
                    result.append(" OR \"" + key.getText() + "\"");
                }
            } else {
                if (expansion) {
                    result.append(") OR \"" + key.getText() + "\"");
                    expansion = false;
                } else if (result.length() > 0)
                    result.append(" \"" + key.getText() + "\"");
                else
                    result.append("\"" + key.getText() + "\"");
            }
        }
        if (expansion)
            result.append(")");

        return result.toString();
    }

    /**
     * expands a query
     * 
     * @param q
     * @param maxTermsToExpand
     * @return
     */
    static SecureUserProfile expandQuery(Query q, int maxTermsToExpand) {

        SecureUserProfile expandedSecureProfile = null;
        SecureUserProfile userProfile = newSecureUserProfile(q);
        decomposer.setMaxNumTermsToExpand(maxTermsToExpand);

        long timestamp = System.currentTimeMillis();
        expandedSecureProfile = decomposer.decompose(userProfile);
        double localDuration = System.currentTimeMillis() - timestamp;

        totalTime += localDuration;
        decomposeCounts++;
        double mean = (double) totalTime / (double) decomposeCounts;

        if (decomposeCounts % 20 == 0) {
            LOGGER.info("decomposer: mean [" + mean + "] total [" + decomposeCounts + "] calls [" + totalTime + "]ms numTerms[" + maxTermsToExpand
                    + "] last call [" + localDuration + "]");
        }

        return expandedSecureProfile;
    }

    /**
     * Splits the query string into terms and creates a new
     * {@link SecureUserProfile}.
     * 
     * @param q
     * @return
     */
    private static SecureUserProfile newSecureUserProfile(Query q) {
        ArrayList<ContextKeyword> keywords = new ArrayList<>();
        for (String term : q.query.split("\\s+")) {
            keywords.add(new ContextKeyword(term, ExpansionType.NONE));
        }
        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.setContextKeywords(keywords);
        return userProfile;
    }

    private static PseudoRelevanceWikipediaDecomposer newDecomposer() {
        PseudoRelevanceWikipediaDecomposer sUPDecomposer = null;
        try {
            sUPDecomposer = new PseudoRelevanceWikipediaDecomposer();
            FederatedRecommenderConfiguration fedRecConfig = new FederatedRecommenderConfiguration();
            fedRecConfig.setWikipediaIndexDir(Settings.RelevanceEvaluation.IOFiles.inLuceneIndexParentDirectory.getAbsolutePath());
            sUPDecomposer.setConfiguration(fedRecConfig);
        } catch (IOException | FederatedRecommenderException e) {
            LOGGER.log(Level.SEVERE, "Wikipedia index directory could be wrong or not readable: "
                    + Settings.RelevanceEvaluation.IOFiles.inLuceneIndexParentDirectory.getAbsolutePath(), e);
        }
        return sUPDecomposer;
    }
}
