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
package eu.eexcess.federatedrecommender.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SerializationUtils;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeList;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.dataformats.D3GraphDocument;
import eu.eexcess.federatedrecommender.dbpedia.DBPediaGraphInterface;
import eu.eexcess.federatedrecommender.dbpedia.DBPediaGraphJGraph;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaSolrIndex;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationManager;
import eu.eexcess.federatedrecommender.evaluation.picker.BlockPicker;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * Core for the Evaluation (keeping evaluation details out of the main core)
 * 
 * @author hziak
 *
 */
public class FederatedRecommenderEvaluationCore {
    private EvaluationManager evalManager = null;
    private final FederatedRecommenderConfiguration federatedRecommenderConfiguration;
    private final FederatedRecommenderCore fRCore;
    private final ExecutorService threadPool;

    private static final Logger LOGGER = Logger.getLogger(FederatedRecommenderEvaluationCore.class.getName());

    public FederatedRecommenderEvaluationCore(FederatedRecommenderConfiguration federatedRecConfiguration) throws FederatedRecommenderException, FileNotFoundException {
        this.federatedRecommenderConfiguration = federatedRecConfiguration;
        try {
            this.fRCore = FederatedRecommenderCore.getInstance(federatedRecommenderConfiguration);
        } catch (FederatedRecommenderException e1) {
            LOGGER.log(Level.SEVERE, "Could not get FederatedRecommenderCore");
            throw e1;
        }

        this.evalManager = new EvaluationManager(federatedRecConfiguration.getEvaluationQueriesFile());

        threadPool = Executors.newFixedThreadPool(30);
    }

