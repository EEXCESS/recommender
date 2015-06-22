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

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;
import eu.eexcess.federatedrecommender.sourceselection.LanguageGuessingSourceSelector;

public class LanguageGuessingSourceSelectorTest {

	@Test
	public void languageSourceSelector_sourceSelect_withNoPreselectedLanguages_expectDELanguageToBeGuessed() {

		PartnerBadge germanPartner = new PartnerBadge();
		germanPartner.setLanguageContent(Arrays.asList(new String[] { "de" }));
		germanPartner.systemId = StringUtils.join(germanPartner.getLanguageContent().toArray());

		PartnerBadge frenchPartner = new PartnerBadge();
		frenchPartner.setLanguageContent(Arrays.asList(new String[] { "fr" }));
		frenchPartner.systemId = StringUtils.join(frenchPartner.getLanguageContent().toArray());
		
		PartnerBadge englishPartner = new PartnerBadge();
		englishPartner.setLanguageContent(Arrays.asList(new String[] { "en" }));
		englishPartner.systemId = StringUtils.join(englishPartner.getLanguageContent().toArray());

		List<PartnerBadge> partners = new ArrayList<>();
		partners.add(germanPartner);
		partners.add(frenchPartner);
		partners.add(englishPartner);

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.addAll(Arrays.asList(new ContextKeyword[] { new ContextKeyword("das"),
						new ContextKeyword("ist"), new ContextKeyword("ein"), new ContextKeyword("auto") }));

		PartnerSelector selector = new LanguageGuessingSourceSelector();
		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

		assertEquals(1, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.get(0) == germanPartner);
		assertTrue(userProfile.partnerList.get(0) != englishPartner);
		assertTrue(userProfile.partnerList.get(0) != frenchPartner);
	}

	@Test
	public void languageSourceSelector_sourceSelect_withNoPreselectedLanguages_expectENLanguageToBeGuessed() {

		PartnerBadge germanPartner = new PartnerBadge();
		germanPartner.setLanguageContent(Arrays.asList(new String[] { "de" }));
			germanPartner.systemId = StringUtils.join(germanPartner.getLanguageContent().toArray());

		PartnerBadge frenchPartner = new PartnerBadge();
		frenchPartner.setLanguageContent(Arrays.asList(new String[] { "fr" }));
			frenchPartner.systemId = StringUtils.join(frenchPartner.getLanguageContent().toArray());

		PartnerBadge englishPartner = new PartnerBadge();
		englishPartner.setLanguageContent(Arrays.asList(new String[] { "en" }));
			englishPartner.systemId = StringUtils.join(englishPartner.getLanguageContent().toArray());

		List<PartnerBadge> partners = new ArrayList<>();
		partners.add(germanPartner);
		partners.add(frenchPartner);
		partners.add(englishPartner);

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.addAll(Arrays.asList(new ContextKeyword[] { new ContextKeyword("this"),
						new ContextKeyword("is"), new ContextKeyword("a"), new ContextKeyword("car") }));

		PartnerSelector selector = new LanguageGuessingSourceSelector();
		SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

		assertEquals(1, userProfile.partnerList.size());
		assertSame(userProfile, refinedUserProfile);
		assertTrue(userProfile.partnerList.get(0) != germanPartner);
		assertTrue(userProfile.partnerList.get(0) == englishPartner);
		assertTrue(userProfile.partnerList.get(0) != frenchPartner);
	}

}
