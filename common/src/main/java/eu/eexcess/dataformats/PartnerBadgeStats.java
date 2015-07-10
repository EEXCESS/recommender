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
package eu.eexcess.dataformats;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.eexcess.dataformats.result.ResultStats;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PartnerBadgeStats implements Serializable {

    private static final long serialVersionUID          = -227986625685031717L;

    @XmlElement(name = "requestCount")
    public Integer            requestCount              = 0;

    @XmlElement(name = "failedRequestCount")
    public Integer            failedRequestCount        = 0;

    @XmlElement(name = "failedRequestTimeoutCount")
    public Integer            failedRequestTimeoutCount = 0;

    @XmlElement(name = "lastQueries")
    public List<ResultStats>  lastQueries               = new LinkedList<ResultStats>();

    // Begin response time
    @XmlTransient
    public Deque<Long>        lastResponseTimes         = new ArrayDeque<Long>();
    @XmlElement(name = "shortTimeResponseTimes")
    public Long               shortTimeResponseTime;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((failedRequestCount == null) ? 0 : failedRequestCount.hashCode());
        result = prime * result + ((failedRequestTimeoutCount == null) ? 0 : failedRequestTimeoutCount.hashCode());
        result = prime * result + ((lastQueries == null) ? 0 : lastQueries.hashCode());
        result = prime * result + ((lastResponseTimes == null) ? 0 : lastResponseTimes.hashCode());
        result = prime * result + ((requestCount == null) ? 0 : requestCount.hashCode());
        result = prime * result + ((shortTimeResponseTime == null) ? 0 : shortTimeResponseTime.hashCode());
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
        PartnerBadgeStats other = (PartnerBadgeStats) obj;
        if (failedRequestCount == null) {
            if (other.failedRequestCount != null)
                return false;
        } else if (!failedRequestCount.equals(other.failedRequestCount))
            return false;
        if (failedRequestTimeoutCount == null) {
            if (other.failedRequestTimeoutCount != null)
                return false;
        } else if (!failedRequestTimeoutCount.equals(other.failedRequestTimeoutCount))
            return false;
        if (lastQueries == null) {
            if (other.lastQueries != null)
                return false;
        } else if (!lastQueries.equals(other.lastQueries))
            return false;
        if (lastResponseTimes == null) {
            if (other.lastResponseTimes != null)
                return false;
        } else if (!lastResponseTimes.equals(other.lastResponseTimes))
            return false;
        if (requestCount == null) {
            if (other.requestCount != null)
                return false;
        } else if (!requestCount.equals(other.requestCount))
            return false;
        if (shortTimeResponseTime == null) {
            if (other.shortTimeResponseTime != null)
                return false;
        } else if (!shortTimeResponseTime.equals(other.shortTimeResponseTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartnerBadgeStats [requestCount=" + requestCount + ", failedRequestCount=" + failedRequestCount + ", failedRequestTimeoutCount="
                + failedRequestTimeoutCount + ", lastQueries=" + lastQueries + ", lastResponseTimes=" + lastResponseTimes + ", shortTimeResponseTime="
                + shortTimeResponseTime + "]";
    }

    // End response time

}