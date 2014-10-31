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
package eu.eexcess.federatedrecommender;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.dataformats.D3GraphDocument;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaGraph;
import eu.eexcess.federatedrecommender.dbpedia.DbPediaSolrIndex;
import eu.eexcess.federatedrecommender.decomposer.DBPediaDecomposer;
import eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceSourcesDecomposer;
import eu.eexcess.federatedrecommender.decomposer.PseudoRelevanceWikipediaDecomposer;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.picker.FiFoPicker;
import eu.eexcess.federatedrecommender.picker.OccurrenceProbabilityPicker;
import eu.eexcess.federatedrecommender.picker.SimilarityDiversificationPicker;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * FederatedRecommenderCore (Singleton)
 */
public class FederatedRecommenderCore {

	private static final Logger logger = Logger.getLogger(FederatedRecommenderCore.class.getName());
	private static final String[] SUPPORTED_LOCALES = new String[] { "en", "de" };

	private static volatile FederatedRecommenderCore instance;
	private final FederatedRecommenderConfiguration federatedRecConfiguration;

	private PartnerRegister partnerRegister = new PartnerRegister();
	private ExecutorService threadPool;
	private final DbPediaSolrIndex dbPediaSolrIndex;

	private FederatedRecommenderCore(FederatedRecommenderConfiguration federatedRecConfiguration) {
		threadPool = Executors.newFixedThreadPool(federatedRecConfiguration.numRecommenderThreads);
		this.federatedRecConfiguration = federatedRecConfiguration;
		this.dbPediaSolrIndex = new DbPediaSolrIndex(federatedRecConfiguration);
	}

	public PartnerRegister getPartnerRegister() {
		return partnerRegister;
	}

	public void addPartner(PartnerBadge badge) {
		partnerRegister.addPartner(badge);
	}

	/**
	 * returns the instance of the {@link FederatedRecommenderCore} has to be
	 * lazy caused by the configuration by now
	 * 
	 * @return
	 * @throws FederatedRecommenderException
	 */
	public static FederatedRecommenderCore getInstance(FederatedRecommenderConfiguration federatedRecommenderConfiguration)
			throws FederatedRecommenderException {
		if (instance == null) {
			synchronized (FederatedRecommenderCore.class) {
				// Double check
				if (instance == null) {
					instance = new FederatedRecommenderCore(federatedRecommenderConfiguration);
				}
			}
		}
		return instance;
	}

	/**
	 * returns the rcommendations for all registered partners as
	 * {@link PartnersFederatedRecommendations} object (containing a Hashmap
	 * with the partner as key and the data as value)
	 * 
	 * @param secureUserProfile
	 * @return
	 */
	public PartnersFederatedRecommendations getPartnersRecommendations(final SecureUserProfile secureUserProfile) {
		final PartnersFederatedRecommendations partnersFederatedResults = new PartnersFederatedRecommendations();

		long start = System.currentTimeMillis();
		Map<PartnerBadge, Future<ResultList>> futures = new HashMap<PartnerBadge, Future<ResultList>>();
		for (final PartnerBadge partner : partnerRegister.getPartners()) {
			if (checkUserSelectedPartners(secureUserProfile, partner)) {
				final Client tmpClient = partnerRegister.getClient(partner);

				Future<ResultList> future = threadPool.submit(new Callable<ResultList>() {
					@Override
					public ResultList call() throws Exception {
						long startTime = System.currentTimeMillis();
						ResultList resultList = getPartnerResult(partner, tmpClient, secureUserProfile);
						long endTime = System.currentTimeMillis();
						long respTime = endTime - startTime;
						partner.updatePartnerResponseTime(respTime);
						return resultList;
					}

				});
				futures.put(partner, future);

			}
		}
		long timeout = federatedRecConfiguration.partnersTimeout; // ms
		for (Entry<PartnerBadge, Future<ResultList>> entry : futures.entrySet()) {
			long startT = System.currentTimeMillis();
			try {
				entry.getKey().requestCount++;
				ResultList rL = entry.getValue().get(timeout, TimeUnit.MILLISECONDS);
				entry.getValue().cancel(true);
				partnersFederatedResults.getResults().put(entry.getKey(), rL);
				timeout -= System.currentTimeMillis() - startT;
				// entry.getValue().get(start + 15 * 10000 - now,
				// TimeUnit.MILLISECONDS);
				timeout = timeout - (System.currentTimeMillis() - startT);

			} catch (TimeoutException e) {
				entry.getKey().failedRequestCount++;
				entry.getValue().cancel(true);
				timeout -= System.currentTimeMillis() - startT;
				logger.log(Level.WARNING, "Waited too long for partner system '" + entry.getKey() + "' to respond " + timeout, e);
			} catch (Exception e) {
				entry.getKey().failedRequestCount++;
				entry.getValue().cancel(true);
				timeout -= System.currentTimeMillis() - startT;
				logger.log(Level.SEVERE, "Failed to retrieve results from a parter system '" + entry.getKey() + "'" + (timeout) + "ms ", e);
			}
			entry.setValue(null);

		}
		long end = System.currentTimeMillis();
		logger.log(Level.INFO, "Federated Recommender took " + (end - start) + "ms for query '" + secureUserProfile.contextKeywords + "'");

		return partnersFederatedResults;
	}

