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
