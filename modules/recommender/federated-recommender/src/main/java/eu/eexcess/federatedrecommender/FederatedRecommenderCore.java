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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeStats;
import eu.eexcess.dataformats.RecommenderStats;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.DocumentBadgePredicate;
import eu.eexcess.dataformats.result.PartnerResponseState;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.result.ResultStats;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
import eu.eexcess.sqlite.Database;
import eu.eexcess.sqlite.DatabaseQueryStats;

/**
 * FederatedRecommenderCore (Singleton)
 * 
 */
public class FederatedRecommenderCore {

	private static final Logger logger = Logger.getLogger(FederatedRecommenderCore.class.getName());

	private static volatile FederatedRecommenderCore instance;
	private final FederatedRecommenderConfiguration federatedRecConfiguration;

	private PartnerRegister partnerRegister = new PartnerRegister();
	private ExecutorService threadPool;
	private RecommenderStats recommenderStats;

	/**
	 * references to re-usable state-less source selection instances
	 */
	HashMap<String, PartnerSelector> statelessSourceSelectionInstances = new HashMap<>();

	private FederatedRecommenderCore(FederatedRecommenderConfiguration federatedRecConfiguration) {
		threadPool = Executors.newFixedThreadPool(federatedRecConfiguration.numRecommenderThreads);
		this.federatedRecConfiguration = federatedRecConfiguration;
		this.recommenderStats = new RecommenderStats();
	}

	/**
	 * returns the registered partners in the system
	 * 
	 * @return
	 */
	public PartnerRegister getPartnerRegister() {
		synchronized (partnerRegister) {
			return partnerRegister;
		}
	}

	/**
	 * adds a partner to the system
	 * 
	 * @param badge
	 */
	public void addPartner(PartnerBadge badge) {
		synchronized (partnerRegister) {
			partnerRegister.addPartner(badge);
		}
	}

