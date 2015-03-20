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

package eu.eexcess.sourceselection.redde.indexer.trec.topic;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.indexer.trec.topic.Topic;
import eu.eexcess.sourceselection.redde.indexer.trec.topic.TrecTopicTokenizer;

public class TrecTopicTokenizerTest {

	@Test
	public void tokenize_givenValidFile_expectNotExceptional_verifyingTopic278() {
		try {
			TrecTopicTokenizer tokenizer = new TrecTopicTokenizer();
			List<Topic> topics = tokenizer.tokenize(new FileInputStream(Settings.Topics.TREC05_TOPICS_PATH));

			boolean isSpecificTopicValid = false;
			for (Topic topic : topics) {
				if (topic.topicNumber == 278
								&& topic.title.compareTo("DNA Information about Human Ancestry") == 0
								&& topic.description
												.compareTo("A relevant document will discuss geneticists findings concerning the ancestry of the world's peoples.") == 0
								&& topic.longDescription
												.compareTo("To be relevant, a chosen item will discuss the genetic code research currently being done to determine the mysteries of mankind's origins and migrations.") == 0) {
					isSpecificTopicValid = true;
				}
			}
			assertTrue(isSpecificTopicValid);

		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
