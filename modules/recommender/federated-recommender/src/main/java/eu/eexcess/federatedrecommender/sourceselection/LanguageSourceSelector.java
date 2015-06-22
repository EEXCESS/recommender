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

import java.util.List;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.Language;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.PartnerSelector;
import eu.eexcess.utils.LanguageGuesser;

/**
 * @author Raoul Rubien
 */
public class LanguageSourceSelector implements PartnerSelector {

	/**
	 * Selects partners according to language matches. Languages may be
	 * specified in the user profile. If no language is specified, it will be
	 * guessed from the context keywords.
	 * 
	 * @return the same userProfile with eventually added sources if
	 *         (userProfile.partnerList.size() <= 0)
	 */
	@Override
	public SecureUserProfile sourceSelect(SecureUserProfile userProfile, List<PartnerBadge> partners) {
		if (userProfile.partnerList.size() <= 0) {
			// query language(s) are specified in user profile
			if (userProfile.languages.size() > 0) {
				// match partners and user profile languages
				for (Language userLangDetails : userProfile.languages) {
					String userLanguage = userLangDetails.iso2;
					collectPartnersOnLanguageMatch(userLanguage, partners, userProfile.partnerList);
				}
			}
			// no query language(s) are specified; try to guess
			else {
				String textFragment = joinContextKeywords(userProfile.contextKeywords);
				String userLanguage = LanguageGuesser.getInstance().guessLanguage(textFragment);
				collectPartnersOnLanguageMatch(userLanguage, partners, userProfile.partnerList);
			}
		}
		return userProfile;
	}

	private String joinContextKeywords(List<ContextKeyword> contextKeywords) {
		StringBuilder builder = new StringBuilder();

		for (ContextKeyword keyword : contextKeywords) {
			builder.append(keyword.text + " ");
		}

		return builder.toString().trim();
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
	private void collectPartnersOnLanguageMatch(String language, List<PartnerBadge> partners,
					List<PartnerBadge> partnerConnectorList) {
		for (PartnerBadge partner : partners) {
			for (String partnerLanguage : partner.getLanguageContent()) {
				if (partnerLanguage.compareTo(language) == 0) {
					if (false == partnerConnectorList.contains(partner)) {
						partnerConnectorList.add(partner);
					}
				}
			}
		}
	}
}
