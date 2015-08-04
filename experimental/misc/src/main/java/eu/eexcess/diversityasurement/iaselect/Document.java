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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Instances of this class keep information about a document and categories the
 * document belongs to and their distribution.
 * <p>
 * See also [Agrawal, R., Gollapudi, S., Halverson, A., & Ieong, S. (2009).
 * Diversifying search results. In Proceedings of the Second ACM International
 * Conference on Web Search and Data Mining - WSDM ’09 (p. 5). New York, New
 * York, USA: ACM Press. http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public class Document extends MessageCategories {

    // public static double maxDocumentScore = 0.0f;
    public double documentScore = 0.0f;

    public String name;
    public Integer documentId;

    public int priority;

    public Document(String name, Set<Category> categories, Integer documentId) {
        this(name, categories);
        this.documentId = documentId;
    }

    public Document(String name, Set<Category> categories) {
        super(categories);
        this.name = name;
    }

    public Document(String name, Category category) {
        this(name);
        super.addCategory(category);
    }

    public Document(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((documentId == null) ? 0 : documentId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Document other = (Document) obj;
        if (documentId == null) {
            if (other.documentId != null)
                return false;
        } else if (!documentId.equals(other.documentId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Document [name=" + name + ", documentId=" + documentId + ", documentScore=" + documentScore + "]";
    }

    public List<Category> getTopCategories(int size) {
        List<Category> list = new ArrayList<Category>(categories());
        Comparator<Category> comparator = new Comparator<Category>() {

            @Override
            public int compare(Category o1, Category o2) {
                if (o1.probability < o2.probability) {
                    return -1;
                } else if (o1.probability > o2.probability)
                    return 1;
                return 0;
            }
        };
        Collections.sort(list, comparator);
        size = (size > list.size()) ? list.size() : size;
        return list.subList(0, size);
    }

}
