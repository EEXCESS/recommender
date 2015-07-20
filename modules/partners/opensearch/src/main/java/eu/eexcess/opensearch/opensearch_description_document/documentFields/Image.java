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
 * Holds attributes of an xml image tag.
 * 
 * @author Raoul Rubien
 */
public class Image {
	public int height;
	public int width;
	public String type;
	public String url;

	public Image() {
		height = -1;
		width = -1;
		type = null;
		url = null;
	}

	public Image(int height, int width, String type, String url) {
		this.height = height;
		this.width = width;
		this.type = type;
		this.url = url;
	}

	/**
	 * hashCode() and equals() depend on this implementation
	 */
	@Override
	public String toString() {
		return new StringBuilder().append(
				"height[" + height + "] width[" + width + "] type[" + type
						+ "] url[" + url + "]").toString();
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
