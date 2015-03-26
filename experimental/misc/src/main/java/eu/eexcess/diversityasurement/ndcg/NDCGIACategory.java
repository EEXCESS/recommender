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
*/
package eu.eexcess.diversityasurement.ndcg;

/**
 * 
 * @author hziak
 *
 */
public class NDCGIACategory {

	private String categoryName;
	private Double documentWeight;
	private Double queryWeight;

	public NDCGIACategory(String categoryName, Double documentWeight, Double queryWeight) {
		this.categoryName = categoryName;
		this.documentWeight = documentWeight;
		this.queryWeight = queryWeight;
	}

	public Double getWeight() {
		return documentWeight;
	}

	public void setWeight(Double weight) {
		this.documentWeight = weight;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Double getQueryWeight() {
		return queryWeight;
	}

	public void setQueryWeight(Double queryWeight) {
		this.queryWeight = queryWeight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
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
		NDCGIACategory other = (NDCGIACategory) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NDCGIACategory [categoryName=" + categoryName + ", weight=" + documentWeight + ", queryWeight="
						+ queryWeight + "]";
	}

}
