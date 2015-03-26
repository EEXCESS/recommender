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

/**
 * This class is a namespace for P(c|q)
 * <p>
 * See [Agrawal, R., Gollapudi, S., Halverson, A., & Ieong, S. (2009).
 * Diversifying search results. In Proceedings of the Second ACM International
 * Conference on Web Search and Data Mining - WSDM ’09 (p. 5). New York, New
 * York, USA: ACM Press. http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public class ProbabilityDistributionCQ {

	/**
	 * P(c|q) - assumed that there is a known distribution that specifies the
	 * probability of a given query belonging to given categories, P(c|q) is the
	 * probability a category belongs to query q.
	 * 
	 * @param c
	 *            category
	 * @param q
	 *            query
	 * @return probability q belongs to c
	 */
	public static double P(Category c, Query q) throws IllegalArgumentException {
		return P(q.getCategory(c));
	}

	/**
	 * @param c
	 *            category
	 * @return probability associated with given c
	 */
	static double P(Category c) {
		return c.probability;
	}
}
