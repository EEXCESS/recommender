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
 * Holds attributes of an url tag.
 * 
 * @author Raoul Rubien
 */
public class Url {

	public enum UrlRel {
		RESULTS, SUGGESTIONS, SELF, COLLECTION, UNDEFINED
	}

	public String type;
	public String template; // config template url
	public UrlRel rel;
	public int indexOffset;
	public int pageOffset;

	public Url() {
		type = null;
		template = null;
		rel = UrlRel.RESULTS;
		indexOffset = -1;
		pageOffset = -1;

	}

	public Url(String type, String template, UrlRel rel,
			int indexOffset, int pageOffset) {
		this.type = type;
		this.template = template;
		this.rel = rel;
		this.indexOffset = indexOffset;
		this.pageOffset = pageOffset;
	}

	/**
	 * hashCode() and equals() depend on this implementation
	 */
	@Override
	public String toString() {
		return new StringBuilder().append(
				"type[" + type + "] template["
						+ template + "] rel[" + rel + "] indexOffset["
						+ indexOffset + "] pageOffset[" + pageOffset + "]")
				.toString();
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
