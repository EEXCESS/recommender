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
import java.util.HashSet;
import java.util.Map;

/**
 * V(d|q,c) - relevance of a document or the quality of a document d for query q
 * when the intended category is c
 * <p>
 * TODO: this is just a lookup at the moment; implementation is missing
 * <p>
 * See also [Agrawal, R., Gollapudi, S., Halverson, A., & Ieong, S. (2009).
 * Diversifying search results. In Proceedings of the Second ACM International
 * Conference on Web Search and Data Mining - WSDM ’09 (p. 5). New York, New
 * York, USA: ACM Press. http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public class DocumentQualityValueV {

	Map<Document, HashSet<Category>> documentQualities = new HashMap<Document, HashSet<Category>>();

	public DocumentQualityValueV() {
	}

	/**
	 * V(d|q,c) - relevance of a document or the quality of a document d for
	 * query q when the intended category is c
	 * 
	 * @param d
	 *            one document out of R(q)
	 * @param q
	 *            query used for R(q)
	 * @param c
	 *            category the document belongs to
	 * @return document relevance
	 * @throws IllegalArgumentException
	 *             if document or category is not found
	 */
	public double V(Document d, Query q, Category c) throws IllegalArgumentException {
		for (Category documentCategory : documentQualities.get(d)) {
			if (documentCategory.equals(c)) {
				return documentCategory.probability;
			}
		}
		throw new IllegalArgumentException("failed fetching document quality value: category[" + c.name
						+ "] for document[" + d.name + "] not found");
	}
}
