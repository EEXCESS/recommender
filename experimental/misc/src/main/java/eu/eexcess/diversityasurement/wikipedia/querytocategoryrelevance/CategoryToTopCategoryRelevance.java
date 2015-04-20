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

package eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * describes the relevance of one category to n top most categories
 * 
 * @author Raoul Rubien
 *
 */
public class CategoryToTopCategoryRelevance implements Serializable {
	private static final long serialVersionUID = 2662323424344375687L;

	public static class CategoryRelevance implements Serializable {

		public static class DescRelevanceComparator implements Comparator<CategoryRelevance> {
			@Override
			public int compare(CategoryRelevance o1, CategoryRelevance o2) {
				if (o1.categoryRelevance == o2.categoryRelevance) {
					return 0;
				}
				if (o1.categoryRelevance > o2.categoryRelevance) {
					return -1;
				}
				return 1;
			}
		}

		private static final long serialVersionUID = 2990339220973007267L;
		public String categoryName = "";
		public Integer categoryId = -1;
		public Double categoryRelevance = Double.NaN;

		@Override
		public String toString() {
			return "CategoryRelevance [categoryName=" + categoryName + ", categoryId=" + categoryId
							+ ", categoryRelevance=" + categoryRelevance + "]";
		}
	};

	public String categoryName;
	public Integer categoryId = -1;

	/**
	 * optional details how the category was chosen
	 */
	public String query;
	public String queryDescription;

	/**
	 * relevances to top most categories
	 */
	public CategoryRelevance[] topCategoryRelevances;

	@Override
	public String toString() {
		return "CategoryToTopCategoryRelevance [categoryName=" + categoryName + ", categoryId=" + categoryId
						+ ", query=" + query + ", queryDescription=" + queryDescription + ", topCategoryRelevances="
						+ Arrays.toString(topCategoryRelevances) + "]";
	}

}
