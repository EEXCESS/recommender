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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.Language;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;

public class LanguageSourceSelectorTest {

    @Test
    public void languageSourceSelector_sourceSelect_expectOneSelection() {
        PartnerSelector selector = new LanguageSourceSelector(null);

        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.setLanguages(Arrays.asList(new Language[] { new Language("de", 1.0), new Language("en", 1.0) }));

        List<PartnerBadge> partners = new ArrayList<>();
        PartnerBadge pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

        assertSame(userProfile, refinedUserProfile);
        assertTrue(userProfile.getPartnerList().contains(pb));
        assertEquals(1, userProfile.getPartnerList().size());
    }

    @Test
    public void languageSourceSelector_sourceSelect_expecttThreeOfeightSelections() {
        PartnerSelector selector = new LanguageSourceSelector(null);

        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.setLanguages(Arrays.asList(new Language[] { new Language("de", 1.0), new Language("en", 1.0) }));

        List<PartnerBadge> partners = new ArrayList<>();
        PartnerBadge pb_deenfr = new PartnerBadge();
        pb_deenfr.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));
        pb_deenfr.setSystemId(StringUtils.join(pb_deenfr.getLanguageContent().toArray()));
        partners.add(pb_deenfr);

        PartnerBadge pb_en = new PartnerBadge();
        pb_en.setLanguageContent(Arrays.asList(new String[] { "en" }));
        pb_en.setSystemId(StringUtils.join(pb_en.getLanguageContent().toArray()));
        partners.add(pb_en);

        PartnerBadge pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "fr" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        PartnerBadge pb_de = new PartnerBadge();
        pb_de.setLanguageContent(Arrays.asList(new String[] { "de" }));
        pb_de.setSystemId(StringUtils.join(pb_de.getLanguageContent().toArray()));
        partners.add(pb_de);

        pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "tr" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "ro" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "it" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "gr" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

        assertSame(userProfile, refinedUserProfile);
        assertTrue(userProfile.getPartnerList().contains(pb_en));
        assertTrue(userProfile.getPartnerList().contains(pb_de));
        assertTrue(userProfile.getPartnerList().contains(pb_deenfr));
        assertEquals(3, userProfile.getPartnerList().size());
        assertEquals(8, partners.size());
    }

    @Test
    public void languageSourceSelector_sourceSelect_expectOneOfTwoSelections() {
        PartnerSelector selector = new LanguageSourceSelector(null);

        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.setLanguages(Arrays.asList(new Language[] { new Language("de", 1.0) }));

        List<PartnerBadge> partners = new ArrayList<>();
        PartnerBadge pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));
        partners.add(pb);

        PartnerBadge pb2 = new PartnerBadge();
        pb2.setLanguageContent(Arrays.asList(new String[] { "ak", "am", "ar" }));
        pb2.setSystemId(StringUtils.join(pb2.getLanguageContent().toArray()));
        partners.add(pb2);

        assertEquals(2, partners.size());
        SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

        assertEquals(1, userProfile.getPartnerList().size());
        assertSame(userProfile, refinedUserProfile);
        assertTrue(userProfile.getPartnerList().contains(pb));
    }

    @Test
    public void languageSourceSelector_sourceSelect_withPreselectedSources_expectSkippedSelection() {
        PartnerSelector selector = new LanguageSourceSelector(null);

        SecureUserProfile userProfile = new SecureUserProfile();
        userProfile.setLanguages(Arrays.asList(new Language[] { new Language("de", 1.0) }));

        List<PartnerBadge> partners = new ArrayList<>();
        PartnerBadge pb = new PartnerBadge();
        pb.setLanguageContent(Arrays.asList(new String[] { "de", "en", "fr" }));
        pb.setSystemId(StringUtils.join(pb.getLanguageContent().toArray()));

        userProfile.getPartnerList().add(pb);
        assertEquals(1, userProfile.getPartnerList().size());

        PartnerBadge pb2 = new PartnerBadge();
        pb2.setLanguageContent(Arrays.asList(new String[] { "de", "am", "ar" }));
        pb2.setSystemId(StringUtils.join(pb2.getLanguageContent().toArray()));
        partners.add(pb2);

        assertEquals(1, partners.size());

        SecureUserProfile refinedUserProfile = selector.sourceSelect(userProfile, partners);

        assertEquals(1, userProfile.getPartnerList().size());
        assertSame(userProfile, refinedUserProfile);
        assertTrue(userProfile.getPartnerList().contains(pb));
    }
}