    // Begin Evaluation
    /**
     * decides which source selection, decomposer and picker to take bases on
     * the userprofile
     * 
     * @param userProfile
     * @return
     */
    public EvaluationResultList getEvaluationResults(SecureUserProfileEvaluation userProfile) {
        SecureUserProfileEvaluation userProfileEvaluation = null;
        String[] sourceSelectionModel = { "Model" };// TODO: should not be
                                                    // "model"
        if (userProfile.getSourceSelect() != null)

            switch (userProfile.getSourceSelect()) {
            case "langModel":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.sourceSelection(userProfile, new ArrayList<String>(Arrays.asList(sourceSelectionModel)));
                break;
            case "wordnet":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.sourceSelection(userProfile, new ArrayList<String>(Arrays.asList(sourceSelectionModel)));
                break;
            case "wikipedia":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.sourceSelection(userProfile, new ArrayList<String>(Arrays.asList(sourceSelectionModel)));
                break;
            default:
                LOGGER.log(Level.WARNING, "Source selection unknown or not defined, not using any source selection at all");
                userProfileEvaluation = userProfile;
                break;
            }
        if (userProfileEvaluation == null) {
            userProfileEvaluation = userProfile;

        }

        if (userProfile.getDecomposer() != null)
            switch (userProfile.getDecomposer()) {
            case "wikipedia":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.addQueryExpansionTerms(userProfileEvaluation,
                        "eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceWikipediaDecomposer");
                break;
            case "source":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.addQueryExpansionTerms(userProfileEvaluation,
                        "eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceSourcesDecomposer");

                break;
            case "dbpediagraph":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.addQueryExpansionTerms(userProfileEvaluation,
                        "eu.eexcess.federatedrecommender.decomposer.DBPediaDecomposer");
                break;
            case "serendipity":
                userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.addQueryExpansionTerms(userProfileEvaluation,
                        "eu.eexcess.federatedrecommender.decomposer.SerendiptiyDecomposer");
                break;
            default:
                LOGGER.log(Level.WARNING, "Decomposer unknown or not defined, not using any decomposer at all");
                userProfileEvaluation = userProfile;
                break;
            }
        if (userProfileEvaluation == null) {
            userProfileEvaluation = userProfile;
            userProfileEvaluation.setNumResults(10);
            if (userProfile.getNumResults() != null)
                userProfileEvaluation.setNumResults(userProfile.getNumResults());
        } else {
            userProfileEvaluation.setNumResults(10);
            if (userProfile.getNumResults() != null)
                userProfileEvaluation.setNumResults(userProfile.getNumResults());
        }

        EvaluationResultList resultList = null;
        //
        // logger.log(Level.SEVERE, "Picker class not found", new Exception());
        // return null;
        // }
        if (userProfile.getPicker() == null)
            userProfile.setPicker("occurenceprobability");
        try {
            switch (userProfile.getPicker()) {

            case "simdiversification":

                resultList = new EvaluationResultList(
                        fRCore.getAndAggregateResults(userProfileEvaluation, "eu.eexcess.federatedrecommender.picker.SimilarityDiversificationPicker"));
                break;
            case "FiFoPicker":
                resultList = new EvaluationResultList(fRCore.getAndAggregateResults(userProfileEvaluation, "eu.eexcess.federatedrecommender.picker.FiFoPicker"));
                break;
            case "occurenceprobability":
            default:
                resultList = new EvaluationResultList(fRCore.getAndAggregateResults(userProfileEvaluation, "eu.eexcess.federatedrecommender.picker.OccurrenceProbabilityPicker"));
            }
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.SEVERE, "Could get or aggregate results!", e);
        }
        resultList.provider = userProfile.getDecomposer() + " partners:" + userProfile.getPicker() + " expansion source partners:" + userProfile.getQueryExpansionSourcePartner();
        return resultList;
    }

    /**
     * calls the eval manager to write the result file for the specified user
     * 
     * @param id
     * @param result
     * @return
     */
    public boolean logEvaluationResult(Integer id, EvaluationResultLists result) {
        boolean noQueryLeft = evalManager.addQueryResult(id, result);
        if (noQueryLeft)
            evalManager.writeUserResultToFile(id);
        return noQueryLeft;
    }

    /**
     * returns the next result from the evaluation manager and checks if the
     * manager chache is warmed
     * 
     * @param uID
     * @return
     * @throws IOException
     */
    public EvaluationResultLists getExpansionEvaluation(Integer uID) throws IOException {
        if (!evalManager.isCacheWarmed()) {
            for (SecureUserProfileEvaluation query : evalManager.getAllQueries()) {
                LOGGER.log(Level.INFO, "Warming evalualtion query cache");
                evalManager.addResultToQueryCache(query, getExpansionEvaluationResult(-1));
            }
        }

        return evalManager.getNextResultsForQuery(uID);

    }

    /**
     * returns the next result from the evaluation manager and checks if the
     * manager chache is warmed
     * 
     * @param uID
     * @return
     * @throws IOException
     */
    public EvaluationResultLists getBlockEvaluation(Integer uID) throws IOException {
        if (!evalManager.isCacheWarmed()) {
            for (SecureUserProfileEvaluation query : evalManager.getAllQueries()) {
                LOGGER.log(Level.INFO, "Warming evalualtion query cache");
                evalManager.addResultToQueryCache(query, getBlockEvaluationResultFromQManager(-1));
            }
        }

        return evalManager.getNextResultsForQuery(uID);

    }

    /**
     * main function for the block evaluation
     * 
     * @param id
     * @return
     */
    private EvaluationResultLists getBlockEvaluationResultFromQManager(Integer id) {

        final SecureUserProfileEvaluation evalProfil = evalManager.getNextQuery(id);
        return getblockResult(evalProfil);
    }

    public EvaluationResultLists getblockResult(final SecureUserProfileEvaluation evalProfil) {
        final EvaluationResultLists results = new EvaluationResultLists();
        final EvaluationResultLists blockTmpResults = new EvaluationResultLists();
        // EvaluationQuery query = evalManager.getNextQuery(id);
        // evalProfil.contextKeywords.add(new ContextKeyword(query.query));
        // //BEGIN Partner Selection
        // ArrayList<PartnerBadge> blockEvalPartners= new
        // ArrayList<PartnerBadge>();
        // for (PartnerBadge partner :
        // fRCore.getPartnerRegister().getPartners()) {
        // if(partner.systemId.contains("ZBW"))
        // blockEvalPartners.add(partner);
        // }
        // ArrayList<PartnerBadge> queryPartner= new ArrayList<PartnerBadge>();
        // for (PartnerBadge partner :
        // fRCore.getPartnerRegister().getPartners()) {
        // if(partner.systemId.contains("Mendeley"))
        // queryPartner.add(partner);
        // }
        // //END Partner Selection

        results.query = evalProfil.getContextKeywordConcatenation();
        results.queryDescription = evalProfil.getDescription();
        evalProfil.setPicker("FiFoPicker");

        final SecureUserProfileEvaluation diversityProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);

        // diversityProfile.partnerList=queryPartner;

        Future<Void> diversityFuture = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                diversityProfile.setDecomposer("wikipedia");
                EvaluationResultList evaluationResults = getEvaluationResults(diversityProfile);
                evaluationResults.provider = "Diversity";
                blockTmpResults.results.add(evaluationResults);
                return null;
            }
        });
        final SecureUserProfileEvaluation unmodifiedProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);

        Future<Void> unmodifiedFutur = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                unmodifiedProfile.setDecomposer("none");
                EvaluationResultList eRL = getEvaluationResults(unmodifiedProfile);
                eRL.provider = "Basic";
                blockTmpResults.results.add(eRL);
                results.results.add(eRL); // ADDING IT ALLREADY IN THE FINAL
                                          // RESULT LIST SINCE IT'S THE SECOND
                                          // RESULT LIST TO PRESENT
                return null;
            }
        });

        final SecureUserProfileEvaluation serendipityProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);

        Future<Void> serendipityFutur = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                serendipityProfile.setDecomposer("serendipity");
                EvaluationResultList evaluationResults = getEvaluationResults(serendipityProfile);
                evaluationResults.provider = "Serendipity";
                blockTmpResults.results.add(evaluationResults);
                return null;
            }
        });
        try {
            unmodifiedFutur.get();
            diversityFuture.get();
            serendipityFutur.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.WARNING, "Some thread had an error ", e);
        }

        BlockPicker bP = new BlockPicker();
        try {
            results.results.add(bP.pickBlockResults(blockTmpResults, evalProfil.getNumResults()));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }

        return results;
    }

    /**
     * main function for the decomposer evaluation
     * 
     * @param id
     * @return
     */
    private EvaluationResultLists getExpansionEvaluationResult(Integer id) {
        final EvaluationResultLists results = new EvaluationResultLists();
        final SecureUserProfileEvaluation evalProfil = evalManager.getNextQuery(id);

        ArrayList<PartnerBadge> sourceExpansionPartners = new ArrayList<PartnerBadge>();
        for (PartnerBadge partner : fRCore.getPartnerRegister().getPartners()) {
            if (partner.getSystemId().contains("ZBW"))
                sourceExpansionPartners.add(partner);
        }
        ArrayList<PartnerBadge> queryPartner = new ArrayList<PartnerBadge>();
        for (PartnerBadge partner : fRCore.getPartnerRegister().getPartners()) {
            if (partner.getSystemId().contains("Mendeley"))
                queryPartner.add(partner);
        }

        results.query = evalProfil.getContextKeywordConcatenation(); // TODO!
        results.queryDescription = evalProfil.getDescription();
        evalProfil.setPicker("FiFoPicker");

        final SecureUserProfileEvaluation wikipediaProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
        wikipediaProfile.setPartnerList(queryPartner);
        Future<Void> wikipedia = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                wikipediaProfile.setDecomposer("wikipedia");
                results.results.add((EvaluationResultList) getEvaluationResults(wikipediaProfile));
                return null;
            }
        });
        final SecureUserProfileEvaluation noneProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
        // noneProfile.partnerList=queryPartner;
        Future<Void> none = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                noneProfile.setDecomposer("");
                results.results.add((EvaluationResultList) getEvaluationResults(noneProfile));
                return null;
            }
        });

        final SecureUserProfileEvaluation sourceProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
        sourceProfile.setPartnerList(queryPartner);
        Future<Void> source = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                sourceProfile.setDecomposer("source");
                results.results.add((EvaluationResultList) getEvaluationResults(sourceProfile));
                return null;
            }
        });
        final SecureUserProfileEvaluation sourceProfile2 = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
        sourceProfile2.setPartnerList(queryPartner);
        sourceProfile2.setQueryExpansionSourcePartner(sourceExpansionPartners);
        Future<Void> source2 = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                sourceProfile2.setDecomposer("source");
                results.results.add((EvaluationResultList) getEvaluationResults(sourceProfile2));
                return null;
            }
        });
        //
        //
        try {
            source.get();
            source2.get();
            none.get();
            wikipedia.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        return results;
    }

    /**
     * calls eval manager to write all evaluated results into files and clean
     * the evaluated results cache
     */
    public void evaluationWriteEraseResults() {
        evalManager.writeAllResultsToFile();
    }

    /**
     * returns a graph as object to convert to json for d3
     * 
     * @param userProfileDefaultEdge
     * @return
     * @throws FederatedRecommenderException
     */
    @SuppressWarnings("unchecked")//Just using one implementation here, quite sure it works
	public D3GraphDocument getGraph(SecureUserProfile userProfile) throws FederatedRecommenderException {

        DBPediaGraphInterface dbPediaGraph = new DBPediaGraphJGraph(new DbPediaSolrIndex(federatedRecommenderConfiguration));
        List<String> keynodes = new ArrayList<String>();
        int hitsLimit = federatedRecommenderConfiguration.getGraphHitsLimitPerQuery();
        int depthLimit = federatedRecommenderConfiguration.getGraphQueryDepthLimit();
        SimpleWeightedGraph<String, DefaultEdge> graph = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);

        try {
            graph = (SimpleWeightedGraph<String, DefaultEdge>) dbPediaGraph.getGraphFromKeywords(userProfile.getContextKeywords(), keynodes, hitsLimit, depthLimit);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "There was an error while building the graph", e);
            throw new FederatedRecommenderException("There was an error while building the graph", e);
        }
        D3GraphDocument d3GraphDocument = new D3GraphDocument(graph);
        d3GraphDocument = normalizeGraphValues(d3GraphDocument);
        return d3GraphDocument;
    }

    private D3GraphDocument normalizeGraphValues(D3GraphDocument d3GraphDocument) {
        String regexp = "^http://.*/";

        for (int i = 0; i < d3GraphDocument.nodes.size(); i++) {
            String node = d3GraphDocument.nodes.get(i).replaceAll(regexp, "");
            d3GraphDocument.nodes.set(i, node);
        }
        for (int i = 0; i < d3GraphDocument.edges.size(); i++) {
            String targetShortString = d3GraphDocument.edges.get(i).getTarget().replaceAll(regexp, "");
            String sourceShortString = d3GraphDocument.edges.get(i).getSource().replaceAll(regexp, "");

            d3GraphDocument.edges.get(i).setTarget(targetShortString);
            d3GraphDocument.edges.get(i).setSource(sourceShortString);
        }

        return d3GraphDocument;
    }

    public void setPartners(PartnerBadgeList list) {
        for (PartnerBadge partner : list.partners) {
            this.fRCore.addPartner(partner);
        }
    }

    // End Evaluation
}
