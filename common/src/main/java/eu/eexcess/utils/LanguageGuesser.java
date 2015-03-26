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
package eu.eexcess.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.knowcenter.ie.Language;
import at.knowcenter.ie.languagedetection.LanguageDetector;

/**
 * Helper class to guess the language of a short snippet of text.
 * 
 * @author rkern
 */
public class LanguageGuesser {
	private final static Logger logger = Logger.getLogger(LanguageGuesser.class.getName());
	
	private final static Language[] languages = new Language[] {
			Language.English, Language.French, Language.German, Language.Italian, Language.Spanish
	};

	private static final int MAX_LENGTH = 256;
	
	private static LanguageGuesser instance;
	
	private LanguageDetector languageDetector;
	
	static {
		instance = new LanguageGuesser();
	}
	
	public LanguageGuesser() {
		try {
			languageDetector = new LanguageDetector(languages);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot intialise the language guesser", e);
		}
	}

	/**
	 * Returns the singleton instance.
	 * @return the instance
	 */
	public static LanguageGuesser getInstance() {
		return instance;
	}
	
	/**
	 * Tries to detect the language of the text fragment and returns its ISO code, or null if unknown.
	 * 
	 * @param textFragment the text, must not be null
	 * @return the ISO 639-1 code, or null if the language is unknown
	 */
	public String guessLanguage(String textFragment) {
		String text = textFragment.length() > MAX_LENGTH ? textFragment.substring(0, MAX_LENGTH) : textFragment;
		Language language = languageDetector.detect(text);
		return language == Language.Undefined ? null :  language.asTwoChars();
	}
}
