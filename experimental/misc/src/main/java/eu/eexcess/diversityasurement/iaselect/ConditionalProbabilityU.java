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

import java.util.HashMap;
import java.util.Set;

/**
 * Conditional probability that a query q belongs to category c given that all
 * documents in S fail to satisfy the user.
 * <p>
 * Implementation of U(c|q,S) as described in [Agrawal, R., Gollapudi, S.,
 * Halverson, A., & Ieong, S. (2009). Diversifying search results. In
 * Proceedings of the Second ACM International Conference on Web Search and Data
 * Mining - WSDM ’09 (p. 5). New York, New York, USA: ACM Press.
 * http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public class ConditionalProbabilityU {

	private HashMap<Category, Double> probabilitiesU;

	/**
	 * Constructs this class and initializes probabilities for the case:
	 * <p>
	 * if S=Ø, ∀c, U(c|q,S) = P(c|q)
	 * 
	 * @param categories
	 *            set of c ∈ C(q)
	 */
	public ConditionalProbabilityU(Set<Category> categories) {
		probabilitiesU = new HashMap<Category, Double>(categories.size());

		for (Category c : categories) {
			probabilitiesU.put(c, ProbabilityDistributionCQ.P(c));
		}
	}

	/**
	 * U(c|q,S) - conditional probability that the query q belongs to category c
	 * given that all documents in S fail to satisfy the user
	 * <p>
	 * 
	 * @param c
	 *            category
	 * @param q
	 *            query (treated as constant)
	 * @param S
	 *            set of already selected documents (treated as constant)
	 * @return
	 */
	public double U(Category c, Query q, Set<Document> S) {
		return probabilitiesU.get(c);
	}

	/**
	 * ∀ c ∈ { C(d*) ⋂ C(q)), U(c|q,S) ← (1 - V(d*|q,c)) * U(c|q,S) - updates
	 * U(c|q,S) in relation to its previous value and the document quality value
	 * V(d*|q,c).
	 * 
	 * @param c set of categories ∈ { C(d*) ⋂ C(q))
	 * @param q query (treated as constant)
	 * @param S set of already seen documents (treated as constant)
	 * @param d the desired document to update U(c|q,S)
	 * @param V document quality values 
	 */
	public void updateU(Category c, Query q, Set<Document> S, Document d, DocumentQualityValueV V) {
		double previousU = probabilitiesU.get(c);
		double newU = (1 - V.V(d, q, c)) * previousU;
		probabilitiesU.put(c, newU);
	}

	@Override
	public String toString() {

		boolean isFirst = true;
		StringBuilder mapString = new StringBuilder();
		for (HashMap.Entry<Category, Double> entry : probabilitiesU.entrySet()) {
			if (!isFirst) {
				mapString.append(", ");
			}
			isFirst = false;
			mapString.append(entry.getKey() + "=" + entry.getValue());
		}

		return "U:{" + mapString + "}";
	}
}
