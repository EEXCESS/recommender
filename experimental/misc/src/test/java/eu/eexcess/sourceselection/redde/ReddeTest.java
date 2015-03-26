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

package eu.eexcess.sourceselection.redde;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.sourceselection.redde.Redde;
import eu.eexcess.sourceselection.redde.Redde.QueryRelevance;
import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.dbsampling.DatabaseDetails;

public class ReddeTest {

	private static Map<String, HashSet<String>> keyWords = new HashMap<String, HashSet<String>>();
	private static HashSet<String> setsUnderTest = new HashSet<String>();

	@BeforeClass
	public static void constructKeywords() {
		HashSet<String> latimes = new HashSet<String>();
		HashSet<String> lt = new HashSet<String>();
		HashSet<String> cr = new HashSet<String>();

		latimes.add("santa");
		latimes.add("market");
		latimes.add("children");
		latimes.add("fund");
		latimes.add("car");
		latimes.add("kill");
		latimes.add("america");

		lt.add("earn");
		lt.add("nation");
		lt.add("europe");
		lt.add("growth");
		lt.add("parliment");
		lt.add("health");
		lt.add("strike");

		cr.add("bill");
		cr.add("country");
		cr.add("help");
		cr.add("committe");
		cr.add("america");
		cr.add("honor");
		cr.add("citizen");
		cr.add("economy");
		cr.add("america");

		keyWords = new HashMap<String, HashSet<String>>();
		keyWords.put("latimes", latimes);
		keyWords.put("lt", lt);
		keyWords.put("cr", cr);

		setsUnderTest.add("ft");
		setsUnderTest.add("cr");
		setsUnderTest.add("latimes");
	}

