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
import java.util.List;

/**
 * ExtendedSecureUserProfilePair support class
 * 
 * @author hziak
 *
 */
public class ESUPPair implements Cloneable, Serializable {
    private static final long serialVersionUID = 6130633513641518529L;

    private String source;
    private List<ESUPSource> sourceClasses;

    public ESUPPair(String source, List<ESUPSource> sourceClass) {
        this.source = source;
        this.setSourceClasses(sourceClass);
    }

    public ESUPPair(ESUPPair mean) {
        this.source = new String(mean.source);
        this.sourceClasses = new ArrayList<ESUPSource>();
        for (ESUPSource esupSource : mean.getSourceClasses()) {
            sourceClasses.add(new ESUPSource(esupSource));
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<ESUPSource> getSourceClasses() {
        return sourceClasses;
    }

    public void setSourceClasses(List<ESUPSource> sourceClass) {
        this.sourceClasses = sourceClass;
    }

    public ESUPSource getBiggestSourceClass() {
        ESUPSource result = null;
        double value = 0.0;
        for (ESUPSource esupSource : sourceClasses) {
            double linksTotalValue = esupSource.getLinksTotalValue();
            if (linksTotalValue > value) {
                result = esupSource;
                value = linksTotalValue;
            }
        }
        return result;
    }

    public String toString() {
        return source + " " + getBiggestSourceClass();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((sourceClasses == null) ? 0 : sourceClasses.hashCode());
        return result;
    }

    /**
     * Careful, Equals overwritten
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ESUPPair) {
            if (this.source.equals(((ESUPPair) obj).getSource()))
                return true;
        }
        return false;
    }

}