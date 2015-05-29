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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SerializationUtils;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeList;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.dataformats.D3GraphDocument;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaGraph;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaSolrIndex;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationManager;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;
import eu.eexcess.federatedrecommender.evaluation.picker.BlockPicker;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;



/**
 * Core for the  Evaluation
 * (keeping evaluation details out of the main core)
 * 
 * @author hziak
 *
 */
public class FederatedRecommenderEvaluationCore   {
	private EvaluationManager evalManager=null;
	private final FederatedRecommenderConfiguration federatedRecommenderConfiguration;
	private final FederatedRecommenderCore fRCore;
	private final ExecutorService threadPool;
	
	private static final Logger logger = Logger
			.getLogger(FederatedRecommenderEvaluationCore.class.getName());
	
   public FederatedRecommenderEvaluationCore(FederatedRecommenderConfiguration federatedRecConfiguration) throws FederatedRecommenderException, FileNotFoundException{
	   this.federatedRecommenderConfiguration = federatedRecConfiguration;
	   try {
		this.fRCore= FederatedRecommenderCore.getInstance(federatedRecommenderConfiguration);
	} catch (FederatedRecommenderException e1) {
		logger.log(Level.SEVERE,"Could not get FederatedRecommenderCore");
		throw e1;
	}
	   
		this.evalManager = new EvaluationManager(federatedRecConfiguration.evaluationQueriesFile);
		
		threadPool = Executors.newFixedThreadPool(30);
   }
	// Begin Evaluation
	/**
	 * decides which source selection, decomposer and picker to take bases on the userprofile
	 * @param userProfile
	 * @return
	 */
	public EvaluationResultList getEvaluationResults(
			SecureUserProfileEvaluation userProfile) {
		SecureUserProfileEvaluation userProfileEvaluation = null;
		if (userProfile.sourceSelect != null)
			switch (userProfile.sourceSelect) {
			case "langModel":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.sourceSelection(userProfile,"Model");
				break;
			case "wordnet":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.sourceSelection(userProfile,"Model");
				break;
			case "wikipedia":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.sourceSelection(userProfile,"Model");
				break;
			default:
				logger.log(Level.WARNING,
						"Source selection unknown or not defined, not using any source selection at all");
				userProfileEvaluation = userProfile;
				break;
			}
		if(userProfileEvaluation==null){
			userProfileEvaluation = userProfile;
			
		}
		
		if (userProfile.decomposer != null)
			switch (userProfile.decomposer) {
			case "wikipedia":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.
						addQueryExpansionTerms(userProfileEvaluation, "eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceWikipediaDecomposer");
				break;
			case "source":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.
				addQueryExpansionTerms(userProfileEvaluation, "eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceSourcesDecomposer");

				break;
			case "dbpediagraph":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.
				addQueryExpansionTerms(userProfileEvaluation, "eu.eexcess.federatedrecommender.decomposer.DBPediaDecomposer");
				break;
			case "serendipity":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore.
				addQueryExpansionTerms(userProfileEvaluation, "eu.eexcess.federatedrecommender.decomposer.SerendiptiyDecomposer");
				break;
			default:
				logger.log(Level.WARNING,
						"Decomposer unknown or not defined, not using any decomposer at all");
				userProfileEvaluation = userProfile;
				break;
			}
		if (userProfileEvaluation == null){
			userProfileEvaluation = userProfile;
			userProfileEvaluation.numResults=10;
			if(userProfile.numResults !=null)	
			userProfileEvaluation.numResults = userProfile.numResults;
		}
		else{
			userProfileEvaluation.numResults=10;
			if(userProfile.numResults !=null)	
			userProfileEvaluation.numResults = userProfile.numResults;
		}
			
		EvaluationResultList resultList = null;
		//
		// logger.log(Level.SEVERE, "Picker class not found", new Exception());
		// return null;
		// }
		if (userProfile.picker == null)
			userProfile.picker = "occurenceprobability";
		try {
		switch (userProfile.picker) {

		case "simdiversification":
		
				resultList = new EvaluationResultList(
						fRCore.getAndAggregateResults(userProfileEvaluation, "eu.eexcess.federatedrecommender.picker.SimilarityDiversificationPicker"));
			break;
		case "FiFoPicker":
			resultList = new EvaluationResultList(
					fRCore.getAndAggregateResults(userProfileEvaluation, "eu.eexcess.federatedrecommender.picker.FiFoPicker"));
			break;
		case "occurenceprobability":
		default:
			resultList = new EvaluationResultList(
					fRCore.getAndAggregateResults(userProfileEvaluation, "eu.eexcess.federatedrecommender.picker.OccurrenceProbabilityPicker"));
		}
		} catch (FederatedRecommenderException e) {
			logger.log(Level.SEVERE,"Could get or aggregate results!",e);
		}
		resultList.provider = userProfile.decomposer + " partners:" + userProfile.picker
				+ " expansion source partners:" + userProfile.queryExpansionSourcePartner;
		return resultList;
	}
	/**
	 * calls the eval manager to write the result file for the specified user
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
	 * returns the next result from the evaluation manager and checks if the manager chache is warmed
	 * @param uID
	 * @return
	 * @throws IOException
	 */
	public EvaluationResultLists getExpansionEvaluation(Integer uID)
			throws IOException {
		if (!evalManager.isCacheWarmed()) {
			for (SecureUserProfileEvaluation query : evalManager.getAllQueries()) {
				logger.log(Level.INFO,"Warming evalualtion query cache");
				evalManager.addResultToQueryCache(query, getExpansionEvaluationResult(-1));
			}
		}

		return evalManager.getNextResultsForQuery(uID);

	}

