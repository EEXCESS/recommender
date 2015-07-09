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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetectorException;
import eu.eexcess.federatedrecommender.domaindetection.probing.PartnerDomainsProbe;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;

public class PartnersDomainsProbeTest {

    private static class TestablePartnersDomainsProbe extends PartnerDomainsProbe {
        TestablePartnersDomainsProbe(DomainDetector detector, int numWords, int numResults) {
            super(detector, numWords, numResults);
        }

        public void assertCloneEquals(PartnerDomainsProbe other) {
            assertEquals(maxWords, other.maxWords);
            assertEquals(maxResults, other.maxResults);
            assertSame(domainDetector, other.domainDetector);
            assertThat(ambiguousPhrases, containsInAnyOrder(other.ambiguousPhrases.toArray()));
        }
    }

    private static PartnerRegister partnerRegister = new PartnerRegister();
    private static DomainDetector detector = null;

    @BeforeClass
    public static void setupDomainDetector() throws DomainDetectorException {
        PartnersDomainsProbeTest.detector = new WordnetDomainsDetector(new File("/opt/data/wordnet/WordNet-2.0/dict/"), new File(
                "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223"), true);
    }

    @BeforeClass
    public static void setupPartners() {
        Map<String, String> partners = new HashMap<String, String>();
        partners.put("Opensearch", "http://127.0.0.1/eexcess-partner-reference-opensearch-1.0-SNAPSHOT/partner/");
        partners.put("Mendeley", "http://127.0.0.1/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/");
        partners.put("ZBW", "http://127.0.0.1/eexcess-partner-zbw-1.0-SNAPSHOT/partner/");
        partners.put("Europeana", "http://127.0.0.1/eexcess-partner-europeana-1.0-SNAPSHOT/partner/");
        partners.put("Wissenmedia", "http://127.0.0.1/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/");

        for (Map.Entry<String, String> entry : partners.entrySet()) {
            PartnerBadge partner = new PartnerBadge();
            partner.setSystemId(entry.getKey());
            partner.setPartnerConnectorEndpoint(entry.getValue());
            PartnersDomainsProbeTest.partnerRegister.addPartner(partner);
        }
    }

    @Test
    public void probe_withGivenOnlineParnter_expectNotExceptional() throws DomainDetectorException {

        int numPartnersHavingNoDomains = 0;
        PartnerDomainsProbe probe = new PartnerDomainsProbe(detector, 15, 3);

        for (PartnerBadge partner : partnerRegister.getPartners()) {
            HashSet<PartnerDomain> partnerToDomain = probe.probePartner(partnerRegister.getClient(partner.getSystemId()), partner);
            System.out.println("domains for partner [" + partner.getSystemId() + "]");
            for (PartnerDomain domain : partnerToDomain) {
                System.out.println("name: " + domain.domainName + " weight: " + domain.weight);
            }

            if (partnerToDomain.size() <= 0) {
                numPartnersHavingNoDomains++;
            }

            if (numPartnersHavingNoDomains >= 2) {
                assertThat("partner [" + partner.getSystemId() + "] has no domains and also do [" + (numPartnersHavingNoDomains - 1) + "] other partners",
                        partnerToDomain.size(), greaterThan(0));
            }
        }
    }

    @Test
    public void cloneable_expectNewObjectWithIdenticState() throws CloneNotSupportedException {
        TestablePartnersDomainsProbe template = new TestablePartnersDomainsProbe(detector, 15, 3);
        TestablePartnersDomainsProbe clone = (TestablePartnersDomainsProbe) template.clone();
        template.assertCloneEquals(clone);
    }
}
