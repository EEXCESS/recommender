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
package eu.eexcess.dataformats.evaluation;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import eu.eexcess.dataformats.result.ResultList;

/**
 * 
 * @author hziak
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationResultList extends ResultList implements Serializable {
    private static final long serialVersionUID = 2401989277808424955L;

    @XmlAttribute
    private String numSelect;

    @SuppressWarnings("unused")
    private EvaluationResultList() {
    };

    public EvaluationResultList(ResultList parent) {
        this.provider = parent.provider;
        this.results = parent.results;
        this.partnerResponseState = parent.partnerResponseState;
        this.totalResults = parent.totalResults;
    }

    @Override
    public String toString() {
        return "EvaluationResultList [numSelect=" + getNumSelect() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getNumSelect() == null) ? 0 : getNumSelect().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        EvaluationResultList other = (EvaluationResultList) obj;
        if (getNumSelect() == null) {
            if (other.getNumSelect() != null)
                return false;
        } else if (!getNumSelect().equals(other.getNumSelect()))
            return false;
        return true;
    }

    public String getNumSelect() {
        return numSelect;
    }

    public void setNumSelect(String numSelect) {
        this.numSelect = numSelect;
    }

}