	/**
	 * returns the instance of the {@link FederatedRecommenderCore} has to be
	 * lazy caused by the configuration by now
	 * 
	 * @return
	 * @throws FederatedRecommenderException
	 */
	public static FederatedRecommenderCore getInstance(
					FederatedRecommenderConfiguration federatedRecommenderConfiguration)
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
		List<PartnerBadge> partners = new LinkedList<PartnerBadge>(getPartnerRegister().getPartners());
		for (final PartnerBadge partner : partners) {
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

					/**
					 * trys to recieve the results from the partners
					 * 
					 * @param partner
					 * @param secureUserProfile
					 * @return
					 */
					private ResultList getPartnerResult(PartnerBadge partner, Client client,
									SecureUserProfile secureUserProfile) {
						ResultList resultList = new ResultList();
						if (client != null) {
							try {
								WebResource resource = client.resource(partner.getPartnerConnectorEndpoint()
												+ "recommend");
								resource.accept(MediaType.APPLICATION_JSON);
								resultList = resource.post(ResultList.class, secureUserProfile);
							} catch (Exception e) {
								logger.log(Level.WARNING, "Partner: " + partner.getSystemId()
												+ " is not working currently.", e);
								throw e;
							}
						}
						client.destroy();
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

				entry.getKey().shortTimeStats.requestCount++;
				ResultList rL = entry.getValue().get(timeout, TimeUnit.MILLISECONDS);
				PartnerResponseState responseState = new PartnerResponseState();

				responseState.success = true;
				responseState.systemID = entry.getKey().systemId;
				rL.partnerResponseState.add(responseState);

				entry.getValue().cancel(true);
				partnersFederatedResults.getResults().put(entry.getKey(), rL);
				entry.getKey().addLastQueries(rL.getResultStats());

				timeout -= System.currentTimeMillis() - startT;
				timeout = timeout - (System.currentTimeMillis() - startT);

			} catch (TimeoutException e) {
				entry.getKey().shortTimeStats.failedRequestCount++;
				entry.getKey().shortTimeStats.failedRequestTimeoutCount++;
				entry.getValue().cancel(true);

				timeout -= System.currentTimeMillis() - startT;
				String msg = "Waited too long for partner system '" + entry.getKey().systemId + "' to respond "
								+ (federatedRecConfiguration.partnersTimeout - timeout) + " ms ";
				ResultList rL = new ResultList();
				PartnerResponseState responseState = new PartnerResponseState();
				responseState.errorMessage = msg;
				responseState.success = false;
				responseState.systemID = entry.getKey().systemId;
				rL.partnerResponseState.add(responseState);
				partnersFederatedResults.getResults().put(entry.getKey(), rL);
				logger.log(Level.WARNING, msg, e);
			} catch (Exception e) {
				if (entry.getKey() != null) {
					entry.getKey().shortTimeStats.failedRequestCount++;
					entry.getValue().cancel(true);
					timeout -= System.currentTimeMillis() - startT;
				}
				String msg = "Failed to retrieve results from a parter system '" + entry.getKey().systemId;
				PartnerResponseState responseState = new PartnerResponseState();
				responseState.errorMessage = msg;
				responseState.success = false;
				responseState.systemID = entry.getKey().systemId;
				ResultList rL = new ResultList();
				rL.partnerResponseState.add(responseState);
				logger.log(Level.SEVERE, msg, e);
			}
			entry.setValue(null);

		}
		long end = System.currentTimeMillis();
		logger.log(Level.INFO, "Federated Recommender took " + (end - start) + "ms for query '"
						+ secureUserProfile.contextKeywords + "'");

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
								if (partner.partnerKey.equals(uBadge.partnerKey)
												&& uBadge.getSystemId().equals(partner.getSystemId()))
									return true;
					}
			} else
				return true;
		} else
			return true;

		return false;
	}

	/**
	 * main function to generate a federated recommendation
	 * 
	 * @return
	 */
	public ResultList generateFederatedRecommendation(SecureUserProfile secureUserProfile) throws FileNotFoundException {
		// ResultList result = new ResultList();
		ResultList resultList = null;
		if (federatedRecConfiguration.sourceSelectors != null) {
			ArrayList<String> sourceSelectors = new ArrayList<String>();
			Collections.addAll(sourceSelectors, federatedRecConfiguration.sourceSelectors);
			secureUserProfile = sourceSelection(secureUserProfile, sourceSelectors);
		}
		// SecureUserProfileDecomposer sUPDecomposer = null;
		// sUPDecomposer = new
		// SecureUserProfileDecomposer(federatedRecConfiguration,dbPediaSolrIndex);
		try {
			resultList = getAndAggregateResults(secureUserProfile, this.federatedRecConfiguration.defaultPickerName);
		} catch (FederatedRecommenderException e) {
			logger.log(Level.SEVERE, "Some error retrieving or aggregation results occured.", e);
		}
		return resultList;

	}

	/**
	 * Distributes the documents to each of the
	 * 
	 * @param documents
	 * @return
	 */
	public DocumentBadgeList getDocumentDetails(DocumentBadgeList documents) {
		Map<PartnerBadge, Future<DocumentBadgeList>> futures = new HashMap<>();
		for (PartnerBadge partner : getPartnerRegister().getPartners()) {
			final Client tmpClient = partnerRegister.getClient(partner);
			DocumentBadgeList currentDocs = filterDocuments(documents,
							(DocumentBadge document) -> partner.systemId.equals(document.provider));
			Future<DocumentBadgeList> future = threadPool.submit(new Callable<DocumentBadgeList>() {
				@Override
				public DocumentBadgeList call() throws Exception {

					DocumentBadgeList resultList = getDocsResult(partner, tmpClient, currentDocs);
					return resultList;
				}

				/**
				 * trys to recieve the results from the partners
				 * 
				 * @param partner
				 * @param currentDocs
				 * @return
				 */
				private DocumentBadgeList getDocsResult(PartnerBadge partner, Client client,
								DocumentBadgeList currentDocs) {
					DocumentBadgeList docList = new DocumentBadgeList();
					if (client != null) {
						try {
							WebResource resource = client.resource(partner.getPartnerConnectorEndpoint() + "getDetails");
							resource.accept(MediaType.APPLICATION_JSON);
							docList = resource.post(DocumentBadgeList.class, currentDocs);
						} catch (Exception e) {
							logger.log(Level.WARNING, "Partner: " + partner.getSystemId()
											+ " is not working currently.", e);
							throw e;
						}
					}
					client.destroy();
					return docList;
				}
			});
			futures.put(partner, future);
		}
		DocumentBadgeList resultDocs = new DocumentBadgeList();
		long timeout = federatedRecConfiguration.partnersTimeout;
		for (PartnerBadge partner : futures.keySet()) {
			try {
				resultDocs.documentBadges
								.addAll(futures.get(partner).get(timeout, TimeUnit.MILLISECONDS).documentBadges);
			} catch (TimeoutException e) {
				logger.log(Level.WARNING, "Parnter " + partner.systemId + " timed out for document detail call", e);
			} catch (ExecutionException | InterruptedException e) {
				logger.log(Level.WARNING, "Can not get detail results from parnter:" + partner.systemId, e);
			}
		}
		return resultDocs;
	}

	/**
	 * Helper function to filter the documents
	 * 
	 * @param documents
	 * @param predicate
	 * @return
	 */
	private DocumentBadgeList filterDocuments(DocumentBadgeList documents, DocumentBadgePredicate predicate) {
		DocumentBadgeList result = new DocumentBadgeList();
		for (DocumentBadge docs : documents.documentBadges) {
			if (predicate.isPartnerDocument(docs)) {
				result.documentBadges.add(docs);
			}
		}
		return result;
	}

	/**
	 * calls the given query expansion algorithm
	 * 
	 * @param userProfile
	 * @return
	 */
	public SecureUserProfile addQueryExpansionTerms(SecureUserProfileEvaluation userProfile, String qEClass) {
		SecureUserProfileDecomposer<SecureUserProfile, SecureUserProfile> sUPDecomposer = null;
		try {
			sUPDecomposer = (SecureUserProfileDecomposer<SecureUserProfile, SecureUserProfile>) Class.forName(qEClass)
							.newInstance();
			sUPDecomposer.setConfiguration(federatedRecConfiguration);
		} catch (InstantiationException | FederatedRecommenderException | IllegalAccessException
						| ClassNotFoundException e) {
			logger.log(Level.WARNING, "Could not initalize query expansion algorithm", e);
		}
		if (sUPDecomposer == null)
			return (SecureUserProfile) userProfile;
		return sUPDecomposer.decompose(userProfile);
	}

	/**
	 * Performs partner source selection according to the given classes and
	 * their order. Multiple identical selectors are allowed but instantiated
	 * only once.
	 * 
	 * @param userProfile
	 * 
	 * @param sourceSelectorClassName
	 *            class names of source selectors to be applied in same order
	 * @return same userProfile but with changed partner list
	 */
	public SecureUserProfile sourceSelection(SecureUserProfile userProfile, ArrayList<String> selectorsClassNames) {

		if (selectorsClassNames == null || selectorsClassNames.size() <= 0) {
			return userProfile;
		}

		SecureUserProfile lastEvaluatedProfile = userProfile;

		for (String sourceSelectorClassName : selectorsClassNames) {
			try {
				PartnerSelector sourceSelector = statelessSourceSelectionInstances.get(sourceSelectorClassName);
				if (null == sourceSelector) {
					sourceSelector = (PartnerSelector) Class.forName(sourceSelectorClassName).newInstance();
					logger.info("instanciating new source selection [" + sourceSelector.getClass().getSimpleName()
									+ "]");
					statelessSourceSelectionInstances.put(sourceSelectorClassName, sourceSelector);
				}
				lastEvaluatedProfile = sourceSelector.sourceSelect(lastEvaluatedProfile, getPartnerRegister()
								.getPartners());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				logger.log(Level.SEVERE, "failed to instanciate source selector [" + sourceSelectorClassName
								+ "] by name: ignoring source selection", e);
			}
		}
		return lastEvaluatedProfile;
	}

	/**
	 * returns the statistics of the federated recommender
	 * 
	 * @return
	 */
	public RecommenderStats getRecommenderStats() {
		return recommenderStats;

	}

	/**
	 * removes partners from the partner register
	 * 
	 * @param badge
	 */
	public void unregisterPartner(PartnerBadge badge) {
		synchronized (partnerRegister) {
			if (this.getPartnerRegister().getPartners().contains(badge)) {
				writeStatsToDB();
				this.getPartnerRegister().getPartners().remove(badge);
			}
		}
	}

	/**
	 * Writes the statistics for all the partners into the Database
	 */
	public void writeStatsToDB() {

		logger.log(Level.INFO, "Writing statistics into Database");
		Database db = new Database(this.federatedRecConfiguration.statsLogDatabase, DatabaseQueryStats.values());
		for (PartnerBadge partner : this.partnerRegister.getPartners()) {
			if (partner != null) {
				PartnerBadgeStats longStats = partner.longTimeStats;
				PartnerBadgeStats shortStats = partner.shortTimeStats;
				logger.log(Level.INFO, "Writing " + partner.systemId + " statistics into Database");
				PreparedStatement updateS = db.getPreparedUpdateStatement(DatabaseQueryStats.REQUESTLOG);
				// Database Entry Style
				// ('SYSTEM_ID','REQUESTCOUNT','FAILEDREQUESTCOUNT','FAILEDREQUESTTIMEOUTCOUNT')

				if (updateS != null) {

					try {
						updateS.clearBatch();
						updateS.setString(1, partner.getSystemId());
						updateS.setInt(2, longStats.requestCount + shortStats.requestCount);
						updateS.setInt(3, longStats.failedRequestCount + shortStats.failedRequestCount);
						updateS.setInt(4, longStats.failedRequestTimeoutCount + shortStats.failedRequestTimeoutCount);
						updateS.execute();
					} catch (SQLException e) {
						logger.log(Level.INFO, "Could not write into StatsDatabase: " + e.getMessage());
					} finally {
						db.commit();
						// try {
						// db.close();
						// } catch (SQLException e) {
						// logger.log(Level.WARNING, "Could not close Database",
						// e);
						// }
					}
				} else
					logger.log(Level.INFO, "Could write into request statistics database");
				PreparedStatement updateQ = db.getPreparedUpdateStatement(DatabaseQueryStats.QUERYLOG);
				if (updateQ != null) {
					// ('ID','SYSTEM_ID','QUERY','CALLTIME','FIRSTTRANSFORMATIONTIME','SECONDTRANSFORMATIONTIME','ENRICHMENTTIME','RESULTCOUNT')
					for (ResultStats queryStats : partner.getLastQueries()) {
						try {
							updateQ.clearBatch();

							updateQ.setString(1, partner.getSystemId());
							updateQ.setString(2, queryStats.getPartnerQuery());
							updateQ.setInt(3, (int) queryStats.getPartnerCallTime());
							updateQ.setInt(4, (int) queryStats.getFirstTransformationTime());
							updateQ.setInt(5, (int) queryStats.getSecondTransformationTime());
							updateQ.setInt(6, (int) queryStats.getEnrichmentTime());
							updateQ.setInt(7, queryStats.getResultCount());
							updateQ.addBatch();

						} catch (SQLException e) {
							logger.log(Level.INFO, "Could not write into StatsDatabase: " + e.getMessage());
						}
					}
					try {
						updateQ.executeBatch();
					} catch (SQLException e) {
						logger.log(Level.INFO, "Could not write into StatsDatabase: " + e.getMessage());
					}

				} else
					logger.log(Level.INFO, "Could not write into query statistics database");
			}
		}

	}

	/**
	 * Adds the partner to the partner register if not existing allready and
	 * trys to get the old statistics for this partner from the database
	 * 
	 * @param badge
	 * @return
	 */
	public String registerPartner(PartnerBadge badge) {
		if (this.getPartnerRegister().getPartners().contains(badge)) {
			logger.log(Level.INFO, "Partner: " + badge.getSystemId() + " allready registered!");
			return "Allready Registered";
		}

		if (badge.partnerKey != null)
			if (!badge.partnerKey.isEmpty())
				if (badge.partnerKey.length() < 20)
					return "Partner Key is too short (<20)";

		Database db = new Database(this.federatedRecConfiguration.statsLogDatabase, DatabaseQueryStats.values());
		PreparedStatement getS = db.getPreparedSelectStatement(DatabaseQueryStats.REQUESTLOG);
		// Database Entry Style
		// ('SYSTEM_ID','REQUESTCOUNT','FAILEDREQUESTCOUNT','FAILEDREQUESTTIMEOUTCOUNT')

		if (getS != null) {
			try {
				getS.setString(1, badge.getSystemId());
				ResultSet rs = getS.executeQuery();
				if (rs.next()) {
					badge.longTimeStats.requestCount = rs.getInt(2);
					badge.longTimeStats.failedRequestCount = rs.getInt(3);
					badge.longTimeStats.failedRequestTimeoutCount = rs.getInt(4);
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "could net get statistics for partner " + badge.getSystemId(), e);
			}

			try {
				db.close();
			} catch (SQLException e) {
				logger.log(Level.WARNING, "Could not close Database", e);
			}
		} else
			logger.log(Level.WARNING, "Could read from Statistics Database");

		this.addPartner(badge);

		return "Partner Added";

	}

	/**
	 * gets the Partners Results and calls the given picker to aggregate results
	 * between the partners result lists
	 * 
	 * @param userProfile
	 * @return
	 * @throws FederatedRecommenderException
	 */
	public ResultList getAndAggregateResults(SecureUserProfile userProfile, String pickerName)
					throws FederatedRecommenderException {
		ResultList resultList;
		PartnersFederatedRecommendationsPicker pFRPicker = null;
		try {
			pFRPicker = (PartnersFederatedRecommendationsPicker) Class.forName(pickerName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Could not get Picker from Class: " + pickerName, e);
			throw new FederatedRecommenderException("Could not get Picker from Class: " + pickerName, e);
		}
		long start = System.currentTimeMillis();
		PartnersFederatedRecommendations pFR = getPartnersRecommendations(userProfile);
		List<PartnerResponseState> partnerResponseState = new ArrayList<PartnerResponseState>();
		for (PartnerBadge badge : pFR.getResults().keySet()) {
			partnerResponseState.addAll(pFR.getResults().get(badge).partnerResponseState);
		}
		long end = System.currentTimeMillis();
		long timeToGetPartners = end - start;
		start = System.currentTimeMillis();
		int numResults = 10;
		if (userProfile.numResults != null)
			numResults = userProfile.numResults;
		resultList = pFRPicker.pickResults(userProfile, pFR, partnerRegister.getPartners(), numResults);
		end = System.currentTimeMillis();
		long timeToPickResults = end - start;
		recommenderStats.setAverageGlobalTime(timeToGetPartners);
		recommenderStats.setAverageAggregationTime(timeToPickResults);
		logger.log(Level.INFO, " Time to get " + resultList.results.size() + " Results from the Partners: "
						+ timeToGetPartners + "ms. Time to pick the best results: " + timeToPickResults + "ms");
		resultList.totalResults = resultList.results.size();
		resultList.provider = "federated";
		resultList.partnerResponseState = partnerResponseState;
		return resultList;
	}

}
