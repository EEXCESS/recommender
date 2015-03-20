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

package eu.eexcess.sourceselection.redde.dbsampling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.extjwnl.JWNLException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.dbsampling.DBSampler;

public class DBSamplerTest {

	@SuppressWarnings("resource")
	@Test
	public void generateRandomWords_expectRandomWords_notExceptional() {
		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet30ResourceAvailable()) {
			try {
				DBSampler sampler = new DBSampler(Settings.BaseIndex.baseIndexPath,
								Settings.BaseIndex.sampledIndexPath, Settings.LuceneVersion, Settings.WordNet.Path_3_0);

				Set<String> randomWords = new HashSet<String>();
				int numWords = 10000;
				for (int wordIdx = 0; wordIdx < numWords; wordIdx++) {
					String newWord = sampler.drawRandomWord(randomWords);
					assertNotNull(newWord);
					assertTrue(randomWords.add(newWord));
				}

				assertEquals(numWords, randomWords.size());

			} catch (JWNLException e) {
				assertTrue(false);
			}
		}

	}

	@Test
	public void sample_writetRandomSamplesToIndex_atLeast50PercentOfDesiredSamplesStored() {
		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet30ResourceAvailable()) {
			try {
				DBSampler sampler = new DBSampler(Settings.BaseIndex.baseIndexPath,
								Settings.BaseIndex.sampledIndexPath, Settings.LuceneVersion, Settings.WordNet.Path_3_0);

				sampler.open();
				sampler.sample(80, 4);
				int minSamples = (int) (0.5 * (80.0 * 4.0));
				assertTrue(minSamples <= sampler.size());
				sampler.close();
			} catch (JWNLException | IOException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void estimationCalculator_calcSimpleEstimation_expectCorrectValue() {
		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet30ResourceAvailable()) {
			try {
				DBSampler sampler = new DBSampler(Settings.BaseIndex.baseIndexPath,
								Settings.BaseIndex.sampledIndexPath, Settings.LuceneVersion, Settings.WordNet.Path_3_0);

				sampler.open();
				assertEquals(272698.1300089047, sampler.estimationCalculator(30000, 1123, 10208, false), 1E-10);
				assertEquals(2726981.300089047, sampler.estimationCalculator(300000, 1123, 10208, false), 1E-09);
				sampler.close();
			} catch (JWNLException | IOException e) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void estimateSize_estimateDatabaseSize_expectGoodApproximation() {
		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet30ResourceAvailable()) {
			try {
				DBSampler sampler = new DBSampler(Settings.BaseIndex.baseIndexPath,
								Settings.BaseIndex.sampledIndexPath, Settings.LuceneVersion, Settings.WordNet.Path_3_0);

				sampler.open();
				sampler.sample(300, 4);

				int numSamples = 5;
				double estimatedSize = sampler.estimateSize(numSamples);
				double errorRate = sampler.absoluteErrorRate();

				System.out.println("estimated size[" + estimatedSize + "] error rate[" + errorRate
								+ "] failed resample attempts of total [" + (sampler.resampleAttempts() - numSamples)
								+ "/" + sampler.resampleAttempts() + "]");
				sampler.close();
			} catch (JWNLException | ParseException | IOException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

}
