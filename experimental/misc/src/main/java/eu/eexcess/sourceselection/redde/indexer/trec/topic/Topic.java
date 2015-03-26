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

/**
 * this class represents a TREC-topic. 
 */
package eu.eexcess.sourceselection.redde.indexer.trec.topic;

public class Topic {
	public int topicNumber;
	public String title;
	public String description;
	public String longDescription;

	@Override
	public String toString() {
		return "# [" + topicNumber + "] title [" + removeNewline(title) + "] desc [" + removeNewline(description)
						+ "] longDesc [" + removeNewline(longDescription) + "]";
	}

	private String removeNewline(String s) {
		if (null == s) {
			return "null";
		} else {
			return s.replace("\n", "");
		}
	}

}
