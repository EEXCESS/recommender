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

package eu.eexcess.sourceselection.redde.indexer.trec;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.config.Settings.TestIndexSettings;
import eu.eexcess.sourceselection.redde.indexer.IndexTopicDocumentsExtractor;
import eu.eexcess.sourceselection.redde.indexer.trec.topic.Topic;
import eu.eexcess.sourceselection.redde.indexer.trec.topic.TrecTopicTokenizer;

public class IndexTopicDocumentsExtractorTest {

	@Test
	public void storeTopics_expectNotExcetptional() {
		if (Settings.isTopicListingAvailable()) {
			try {
				TestIndexSettings testSet = Settings.topicBasedTestSets().iterator().next();
				assertTrue(testSet.topicNumbers.length > 0);

				List<Topic> topics = getTrecTopicsByID(testSet.topicNumbers);
				assertTrue(topics.size() > 0);

				IndexTopicDocumentsExtractor extractor = new IndexTopicDocumentsExtractor(
								Settings.BaseIndex.baseIndexPath, testSet.baseIndexPath, Settings.LuceneVersion);

				extractor.storeTopicDocs(topics.toArray(new Topic[0]));
				extractor.close();
			} catch (ParseException | IOException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	private List<Topic> getTrecTopicsByID(int topicNumbers[]) throws IOException {
		List<Topic> topics = new ArrayList<Topic>();

		for (Topic topic : getTrec4Topics()) {
			if (containsPrimitive(topicNumbers, topic.topicNumber)) {
				topics.add(topic);
			}
		}

		for (Topic topic : getTrec5Topics()) {
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

	private List<Topic> getTrec4Topics() throws IOException {
		TrecTopicTokenizer tokenizer = new TrecTopicTokenizer();
		return tokenizer.tokenize(new FileInputStream(Settings.Topics.TREC04_TOPICS_PATH));
	}

	private List<Topic> getTrec5Topics() throws IOException {
		TrecTopicTokenizer tokenizer = new TrecTopicTokenizer();
		return tokenizer.tokenize(new FileInputStream(Settings.Topics.TREC05_TOPICS_PATH));
	}

}
