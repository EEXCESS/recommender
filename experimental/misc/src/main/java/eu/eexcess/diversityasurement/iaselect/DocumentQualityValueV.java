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

/**
 * V(d|q,c) - relevance of a document or the quality of a document d for query q
 * when the intended category is c
 * <p>
 * See also [Agrawal, R., Gollapudi, S., Halverson, A., & Ieong, S. (2009).
 * Diversifying search results. In Proceedings of the Second ACM International
 * Conference on Web Search and Data Mining - WSDM ’09 (p. 5). New York, New
 * York, USA: ACM Press. http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public interface DocumentQualityValueV {

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
     */
    public double v(Document d, Query q, Category c) throws IllegalArgumentException;
}
