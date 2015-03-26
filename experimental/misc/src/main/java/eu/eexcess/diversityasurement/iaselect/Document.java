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

package eu.eexcess.diversityasurement.iaselect;

import java.util.Set;

/**
 * Instances of this class keep information about a document and categories the
 * document belongs to and their distribution.
 * <p>
 * See also [Agrawal, R., Gollapudi, S., Halverson, A., & Ieong, S. (2009).
 * Diversifying search results. In Proceedings of the Second ACM International
 * Conference on Web Search and Data Mining - WSDM ’09 (p. 5). New York, New
 * York, USA: ACM Press. http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public class Document extends MessageCategories {

	public String name;

	public Document(String name, Set<Category> categories) {
		super(categories);
		this.name = name;
	}

	public Document(String name, Category category) {
		this(name);
		super.addCategory(category);
	}

	public Document(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Document [name=" + name + "]";
	}
}
