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

package eu.eexcess.federatedrecommender.domaindetection;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.jersey.api.client.Client;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.domaindetection.AsyncPartnerDomainsProbe.ProbeDoneCallback;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.probing.PartnerDomainsProbe;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;

public class AsyncPartnerDomainsProbeMonitor implements ProbeDoneCallback {

    public static interface ProbeResultChanged {
        /**
         * passes all probes after changes hat occurred
         * 
         * @param updatedProbes
         *            all probes including changes
         */
        public void onProbeResultsChanged(Map<String, Set<PartnerDomain>> updatedProbes);
    }

    private DomainDetector domainDetector;
    private PartnerDomainsProbe probeTemplate;
    private long probeTimeout = 0;
    private static final Logger logger = Logger.getLogger(AsyncPartnerDomainsProbeMonitor.class.getName());
    protected Map<String, AsyncPartnerDomainsProbe> runningProbes = new HashMap<String, AsyncPartnerDomainsProbe>();
    private Map<String, Set<PartnerDomain>> partnersDomains = new HashMap<String, Set<PartnerDomain>>();
    private ProbeResultChanged onDomainsChangedCallback;

    public AsyncPartnerDomainsProbeMonitor(File wordnetDir, File wordNetFile, int numRandomPhrases, int considerNumPartnerResults, long probeDurationTimeout) {
        probeTimeout = probeDurationTimeout;
        try {
            domainDetector = new WordnetDomainsDetector(wordnetDir, wordNetFile, true);
            probeTemplate = new PartnerDomainsProbe(domainDetector, numRandomPhrases, considerNumPartnerResults);
        } catch (Exception e) {
            logger.severe("failed to instanciate domain detection resources: " + e.getMessage());
        }
    }

    private AsyncPartnerDomainsProbe newAsyncPartnerDomainsProbe(PartnerBadge partnerConfig, Client partnerClient) {
        if (probeTemplate == null) {
            logger.severe("failed to instanciate domain detection resources");
            return null;
        }
        try {
            return new AsyncPartnerDomainsProbe(partnerConfig, partnerClient, probeTemplate, probeTimeout);
        } catch (CloneNotSupportedException e) {
            logger.severe("failed to create new instance of partner probe because of unsopported clone(): " + e.getMessage());
            return null;
        }
    }

    public void probe(PartnerBadge partnerConfig, Client partnerClient) {
        synchronized (runningProbes) {
            if (runningProbes.containsKey(partnerConfig.getSystemId())) {
                logger.info("refusing to start probe for partner [" + partnerConfig.getSystemId() + "] because found already running probe");
                return;
            }

            AsyncPartnerDomainsProbe probe = newAsyncPartnerDomainsProbe(partnerConfig, partnerClient);
            if (null != probe) {
                runningProbes.put(partnerConfig.getSystemId(), probe);
                probe.addCallback(this);
                probe.probeAsyncronous();
            } else {
                logger.info("refusing to start probe for partner [" + partnerConfig.getSystemId() + "] because no resources available");
            }
        }
    }

    /**
     * Callback when partner's probe is ready.
     */
    @Override
    public void onProbeDoneCallback(String partnerId, Set<PartnerDomain> probeResult) {
        synchronized (runningProbes) {
            if (null == runningProbes.remove(partnerId)) {
                logger.warning("failed to remove booked probe of partner [" + partnerId + "]");
            } else {
                logger.info("remove booked partner probe [" + partnerId + "]");
            }
            partnersDomains.put(partnerId, probeResult);
            if (null != onDomainsChangedCallback) {
                onDomainsChangedCallback.onProbeResultsChanged(getPartnersToDomainsCopy());
            }
        }
    }

    /**
     * Callback when partner's callback fails.
     */
    @Override
    public void onProbeFailedCallback(String partnerId) {
        synchronized (runningProbes) {
            logger.warning("removing [" + partnerId + "] probe due to error");
            if (null == runningProbes.remove(partnerId)) {
                logger.severe("failed to remove probe due to error: [" + partnerId + "] probe not found");
            }
        }
    }

    /**
     * @return a new copy of the probed partner to domains mapping
     */
    private Map<String, Set<PartnerDomain>> getPartnersToDomainsCopy() {
        synchronized (runningProbes) {
            Map<String, Set<PartnerDomain>> newMap = new HashMap<String, Set<PartnerDomain>>(runningProbes.size());
            for (Map.Entry<String, Set<PartnerDomain>> entry : partnersDomains.entrySet()) {

                Set<PartnerDomain> newEntrySet = new HashSet<PartnerDomain>(entry.getValue().size());

                for (PartnerDomain domain : entry.getValue()) {
                    newEntrySet.add(new PartnerDomain(domain.domainName, domain.weight));
                }
                newMap.put(entry.getKey(), newEntrySet);
            }
            return newMap;
        }
    }

    /**
     * sets/unsets the one and only callback to be called on
     * {@link ProbeResultChanged#onProbeResultsChanged(Map)}
     * 
     * @param callback
     *            if != null the new callback, else removes the callback
     */
    public void setCallback(ProbeResultChanged callback) {
        onDomainsChangedCallback = callback;
    }
}