	/**
	 * checks which partners are selected and if there is an partner access key
	 * for that partner if one is needed
	 * 
	 * @param secureUserProfile
	 * @param partner
	 * @return true/false
	 */
	private boolean checkUserSelectedPartners(SecureUserProfile secureUserProfile, PartnerBadge partner) {
		if (secureUserProfile.partnerList != null) { // if the list is null then
														// we query every
														// partner
			if (!secureUserProfile.partnerList.isEmpty()) {
				boolean withKey = false;
				if (partner.partnerKey != null)
					if (!partner.partnerKey.isEmpty())
						withKey = true;
				if (!withKey)
					for (PartnerBadge uBadge : secureUserProfile.partnerList) {
						if (uBadge.getSystemId().equals(partner.getSystemId()))
							return true;
					}
				else
					for (PartnerBadge uBadge : secureUserProfile.protectedPartnerList) {
						if (uBadge.partnerKey != null)
							if (!uBadge.partnerKey.isEmpty())
								if (partner.partnerKey.equals(uBadge.partnerKey) && uBadge.getSystemId().equals(partner.getSystemId()))
									return true;
					}
			} else
				return true;
		} else
			return true;

		return false;
	}

	/**
	 * trys to recieve the results from the partners
	 * 
	 * @param partner
	 * @param secureUserProfile
	 * @return
	 */
	private ResultList getPartnerResult(PartnerBadge partner, Client client, SecureUserProfile secureUserProfile) {

		ResultList resultList = new ResultList();
		// SecureUserProfile secureUserProfileTmp = (SecureUserProfile)
		// secureUserProfile;

		if (client != null) {
			try {
				WebResource resource = client.resource(partner.getEndpoint());
				resource.accept(MediaType.APPLICATION_JSON);

				resultList = resource.post(ResultList.class, secureUserProfile);

			} catch (Exception e) {
				logger.log(Level.WARNING, "Partner: " + partner.getSystemId() + " is not working currently.", e);

			}
		}
		return resultList;
	}

	/**
	 * main function to generate a federated recommendation
	 * 
	 * @return
	 */
	public ResultList generateFederatedRecommendation(SecureUserProfile secureUserProfile) throws FileNotFoundException {
		// ResultList result = new ResultList();
		ResultList resultList = null;

		// SecureUserProfileDecomposer sUPDecomposer = null;
		// sUPDecomposer = new
		// SecureUserProfileDecomposer(federatedRecConfiguration,dbPediaSolrIndex);

		resultList = useOccurenceProbabilityPicker(secureUserProfile);
		return resultList;

	}

