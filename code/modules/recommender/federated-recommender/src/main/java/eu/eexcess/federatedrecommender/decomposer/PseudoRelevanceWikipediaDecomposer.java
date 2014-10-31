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
package eu.eexcess.federatedrecommender.decomposer;

import java.io.IOException;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;

/**
 * Class to provide query expansion from
 * 
 * @author hziak
 *
 */
public class PseudoRelevanceWikipediaDecomposer implements SecureUserProfileDecomposer<SecureUserProfile,SecureUserProfile> {
/*
	private static final Logger logger = Logger.getLogger(PseudoRelevanceWikipediaDecomposer.class.getName());
	
	private Map<String, WikipediaQueryExpansion> localeToQueryExpansion;
	private String[] supportedLocales;
*/

	/**
	 * 
	 * @param wikipediaBaseIndexDir the base directory for the Wikipedia indices, it is expected to contain folders like "enwiki" and "dewiki"
	 * @throws IOException
	 */
	
	public PseudoRelevanceWikipediaDecomposer(String wikipediaBaseIndexDir, String[] supportedLocales) throws IOException {
		/*this.supportedLocales = supportedLocales;
		localeToQueryExpansion = new HashMap<String, WikipediaQueryExpansion>();
		
		for (String localeName : supportedLocales) {
			Locale locale = LocaleUtils.toLocale(localeName);
			localeToQueryExpansion.put(localeName, new WikipediaQueryExpansion(new File(wikipediaBaseIndexDir, locale+"wiki"), locale));
			
		}
		*/
	}

	@Override
	public SecureUserProfile decompose(SecureUserProfile inputSecureUserProfile) {
		/*
		TermSet<TypedTerm> terms = new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger());
		StringBuilder builder = new StringBuilder();
		for (ContextKeyword keyword : inputSecureUserProfile.contextKeywords) {
			if (builder.length() > 0) { builder.append(" "); }
			builder.append(keyword.text);
			terms.add(new TypedTerm(keyword.text, null, 1));
		}
		String query = builder.toString();
		
		String localeName = null;
		// first, pick up the language specified by the user
		if (inputSecureUserProfile.languages != null && !inputSecureUserProfile.languages.isEmpty()) {
			Language firstLanguage = inputSecureUserProfile.languages.iterator().next();
			localeName = firstLanguage.iso2;
		} else {
			// then try to detect the language from the query
			String guessedLanguage = LanguageGuesser.getInstance().guessLanguage(query);
			if (guessedLanguage != null) {
				localeName = guessedLanguage;
			}
		}
		
		WikipediaQueryExpansion wikipediaQueryExpansion = localeToQueryExpansion.get(localeName);
		if (wikipediaQueryExpansion == null) {
			// no query expansion for the current locale, fall back to the first supported locale
			wikipediaQueryExpansion = localeToQueryExpansion.get(supportedLocales[0]);
		}

		try {
			TermSet<TypedTerm> queryExpansionTerms;
			queryExpansionTerms = wikipediaQueryExpansion.expandQuery(query);
			terms.addAll(queryExpansionTerms.getTopTerms(5));
		} catch (IOException e) {
			logger.log(Level.WARNING, "Cannot expand the query using Wikipedia", e);
		}
		
		ArrayList<ContextKeyword> newContextKeywords = new ArrayList<ContextKeyword>();
		for (TypedTerm typedTerm : terms.getTopTerms(5)) {
			newContextKeywords.add(new ContextKeyword(typedTerm.getText(),true));
		}
		inputSecureUserProfile.contextKeywords.addAll(newContextKeywords);
		logger.log(Level.INFO, "Wikipedia Expansion: " + newContextKeywords.toString());
		return inputSecureUserProfile;
		*/
		return null;
	}
}
