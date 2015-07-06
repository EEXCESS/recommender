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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.sourceselection.WndomainSourceSelector.DomainWeight;

public class WndomainSourceSelectorTest {

	private static class TestableWndomainsSourceSelector extends WndomainSourceSelector {
		public TestableWndomainsSourceSelector(FederatedRecommenderConfiguration configuration) {
			super(configuration);
		}

		Map<PartnerBadge, TreeSet<DomainWeight>> getMatchingPartners() {
			return matchingPartners;
		}
	}

	private TestableWndomainsSourceSelector selector = null;

	@Before
	public void initSelector() {
		FederatedRecommenderConfiguration recommenderConfig = new FederatedRecommenderConfiguration();
		recommenderConfig.wordnetPath = "/opt/data/wordnet/WordNet-2.0/dict/";
		recommenderConfig.wordnetDomainFilePath = "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223";
		selector = new TestableWndomainsSourceSelector(recommenderConfig);
	}

	@Test
	public void domainSourceSelector_sourceSelect_expectOneSelection() throws IOException {

		// domains of "health": "medicine"

		PartnerBadge humanitiesPartner = new PartnerBadge();
		humanitiesPartner.setSystemId("humanities-id");
		ArrayList<PartnerDomain> pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("humanities", 1));
		humanitiesPartner.setDomainContent(pdomains);

		PartnerBadge freeTimePartner = new PartnerBadge();
		freeTimePartner.setSystemId("free_time-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("free_time", 1));
		freeTimePartner.setDomainContent(pdomains);

		PartnerBadge allroundPartner = new PartnerBadge();
		allroundPartner.setSystemId("allrounder-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("transport", 1));
		pdomains.add(new PartnerDomain("humanities", 1));
		pdomains.add(new PartnerDomain("free_time", 1));
		pdomains.add(new PartnerDomain("applied_science", 1));
		pdomains.add(new PartnerDomain("medicine", 1));
		allroundPartner.setDomainContent(pdomains);

		List<PartnerBadge> partners = new ArrayList<>();
		partners.add(humanitiesPartner);
		partners.add(freeTimePartner);
		partners.add(allroundPartner);

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.addAll(Arrays.asList(new ContextKeyword[] { new ContextKeyword("health") }));

		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);
		assertEquals(1, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.get(0) == allroundPartner);

		// assert domain weights are as expected
		Iterator<DomainWeight> iterator = selector.getMatchingPartners().get(refinedUserProfile.partnerList.get(0))
						.iterator();
		assertEquals(1, iterator.next().weight, 0.0001);
		assertEquals(false, iterator.hasNext());
	}

	@Test
	public void domainSourceSelector_sourceSelect_expectOneSelectionMatchingTwoDmains() throws IOException {

		// transport -> enterprise, telecommunication

		PartnerBadge humanitiesPartner = new PartnerBadge();
		humanitiesPartner.setSystemId("humanities-id");
		ArrayList<PartnerDomain> pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("humanities", 1));
		humanitiesPartner.setDomainContent(pdomains);

		PartnerBadge freeTimePartner = new PartnerBadge();
		freeTimePartner.setSystemId("free_time-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("free_time", 1));
		freeTimePartner.setDomainContent(pdomains);

		PartnerBadge allroundPartner = new PartnerBadge();
		allroundPartner.setSystemId("allrounder-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("transport", 1));
		pdomains.add(new PartnerDomain("humanities", 1));
		pdomains.add(new PartnerDomain("telecommunication", 1));
		pdomains.add(new PartnerDomain("enterprise", 1));
		pdomains.add(new PartnerDomain("medicine", 1));
		allroundPartner.setDomainContent(pdomains);

		List<PartnerBadge> partners = new ArrayList<>();
		partners.add(humanitiesPartner);
		partners.add(freeTimePartner);
		partners.add(allroundPartner);

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.addAll(Arrays.asList(new ContextKeyword[] { new ContextKeyword("transport") }));

		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);
		assertEquals(1, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.get(0) == allroundPartner);

		// assert domain weights are as expected
		Iterator<DomainWeight> iterator = selector.getMatchingPartners().get(refinedUserProfile.partnerList.get(0))
						.iterator();
		assertEquals(0.5, iterator.next().weight, 0.0001);
		assertEquals(0.5, iterator.next().weight, 0.0001);
		assertEquals(false, iterator.hasNext());
	}

