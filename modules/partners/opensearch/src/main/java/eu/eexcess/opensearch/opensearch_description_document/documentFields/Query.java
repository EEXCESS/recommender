/**
 * Copyright (C) 2014
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
 */

package eu.eexcess.opensearch.opensearch_description_document.documentFields;

/**
 * Holds attributes of a query tag.
 * 
 * @author Raoul Rubien
 */
public class Query {
	public String role;
	public String title;
	public String searchTerms;
	public String language;
	public String inputEncoding;
	public String outputEncoding;
	public int totalResults;
	public int count;
	public int startIndex;
	public int startPage;

	public Query() {
		title = null;
		searchTerms = null;
		language = "*";
		inputEncoding = "UTF-8";
		outputEncoding = "UTF-8";
		totalResults = -1;
		count = -1;
		startIndex = -1;
		startPage = -1;
	}

	public Query(String role, String title, String searchTerms,
			String language, String inputEncoding, String outputEncodign,
			int totalResults, int count, int startIndex, int startPage) {
		this.role = role;
		this.title = title;
		this.searchTerms = searchTerms;
		this.language = language;
		this.inputEncoding = inputEncoding;
		this.outputEncoding = outputEncodign;
		this.totalResults = totalResults;
		this.startIndex = startIndex;
		this.startPage = startPage;
		this.count = count;
	}

	/**
	 * hashCode() and equals() depend on this implementation
	 */
	@Override
	public String toString() {
		return new StringBuilder().append(
				"role[" + role + "] title[" + title + "]  searchTerms["
						+ searchTerms + "] language [" + language
						+ "] inputEncoding[" + inputEncoding
						+ "] outputEncoding" + outputEncoding
						+ "] totalresults[" + totalResults + "] count[" + count
						+ "] startindex[" + startIndex + "] startPage["
						+ startPage + "]").toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (this.hashCode() == obj.hashCode());
	}
}
