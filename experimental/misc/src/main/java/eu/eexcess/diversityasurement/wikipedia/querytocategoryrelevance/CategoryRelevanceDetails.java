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

public class CategoryRelevanceDetails implements Serializable {
	private static final long serialVersionUID = 2662323424344375687L;

	public static class TopCategoryRelevance implements Serializable {
		private static final long serialVersionUID = 2990339220973007267L;
		public String categoryName = "";
		public Integer categoryId = -1;
		public Double categoryRelevance = Double.NaN;
	};

	// public static class SubCategory implements Serializable {
	// private static final long serialVersionUID = 7496329867387904375L;
	// public String categoryName = "";
	// public Integer categoryId = -1;
	// }

	public String categoryName;
	public Integer categoryId = -1;

	public String query;
	public String queryDescription;
	public Integer queryNumber;
	
	public Integer documenId;

	public TopCategoryRelevance[] topCategoryRelevances;
	// public SubCategory[] startCategories;
}
