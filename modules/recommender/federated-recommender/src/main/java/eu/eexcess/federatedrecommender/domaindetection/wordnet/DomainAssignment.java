/* Copyright (C) 2010 
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
package eu.eexcess.federatedrecommender.domaindetection.wordnet;

import java.io.Serializable;

class DomainAssignment implements Comparable<DomainAssignment>, Serializable {
	private static final long serialVersionUID = -6947985645523081761L;
	public final String domain;
    public final double weight;
    
    /**
     * Creates a new instance of this class.
     * @param domain
     * @param weight
     */
    public DomainAssignment(String domain, double weight) {
        this.domain = domain;
        this.weight = weight;
    }

    @Override
    public int compareTo(DomainAssignment o) {
        int d = 0;
        if (weight > o.weight) {
            d = -1;
        } else {
            if (weight < o.weight) {
                d = +1;
            } else {
                d = domain.compareTo(o.domain);
            }
        }
        return d;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DomainAssignment other = (DomainAssignment)obj;
        if (domain == null) {
            if (other.domain != null) return false;
        } else if (!domain.equals(other.domain)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s^%f", domain, weight);
    }
}