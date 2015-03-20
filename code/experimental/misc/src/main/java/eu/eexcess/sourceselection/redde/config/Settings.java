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

package eu.eexcess.sourceselection.redde.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.util.Version;

public class Settings {

	public static final Version LuceneVersion = Version.LATEST;
	public static final double RamBufferSizeMB = 512.0;

	public static class TestIndexSettings {

		public String testSetName;
		public String documentsPath;
		public String baseIndexPath;
		public String sampledIndexPath;
		public int topicNumbers[];

		public TestIndexSettings(String testSetName, String documentsPath, String baseIndexPath,
						String sampleIndexPath, int topics[]) {
			this.testSetName = testSetName;
			this.documentsPath = documentsPath;
			this.baseIndexPath = baseIndexPath;
			this.sampledIndexPath = sampleIndexPath;
			this.topicNumbers = topics;
		}
	}

	public static class Topics {
		public static final String TREC04_TOPICS_PATH = "/opt/data/trec-topics/vol-4-topics/topics.201-250";
		public static final String TREC05_TOPICS_PATH = "/opt/data/trec-topics/vol-5-topics/topics.251-300";
	}

	public static HashSet<TestIndexSettings> testSets() {
		HashSet<TestIndexSettings> testSets = new HashSet<TestIndexSettings>();
		// financial times ltd
		testSets.add(new TestIndexSettings("ft", "/opt/data/trec-uncompressed/vol-4/ft", "/opt/data/redde/ft-base",
						"/opt/data/redde/ft-sampled", null));

		// federal register
		// not possible to create index of fr94:
		// testSets.add(new TestIndexSettings("fr94",
		// "/opt/data/trec-uncompressed/vol-4/fr94",
		// "/opt/data/redde/fr94-base", "/opt/data/redde/fr94-sampled"));

		// congressional record
		testSets.add(new TestIndexSettings("cr", "/opt/data/trec-uncompressed/vol-4/cr", "/opt/data/redde/cr-base",
						"/opt/data/redde/cr-sampled", null));

		// foreign broadcast information service
		// not possible to create index of fbis:
		// testSets.add(new TestIndexSettings("fbis",
		// "/opt/data/trec-uncompressed/vol-5/fbis",
		// "/opt/data/redde/fbis-base", "/opt/data/redde/fbis-sampled"));

		// los angeles times
		testSets.add(new TestIndexSettings("latimes", "/opt/data/trec-uncompressed/vol-5/latimes",
						"/opt/data/redde/latimes-base", "/opt/data/redde/latimes-sampled", null));
		return testSets;
	}

	/**
	 * manually selected topics to be used for creating test sets out of trec
	 * data
	 * 
	 * @return
	 */
	public static HashSet<TestIndexSettings> topicBasedTestSets() {
		HashSet<TestIndexSettings> testSets = new HashSet<TestIndexSettings>();
		String path = "/opt/data/redde/by-topic-";
		String topics = "medicine-health";
		testSets.add(new TestIndexSettings(topics, null, path + topics, null, new int[] { 210, 275, 297, 254, 272, 263,
						289 }));
		topics = "violence-arm-crime";
		testSets.add(new TestIndexSettings(topics, null, path + topics, null, new int[] { 213, 222, 246, 240, 250, 282,
						261, 282, 298, 265, 276 }));
		topics = "military";
		testSets.add(new TestIndexSettings(topics, null, path + topics, null, new int[] { 261, 293, 299 }));
		topics = "economic-automobile";
		testSets.add(new TestIndexSettings(topics, null, path + topics, null, new int[] { 247, 203, 230, 274, 290, 294,
						299 }));
		return testSets;
	}

	/**
	 * index containing all TREC data
	 */
	public static TestIndexSettings BaseIndex = new TestIndexSettings("generalized-base",
					"/opt/data/trec-uncompressed", "/opt/data/redde/base-index", "/opt/data/redde/base-index-sapled",
					null);

	public static class WordNet {
		public static final String Path_2_0 = "/opt/data/wordnet/WordNet-2.0/dict";
		public static final String Path_3_0 = "/opt/data/wordnet/WordNet-3.0/dict";
	}

	public static class WordnetDomains {
		public static final String Path = "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223";
		public static final String CSVDomainPath = "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-tree.csv";
	}

	public static class IndexFields {
		public static final String IndexTextField = "text";
		public static final String IndexNameField = "indexName";
	}

	public static boolean isResourceAvailable(Set<TestIndexSettings> settingsSet) {
		for (TestIndexSettings settings : settingsSet) {
			if (false == isResourceAvailable(settings)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isResourceAvailable(TestIndexSettings settings) {

		if (!(new File(settings.documentsPath)).canRead()) {
			return printWarning(settings.documentsPath);
		}
		return true;
	}

	public static boolean isWordNet20ResourceAvailable() {
		if (!(new File(WordNet.Path_2_0)).canRead()) {
			return printWarning(WordNet.Path_2_0);
		}
		return true;
	}

	public static boolean isWordNet30ResourceAvailable() {
		if (!(new File(WordNet.Path_3_0)).canRead()) {
			return printWarning(WordNet.Path_3_0);
		}
		return true;
	}

	public static boolean isWordNetDomainsResourceAvailable() {
		if (!(new File(WordnetDomains.Path)).canRead()) {
			return printWarning(WordnetDomains.Path);
		} else if (!(new File(WordnetDomains.CSVDomainPath)).canRead()) {
			return printWarning(WordnetDomains.CSVDomainPath);
		}
		return true;
	}

	public static boolean isTopicListingAvailable() {
		if (!(new File(Topics.TREC04_TOPICS_PATH).canRead())) {
			return printWarning(Topics.TREC04_TOPICS_PATH);
		} else if (!(new File(Topics.TREC05_TOPICS_PATH).canRead())) {
			return printWarning(Topics.TREC05_TOPICS_PATH);
		}
		return true;
	}

	private static boolean printWarning(String path) {
		System.err.println("resource does not exist [" + path + "]");
		return false;
	}

}
