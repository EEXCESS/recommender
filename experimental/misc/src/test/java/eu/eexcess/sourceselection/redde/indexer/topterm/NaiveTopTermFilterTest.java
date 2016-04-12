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

package eu.eexcess.sourceselection.redde.indexer.topterm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

import eu.eexcess.sourceselection.redde.config.ReddeSettings;

public class NaiveTopTermFilterTest {

	@Test
	public void getTopTerms_fetchNTopTerms_expectNTermsAndNotExceptional() {
		if (ReddeSettings.isResourceAvailable(ReddeSettings.BaseIndex) && ReddeSettings.isWordNet30ResourceAvailable()) {
			try {
				NaiveTopTermFilter testSetBuilder = new NaiveTopTermFilter(ReddeSettings.BaseIndex.baseIndexPath,
						ReddeSettings.WordNet.Path_3_0);
				String[] terms = testSetBuilder.getTopTerms(0, 99);
				for (String term : terms) {
					System.out.println("term " + term);
				}
				assertEquals(100, terms.length);
				testSetBuilder.close();
				testSetBuilder = null;
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void getTopUnrelatedTerms_expectNTerms_notExceptional() {
		if (ReddeSettings.isResourceAvailable(ReddeSettings.BaseIndex) && ReddeSettings.isWordNet30ResourceAvailable()) {
			try {
				NaiveTopTermFilter builder = new NaiveTopTermFilter(ReddeSettings.BaseIndex.baseIndexPath,
						ReddeSettings.WordNet.Path_3_0);

				TreeSet<WordRelation> topUnrelated = builder.getTopUnrelatedTerms(200, 300);
				System.out.println(WordRelation.fieldsToString());
				for (WordRelation wr : topUnrelated) {
					System.out.println(wr);
				}
				builder.close();
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}
}
