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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeStats;
import eu.eexcess.dataformats.PartnerDomain;
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
import eu.eexcess.federatedrecommender.domaindetection.AsyncPartnerDomainsProbeMonitor;
import eu.eexcess.federatedrecommender.domaindetection.AsyncPartnerDomainsProbeMonitor.ProbeResultChanged;
import eu.eexcess.federatedrecommender.domaindetection.storage.PartnersDomainsTableQuery;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;
import eu.eexcess.federatedrecommender.sourceselection.WndomainSourceSelector;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
import eu.eexcess.sqlite.Database;
import eu.eexcess.sqlite.DatabaseQueryStats;

/**
 * FederatedRecommenderCore (Singleton)
 * 
 */
public class FederatedRecommenderCore implements ProbeResultChanged {

    private static final Logger LOGGER = Logger.getLogger(FederatedRecommenderCore.class.getName());
    private static volatile FederatedRecommenderCore instance;
    private final FederatedRecommenderConfiguration federatedRecConfiguration;
    private PartnerRegister partnerRegister = new PartnerRegister();
    private ExecutorService threadPool;
    private RecommenderStats recommenderStats;
    private AsyncPartnerDomainsProbeMonitor partnersDomainsDetectors;

    /**
     * references to re-usable state-less source selection instances
     */
    private Map<String, Object> statelessClassInstances = new HashMap<>();

    private FederatedRecommenderCore(FederatedRecommenderConfiguration federatedRecConfiguration) {
        threadPool = Executors.newFixedThreadPool(federatedRecConfiguration.getNumRecommenderThreads());
        this.federatedRecConfiguration = federatedRecConfiguration;
        this.recommenderStats = new RecommenderStats();
        instanciateSourceSelectors(this.federatedRecConfiguration);

        // activate partner probing only if the respective source selector is
        // requested to be applied
        String[] sourceSelectors = this.federatedRecConfiguration.getSourceSelectors();
        String domainSelectorName = WndomainSourceSelector.class.getCanonicalName();
        if (sourceSelectors != null && Arrays.asList(sourceSelectors).contains(domainSelectorName)) {
            LOGGER.info("activating partner domaindetection since [" + domainSelectorName + "] is requested to be applied");
            partnersDomainsDetectors = new AsyncPartnerDomainsProbeMonitor(new File(this.federatedRecConfiguration.getWordnetPath()), new File(
                    this.federatedRecConfiguration.getWordnetDomainFilePath()), 50, 10, new Double(0.8 * 2000000).intValue());

            partnersDomainsDetectors.setCallback(this);
        } else {
            LOGGER.info("refused to activate partner domaindetection since [" + domainSelectorName + "] is not requested to be applied");
        }
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
    public static FederatedRecommenderCore getInstance(FederatedRecommenderConfiguration federatedRecommenderConfiguration) throws FederatedRecommenderException {
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
        List<PartnerBadge> partners = getPartnerRegister().getPartners();
        for (final PartnerBadge partner : partners) {
            if (checkUserSelectedPartners(secureUserProfile, partner)) {
                final Client tmpClient = partnerRegister.getClient(partner.getSystemId());

                Future<ResultList> future = threadPool.submit(new Callable<ResultList>() {
                    @Override
                    public ResultList call() throws UniformInterfaceException, ClientHandlerException {
                        long startTime = System.currentTimeMillis();
                        ResultList resultList = new ResultList();
                        if (tmpClient != null) {
                            resultList = getPartnerRecommendationResult(partner, tmpClient, secureUserProfile);
                            tmpClient.destroy();
                        }
                        long endTime = System.currentTimeMillis();
                        long respTime = endTime - startTime;
                        partner.updatePartnerResponseTime(respTime);
                        return resultList;
                    }
                });
                futures.put(partner, future);

            }
        }

        long timeout = federatedRecConfiguration.getPartnersTimeout(); // ms
        for (Entry<PartnerBadge, Future<ResultList>> entry : futures.entrySet()) {
            long startT = System.currentTimeMillis();
            try {

                entry.getKey().getShortTimeStats().requestCount++;
                ResultList rL = entry.getValue().get(timeout, TimeUnit.MILLISECONDS);
                PartnerResponseState responseState = new PartnerResponseState();

                responseState.success = true;
                responseState.systemID = entry.getKey().getSystemId();
                rL.partnerResponseState.add(responseState);

                entry.getValue().cancel(true);
                partnersFederatedResults.getResults().put(entry.getKey(), rL);
                entry.getKey().addLastQueries(rL.getResultStats());

                timeout -= System.currentTimeMillis() - startT;
                timeout = timeout - (System.currentTimeMillis() - startT);

            } catch (TimeoutException e) {
                entry.getKey().getShortTimeStats().failedRequestCount++;
                entry.getKey().getShortTimeStats().failedRequestTimeoutCount++;
                entry.getValue().cancel(true);

                timeout -= System.currentTimeMillis() - startT;
                String msg = "Waited too long for partner system '" + entry.getKey().getSystemId() + "' to respond " + (federatedRecConfiguration.getPartnersTimeout() - timeout)
                        + " ms ";
                ResultList rL = new ResultList();
                PartnerResponseState responseState = new PartnerResponseState();
                responseState.errorMessage = msg;
                responseState.success = false;
                responseState.systemID = entry.getKey().getSystemId();
                rL.partnerResponseState.add(responseState);
                partnersFederatedResults.getResults().put(entry.getKey(), rL);
                LOGGER.log(Level.WARNING, msg, e);
            } catch (Exception e) {
                if (entry.getKey() != null) {
                    entry.getKey().getShortTimeStats().failedRequestCount++;
                    entry.getValue().cancel(true);
                    timeout -= System.currentTimeMillis() - startT;
                }
                String msg = "Failed to retrieve results from a parter system '" + entry.getKey().getSystemId();
                PartnerResponseState responseState = new PartnerResponseState();
                responseState.errorMessage = msg;
                responseState.success = false;
                responseState.systemID = entry.getKey().getSystemId();
                ResultList rL = new ResultList();
                rL.partnerResponseState.add(responseState);
                LOGGER.log(Level.SEVERE, msg, e);
            }
            entry.setValue(null);

        }
        long end = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "Federated Recommender took " + (end - start) + "ms for query '" + secureUserProfile.contextKeywords + "'");

        return partnersFederatedResults;
    }

