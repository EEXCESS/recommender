/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.eexcess.federatedrecommender.sourceselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.Language;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;

/**
 * @author Raoul Rubien
 */
public class LanguageSourceSelector implements PartnerSelector {

    private static final Logger LOGGER = Logger.getLogger(LanguageSourceSelector.class.getName());
    private Map<PartnerBadge, List<String>> selectedPartners = new HashMap<>();

    public LanguageSourceSelector(FederatedRecommenderConfiguration configuration) {
    }

    /**
     * Selects partners according to language matches.
     * 
     * @return the same userProfile with eventually added sources if
     *         (userProfile.partnerList.size() <= 0)
     */
    @Override
    public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
        selectedPartners.clear();
        // don't touch if already selected
        if (userProfile.partnerList.size() <= 0) {
            // query language(s) are specified in user profile
            if (userProfile.languages.size() > 0) {
                // match partners and user profile languages
                for (Language userLangDetails : userProfile.languages) {
                    String userLanguage = userLangDetails.getIso2();
                    collectPartnersOnLanguageMatch(userLanguage, partners, userProfile.partnerList);
                }
            } else {
                LOGGER.info("refusing to select partners due to no specified languages");
                return userProfile;
            }
        } else {
            LOGGER.info("refusing to select partners due to [" + userProfile.partnerList.size() + "] prevoiously selected partners");
            return userProfile;
        }

        if (selectedPartners.size() > 0) {
            LOGGER.info("language-based source selection:");
            for (Map.Entry<PartnerBadge, List<String>> entry : selectedPartners.entrySet()) {
                StringBuilder info = new StringBuilder();
                info.append("partner [" + entry.getKey().getSystemId() + "] matching language(s):");
                for (String language : entry.getValue()) {
                    info.append(" [" + language + "]");
                }
                LOGGER.info(info.toString());
            }
        } else {
            LOGGER.info("unsuccessfull partner selection");
        }

        return userProfile;
    }

    /**
     * Store(s) partner(s) to userProfile if it supports the given language.
     * 
     * @param language
     *            the given language
     * @param partners
     *            list of partners to consider
     * @param partnerConnectorList
     *            where to store the partner reference if it supports the given
     *            language
     */
    private void collectPartnersOnLanguageMatch(String language, List<PartnerBadge> partners, List<PartnerBadge> partnerConnectorList) {
        if (null == language) {
            return;
        }
        for (PartnerBadge partner : partners) {
            for (String partnerLanguage : partner.getLanguageContent()) {
                if (partnerLanguage.compareTo(language) == 0) {
                    if (!partnerConnectorList.contains(partner)) {
                        partnerConnectorList.add(partner);
                    }
                    if (!selectedPartners.containsKey(partner)) {
                        ArrayList<String> newList = new ArrayList<String>();
                        newList.add(language);
                        selectedPartners.put(partner, newList);
                    } else {
                        selectedPartners.get(partner).add(language);
                    }
                }
            }
        }
    }
}
