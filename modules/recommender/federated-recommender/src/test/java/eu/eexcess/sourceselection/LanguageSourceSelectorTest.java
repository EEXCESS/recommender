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

package eu.eexcess.sourceselection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.Language;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;
import eu.eexcess.federatedrecommender.sourceselection.LanguageSourceSelector;

public class LanguageSourceSelectorTest {

	@Test
	public void languageSourceSelector_sourceSelect_expectOneSelection() {
		PartnerSelector selector = new LanguageSourceSelector();

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.languages = Arrays.asList(new Language[] { new Language("de", 1.0), new Language("en", 1.0) });

		List<PartnerBadge> partners = new ArrayList<>();
		PartnerBadge pb = new PartnerBadge();
		pb.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));
		partners.add(pb);

		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.contains(pb));
		assertEquals(1, userProfile.partnerList.size());
	}

	@Test
	public void languageSourceSelector_sourceSelect_expectOneOfTwoSelections() {
		PartnerSelector selector = new LanguageSourceSelector();

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.languages = Arrays.asList(new Language[] { new Language("de", 1.0) });

		List<PartnerBadge> partners = new ArrayList<>();
		PartnerBadge pb = new PartnerBadge();
		pb.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));
		partners.add(pb);

		PartnerBadge pb2 = new PartnerBadge();
		pb2.setLanguageContent(Arrays.asList(new String[] { "ak", "am", "ar" }));
		partners.add(pb2);

		assertEquals(2, partners.size());
		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

		assertEquals(1, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.contains(pb));
	}

	@Test
	public void languageSourceSelector_sourceSelect_withPreselectedSources_expectSkippedSelection() {
		PartnerSelector selector = new LanguageSourceSelector();

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.languages = Arrays.asList(new Language[] { new Language("de", 1.0) });

		List<PartnerBadge> partners = new ArrayList<>();
		PartnerBadge pb = new PartnerBadge();
		pb.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));

		userProfile.partnerList.add(pb);
		assertEquals(1, userProfile.partnerList.size());

		PartnerBadge pb2 = new PartnerBadge();
		pb2.setLanguageContent(Arrays.asList(new String[] { "de", "am", "ar" }));
		partners.add(pb2);

		assertEquals(1, partners.size());

		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

		assertEquals(1, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.contains(pb));
	}
}
