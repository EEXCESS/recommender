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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
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
import eu.eexcess.federatedrecommender.registration.PartnerRegister;

public class PartnersDomainsProbe {

    // private Logger logger =
    // Logger.getLogger(PartnersDomainsProbe.class.getName());
    private PartnerRegister partnerRegistration;
    private Map<PartnerBadge, TermSet<TypedTerm>> partnerToDomainProbes = null;

    private int maxWords = 100;
    private int maxResults = 10;
    private DomainDetector domainDetector;

    private final String recommendationEndpoint = "recommend";

    /**
     * @param partners
     *            partners to probe
     * @param domainDetector
     *            detector to be invoked
     */
    public PartnersDomainsProbe(PartnerRegister partners, DomainDetector domainDetector) {
        this.partnerRegistration = partners;
        this.domainDetector = domainDetector;
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
    public Map<PartnerBadge, HashSet<PartnerDomain>> probePartners() throws DomainDetectorException {

        partnerToDomainProbes = newPartnerDomainCounter(partnerRegistration);
        Set<String> seenWords = new HashSet<String>();

        for (int i = 0; i < maxWords; i++) {
            String ambiguousPhrase = domainDetector.drawRandomAmbiguousWord(seenWords);
            // logger.info("searching for ambiguous phrase '" + ambiguousPhrase
            // + "' (" + (i + 1) + "/" + maxWords + ")");
            seenWords.add(ambiguousPhrase);

            SecureUserProfile sup = new SecureUserProfile();
            sup.contextKeywords = Arrays.asList(new ContextKeyword(ambiguousPhrase));

            for (PartnerBadge partner : partnerRegistration.getPartners()) {
                System.out.println("Searching partner '" + partner.getSystemId() + "'");

                TermSet<TypedTerm> domainToCount = partnerToDomainProbes.get(partner);
                ResultList partnerResult = getPartnerResult(partner, sup);
                Set<String> seenResults = new HashSet<String>();
                for (Result result : partnerResult.results.subList(0, Math.min(maxResults, partnerResult.results.size()))) {
                    // System.out.println("Got result '" + result.title + "'");

                    if (result.title != null && !result.title.trim().isEmpty()) {
                        String title = result.title.trim();
                        String titleNormalised = title.replaceAll("\\W", "").toLowerCase(Locale.US);

                        if (!seenResults.contains(titleNormalised)) {

                            Set<Domain> detectedDomains = domainDetector.detect(title);
                            for (Domain domain : detectedDomains) {
                                domainToCount.add(new TypedTerm(domain.getName(), null, 1f));
                            }
                            // logger.info("domains for '" + title + "' -> '" +
                            // detectedDomains + "'");
                            seenResults.add(titleNormalised);
                        }
                    }
                }
            }
            // logger.info("detected domains:");
            // for (Entry<PartnerBadge, TermSet<TypedTerm>> e :
            // partnerToDomainProbes.entrySet()) {
            // logger.info(e.getKey().getSystemId() + " -> " + e.getValue());
            // }
        }
        return getProbes(partnerToDomainProbes);
    }

    public void setMaxWords(int maxWords) {
        this.maxWords = maxWords;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Returns the resulted mapping of {@link #probePartners()}.
     * 
     * @return the mapping of partners to their corresponding domains and domain
     *         weights
     */
    private Map<PartnerBadge, HashSet<PartnerDomain>> getProbes(Map<PartnerBadge, TermSet<TypedTerm>> partnerProbes) {

        Map<PartnerBadge, HashSet<PartnerDomain>> partnerDomains = new HashMap<PartnerBadge, HashSet<PartnerDomain>>(partnerProbes.size());

        if (0 >= partnerProbes.size()) {
            return partnerDomains;
        }

        for (Entry<PartnerBadge, TermSet<TypedTerm>> entry : partnerProbes.entrySet()) {
            HashSet<PartnerDomain> domains = new HashSet<PartnerDomain>(entry.getValue().size());
            for (TypedTerm term : entry.getValue()) {
                PartnerDomain domain = new PartnerDomain(term.getText(), term.getWeight());
                domains.add(domain);
            }
            partnerDomains.put(entry.getKey(), domains);
        }
        return partnerDomains;
    }

    private Map<PartnerBadge, TermSet<TypedTerm>> newPartnerDomainCounter(PartnerRegister registeredPartners) {
        Map<PartnerBadge, TermSet<TypedTerm>> partnerToDomainToCount = new HashMap<PartnerBadge, TermSet<TypedTerm>>();
        for (PartnerBadge partner : registeredPartners.getPartners()) {
            partnerToDomainToCount.put(partner, new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger()));
        }
        return partnerToDomainToCount;
    }

    /**
     * Sends a secure user profile request to a partner.
     * 
     * @param partner
     * @param secureUserProfile
     * @return the received result list
     * @throws RuntimeErrorException
     *             if an exception occurs
     */
    private ResultList getPartnerResult(PartnerBadge partner, SecureUserProfile secureUserProfile) throws RuntimeErrorException {
        ResultList resultList = new ResultList();

        Client client = partnerRegistration.getClient(partner);
        if (client != null) {
            try {
                WebResource resource = null;
                if (partner.getPartnerConnectorEndpoint().endsWith("/")) {
                    resource = client.resource(partner.getPartnerConnectorEndpoint() + recommendationEndpoint);
                } else {
                    resource = client.resource(partner.getPartnerConnectorEndpoint() + "/" + recommendationEndpoint);
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