	/**
	 * Generates a simple recommendation from N registered partners (for demo
	 * purposes)
	 * 
	 * @return
	 */
	public ResultList generateSafeModeFederatedRecommendation(SecureUserProfile secureUserProfile) throws FileNotFoundException {

		ResultList federatedResults = new ResultList();

		federatedResults.provider = "Federated";

		for (PartnerBadge partner : this.getPartnerRegister().getPartners()) {
			Client client = getPartnerRegister().getClient(partner);
			for (Result partnerResults : getPartnerResult(partner, client, secureUserProfile).results) {
				federatedResults.results.add(partnerResults);
			}
		}

		federatedResults.totalResults = federatedResults.results.size();

		Collections.shuffle(federatedResults.results, new Random(666));

		return federatedResults;
	}

	/**
	 * returns a graph as object to convert to json for d3
	 * 
	 * @param userProfile
	 * @return
	 * @throws FederatedRecommenderException
	 */
	public D3GraphDocument getGraph(SecureUserProfile userProfile) throws FederatedRecommenderException {
		DbPediaGraph dbPediaGraph = new DbPediaGraph(dbPediaSolrIndex);
		List<String> keynodes = new ArrayList<String>();
		int hitsLimit = 10;
		int depthLimit = 10;
		SimpleWeightedGraph<String, DefaultEdge> graph = null;
		try {
			graph = dbPediaGraph.getFromKeywords(userProfile.contextKeywords, keynodes, hitsLimit, depthLimit);
		} catch (FederatedRecommenderException e) {
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

	// /**
	// * test method for decomposers
	// */
	// public ResultList generateFederatedRecommendationDecomposerTests(
	// SecureUserProfile secureUserProfile) throws IOException {
	// // ResultList result = new ResultList();
	// ResultList resultList = null;
	// String wikipediaIndexDirectory = "/home/hziak/Datasets/EExcess/dewiki/";
	// SecureUserProfileDecomposer<SecureUserProfile,
	// SecureUserProfileEvaluation> sUPDecomposer = new
	// PseudoRelevanceWikipediaDecomposer(
	// wikipediaIndexDirectory, SUPPORTED_LOCALES);
	// sUPDecomposer.decompose((SecureUserProfile) SerializationUtils
	// .clone(secureUserProfile));
	// sUPDecomposer = new PseudoRelevanceSourcesDecomposer();
	// sUPDecomposer.decompose(secureUserProfile);
	//
	// resultList = useOccurenceProbabilityPicker(secureUserProfile);
	// return resultList;
	//
	// }

	public ResultList useOccurenceProbabilityPicker(SecureUserProfile userProfile) {
		ResultList resultList;
		PartnersFederatedRecommendationsPicker pFRPicker = new OccurrenceProbabilityPicker();
		long start = System.currentTimeMillis();
		PartnersFederatedRecommendations pFR = getPartnersRecommendations(userProfile);
		long end = System.currentTimeMillis();
		long timeToGetPartners = end - start;
		start = System.currentTimeMillis();
		int numResults = 10;
		if (userProfile.numResults != null)
			numResults = userProfile.numResults;
		resultList = pFRPicker.pickResults(userProfile, pFR, partnerRegister.getPartners(), numResults);
		end = System.currentTimeMillis();
		long timeToPickResults = end - start;
		logger.log(Level.INFO, " Time to get " + resultList.results.size() + " Results from the Partners: " + timeToGetPartners
				+ "ms. Time to pick the best results: " + timeToPickResults + "ms");
		resultList.totalResults = resultList.results.size();
		resultList.provider = "federated";
		return resultList;
	}

	public ResultList useFiFoPicker(SecureUserProfile userProfile) {
		ResultList resultList;
		PartnersFederatedRecommendationsPicker pFRPicker = new FiFoPicker();
		long start = System.currentTimeMillis();
		PartnersFederatedRecommendations pFR = getPartnersRecommendations(userProfile);
		long end = System.currentTimeMillis();
		long timeToGetPartners = end - start;
		start = System.currentTimeMillis();
		int numResults = 10;
		if (userProfile.numResults != null)
			numResults = userProfile.numResults;
		resultList = pFRPicker.pickResults(userProfile, pFR, partnerRegister.getPartners(), numResults);
		end = System.currentTimeMillis();
		long timeToPickResults = end - start;
		logger.log(Level.INFO, " Time to get " + resultList.results.size() + " Results from the Partners: " + timeToGetPartners
				+ "ms. Time to pick the best results: " + timeToPickResults + "ms");
		resultList.totalResults = resultList.results.size();
		resultList.provider = "federated";
		return resultList;
	}

	public ResultList useSimDiversificationPicker(SecureUserProfile userProfile) {
		ResultList resultList;
		SecureUserProfile currentUserProfile = new SecureUserProfile();
		PartnersFederatedRecommendationsPicker pFRPicker = new SimilarityDiversificationPicker(0.5);
		long start = System.currentTimeMillis();
		PartnersFederatedRecommendations pFR = getPartnersRecommendations(userProfile);
		long end = System.currentTimeMillis();
		long timeToGetPartners = end - start;
		start = System.currentTimeMillis();
		int numResults = 10;
		if (currentUserProfile.numResults != null)
			numResults = currentUserProfile.numResults;
		resultList = pFRPicker.pickResults(currentUserProfile, pFR, partnerRegister.getPartners(), numResults);
		end = System.currentTimeMillis();
		long timeToPickResults = end - start;
		logger.log(Level.INFO, " Time to get " + resultList.totalResults + " Results from the Partners: " + timeToGetPartners
				+ "ms. Time to pick the best results: " + timeToPickResults + "ms");
		resultList.totalResults = resultList.results.size();
		resultList.provider = "federated";
		return resultList;
	}

	public SecureUserProfile generateFederatedRecommendationQEWikipedia(SecureUserProfileEvaluation userProfile) {
		SecureUserProfileDecomposer<SecureUserProfile, SecureUserProfile> sUPDecomposer = null;
		try {
			sUPDecomposer = new PseudoRelevanceWikipediaDecomposer(federatedRecConfiguration.wikipediaIndexDir, SUPPORTED_LOCALES);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Wikipedia index directory could be wrong or not readable:" + federatedRecConfiguration.wikipediaIndexDir, e);
		}
		if (sUPDecomposer == null)
			return (SecureUserProfile) userProfile;
		return (SecureUserProfile) sUPDecomposer.decompose(userProfile);

	}

	public SecureUserProfile generateFederatedRecommendationQESources(SecureUserProfileEvaluation userProfile) {
		SecureUserProfileDecomposer<?, SecureUserProfileEvaluation> sUPDecomposer = new PseudoRelevanceSourcesDecomposer();
		return (SecureUserProfile) sUPDecomposer.decompose(userProfile);
	}

	public SecureUserProfile generateFederatedRecommendationDBPedia(SecureUserProfileEvaluation userProfile) {

		SecureUserProfileDecomposer<?, SecureUserProfileEvaluation> sUPDecomposer = new DBPediaDecomposer(federatedRecConfiguration, dbPediaSolrIndex,
				federatedRecConfiguration.graphQueryDepthLimit);
		return sUPDecomposer.decompose(userProfile);
	}

	public SecureUserProfile sourceSelectionLanguageModel(SecureUserProfileEvaluation userProfile) {
		// TODO add connection to langModelSourceSelection and alter
		// userProfile.partnerList to select sources
		return userProfile;
	}

	public SecureUserProfile sourceSelectionWordnet(SecureUserProfileEvaluation userProfile) {
		// TODO add connection to WordnetSourceSelection and alter
		// userProfile.partnerList to select sources
		return userProfile;
	}

	public SecureUserProfile sourceSelectionWikipedia(SecureUserProfileEvaluation userProfile) {
		// TODO add connection to WikipediaSourceSelection and alter
		// userProfile.partnerList to select sources
		return userProfile;
	}

	
}