	@Test
	public void meanAbsoluteError_calculateMeanWithoutResult_expectError() {
		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
							Redde.newDefaultParameters());
			try {
				estimator.meanAbsoluteError();
			} catch (IllegalStateException e) {
				return;
			}
			assertTrue(false);
		}
	}

	@Test
	public void meanAbsoluteError_calculateMean_expectAccurateValue() {

		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			try {
				Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
								Redde.newDefaultParameters());

				DatabaseDetails result = new DatabaseDetails();
				result.absoluteErrorRate = 3.14159265359;
				estimator.sourceDetails = new HashMap<String, DatabaseDetails>();
				estimator.sourceDetails.put("cake", result);

				result = new DatabaseDetails();
				result.absoluteErrorRate = 9.81274;
				estimator.sourceDetails.put("g", result);

				result = new DatabaseDetails();
				result.absoluteErrorRate = 1.41421356237;
				estimator.sourceDetails.put("something-of-two", result);

				result = new DatabaseDetails();
				result.absoluteErrorRate = 299792458;
				estimator.sourceDetails.put("fast", result);

				assertEquals(7.494811809213654E7, estimator.meanAbsoluteError(), 1E-7);
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void queryRelevance_calculateQueryRelevance_expectNotExceptional() {

		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			Redde estimator = null;

			int maxTestRetry = 10;
			boolean hasTestRunThrough = false;
			while (true) {
				estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
								Redde.newDefaultParameters());
				// estimate database sizes and collect some more details
				try {
					estimator.estimateSourcesSize();
				} catch (IOException e) {
					System.out.println("some sample indices are empty -> resample/re-test [" + maxTestRetry + "]");
					if (maxTestRetry-- <= 0) {
						break;
					}
					continue;
				}

				double meanAbsoluteError = estimator.meanAbsoluteError();
				System.out.println("est. MAER=" + meanAbsoluteError);
				System.out.println("est. centralized complete db size = "
								+ estimator.estimatedTotalDocumentsOfCentralizedCompleteDB());

				HashSet<QueryRelevance> relevanceResults = new HashSet<QueryRelevance>();
				try {
					System.out.println();
					for (Map.Entry<String, HashSet<String>> entry : keyWords.entrySet()) {
						for (String query : entry.getValue()) {
							for (String source : setsUnderTest) {

								System.out.print("relevance(query=" + query + ", index=" + source + ")");
								DatabaseDetails dtd = new DatabaseDetails();
								dtd.indexName = source;
								QueryRelevance relevance = estimator.queryRelevance(query, dtd);
								relevanceResults.add(relevance);
								System.out.println("{filtered [" + relevance.documents.numberScoredFiltered + "/"
												+ relevance.documents.numberScoredCentralized
												+ "] while centralized index contains ["
												+ relevance.documents.numberCentralized + "]} = " + relevance.relevance);
							}
						}
					}
					// outputCsv(relevanceResults);
				} catch (ParseException | IOException | IllegalStateException e) {
					e.printStackTrace();
					assertTrue(false);
				}
				hasTestRunThrough = true;
				break;
			}

			assertTrue(hasTestRunThrough);
		}

	}

	@Test
	public void tqueryRelevanceDistribution_calculateQueryRelevanceDistribution_expectAccurateResult() {
		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
							Redde.newDefaultParameters());
			Set<QueryRelevance> relevanceResults = createMockResults(10);

			for (QueryRelevance queryRelevance : relevanceResults) {
				System.out.println("qrel: " + queryRelevance.indexName + " " + queryRelevance.relevance);

			}
			assertEquals(11, relevanceResults.size());

			double expectedDistribution = 0.1538461538461539;
			DatabaseDetails source = new DatabaseDetails();
			source.indexName = "test";

			assertEquals(expectedDistribution, estimator.queryRelevanceDistribution(relevanceResults, source), 1E-16);
		}
	}

	@Test
	public void queryRelevanceDistribution_calculateQueryRelevanceDistribution_toubleTestSetEntry_expectIllegalArgumentException() {

		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {

			Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
							Redde.newDefaultParameters());
			Set<QueryRelevance> relevanceResults = createMockResults(10);
			assertEquals(11, relevanceResults.size());

			int idx = 0;
			for (QueryRelevance relevance : relevanceResults) {

				if (idx == 3) {
					relevance.indexName = "other10";
				}
				idx++;
			}

			DatabaseDetails source = new DatabaseDetails();
			source.indexName = "test";
			try {
				estimator.queryRelevanceDistribution(relevanceResults, source);
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				return;
			} catch (Exception e) {
				assertTrue(false);
			}
			assertTrue(false);
		}
	}

	@Test
	public void queryRelevanceDistribution_calculateQueryRelevanceDistribution_doubleTargetTestSetEntry_expectIllegalArgumentException() {
		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
							Redde.newDefaultParameters());
			Set<QueryRelevance> relevanceResults = createMockResults(10);
			assertEquals(11, relevanceResults.size());

			int idx = 0;
			for (QueryRelevance relevance : relevanceResults) {

				if (idx == 3) {
					relevance.indexName = "test";
				}
				idx++;
			}

			DatabaseDetails source = new DatabaseDetails();
			source.indexName = "test";
			try {
				estimator.queryRelevanceDistribution(relevanceResults, source);
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				return;
			} catch (Exception e) {
				assertTrue(false);
			}
			assertTrue(false);
		}
	}

	private Set<QueryRelevance> createMockResults(int approximateNumResults) {
		LinkedHashSet<QueryRelevance> relevanceResults = new LinkedHashSet<QueryRelevance>();
		QueryRelevance mockResult = new QueryRelevance();

		mockResult.indexName = "test";
		mockResult.relevance = 10;
		relevanceResults.add(mockResult);

		while (approximateNumResults > 0) {

			mockResult = new QueryRelevance();
			mockResult.indexName = "other" + approximateNumResults;
			mockResult.relevance = approximateNumResults;
			relevanceResults.add(mockResult);
			approximateNumResults--;
		}

		return relevanceResults;
	}

	@Test
	public void estimatedTotalDocumentsOfCentralizedCompleteDB_sumUp_expectAccurateSum() {
		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			try {
				Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
								Redde.newDefaultParameters());

				DatabaseDetails result = new DatabaseDetails();
				result.estimatedDBSize = 3.14159265359;
				estimator.sourceDetails = new HashMap<String, DatabaseDetails>();
				estimator.sourceDetails.put("cake", result);

				result = new DatabaseDetails();
				result.estimatedDBSize = 9.81274;
				estimator.sourceDetails.put("g", result);

				result = new DatabaseDetails();
				result.estimatedDBSize = 1.41421356237;
				estimator.sourceDetails.put("something-of-two", result);

				result = new DatabaseDetails();
				result.estimatedDBSize = 299792458;
				estimator.sourceDetails.put("fast", result);

				assertEquals(2.997924723685462E8, estimator.estimatedTotalDocumentsOfCentralizedCompleteDB(), 1E-7);
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void compareableQueryRelevance_orderDescByRelevanceDistribution_givenDescOrderedEntries_expectCorrectOrder() {
		QueryRelevance relevance = new QueryRelevance();
		TreeSet<QueryRelevance> rankedSet = new TreeSet<QueryRelevance>();

		for (int i = 10; i > 0; i--) {
			relevance.relevanceDistribution = i;
			relevance.indexName = new String("qr" + i);
			rankedSet.add(relevance);
			relevance = new QueryRelevance();
		}

		QueryRelevance predecessor = rankedSet.first();
		Iterator<QueryRelevance> iterator = rankedSet.iterator();
		iterator.next();
		for (; iterator.hasNext();) {
			QueryRelevance current = iterator.next();
			assertTrue(predecessor.relevanceDistribution <= current.relevanceDistribution);
			predecessor = current;
		}
	}

	@Test
	public void compareableQueryRelevance_orderDescByRelevanceDistribution_givenAscOrderedEntries_expectCorrectOrder() {
		QueryRelevance relevance = new QueryRelevance();
		TreeSet<QueryRelevance> rankedSet = new TreeSet<QueryRelevance>();

		for (int i = 0; i < 10; i++) {
			relevance.relevanceDistribution = i;
			rankedSet.add(relevance);
			relevance = new QueryRelevance();
		}

		QueryRelevance predecessor = rankedSet.first();
		Iterator<QueryRelevance> iterator = rankedSet.iterator();
		iterator.next();
		for (; iterator.hasNext();) {
			QueryRelevance current = iterator.next();
			assertTrue(predecessor.relevanceDistribution <= current.relevanceDistribution);
			predecessor = current;
		}
	}

	@Test
	public void rankSources_rankTestSetsGivenWord_expectNotExceptional() {
		if (Settings.isResourceAvailable(Settings.testSets()) && Settings.isWordNet30ResourceAvailable()) {
			Redde estimator = new Redde(Settings.testSets(), Settings.WordNet.Path_3_0, Settings.LuceneVersion,
							Redde.newDefaultParameters());

			try {
				Set<QueryRelevance> ranks = estimator.rankSources("finance");

				System.out.println("sources to be ranked:");
				for (Map.Entry<String, DatabaseDetails> entry : estimator.getSourceDatabaseDetails().entrySet()) {
					System.out.println(entry.getKey());
				}

				System.out.println("ranked sources: ");
				for (QueryRelevance queryRelevance : ranks) {
					System.out.println(queryRelevance.indexName + " - " + queryRelevance.relevanceDistribution + "/"
									+ queryRelevance.relevance + "ratio " + queryRelevance.probabilityOfRelevanceRatio
									+ "");
				}

			} catch (Exception e) {
				e.printStackTrace();
				// TODO
				// FIXME NPE
				/*
				 * java.lang.IllegalArgumentException: division by 0 at
				 * eu.eexcess.sourceselection
				 * .redde.Redde.queryRelevanceDistribution(Redde.java:402) at
				 * eu.eexcess
				 * .sourceselection.redde.Redde.rankSources(Redde.java:465) at
				 * eu.eexcess.sourceselection.redde.ReddeTest.
				 * rankSources_rankTestSetsGivenWord_expectNotExceptional
				 * (ReddeTest.java:380)
				 */
				assertTrue(false);
			}
		}
	}
}