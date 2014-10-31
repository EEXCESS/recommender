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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SerializationUtils;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationManager;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;




public class FederatedRecommenderEvaluationCore   {
	private final EvaluationManager evalManager;
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
	   try {
			this.evalManager = new EvaluationManager(
					federatedRecConfiguration.evaluationQueriesFile);
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING,
					"EvaluationQueries file could not be read "
							+ federatedRecConfiguration.evaluationQueriesFile,
					e);
			throw e;
		}
		threadPool = Executors.newFixedThreadPool(30);
   }
	// Begin Evaluation
	/**
	 * decides which source selection, decomposer and picker to take bases on the userprofile
	 * @param userProfile
	 * @return
	 */
	public ResultList getEvaluationResults(
			SecureUserProfileEvaluation userProfile) {
		SecureUserProfileEvaluation userProfileEvaluation = null;
		if (userProfile.sourceSelect != null)
			switch (userProfile.sourceSelect) {
			case "langModel":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.sourceSelectionLanguageModel(userProfile);
				break;
			case "wordnet":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.sourceSelectionWordnet(userProfile);
				break;
			case "wikipedia":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.sourceSelectionWikipedia(userProfile);
				break;
			default:
				logger.log(Level.WARNING,
						"Sourceselection unknown or not defined, not using any decomposer at all");
				userProfileEvaluation = userProfile;
				break;
			}
		if (userProfile.decomposer != null)
			switch (userProfile.decomposer) {
			case "wikipedia":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.generateFederatedRecommendationQEWikipedia(userProfile);
				break;
			case "source":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.generateFederatedRecommendationQESources(userProfile);
				break;
			case "dbpediagraph":
				userProfileEvaluation = (SecureUserProfileEvaluation) fRCore
						.generateFederatedRecommendationDBPedia(userProfile);
				break;
			default:
				logger.log(Level.WARNING,
						"Decomposer unknown or not defined, not using any decomposer at all");
				userProfileEvaluation = userProfile;
				break;
			}
		if (userProfileEvaluation == null)
			userProfileEvaluation = userProfile;
		else
			userProfileEvaluation.numResults = userProfile.numResults;

		EvaluationResultList resultList = null;
		//
		// logger.log(Level.SEVERE, "Picker class not found", new Exception());
		// return null;
		// }
		if (userProfile.picker == null)
			userProfile.picker = "occurenceprobability";
		switch (userProfile.picker) {

		case "simdiversification":
			resultList = new EvaluationResultList(
					fRCore.useSimDiversificationPicker(userProfileEvaluation));
			break;
		case "FiFoPicker":
			resultList = new EvaluationResultList(
					fRCore.useFiFoPicker(userProfileEvaluation));
			break;
		case "occurenceprobability":
		default:
			resultList = new EvaluationResultList(
					fRCore.useOccurenceProbabilityPicker(userProfileEvaluation));
		}
		for (Result result : resultList.results) {
			result.rdf = null;
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
			for (EvaluationQuery query : evalManager.getAllQueries()) {
				evalManager.addResultToQueryCache(query, getExpansionEvaluationResult(-1));
			}
		}

		return evalManager.getNextResultsForQuery(uID);

	}
	/**
	 * main function for the decomposer evaluation
	 * @param id
	 * @return
	 */
	private EvaluationResultLists getExpansionEvaluationResult(Integer id) {
		final EvaluationResultLists results = new EvaluationResultLists();
		final SecureUserProfileEvaluation evalProfil = new SecureUserProfileEvaluation();
		EvaluationQuery query = evalManager.getNextQuery(id);

		evalProfil.contextKeywords.add(new ContextKeyword(query.query));
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
		
		results.query = query.query;
		results.queryDescription= query.description;
		evalProfil.picker = "FiFoPicker";
		
		final SecureUserProfileEvaluation wikipediaProfile = (SecureUserProfileEvaluation) SerializationUtils
				.clone(evalProfil);
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

	// End Evaluation
}
