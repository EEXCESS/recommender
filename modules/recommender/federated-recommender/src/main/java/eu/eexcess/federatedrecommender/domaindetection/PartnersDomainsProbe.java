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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.management.RuntimeErrorException;
import javax.ws.rs.core.MediaType;

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

public class PartnersDomainsProbe implements Cloneable {

    // private Logger logger =
    // Logger.getLogger(PartnersDomainsProbe.class.getName());
    private TermSet<TypedTerm> partnerDomainsCounter = null;

    protected int maxWords = 100;
    protected int maxResults = 10;
    protected DomainDetector domainDetector;
    protected Set<String> ambiguousPhrases = new HashSet<String>(maxWords);

    private final String recommendationEndpointSuffix = "recommend";

    /**
     * see {@link #PartnersDomainsProbe(DomainDetector, int, int)}
     */
    public PartnersDomainsProbe(DomainDetector domainDetector) throws RuntimeException {
        this.domainDetector = domainDetector;
        generateRandomPhrases();
    }

    /**
     * see {@link #PartnersDomainsProbe(DomainDetector, int, int)}
     */
    public PartnersDomainsProbe(DomainDetector domainDetector, int randomPhrases) throws RuntimeException {
        this.domainDetector = domainDetector;
        this.maxWords = randomPhrases;
        generateRandomPhrases();
    }

    /**
     * 
     * @param domainDetector
     *            detector to be invoked
     * @param numProbePhrases
     * @param considerNumResults
     * @throws RuntimeException
     *             if random words cannot be generated
     */
    public PartnersDomainsProbe(DomainDetector domainDetector, int numProbePhrases, int considerNumResults) throws RuntimeException {
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
    public HashSet<PartnerDomain> probePartners(Client partnerClient, PartnerBadge partner) throws DomainDetectorException {

        partnerDomainsCounter = new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger());

        for (String ambiguousPhrase : ambiguousPhrases) {

            SecureUserProfile ambiguousQuery = new SecureUserProfile();
            ambiguousQuery.contextKeywords = Arrays.asList(new ContextKeyword(ambiguousPhrase));

            ResultList partnerResult = getPartnerResult(partnerClient, partner, ambiguousQuery);
            Set<String> seenResults = new HashSet<String>();
            for (Result result : partnerResult.results.subList(0, Math.min(maxResults, partnerResult.results.size()))) {

                if (result.title != null && !result.title.trim().isEmpty()) {
                    String title = result.title.trim();
                    String titleNormalised = title.replaceAll("\\W", "").toLowerCase(Locale.US);

                    if (!seenResults.contains(titleNormalised)) {

                        Set<Domain> detectedDomains = domainDetector.detect(title);
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        PartnersDomainsProbe clone = (PartnersDomainsProbe) super.clone();
        clone.domainDetector = domainDetector;
        clone.maxResults = maxResults;
        clone.maxWords = maxWords;
        clone.ambiguousPhrases.addAll(ambiguousPhrases);
        return clone;
    }

    /**
     * Generate once ambiguous phrases to be used for all following probes.
     * 
     * @throws DomainDetectorException
     */
    private void generateRandomPhrases() throws RuntimeException {
        int tries = 0;
        while (ambiguousPhrases.size() < maxWords) {
            try {
                tries++;
                if (tries >= (maxWords * 2)) {
                    throw new RuntimeException("failed to generate random abiguous words: tried [" + tries + "] times to generate [" + maxWords
                            + "] phrases but generated only [" + ambiguousPhrases.size() + "] phrases so far");
                }
                ambiguousPhrases.add(domainDetector.drawRandomAmbiguousWord(ambiguousPhrases));
            } catch (DomainDetectorException e) {
                continue;
            }
        }
    }

    /**
     * Converts from set if {@link TypedTerm}s to set of {@link PartnerDomain}s.
     * 
     * @return the converted set
     */
    private HashSet<PartnerDomain> getProbesFromTerms(TermSet<TypedTerm> partnerProbes) {

        HashSet<PartnerDomain> partnerDomains = new HashSet<PartnerDomain>(partnerProbes.size());

        if (0 >= partnerProbes.size()) {
            return partnerDomains;
        }

        for (TypedTerm entry : partnerProbes) {
            PartnerDomain domain = new PartnerDomain(entry.getText(), entry.getWeight());
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
    private ResultList getPartnerResult(Client client, PartnerBadge partner, SecureUserProfile secureUserProfile) throws RuntimeErrorException {
        ResultList resultList = new ResultList();

        if (client != null) {
            try {
                WebResource resource = null;
                if (partner.getPartnerConnectorEndpoint().endsWith("/")) {
                    resource = client.resource(partner.getPartnerConnectorEndpoint() + recommendationEndpointSuffix);
                } else {
                    resource = client.resource(partner.getPartnerConnectorEndpoint() + "/" + recommendationEndpointSuffix);
                }
                resource.accept(MediaType.APPLICATION_JSON);
                resultList = resource.post(ResultList.class, secureUserProfile);
            } catch (Exception e) {
                throw new RuntimeException("Partner: " + partner.getSystemId() + " is not working currently.", e);
            }

        }
        return resultList;
    }
}
