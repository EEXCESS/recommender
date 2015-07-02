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
package eu.eexcess.federatedrecommender.utils.esuputils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ESUPSource implements Serializable {

    private static final long serialVersionUID = -9016455754784897178L;
    private List<ESUPLink> links;
    private String linkClass;

    public ESUPSource(String linkClass, List<ESUPLink> links) {
        this.linkClass = linkClass;
        this.links = links;
    }

    public ESUPSource(ESUPSource esupSource) {
        this.linkClass = new String(esupSource.getLinkClass());
        this.links = new ArrayList<ESUPLink>();
        for (ESUPLink esupLink : esupSource.getLinks()) {
            links.add(new ESUPLink(esupLink));
        }
    }

    public List<ESUPLink> getLinks() {
        return links;
    }

    public void setLinks(List<ESUPLink> links) {
        this.links = links;
    }

    public String getLinkClass() {
        return linkClass;
    }

    public void setLinkClass(String linkClass) {
        this.linkClass = linkClass;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((linkClass == null) ? 0 : linkClass.hashCode());
        result = prime * result + ((links == null) ? 0 : links.hashCode());
        return result;
    }

    /**
     * Careful: Equals just checks for linkClass String not for the Links!
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ESUPSource) {
            if (this.linkClass.equals(((ESUPSource) o).getLinkClass())) {
                return true;
            }
        }
        return false;
    }

    public void sortESUPLinks() {
        Comparator<ESUPLink> esupComparator = new Comparator<ESUPLink>() {

            @Override
            public int compare(ESUPLink o1, ESUPLink o2) {
                if (o1.getLink().equals(o2.getLink()))
                    return 0;
                return (o1.getLink() > o2.getLink()) ? -1 : 1;
            }
        };
        Collections.sort(links, esupComparator);
    }

    public double getLinksTotalValue() {
        double value = 0.0;
        for (ESUPLink eLink : links) {
            value += eLink.getLink();
        }
        return value;
    }

    public String toString() {
        return linkClass + " \n"
        // + links;
        ;
    }

    public void eraseOwnLink() {
        loop: for (ESUPLink esupLink : links) {
            if (esupLink.getClassName().contains(linkClass)) {
                esupLink.setLink(0.0);
                break loop;
            }
        }
    }

}
