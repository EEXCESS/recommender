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

import java.util.HashSet;
import java.util.Set;

import edu.stanford.nlp.util.StringUtils;

/**
 * This class holds information about a message (string) its categories
 * belonging to it and the category probability that it belongs to the message.
 * 
 * @author Raoul Rubien
 *
 */
public class MessageCategories {
	private Set<Category> categories = new HashSet<Category>();

	public MessageCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public MessageCategories() {
		categories = new HashSet<Category>();
	}

	/**
	 * adds c to categories if categories.contains(c) == false.
	 * 
	 * @param c
	 * @throws IllegalArgumentException
	 */
	public void addCategory(Category c) throws IllegalArgumentException {
		if (categories.contains(c)) {
			throw new IllegalArgumentException("failed adding category: [" + c.name + "] already exist");
		}
		categories.add(c);
	}

	/**
	 * returns category c* if c.equals(c*) == true.
	 * 
	 * @param c
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Category getCategory(Category c) throws IllegalArgumentException {
		for (Category iterator : categories) {
			if (iterator.equals(c)) {
				return c;
			}
		}
		throw new IllegalArgumentException("failed fetching category: [" + c.name + "] not found");
	}

	/**
	 * @return the whole category set
	 */
	public Set<Category> categories() {
		return categories;
	}

	@Override
	public String toString() {
		return "MessageCategories [categories=" + StringUtils.join(categories, ", ") + "]";
	}

}
