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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;

public class PartnersDomainsProbeTest {

	private static PartnerRegister partnerRegister = new PartnerRegister();
	private static DomainDetector detector = null;

	@BeforeClass
	public static void setupDomainDetector() throws DomainDetectorException {
		PartnersDomainsProbeTest.detector = new WordnetDomainsDetector(new File("/opt/data/wordnet/WordNet-2.0/dict/"),
						new File("/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223"), true);
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

		PartnersDomainsProbe probe = new PartnersDomainsProbe(partnerRegister, detector);
		probe.setMaxResults(3);
		probe.setMaxWords(5);
		Map<PartnerBadge, HashSet<PartnerDomain>> partnerToDomain = probe.probePartners();

		assertEquals(partnerToDomain.size(), partnerToDomain.size());
	}
}
