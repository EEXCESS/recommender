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
package eu.eexcess.federatedrecommender.dataformats;

import java.io.Serializable;

import eu.eexcess.dataformats.result.Result;

public class ResultSimilarity extends Result implements Serializable {

    private static final long serialVersionUID = 8720498791053242424L;
    private double similarity;

    /**
     * is NOT producing a defensive copy!
     * 
     * @param result
     */

    public ResultSimilarity(Result result) {
        this.date = result.date;
        this.documentBadge = result.documentBadge;
        this.language = result.language;
        this.licence = result.licence;
        this.mediaType = result.mediaType;

        this.description = result.description;
        this.previewImage = result.previewImage;
        this.title = result.title;

    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double calcSimilarity(Result result) {
        Double returnValue = 0.0;
        Double valueCount = 0.0;

        if (this.documentBadge.provider != null && result.documentBadge.provider != null) {
            returnValue += this.documentBadge.provider.compareTo(result.documentBadge.provider) == 0 ? 1.0 : 0.0;
            valueCount += 1.0;
        }
        if (this.description != null && result.description != null) {
            returnValue += this.description.compareTo(result.description) == 0 ? 1.0 : 0.0;
            valueCount += 1.0;
        }

        if (this.documentBadge.id != null && result.documentBadge.id != null) {
            returnValue += this.documentBadge.id.compareTo(result.documentBadge.id) == 0 ? 3.0 : 0.0; // higher
                                                                                                      // weight
                                                                                                      // for
                                                                                                      // id
                                                                                                      // ->
                                                                                                      // should
                                                                                                      // mean
                                                                                                      // same
                                                                                                      // result
                                                                                                      // was
                                                                                                      // returned
                                                                                                      // twice
            valueCount += 3.0;
        }

        if (valueCount.intValue()==0)
            return 0.0;
        return returnValue / valueCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(similarity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        ResultSimilarity other = (ResultSimilarity) obj;
        if (Double.doubleToLongBits(similarity) != Double.doubleToLongBits(other.similarity))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ResultSimilarity [similarity=" + similarity + "]";
    }

}
