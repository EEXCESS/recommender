/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
	 * adds all c ∈ cats to categories if categories.contains(cats) == false.
	 * 
	 * @param cats
	 * @throws IllegalArgumentException
	 */
	public void addCategories(Set<Category> cats) throws IllegalArgumentException {
		for (Category c : cats) {
			addCategory(c);
		}
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
