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

package eu.eexcess.federatedrecommender.domaindetection.probing;

import at.knowcenter.util.term.TermSet;
import at.knowcenter.util.term.TypedTerm;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

import javax.management.RuntimeErrorException;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class performs probing of a {@link PartnerBadge} using random phrases
 * for generating search results. These are then examined by a domain detector
 * to receive the partner's domains. For multi-threaded probing, cloned
 * instances can be used.
 * 
 * @author Raoul Rubien
 *
 */
public class PartnerDomainsProbe implements Cloneable {

    private static final Logger LOGGER = Logger.getLogger(PartnerDomainsProbe.class.getName());
    private static final String RECOMMENDATION_ENDPOINT_SUFFIX = "recommend";
    /**
     * default number of random phrases to generate once
     */
    protected int maxWords = 100;
    /**
     * default maximum amount of partner results to consider for domain
     * detection
     */
    protected int maxResults = 10;
    protected DomainDetector domainDetector;
    protected Set<String> ambiguousPhrases = new HashSet<String>(maxWords);
    private CancelProbeCondition canelCondition;

    public PartnerDomainsProbe(DomainDetector domainDetector) /*
                                                               * throws
                                                               * RuntimeException
                                                               */{
        this.domainDetector = domainDetector;
        generateRandomPhrases();
    }

    public PartnerDomainsProbe(DomainDetector domainDetector, int numProbePhrases) /*
                                                                                    * throws
                                                                                    * RuntimeException
                                                                                    */{
        this.domainDetector = domainDetector;
        this.maxWords = numProbePhrases;
        generateRandomPhrases();
    }

    /**
     *
     * @param domainDetector
     *            detector to be invoked
     * @param numProbePhrases
     *            number of random phrases to generate for later probing (~50 phrases take ~15 seconds)
     * @param considerNumResults
     *            number of partner results to consider for domain detection
     * @throws RuntimeException
     *             if random words cannot be generated
     */
    public PartnerDomainsProbe(DomainDetector domainDetector, int numProbePhrases, int considerNumResults) /*
                                                                                                            * throws
                                                                                                            * RuntimeException
                                                                                                            */{
        this.domainDetector = domainDetector;
        this.maxWords = numProbePhrases;
        this.maxResults = considerNumResults;
        generateRandomPhrases();
    }

    /**
     * Probes all partners from {@code #partnerRegistration} with
     * {@code #maxWords} random ambiguous words considering {@code #maxResults}.
     * Received results are used for domain detection.
     *
     * @return mapping of {@link PartnerBadge}s to {@link PartnerDomain}s
     * @throws DomainDetectorException
     *             on exceptions during
     *             {@link DomainDetector#drawRandomAmbiguousWord(Set)} or
     *             {@link DomainDetector#detect(String)}
     */
    public Set<PartnerDomain> probePartner(Client partnerClient, PartnerBadge partner) throws DomainDetectorException {

        TermSet<TypedTerm> partnerDomainsCounter = new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger());

