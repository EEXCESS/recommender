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

package eu.eexcess.opensearch.querygenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.CharEncoding;
import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.opensearch.querygenerator.OpensearchQueryGenerator;

public class OpensearchQuerygeneratorTest {

	@Test
	public void toQuery_withSecureUserProfileKeywordsButNoLimit_expectPass() {

		SecureUserProfile userProfile = new SecureUserProfile();

		String keywordText = "some Text with special characters ¹²³¼½¬{[]}\\¸";
		userProfile.setNumResults(0);
		userProfile.setContextKeywords(newDummyContextKeywords(7, keywordText));

		OpensearchQueryGenerator generator = new OpensearchQueryGenerator();
		String query = generator.toQuery(userProfile);

		try {
			assertTrue(query.contains(URLEncoder.encode(keywordText,
					CharEncoding.UTF_8)));
			assertTrue(query.contains(URLEncoder.encode(keywordText + "[1]",
					CharEncoding.UTF_8)));
			assertTrue(query.contains(URLEncoder.encode(keywordText + "[6]",
					CharEncoding.UTF_8)));
			assertFalse(query.contains(URLEncoder.encode(keywordText + "[7]",
					CharEncoding.UTF_8)));
			assertFalse(query.contains("&limit="));
			assertFalse(query.contains("limit"));
		} catch (UnsupportedEncodingException e) {
			assertTrue(false);
		}

	}

	@Test
	public void toQuery_withSecureUserProfileKeywordsAndLimit_expectsPass() {

		SecureUserProfile userProfile = new SecureUserProfile();

		String keywordText = "some Text with special characters ¹²³¼½¬{[]}\\¸";
		userProfile.setNumResults(123);
		userProfile.setContextKeywords(newDummyContextKeywords(7, keywordText));

		OpensearchQueryGenerator generator = new OpensearchQueryGenerator();
		String query = generator.toQuery(userProfile);

		try {
			assertTrue(query.contains(URLEncoder.encode(keywordText,
					CharEncoding.UTF_8)));
			assertTrue(query.contains(URLEncoder.encode(keywordText + "[1]",
					CharEncoding.UTF_8)));
			assertTrue(query.contains(URLEncoder.encode(keywordText + "[6]",
					CharEncoding.UTF_8)));
			assertFalse(query.contains(URLEncoder.encode(keywordText + "[7]",
					CharEncoding.UTF_8)));
			assertTrue(query.contains("&limit=" + userProfile.getNumResults()));
		} catch (UnsupportedEncodingException e) {
			assertTrue(false);
		}
	}

	@Test
	public void toQuery_withNoSecureUserProfileButLimit_expectsPass() {

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.setNumResults(123);
		userProfile.setContextKeywords(new ArrayList<ContextKeyword>());

		OpensearchQueryGenerator generator = new OpensearchQueryGenerator();
		String query = generator.toQuery(userProfile);

		assertTrue(query.contains("&limit=" + userProfile.getNumResults()));
	}

	private List<ContextKeyword> newDummyContextKeywords(int amount,
			String textTemplate) {

		List<ContextKeyword> keywords = new ArrayList<ContextKeyword>();

		for (int idx = 0; idx < amount; idx++) {
			if (idx == 0) {
				keywords.add(new ContextKeyword(textTemplate));
			} else {
				keywords.add(new ContextKeyword(textTemplate + "[" + idx + "]"));
			}
		}
		return keywords;
	}
}
