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
 */

package eu.eexcess.sourceselection.redde.indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.eexcess.sourceselection.redde.config.ReddeSettings;

import eu.eexcess.sourceselection.redde.indexer.trec.topic.Topic;
import eu.eexcess.sourceselection.redde.indexer.trec.topic.TrecTopicTokenizer;

/**
 * Create test indices from TREC04 and TREC05
 * 
 * @author Raoul Rubien
 */
public class TestSetBuilder {

	public static void main(String[] args) {
		TestSetBuilder generator = new TestSetBuilder();
		if (args.length > 0) {

			if (args[0].compareToIgnoreCase("help") == 0 || args[0].compareToIgnoreCase("--help") == 0
							|| args[0].compareToIgnoreCase("-h") == 0) {
				generator.usage();
			} else if (args[0].compareToIgnoreCase("spread") == 0) {
				generator.indexSpreadDBs();

			} else if (args[0].compareToIgnoreCase("all") == 0) {
				generator.indexSpreadDBs();
				generator.indexCentralizedCompleteDB();
				generator.indexByTopics();
			} else if (args[0].compareTo("by-topic") == 0) {
				generator.indexCentralizedCompleteDB();
				generator.indexByTopics();

			} else {
				generator.indexCentralizedCompleteDB();
			}
		} else {
			generator.indexCentralizedCompleteDB();
		}
	}

	/**
	 * make indices out of trec data as defined in {@link eu.eexcess.sourceselection.redde.config.ReddeSettings}
	 */
	public void indexSpreadDBs() {
		for (ReddeSettings.TestIndexSettings set : ReddeSettings.testSets()) {
			TrecToLuceneIndexBuilder builder = new TrecToLuceneIndexBuilder(set.documentsPath, set.baseIndexPath,
							set.testSetName, ReddeSettings.RamBufferSizeMB, ReddeSettings.LuceneVersion);
			builder.index();
		}
	}

	/**
	 * make index out of trec data
	 */
	public void indexCentralizedCompleteDB() {
		TrecToLuceneIndexBuilder builder = new TrecToLuceneIndexBuilder(ReddeSettings.BaseIndex.documentsPath,
				ReddeSettings.BaseIndex.baseIndexPath, ReddeSettings.BaseIndex.testSetName, ReddeSettings.RamBufferSizeMB,
				ReddeSettings.LuceneVersion);
		builder.index();
	}

	public void usage() {
		System.out.println("Arguments:");
		System.out.println();
		System.out.println("complete (default) \t - index centralized complete spread dytabase and by topics");
		System.out.println("spread             \t - index to many distributed databases");
		System.out.println("by-topic           \t - index by topics");
		System.out.println("all                \t - index distributed and centralized complete database");
		System.out.println("help               \t - print help message");
	}

	/**
	 * create lucene index from trec data using topic title or description (if
	 * no title given) as query.
	 */
	public void indexByTopics() {
		HashSet<ReddeSettings.TestIndexSettings> testSets = ReddeSettings.topicBasedTestSets();

		for (ReddeSettings.TestIndexSettings set : testSets) {
			try {
				System.out.println("indexing [" + set.testSetName + "] ...");
				List<Topic> topics = getTrecTopicsByID(set.topicNumbers);

				IndexTopicDocumentsExtractor extractor = new IndexTopicDocumentsExtractor(
						ReddeSettings.BaseIndex.baseIndexPath, set.baseIndexPath, ReddeSettings.LuceneVersion);
				extractor.storeTopicDocs(topics.toArray(new Topic[0]));
				extractor.close();
			} catch (Exception e) {
				System.err.println("failed indexing [" + set.testSetName + "]");
				e.printStackTrace();
			}
		}
	}

	/**
	 * retrieve topics by topic id
	 * 
	 * @param topicNumbers
	 * @return
	 * @throws IOException
	 */
	private List<Topic> getTrecTopicsByID(int topicNumbers[]) throws IOException {

		List<Topic> topics = new ArrayList<Topic>();

		TrecTopicTokenizer tokenizer = new TrecTopicTokenizer();
		for (Topic topic : tokenizer.tokenize(new FileInputStream(ReddeSettings.Topics.TREC04_TOPICS_PATH))) {
			if (containsPrimitive(topicNumbers, topic.topicNumber)) {
				topics.add(topic);
			}
		}

		tokenizer.reset();
		for (Topic topic : tokenizer.tokenize(new FileInputStream(ReddeSettings.Topics.TREC05_TOPICS_PATH))) {
			if (containsPrimitive(topicNumbers, topic.topicNumber)) {
				topics.add(topic);
			}
		}
		return topics;
	}

	private boolean containsPrimitive(int array[], int primitive) {
		for (int i : array) {
			if (i == primitive) {
				return true;
			}
		}
		return false;
	}
}