        for (String ambiguousPhrase : ambiguousPhrases) {
            if (isToBeAborted()) {
                break;
            }
            SecureUserProfile ambiguousQuery = new SecureUserProfile();
            ambiguousQuery.setContextKeywords(Arrays.asList(new ContextKeyword(ambiguousPhrase)));

            ResultList partnerResult = getPartnerResult(partnerClient, partner, ambiguousQuery);
            Set<String> seenResults = new HashSet<String>();
            for (Result result : partnerResult.results.subList(0, Math.min(maxResults, partnerResult.results.size()))) {
                if (isToBeAborted()) {
                    break;
                }
                if (result.title != null && !result.title.trim().isEmpty()) {
                    String title = result.title.trim();
                    String titleNormalised = title.replaceAll("\\W", "").toLowerCase(Locale.US);

                    if (!seenResults.contains(titleNormalised)) {
                        Set<Domain> detectedDomains = null;
                        synchronized (domainDetector) {
                            detectedDomains = domainDetector.detect(title);
                        }
                        for (Domain domain : detectedDomains) {
                            partnerDomainsCounter.add(new TypedTerm(domain.getName(), null, 1f));
                        }
                        seenResults.add(titleNormalised);
                    }
                }
            }
        }
        return getProbesFromTerms(partnerDomainsCounter);
    }

    /**
     * Clones this instance but uses the same {@link #domainDetector} reference,
     * since this object is expensive in memory and construction is time
     * consuming.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PartnerDomainsProbe clone = (PartnerDomainsProbe) super.clone();
        clone.domainDetector = domainDetector;
        clone.maxResults = maxResults;
        clone.maxWords = maxWords;
        clone.ambiguousPhrases.addAll(ambiguousPhrases);
        return clone;
    }

    /**
     * Set the one and only condition that will be checked whenever a new query
     * is sent to the partner for probing. If
     * {@link CancelProbeCondition#isProbeToBeCancelled()} == true probing will
     * terminate after the currently request.
     *
     * @param condition
     */
    public void setCondition(CancelProbeCondition condition) {
        canelCondition = condition;
    }

    public void removeCondition() {
        canelCondition = null;
    }

    /**
     * Condition when probing is to be aborted.
     *
     * @return
     */
    private boolean isToBeAborted() {
        boolean toBeCanceled = false;

        if (canelCondition != null) {
            toBeCanceled = canelCondition.isProbeToBeCancelled();
        }
        return toBeCanceled;
    }

    /**
     * Generate once ambiguous phrases to be used for all following probes.
     *
     * @throws DomainDetectorException
     */
    private void generateRandomPhrases() /* throws RuntimeException */{
        int tries = 0;
        long startTimestamp = System.currentTimeMillis();
        while (ambiguousPhrases.size() < maxWords) {
            try {
                tries++;
                if (tries >= (maxWords * 2)) {
                    throw new RuntimeException("failed to generate random abiguous words: tried [" + tries + "] times to generate [" + maxWords
                            + "] phrases but generated only [" + ambiguousPhrases.size() + "] phrases so far");
                }
                ambiguousPhrases.add(domainDetector.drawRandomAmbiguousWord(ambiguousPhrases));
            } catch (DomainDetectorException e) {
                LOGGER.log(Level.SEVERE, "failed to draw random phrase and going to retry", e);
                continue;
            }
        }

        long delayMs = System.currentTimeMillis() - startTimestamp;
        if (delayMs > (3 * 1000)) {
            LOGGER.info("drawing [" + maxWords + "] random phrases took [" + delayMs + "ms]");
        }
    }

    /**
     * Converts from set if {@link TypedTerm}s to set of {@link PartnerDomain}s.
     *
     * @return the converted set
     */
    private Set<PartnerDomain> getProbesFromTerms(TermSet<TypedTerm> partnerProbes) {

        Set<PartnerDomain> partnerDomains = new HashSet<PartnerDomain>(partnerProbes.size());

        if (partnerProbes.isEmpty()) {
            return partnerDomains;
        }

        for (TypedTerm entry : partnerProbes) {
            PartnerDomain domain = new PartnerDomain(entry.getText().toLowerCase(), entry.getWeight());
            partnerDomains.add(domain);
        }

        return partnerDomains;
    }

    /**
     * Sends a secure user profile request to a partner.
     *
     * @param client
     *            client to use for communication
     * @param partner
     *            the partner to query
     * @param secureUserProfile
     * @return the received result list
     * @throws RuntimeErrorException
     *             if an exception occurs
     */
    private ResultList getPartnerResult(Client client, PartnerBadge partner, SecureUserProfile secureUserProfile) /*
                                                                                                                   * throws
                                                                                                                   * RuntimeException
                                                                                                                   */{
        ResultList resultList = new ResultList();

        if (client != null) {
            try {
                WebResource resource = null;
                if (partner.getPartnerConnectorEndpoint().endsWith("/")) {
                    resource = client.resource(partner.getPartnerConnectorEndpoint() + RECOMMENDATION_ENDPOINT_SUFFIX);
                } else {
                    resource = client.resource(partner.getPartnerConnectorEndpoint() + "/" + RECOMMENDATION_ENDPOINT_SUFFIX);
                }
                resource.accept(MediaType.APPLICATION_JSON);
                resultList = resource.post(ResultList.class, secureUserProfile);
            } catch (Exception e) {
                throw new RuntimeException("Partner: " + partner.getSystemId() + " is not working currently.", e);
            }

        }
        return resultList;
    }

    /**
     * Interface that is prompted during probing to check whether the current
     * probing has to be aborted or not.
     *
     * @author Raoul Rubien
     */
    public interface CancelProbeCondition {
        boolean isProbeToBeCancelled();
    }
}
