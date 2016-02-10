/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.dataformats.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Encapsulation of results in EEXCESS data format.
 * 
 * @author plopez@know-center.at
 */
@XmlRootElement(name = "eexcess-results")
public class ResultList implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    public String provider;

    private ResultStats resultStats;

    @XmlAttribute
    public int totalResults;

    @XmlElement(name = "result")
    public LinkedList<Result> results = new LinkedList<Result>();

    @XmlElement(name = "partnerResponseState")
    public List<PartnerResponseState> partnerResponseState = new ArrayList<PartnerResponseState>();

    @XmlElement(name = "queryID")
    public String queryID;

    @Override
    public String toString() {
        return "ResultList [provider=" + provider + ", resultStats=" + resultStats + ", totalResults=" + totalResults + ", results=" + results + ", partnerResponseState="
                + partnerResponseState + ", queryID=" + queryID + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((partnerResponseState == null) ? 0 : partnerResponseState.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        result = prime * result + ((queryID == null) ? 0 : queryID.hashCode());
        result = prime * result + ((resultStats == null) ? 0 : resultStats.hashCode());
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        result = prime * result + totalResults;
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
        ResultList other = (ResultList) obj;
        if (partnerResponseState == null) {
            if (other.partnerResponseState != null)
                return false;
        } else if (!partnerResponseState.equals(other.partnerResponseState))
            return false;
        if (provider == null) {
            if (other.provider != null)
                return false;
        } else if (!provider.equals(other.provider))
            return false;
        if (queryID == null) {
            if (other.queryID != null)
                return false;
        } else if (!queryID.equals(other.queryID))
            return false;
        if (resultStats == null) {
            if (other.resultStats != null)
                return false;
        } else if (!resultStats.equals(other.resultStats))
            return false;
        if (results == null) {
            if (other.results != null)
                return false;
        } else if (!results.equals(other.results))
            return false;
        if (totalResults != other.totalResults)
            return false;
        return true;
    }

    @XmlElement(name = "resultStats")
    public ResultStats getResultStats() {
        return resultStats;
    }

    public void setResultStats(ResultStats resultStats) {
        this.resultStats = resultStats;
    }


}