	/**
	 * returns the next result from the evaluation manager and checks if the manager chache is warmed
	 * @param uID
	 * @return
	 * @throws IOException
	 */
	public EvaluationResultLists getBlockEvaluation(Integer uID)
			throws IOException {
		if (!evalManager.isCacheWarmed()) {
			for (SecureUserProfileEvaluation query : evalManager.getAllQueries()) {
				logger.log(Level.INFO,"Warming evalualtion query cache");
				evalManager.addResultToQueryCache(query, getBlockEvaluationResultFromQManager(-1));
			}
		}

		return evalManager.getNextResultsForQuery(uID);

	}
	/**
	 * main function for the block evaluation
	 * @param id
	 * @return
	 */
	private EvaluationResultLists getBlockEvaluationResultFromQManager(Integer id) {
	
		final SecureUserProfileEvaluation evalProfil = evalManager.getNextQuery(id);
		final EvaluationResultLists results = getblockResult(evalProfil);
		
		return results;
	}
	public EvaluationResultLists getblockResult(final SecureUserProfileEvaluation evalProfil) {
		final EvaluationResultLists results = new EvaluationResultLists();
		final EvaluationResultLists blockTmpResults = new EvaluationResultLists();
		//EvaluationQuery query = evalManager.getNextQuery(id);
		//evalProfil.contextKeywords.add(new ContextKeyword(query.query));
//		//BEGIN Partner Selection
//		ArrayList<PartnerBadge> blockEvalPartners= new ArrayList<PartnerBadge>();
//		for (PartnerBadge partner : fRCore.getPartnerRegister().getPartners()) {
//			if(partner.systemId.contains("ZBW"))
//				blockEvalPartners.add(partner);
//		}
//		ArrayList<PartnerBadge> queryPartner= new ArrayList<PartnerBadge>();
//		for (PartnerBadge partner : fRCore.getPartnerRegister().getPartners()) {
//			if(partner.systemId.contains("Mendeley"))
//				queryPartner.add(partner);
//		}
//		//END Partner Selection		
		
		results.query = evalProfil.getContextKeywordConcatenation();
		results.queryDescription= evalProfil.description;
		evalProfil.picker = "FiFoPicker";
		
		final SecureUserProfileEvaluation diversityProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
		
//		diversityProfile.partnerList=queryPartner;
		
		Future<Void> diversityFuture = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				diversityProfile.decomposer = "wikipedia";
				EvaluationResultList evaluationResults = (EvaluationResultList) getEvaluationResults(diversityProfile);
				evaluationResults.provider="Diversity";
				blockTmpResults.results
						.add(evaluationResults);
				return null;
			}
		});
		final SecureUserProfileEvaluation unmodifiedProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
		
		Future<Void> unmodifiedFutur = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				unmodifiedProfile.decomposer = "none";
				EvaluationResultList eRL= (EvaluationResultList) getEvaluationResults(unmodifiedProfile);
				eRL.provider="Basic";
				blockTmpResults.results
						.add(eRL);
				results.results.add(eRL); //ADDING IT ALLREADY IN THE FINAL RESULT LIST SINCE IT'S THE SECOND RESULT LIST TO PRESENT
				return null;
			}
		});
		
		final SecureUserProfileEvaluation serendipityProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
		
		Future<Void> serendipityFutur = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				serendipityProfile.decomposer = "serendipity";
				EvaluationResultList evaluationResults = (EvaluationResultList) getEvaluationResults(serendipityProfile);
				evaluationResults.provider="Serendipity";
				blockTmpResults.results
						.add(evaluationResults);
				return null;
			}
		});
		try {
			unmodifiedFutur.get();
			diversityFuture.get();
			serendipityFutur.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BlockPicker bP = new BlockPicker();
		try {
			results.results.add(bP.pickBlockResults(blockTmpResults,  evalProfil.numResults));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for (EvaluationResultList evalResultList : blockTmpResults.results) {
//			System.out.println(evalResultList);	
//		}
//		

//		results.results.addAll(blockTmpResults.results);
		return results;
	}
	
	/**
	 * main function for the decomposer evaluation
	 * @param id
	 * @return
	 */
	private EvaluationResultLists getExpansionEvaluationResult(Integer id) {
		final EvaluationResultLists results = new EvaluationResultLists();
		final SecureUserProfileEvaluation evalProfil =  evalManager.getNextQuery(id);
		

	
		ArrayList<PartnerBadge> sourceExpansionPartners= new ArrayList<PartnerBadge>();
		for (PartnerBadge partner : fRCore.getPartnerRegister().getPartners()) {
			if(partner.systemId.contains("ZBW"))
				sourceExpansionPartners.add(partner);
		}
		ArrayList<PartnerBadge> queryPartner= new ArrayList<PartnerBadge>();
		for (PartnerBadge partner : fRCore.getPartnerRegister().getPartners()) {
			if(partner.systemId.contains("Mendeley"))
				queryPartner.add(partner);
		}
		
		results.query = evalProfil.getContextKeywordConcatenation(); //TODO!
		results.queryDescription= evalProfil.description;
		evalProfil.picker = "FiFoPicker";
		
		final SecureUserProfileEvaluation wikipediaProfile = (SecureUserProfileEvaluation) SerializationUtils.clone(evalProfil);
		wikipediaProfile.partnerList=queryPartner;
		Future<Void> wikipedia = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				wikipediaProfile.decomposer = "wikipedia";
				results.results
						.add((EvaluationResultList) getEvaluationResults(wikipediaProfile));
				return null;
			}
		});
		final SecureUserProfileEvaluation noneProfile = (SecureUserProfileEvaluation) SerializationUtils
				.clone(evalProfil);