	@Test
	public void domainSourceSelector_sourceSelect_expectTwoSelectionsMatchingThreeDmains() throws IOException {

		// transport -> enterprise, telecommunication

		PartnerBadge telecommPartner = new PartnerBadge();
		telecommPartner.setSystemId("telecommunication-id");
		ArrayList<PartnerDomain> pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("telecommunication", 1));
		telecommPartner.setDomainContent(pdomains);

		PartnerBadge freeTimePartner = new PartnerBadge();
		freeTimePartner.setSystemId("free_time-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("free_time", 1));
		freeTimePartner.setDomainContent(pdomains);

		PartnerBadge allroundPartner = new PartnerBadge();
		allroundPartner.setSystemId("allrounder-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("transport", 1));
		pdomains.add(new PartnerDomain("humanities", 1));
		pdomains.add(new PartnerDomain("telecommunication", 1));
		pdomains.add(new PartnerDomain("enterprise", 1));
		pdomains.add(new PartnerDomain("medicine", 1));
		allroundPartner.setDomainContent(pdomains);

		List<PartnerBadge> partners = new ArrayList<>();
		partners.add(telecommPartner);
		partners.add(freeTimePartner);
		partners.add(allroundPartner);

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.addAll(Arrays.asList(new ContextKeyword[] { new ContextKeyword("transport") }));

		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);
		assertEquals(2, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.get(0) == telecommPartner);
		assertTrue(userProfile.partnerList.get(1) == allroundPartner);

		// assert domain weights are as expected
		Iterator<DomainWeight> iterator = selector.getMatchingPartners().get(refinedUserProfile.partnerList.get(0))
						.iterator();
		assertEquals(0.3333, iterator.next().weight, 0.0001);
		assertEquals(false, iterator.hasNext());

		iterator = selector.getMatchingPartners().get(refinedUserProfile.partnerList.get(1)).iterator();
		assertEquals(0.3333, iterator.next().weight, 0.0001);
		assertEquals(0.3333, iterator.next().weight, 0.0001);
		assertEquals(false, iterator.hasNext());
	}

	@Test
	public void domainSourceSelector_sourceSelect_expectTwoSelectionsMatchingTwoDmains() throws IOException {

		// transport -> enterprise, telecommunication

		PartnerBadge telecommPartner = new PartnerBadge();
		telecommPartner.setSystemId("telecommunication-id");
		ArrayList<PartnerDomain> pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("telecommunication", 1));
		telecommPartner.setDomainContent(pdomains);

		PartnerBadge freeTimePartner = new PartnerBadge();
		freeTimePartner.setSystemId("free_time-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("free_time", 1));
		freeTimePartner.setDomainContent(pdomains);

		PartnerBadge allroundPartner = new PartnerBadge();
		allroundPartner.setSystemId("allrounder-id");
		pdomains = new ArrayList<>();
		pdomains.add(new PartnerDomain("transport", 1));
		pdomains.add(new PartnerDomain("humanities", 1));
		pdomains.add(new PartnerDomain("telecommunication", 1));
		pdomains.add(new PartnerDomain("applied_science", 1));
		pdomains.add(new PartnerDomain("medicine", 1));
		allroundPartner.setDomainContent(pdomains);

		List<PartnerBadge> partners = new ArrayList<>();
		partners.add(telecommPartner);
		partners.add(freeTimePartner);
		partners.add(allroundPartner);

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.addAll(Arrays.asList(new ContextKeyword[] { new ContextKeyword("transport") }));

		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);
		assertEquals(2, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.get(1) == allroundPartner);
		assertTrue(userProfile.partnerList.get(0) == telecommPartner);

		// assert domain weights are as expected
		Iterator<DomainWeight> iterator = selector.getMatchingPartners().get(refinedUserProfile.partnerList.get(0))
						.iterator();
		assertEquals(0.5, iterator.next().weight, 0.0001);
		assertEquals(false, iterator.hasNext());

		iterator = selector.getMatchingPartners().get(refinedUserProfile.partnerList.get(1)).iterator();
		assertEquals(0.5, iterator.next().weight, 0.0001);
		assertEquals(false, iterator.hasNext());
	}
}