    /**
     * main function to generate a federated recommendation
     * 
     * @return
     */
    public ResultList generateFederatedRecommendation(SecureUserProfile secureUserProfile) throws FileNotFoundException {
        ResultList resultList = null;
        SecureUserProfile secureUserProfileTmp = secureUserProfile;
        if (federatedRecConfiguration.getSourceSelectors() != null) {
            List<String> sourceSelectors = new ArrayList<String>();
            Collections.addAll(sourceSelectors, federatedRecConfiguration.getSourceSelectors());
            secureUserProfileTmp = sourceSelection(secureUserProfileTmp, sourceSelectors);
        }
        try {
            resultList = getAndAggregateResults(secureUserProfileTmp, this.federatedRecConfiguration.getDefaultPickerName());
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.SEVERE, "Some error retrieving or aggregation results occured.", e);
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
            final Client tmpClient = partnerRegister.getClient(partner.getSystemId());
            DocumentBadgeList currentDocs = filterDocuments(documents, (DocumentBadge document) -> partner.getSystemId().equals(document.provider));
            Future<DocumentBadgeList> future = threadPool.submit(new Callable<DocumentBadgeList>() {
                @Override
                public DocumentBadgeList call() throws Exception {
                    return getDocsResult(partner, tmpClient, currentDocs);
                }

                /**
                 * trys to recieve the results from the partners
                 */
                private DocumentBadgeList getDocsResult(PartnerBadge partner, Client client, DocumentBadgeList currentDocs) throws UniformInterfaceException,
                        ClientHandlerException {
                    DocumentBadgeList docList = getPartnerDetailsResult(partner, client, currentDocs);
                    client.destroy();
                    return docList;
                }
            });
            futures.put(partner, future);
        }
        DocumentBadgeList resultDocs = new DocumentBadgeList();
        long timeout = federatedRecConfiguration.getPartnersTimeout();
        for (PartnerBadge partner : futures.keySet()) {
            try {
                resultDocs.documentBadges.addAll(futures.get(partner).get(timeout, TimeUnit.MILLISECONDS).documentBadges);
            } catch (TimeoutException e) {
                LOGGER.log(Level.WARNING, "Parnter " + partner.getSystemId() + " timed out for document detail call", e);
            } catch (ExecutionException | InterruptedException e) {
                LOGGER.log(Level.WARNING, "Can not get detail results from parnter:" + partner.getSystemId(), e);
            }
        }
        return resultDocs;
    }

    /**
     * calls the given query expansion algorithm
     * 
     * @param userProfile
     * @return
     */
    @SuppressWarnings("unchecked")
    public SecureUserProfile addQueryExpansionTerms(SecureUserProfileEvaluation userProfile, String qEClass) {
        SecureUserProfileDecomposer<SecureUserProfile, SecureUserProfile> sUPDecomposer = null;
        try {
            sUPDecomposer = (SecureUserProfileDecomposer<SecureUserProfile, SecureUserProfile>) Class.forName(qEClass).newInstance();
            sUPDecomposer.setConfiguration(federatedRecConfiguration);
        } catch (InstantiationException | FederatedRecommenderException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Could not initalize query expansion algorithm", e);
        }
        if (sUPDecomposer == null)
            return userProfile;
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
    public SecureUserProfile sourceSelection(SecureUserProfile userProfile, List<String> selectorsClassNames) {

        if (selectorsClassNames == null || selectorsClassNames.isEmpty()) {
            return userProfile;
        }

        SecureUserProfile lastEvaluatedProfile = userProfile;
        for (String sourceSelectorClassName : selectorsClassNames) {
            PartnerSelector sourceSelector = (PartnerSelector) statelessClassInstances.get(sourceSelectorClassName);
            if (null == sourceSelector) {
                LOGGER.info("failed to find requested source selector [" + sourceSelectorClassName + "]: ignoring source selection");
            } else {
                lastEvaluatedProfile = sourceSelector.sourceSelect(lastEvaluatedProfile, getPartnerRegister().getPartners());
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

        LOGGER.log(Level.INFO, "Writing statistics into Database");
        Database<DatabaseQueryStats> db = new Database<DatabaseQueryStats>(this.federatedRecConfiguration.getStatsLogDatabase(), DatabaseQueryStats.values());
        for (PartnerBadge partner : this.partnerRegister.getPartners()) {
            if (partner != null) {
                PartnerBadgeStats longStats = partner.longTimeStats;
                PartnerBadgeStats shortStats = partner.getShortTimeStats();
                LOGGER.log(Level.INFO, "Writing " + partner.getSystemId() + " statistics into Database");
                final String dbErrorMsg = "Could not write into StatsDatabase: ";
                writeRequestStatsToDb(db, partner, longStats, shortStats, dbErrorMsg);
                writeQueryStatsToDb(db, partner, dbErrorMsg);
            }
        }
        try {
            db.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to close database", e);
        }
    }

    private void writeQueryStatsToDb(Database<DatabaseQueryStats> db, PartnerBadge partner, final String dbErrorMsg) {
        PreparedStatement updateQ = db.getPreparedUpdateStatement(DatabaseQueryStats.QUERYLOG);
        if (updateQ != null) {
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
                    LOGGER.log(Level.INFO, dbErrorMsg, e);
                }
            }
            try {
                updateQ.executeBatch();
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, dbErrorMsg, e);
            }

        } else
            LOGGER.log(Level.INFO, "Could not write into query statistics database");
    }

    private String writeRequestStatsToDb(Database<DatabaseQueryStats> db, PartnerBadge partner, PartnerBadgeStats longStats, PartnerBadgeStats shortStats, String dbErrorMsg) {
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
                LOGGER.log(Level.INFO, dbErrorMsg, e);
            } finally {
                db.commit();
            }
        } else
            LOGGER.log(Level.INFO, "Could write into request statistics database");
        return dbErrorMsg;
    }

    /**
     * Adds the partner to the partner register if not existing already and
     * tries to get the old statistics for this partner from the database.
     * Adding a new partner also triggers domain detection if the respective
     * partner does not explicitly define domains.
     * 
     * @param badge
     *            partner information
     * @return an informative human readable message
     */
    public String registerPartner(PartnerBadge badge) {
        if (this.getPartnerRegister().getPartners().contains(badge)) {
            LOGGER.log(Level.INFO, "Partner: " + badge.getSystemId() + " allready registered!");
            return "Allready Registered";
        }

        if (badge.getPartnerKey() != null && !badge.getPartnerKey().isEmpty() && badge.getPartnerKey().length() < 20)
            return "Partner Key is too short (<20)";

        Database<DatabaseQueryStats> db = new Database<DatabaseQueryStats>(this.federatedRecConfiguration.getStatsLogDatabase(), DatabaseQueryStats.values());
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
                LOGGER.log(Level.SEVERE, "could net get statistics for partner " + badge.getSystemId(), e);
            }

            try {
                db.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not close Database", e);
            }
        } else
            LOGGER.log(Level.WARNING, "Could read from Statistics Database");

        this.addPartner(badge);

        // if recommender has domain detectors instanced but partner does not
        // provide any domain information
        if (null != partnersDomainsDetectors && (null == badge.getDomainContent() || badge.getDomainContent().isEmpty())) {

            if (false == restoreDomainsFromDatabase(badge)) {
                // if no domain informations can be retrieved from database
                // start domain detectors
                partnersDomainsDetectors.probe(badge, getPartnerRegister().getClient(badge.getSystemId()));
            }
        }
        return "Partner Added";

    }

    /**
     * Reads domain(s) information of the respective partner from database and
     * stores it back to {@link PartnerBadge}.
     * 
     * @param partnerConfig
     *            supplies the {@link PartnerBadge#getSystemId()} and where to
     *            store {@link PartnerBadge#getDomainContent()} the restored
     *            domain information
     * @return false if no domain was restored from database
     */
    private boolean restoreDomainsFromDatabase(PartnerBadge partnerConfig) {
        synchronized (partnerRegister) {
            boolean hasDomainsRestored = false;
            Database<PartnersDomainsTableQuery> db = new Database<PartnersDomainsTableQuery>(federatedRecConfiguration.getStatsLogDatabase(), PartnersDomainsTableQuery.values());
            PreparedStatement selectStatement = db.getPreparedSelectStatement(PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY);

            try {
                selectStatement.setString(1, partnerConfig.getSystemId());
                if (true == selectStatement.execute()) {
                    ResultSet results = selectStatement.getResultSet();

                    if (partnerConfig.getDomainContent() == null) {
                        partnerConfig.setDomainContent(new ArrayList<PartnerDomain>());
                    }

                    int restoredDomainsCount = 0;
                    while (results.next()) {
                        restoredDomainsCount++;
                        PartnerDomain domain = new PartnerDomain();
                        domain.domainName = results.getString(PartnersDomainsTableQuery.Tables.PartnerProbes.Domains.DOMAIN_NAME.ROW_NUMBER);
                        domain.weight = results.getDouble(PartnersDomainsTableQuery.Tables.PartnerProbes.Domains.DOMAIN_WEIGHT.ROW_NUMBER);
                        hasDomainsRestored = true;
                        partnerConfig.getDomainContent().add(domain);
                    }
                    LOGGER.info("restored [" + restoredDomainsCount + "] domain(s) of partner [" + partnerConfig.getSystemId() + "] from database");
                }
            } catch (SQLException sqe) {
                LOGGER.log(Level.SEVERE, "failed to retrieve partner's domain information from database [" + PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY.getInternName()
                        + "]");
                sqe.printStackTrace();
            } finally {
                try {
                    db.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "failed to close database [" + PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY.getInternName() + "]", e);
                }
            }
            return hasDomainsRestored;
        }
    }

    /**
     * gets the Partners Results and calls the given picker to aggregate results
     * between the partners result lists
     * 
     * @param userProfile
     * @return
     * @throws FederatedRecommenderException
     */
    public ResultList getAndAggregateResults(SecureUserProfile userProfile, String pickerName) throws FederatedRecommenderException {
        ResultList resultList;
        PartnersFederatedRecommendationsPicker pFRPicker = null;
        try {
            Object newInstance = statelessClassInstances.get(pickerName);
            if (newInstance == null) {
                newInstance = Class.forName(pickerName).newInstance();
                statelessClassInstances.put(pickerName, newInstance);
            }
            pFRPicker = (PartnersFederatedRecommendationsPicker) newInstance;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not get Picker from Class: " + pickerName, e);
            throw new FederatedRecommenderException("Could not get Picker from Class: " + pickerName, e);
        }
        long start = System.currentTimeMillis();
        PartnersFederatedRecommendations pFR = getPartnersRecommendations(userProfile);
        List<PartnerResponseState> partnerResponseState = new ArrayList<PartnerResponseState>();

        for (PartnerBadge badge : pFR.getResults().keySet()) {
            if (badge != null && pFR != null && !pFR.getResults().isEmpty() && pFR.getResults().get(badge) != null)
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
        LOGGER.log(Level.INFO, " Time to get " + resultList.results.size() + " Results from the Partners: " + timeToGetPartners + "ms. Time to pick the best results: "
                + timeToPickResults + "ms");
        resultList.totalResults = resultList.results.size();
        resultList.provider = "federated";
        resultList.partnerResponseState = partnerResponseState;
        return resultList;
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
                if (partner.getPartnerKey() != null && !partner.getPartnerKey().isEmpty())
                    withKey = true;
                if (!withKey)
                    for (PartnerBadge uBadge : secureUserProfile.partnerList) {
                        if (uBadge.getSystemId().equals(partner.getSystemId()))
                            return true;
                    }
                else
                    for (PartnerBadge uBadge : secureUserProfile.protectedPartnerList) {
                        if (uBadge.getPartnerKey() != null && !uBadge.getPartnerKey().isEmpty() && partner.getPartnerKey().equals(uBadge.getPartnerKey())
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

    private void instanciateSourceSelectors(FederatedRecommenderConfiguration recommenderConfig) {

        List<String> selectorsClassNames = new ArrayList<String>();

        if (null == recommenderConfig.getSourceSelectors()) {
            LOGGER.info("failed to instanciate source selectors");
            return;
        }

        Collections.addAll(selectorsClassNames, recommenderConfig.getSourceSelectors());

        for (String sourceSelectorClassName : selectorsClassNames) {
            try {
                PartnerSelector sourceSelector = (PartnerSelector) statelessClassInstances.get(sourceSelectorClassName);
                if (null == sourceSelector) {
                    Constructor<?> ctor = Class.forName(sourceSelectorClassName).getConstructor(FederatedRecommenderConfiguration.class);

                    sourceSelector = (PartnerSelector) ctor.newInstance(recommenderConfig);
                    LOGGER.info("instanciating new source selector [" + sourceSelector.getClass().getSimpleName() + "]");
                    statelessClassInstances.put(sourceSelectorClassName, sourceSelector);
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.log(Level.SEVERE, "failed to instanciate source selector [" + sourceSelectorClassName + "]", e);
            }
        }
    }

    /**
     * Stores partner domains mapping to {@link #partnerRegister} and database.
     * 
     * @param updatedProbes
     *            the latest known {@link PartnerBadge} to {@link PartnerDomain}
     *            mapping.
     */
    @Override
    public void onProbeResultsChanged(Map<String, Set<PartnerDomain>> updatedProbes) {
        synchronized (partnerRegister) {
            Database<PartnersDomainsTableQuery> db = new Database<PartnersDomainsTableQuery>(federatedRecConfiguration.getStatsLogDatabase(), PartnersDomainsTableQuery.values());
            PreparedStatement deleteStatement = db.getPreparedDeleStatement(PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY);
            PreparedStatement insertStatement = db.getPreparedInsertStatement(PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY);

            try {
                for (PartnerBadge partner : partnerRegister.getPartners()) {

                    Set<PartnerDomain> partnerDomains = updatedProbes.get(partner.getSystemId());
                    if (null != partnerDomains) {
                        // apply changes to memory
                        partner.setDomainContent(new ArrayList<PartnerDomain>(partnerDomains));

                        // apply changes to database
                        if (deleteStatement != null) {
                            try {
                                deleteStatement.clearParameters();
                                deleteStatement.setString(1, partner.getSystemId());
                                deleteStatement.execute();

                                insertStatement.clearBatch();
                                for (PartnerDomain domain : partnerDomains) {
                                    insertStatement.setString(1, partner.getSystemId());
                                    insertStatement.setLong(2, System.currentTimeMillis());
                                    insertStatement.setString(3, domain.domainName);
                                    insertStatement.setDouble(4, domain.weight);
                                    insertStatement.addBatch();
                                }
                                insertStatement.executeBatch();
                                db.commit();

                                // make some noise about the batch result(s)
                                int index = 0;
                                int numFailed = 0;
                                for (Integer resultStatus : insertStatement.executeBatch()) {
                                    if (PreparedStatement.EXECUTE_FAILED == resultStatus) {
                                        LOGGER.warning("failed to execute batch [" + index + "/" + partnerDomains.size() + "] of partner [" + partner.getSystemId() + "]");
                                        numFailed++;
                                    }
                                    index++;
                                }
                                LOGGER.info("stored [" + (partnerDomains.size() - numFailed) + "/" + partnerDomains.size() + "] domains of partner [" + partner.getSystemId()
                                        + "] to database");

                            } catch (SQLException e) {
                                LOGGER.log(Level.SEVERE, "failed storing partners domains do database [" + PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY.getInternName()
                                        + "]", e);
                            }

                        } else {
                            LOGGER.log(Level.WARNING, "failed retrieving an expected prepared statement of [" + PartnersDomainsTableQuery.class.getName() + "]");
                        }
                    }
                }
            } finally {
                try {
                    db.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "failed to close database [" + PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY.getInternName() + "]", e);
                }
            }
        }
    }

    private DocumentBadgeList getPartnerDetailsResult(PartnerBadge partner, Client client, DocumentBadgeList currentDocs) throws UniformInterfaceException, ClientHandlerException {
        DocumentBadgeList docList = new DocumentBadgeList();
        if (client != null) {
            try {
                WebResource resource = client.resource(partner.getPartnerConnectorEndpoint() + "getDetails");
                resource.accept(MediaType.APPLICATION_JSON);
                docList = resource.post(DocumentBadgeList.class, currentDocs);
            } catch (UniformInterfaceException | ClientHandlerException e) {
                LOGGER.log(Level.WARNING, "Partner: " + partner.getSystemId() + " is not working currently.", e);
                throw e;
            }
        }
        return docList;
    }

    private ResultList getPartnerRecommendationResult(PartnerBadge partner, Client client, SecureUserProfile secureUserProfile) throws UniformInterfaceException,
            ClientHandlerException {
        ResultList resultList = new ResultList();
        try {
            WebResource resource = client.resource(partner.getPartnerConnectorEndpoint() + "recommend");
            resource.accept(MediaType.APPLICATION_JSON);
            resultList = resource.post(ResultList.class, secureUserProfile);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            LOGGER.log(Level.WARNING, "Partner: " + partner.getSystemId() + " is not working currently.", e);
            throw e;
        }
        return resultList;
    }

}