//		noneProfile.partnerList=queryPartner;
		Future<Void> none = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				noneProfile.decomposer = "";
				results.results
						.add((EvaluationResultList) getEvaluationResults(noneProfile));
				return null;
			}
		});

		final SecureUserProfileEvaluation sourceProfile = (SecureUserProfileEvaluation) SerializationUtils
				.clone(evalProfil);
		sourceProfile.partnerList=queryPartner;
		Future<Void> source = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				sourceProfile.decomposer = "source";
				results.results
						.add((EvaluationResultList) getEvaluationResults(sourceProfile));
				return null;
			}
		});
		final SecureUserProfileEvaluation sourceProfile2 = (SecureUserProfileEvaluation) SerializationUtils
				.clone(evalProfil);
		sourceProfile2.partnerList=queryPartner;
		sourceProfile2.queryExpansionSourcePartner=sourceExpansionPartners;
				Future<Void> source2 = threadPool.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				sourceProfile2.decomposer = "source";
				results.results
						.add((EvaluationResultList) getEvaluationResults(sourceProfile2));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	/**
	 * calls eval manager to write all evaluated results into files and clean the evaluated results cache
	 */
	public void evaluationWriteEraseResults() {
		evalManager.writeAllResultsToFile();
	}
	/**
	 * returns a graph as object to convert to json for d3
	 * 
	 * @param userProfile
	 * @return
	 * @throws FederatedRecommenderException
	 */
	public D3GraphDocument getGraph(SecureUserProfile userProfile) throws FederatedRecommenderException {
		
		DbPediaGraph dbPediaGraph = new DbPediaGraph(new DbPediaSolrIndex(federatedRecommenderConfiguration));
		List<String> keynodes = new ArrayList<String>();
		int hitsLimit = federatedRecommenderConfiguration.graphHitsLimitPerQuery;
		int depthLimit = federatedRecommenderConfiguration.graphQueryDepthLimit;
		SimpleWeightedGraph<String, DefaultEdge> graph = null;
		try {
			graph = dbPediaGraph.getFromKeywords(userProfile.contextKeywords, keynodes, hitsLimit, depthLimit);
			System.out.println(graph +" graph");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "There was an error while building the graph", e);
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
			String targetShortString = d3GraphDocument.edges.get(i).target.replaceAll(regexp, "");
			String sourceShortString = d3GraphDocument.edges.get(i).source.replaceAll(regexp, "");

			d3GraphDocument.edges.get(i).target = targetShortString;
			d3GraphDocument.edges.get(i).source = sourceShortString;
		}

		return d3GraphDocument;
	}
	public void setPartners(PartnerBadgeList list){
		for (PartnerBadge partner : list.partners) {
			this.fRCore.addPartner(partner);	
		}
	}

	// End Evaluation
}
