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

package eu.eexcess.federatedrecommender.sourceselection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.domaindetection.probing.Domain;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetectorException;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;

public class WndomainSourceSelector implements PartnerSelector {

    /**
     * @author Raoul Rubien
     *
     */
    protected static class DomainWeight implements Comparable<DomainWeight> {
        public Double weight;
        public String name;

        public DomainWeight(String name, Double weight) {
            this.name = name;
            this.weight = weight;
        }

        @Override
        public int compareTo(DomainWeight o) {
            if (equals(o)) {
                return 0;
            } else if (this.weight < o.weight) {
                return -1;
            } else if (this.weight > o.weight) {
                return 1;
            }
            return this.name.compareTo(o.name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((weight == null) ? 0 : weight.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DomainWeight other = (DomainWeight) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (weight == null) {
                if (other.weight != null)
                    return false;
            } else if (!weight.equals(other.weight))
                return false;
            return true;
        }

    }

    /**
     * A map of domain matching partners mapping to a descent sorted set of
     * {@link DomainWeight}s
     */
    protected Map<PartnerBadge, TreeSet<DomainWeight>> matchingPartners = new HashMap<PartnerBadge, TreeSet<DomainWeight>>();

    private static final Logger LOGGER = Logger.getLogger(WndomainSourceSelector.class.getName());

    private DomainDetector domainDetector = null;

    /**
     * A descent value-sorted map of domains detected in
     * <code>ecureUserProfile</code> at one a call of
     * {@link #sourceSelect(SecureUserProfile, List)} and how many times
     */
    private Map<String, AtomicInteger> seenDomains = new TreeMap<>();

    private boolean isKeywordGroupingEnabled = true;

    /**
     * Constructs an intance of this class and a needed instance interfacing
     * {@link DomainDetector}. This instance,being rather consuming, can later
     * be exposed for other usage.
     * 
     * @param configuration
     *            containing information where to find resources needed for the
     *            domain detector
     */
    public WndomainSourceSelector(FederatedRecommenderConfiguration configuration) {
        try {
            domainDetector = new WordnetDomainsDetector(new File(configuration.getWordnetPath()), new File(configuration.getWordnetDomainFilePath()), true);
        } catch (DomainDetectorException e) {
            LOGGER.log(Level.SEVERE, "unable to instanciate [" + WordnetDomainsDetector.class.getSimpleName() + "]", e);
        }
    }

    // TODO: Domain detection does not consider weights and ordering of domains
    // now. Therefore source selection is based only on domain hits, regardless
    // of their weights or partner domain weights.
    @Override
    public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {

        if (null == domainDetector) {
            LOGGER.severe("failed to select sources due to missing domain detector: skipping source selection");
            return userProfile;
        }

        matchingPartners.clear();
        seenDomains.clear();

        // don't touch if already selected
        if (userProfile.partnerList.isEmpty()) {
            // match partners' and user profile domains
            matchKeywordDomainsOnParterDomains(userProfile.contextKeywords, partners);
            selectPartners(partners, userProfile.partnerList);
        } else {
            LOGGER.info("refusing to select partners due to [" + userProfile.partnerList.size() + "] prevoiously selected partners");
            return userProfile;
        }

        if (!userProfile.partnerList.isEmpty()) {
            StringBuilder info = new StringBuilder("partners: ");
            for (PartnerBadge entry : userProfile.partnerList) {
                info.append("[" + entry.getSystemId() + "] ");
            }
            LOGGER.info("WordnetDomain-based source selection selected: " + info.toString());
        } else {
            LOGGER.info("unsuccessfull partner selection");
        }
        return userProfile;
    }

    /**
     * Define whether domain detection should be performed on each keyword
     * separately or on the resulting phrase of joined keywords.
     * 
     * @param enable
     *            <p>
     *            true - join keywords before domain detection
     *            <p>
     *            false - perform domain detection on each keyword separately
     */
    public void enableKeywordGroupingStrategy(boolean enable) {
        isKeywordGroupingEnabled = enable;
    }

    /**
     * Exposes the currently referenced domain detector instance. Domain
     * detection is synchronized on the returned instance.
     * 
     * @return the referenced domain detector instance
     */
    public synchronized DomainDetector getDomainDetector() {
        return domainDetector;
    }

    /**
     * Picks partners from {@link #matchingPartners} and adds their references
     * the partner list.
     * 
     * @param partnerList
     *            list where selected partners are added to
     */
    private void selectPartners(List<PartnerBadge> partners, List<PartnerBadge> partnerList) {
        // TODO: This a straight forward implementation not considering domain
        // weights specified in partners' configuration or detected domains.
        for (PartnerBadge partner : partners) {
            if (matchingPartners.containsKey(partner)) {
                partnerList.add(partner);
            }
        }
    }

    /**
     * Matches parter domains with detected domains of context keywords.
     * Considers the amount of domain occurrences and normalizes their weight in
     * {@link #matchingPartners}.
     * 
     * @param contextKeywords
     *            context keywords
     * @param partners
     * @param partnersConnector
     */
    private void matchKeywordDomainsOnParterDomains(List<ContextKeyword> contextKeywords, List<PartnerBadge> partners) {

        // detect keywords' domains and store seen domains and domain count
        List<String> keywords = getQueryTerms(contextKeywords);
        for (String keyword : keywords) {
            try {
                synchronized (domainDetector) {
                    for (Domain domain : domainDetector.detect(keyword)) {
                        AtomicInteger timesSeen = seenDomains.get(domain.getName());
                        if (null == timesSeen) {
                            seenDomains.put(domain.getName().toLowerCase(), new AtomicInteger(1));
                        } else {
                            timesSeen.incrementAndGet();
                        }
                    }
                }
            } catch (DomainDetectorException e) {
                LOGGER.log(Level.SEVERE, "failed to detect domain(s) for kontext keyword", e);
                return;
            }
        }

        // match keywords' domains with partners' domains
        Double totalWeight = 0.0;
        for (PartnerBadge partner : partners) {
            for (PartnerDomain partnerContentDomain : partner.getDomainContent()) {

                // TODO: This is a very simple match implementation, but since
                // domains have a tree structure, utilizing a simple string
                // comparison is a weak matching method. Instead a
                // "is X sub domain of Y" comparison should be performed.
                AtomicInteger timesSeen = seenDomains.get(partnerContentDomain.getName().toLowerCase());
                if (null == timesSeen) {
                    continue;
                } else {
                    if (!matchingPartners.containsKey(partner)) {
                        matchingPartners.put(partner, new TreeSet<DomainWeight>());
                    }
                    DomainWeight domain = new DomainWeight(partnerContentDomain.getName(), timesSeen.doubleValue());
                    matchingPartners.get(partner).add(domain);
                    totalWeight += domain.weight;
                }
            }
        }

        // normalize domain weights to {weight ∈ [0.0;1.0]}
        for (Map.Entry<PartnerBadge, TreeSet<DomainWeight>> entry : matchingPartners.entrySet()) {
            for (DomainWeight domainWeight : entry.getValue()) {
                domainWeight.weight = domainWeight.weight / totalWeight;
            }
        }
    }

    /**
     * Takes the query text form context keywords. When
     * {@link #isKeywordGroupingEnabled} is true the keywords are joined
     * (separated by " ") altogether into the fist list entry. Resulted keywords
     * are lower case.
     * 
     * @param contextKeywords
     * @return
     */
    private List<String> getQueryTerms(List<ContextKeyword> contextKeywords) {
        ArrayList<String> keywords = new ArrayList<>(contextKeywords.size());

        // grouping: concatenate all keywords to one string
        if (isKeywordGroupingEnabled) {
            StringBuilder joinedKeywords = new StringBuilder();
            for (ContextKeyword contextKeyword : contextKeywords) {
                joinedKeywords.append(contextKeyword.text.toLowerCase() + " ");
            }
            keywords.add(joinedKeywords.toString().trim());
        // not grouping
        } else {
            for (ContextKeyword contextKeyword : contextKeywords) {
                keywords.add(contextKeyword.text.toLowerCase());
            }
        }
        return keywords;
    }
}
