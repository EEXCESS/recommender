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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Reads topics from trex-xml file.
 * 
 * @author Raoul Rubien
 */
public class TrecTopicTokenizer {

	// private StringBuilder currentTopicData = null;
	private List<Topic> topics = null;
	private Topic currentTopic = null;

	public static class TrecTopicTags {
		public static final String Topic = "top";
		public static final String TopicNumber = "num";
		public static final String Title = "title";
		public static final String Description = "desc";
		public static final String NarrativeDescription = "narr";
	}

	/**
	 * Tokenize trec-topic xml file respecting missing closing xml tags.
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public List<Topic> tokenize(InputStream input) throws IOException {
		Writer writer = new StringWriter();
		IOUtils.copy(input, writer);
		String data = writer.toString();

		Matcher startTopicMatcher = getStartTopicTagMatcher(data);
		Matcher endTopicTagMatcher = getEndTopicTagMatcher(data);

		int processedChars = 0;
		while (startTopicMatcher.find(processedChars)) {
			if (endTopicTagMatcher.find(startTopicMatcher.end())) {
				String topicData = data.substring(processedChars, endTopicTagMatcher.start());
				Matcher startTagMatcher = getStartGenericTagMatcher(topicData);

				while (startTagMatcher.find()) {
					Matcher endTagMatcher = getStartGenericTagMatcher(topicData);
					if (endTagMatcher.find(startTagMatcher.end())) {
						assignValueToField(startTagMatcher.group(),
										topicData.substring(startTagMatcher.end(), endTagMatcher.start()));
					} else {
						// reached last tag in topic
						assignValueToField(startTagMatcher.group(),
										topicData.substring(startTagMatcher.end(), topicData.length()));
					}

				}

			} else {
				throw new IllegalArgumentException("expected topic end tag");
			}
			processedChars = endTopicTagMatcher.end();
			topics.add(currentTopic);
			currentTopic = new Topic();
		}
		return topics;
	}

	/**
	 * assign value to correct field according to tag
	 * @param tag indicates the destination field
	 * @param value the value to store at field
	 * @throws IllegalArgumentException
	 */
	private void assignValueToField(String tag, String value) throws IllegalArgumentException {

		switch (getNameFromTag(tag)) {
		case TrecTopicTags.Title:
			currentTopic.title = value.replace("Topic:", "").replace("\n", "").trim();
			break;

		case TrecTopicTags.Description:
			currentTopic.description = value.replace("Description:", "").replace("\n", "").trim();
			break;

		case TrecTopicTags.NarrativeDescription:
			currentTopic.longDescription = value.replace("Narrative:", "").replace("\n", "").trim();
			break;

		case TrecTopicTags.TopicNumber:
			String stringNumber = null;
			try {
				stringNumber = value.replace("Number:", "").replace("\n", "").trim();
				currentTopic.topicNumber = Integer.parseInt(stringNumber);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("cannot parse integer from [" + stringNumber + "]");
			}
			break;

		default:
			break;
		}
	}

	private Matcher getStartTopicTagMatcher(String toBeMatched) {
		Pattern startPattern = Pattern.compile("(<" + TrecTopicTags.Topic + ">)");
		return startPattern.matcher(toBeMatched);
	}

	private Matcher getEndTopicTagMatcher(String toBeMatched) {
		Pattern endPattern = Pattern.compile("(</" + TrecTopicTags.Topic + ">)");
		return endPattern.matcher(toBeMatched);
	}

	private Matcher getStartGenericTagMatcher(String toBeMatched) {
		Pattern startPattern = Pattern.compile("(<[a-zA-Z]*>)");
		return startPattern.matcher(toBeMatched);
	}

	public TrecTopicTokenizer() {
		reset();
	}

	public void reset() {
		topics = new ArrayList<Topic>();
		currentTopic = new Topic();
	}

	private String getNameFromTag(String tag) {
		Pattern p = Pattern.compile("</?([A-Za-z]*)>");
		Matcher m = p.matcher(tag);

		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}
