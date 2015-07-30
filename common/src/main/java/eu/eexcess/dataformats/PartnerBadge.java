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
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.eexcess.dataformats.result.ResultStats;

@XmlRootElement(name = "eexcess-partner-badge")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PartnerBadge implements Serializable {

    private static final long serialVersionUID = -6411801334911587483L;

    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "favIconURI")
    private String favIconURI;
    @XmlElement(name = "partnerConnectorEndpoint")
    private String partnerConnectorEndpoint;

    @XmlElement(name = "lowerAgeLimit")
    private Integer lowerAgeLimit;

    @XmlElement(name = "tag")
    @XmlElementWrapper(name = "tags")
    private List<String> tags;

    @XmlElement(name = "domainContent")
    private List<PartnerDomain> domainContent = new ArrayList<PartnerDomain>();
    @XmlElement(name = "languageContent")
    private List<String> languageContent = new ArrayList<String>();

    @XmlElement(name = "systemId")
    private String systemId;

    @XmlElement(name = "queryGeneratorClass")
    private String queryGeneratorClass;

    @XmlElement(name = "isQueryExpansionEnabled")
    private Boolean isQueryExpansionEnabled;

    @XmlElement(name = "isQuerySplitingEnabled")
    private Boolean isQuerySplittingEnabled;

    @XmlElement(name = "partnerKey")
    // has to be the same value than in SecureUserProfile
    private String partnerKey;

    @XmlElement(name = "shortTimeStats", required = false)
    private PartnerBadgeStats shortTimeStats = new PartnerBadgeStats();

    // TODO: Statistics should be moved somewere else! (Specially the logic for
    // it)

    @XmlElement(name = "longTimeStats", required = false)
    public PartnerBadgeStats longTimeStats = new PartnerBadgeStats();

    public Long getShortTimeResponseTime() {
        return getShortTimeStats().shortTimeResponseTime;
    }

    public void setShortTimeResponseTime(Long shortTimeResponseTime) {
        this.getShortTimeStats().shortTimeResponseTime = shortTimeResponseTime;
    }

    public List<PartnerDomain> getDomainContent() {
        return domainContent;
    }

    public void setDomainContent(List<PartnerDomain> domainContent) {
        this.domainContent = domainContent;
    }

    public List<String> getLanguageContent() {
        return languageContent;
    }

    public void setLanguageContent(List<String> languages) {
        this.languageContent = languages;
    }

    public String getPartnerConnectorEndpoint() {
        return partnerConnectorEndpoint;
    }

    public void setPartnerConnectorEndpoint(String endpoint) {
        this.partnerConnectorEndpoint = endpoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Deque<Long> getLastResponseTimes() {
        return this.getShortTimeStats().lastResponseTimes;
    }

    public void pushLastResponseTimes(Long lastResponseTime) {
        while (this.getShortTimeStats().lastResponseTimes.size() > 50)
            this.getShortTimeStats().lastResponseTimes.pop();
        this.getShortTimeStats().lastResponseTimes.push(lastResponseTime);
    }

    /**
     * updates the partner response times (shortTime and longTime) and short
     * time deviation
     * 
     * @param partner
     * @param respTime
     */
    public void updatePartnerResponseTime(long respTime) {
        pushLastResponseTimes(respTime);
        java.util.Iterator<Long> iter = getLastResponseTimes().iterator();
        Long first = iter.next();
        while (first == null && iter.hasNext())
            first = iter.next();
        if (first != null)
            setShortTimeResponseTime(first);
        while (iter.hasNext()) {
            Long next = iter.next();
            if (next != null)
                setShortTimeResponseTime((getShortTimeResponseTime() + next) / 2);
        }

        double[] values = new double[getLastResponseTimes().toArray().length];
        Object[] respTimes = getLastResponseTimes().toArray();
        int count = 0;
        for (Object long1 : respTimes) {
            if (long1 != null)
                values[count] = ((Long) long1).doubleValue();
            count++;
        }
    }

    public List<ResultStats> getLastQueries() {
        return this.getShortTimeStats().lastQueries;
    }

    public void addLastQueries(ResultStats lastQuerie) {
        this.getShortTimeStats().lastQueries.addLast(lastQuerie);
        if (this.getShortTimeStats().lastQueries.size() > 50)
            this.getShortTimeStats().lastQueries.removeFirst();
    }

    public String getFavIconURI() {
        return favIconURI;
    }

    public void setFavIconURI(String favIconURI) {
        this.favIconURI = favIconURI;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
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
        PartnerBadge other = (PartnerBadge) obj;
        if (systemId == null) {
            if (other.systemId != null)
                return false;
        } else if (!systemId.equals(other.systemId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartnerBadge [description=" + description + ", favIconURI=" + favIconURI + ", partnerConnectorEndpoint=" + partnerConnectorEndpoint + ", tags=" + tags
                + ", domainContent=" + domainContent + ", languageContent=" + languageContent + ", systemId=" + systemId + ", queryGeneratorClass=" + getQueryGeneratorClass()
                + ", partnerKey=" + getPartnerKey() + ", shortTimeStats=" + getShortTimeStats() + ", longTimeStats=" + longTimeStats + "]";
    }

    public String getPartnerKey() {
        return partnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    public PartnerBadgeStats getShortTimeStats() {
        return shortTimeStats;
    }

    public void setShortTimeStats(PartnerBadgeStats shortTimeStats) {
        this.shortTimeStats = shortTimeStats;
    }

    public String getQueryGeneratorClass() {
        return queryGeneratorClass;
    }

    public void setQueryGeneratorClass(String queryGeneratorClass) {
        this.queryGeneratorClass = queryGeneratorClass;
    }

    public Integer getLowerAgeLimit() {
        return lowerAgeLimit;
    }

    public void setLowerAgeLimit(Integer lowerAgeLimit) {
        this.lowerAgeLimit = lowerAgeLimit;
    }

    public Boolean isQueryExpansionEnabled() {
        return isQueryExpansionEnabled;
    }

    public void setIsQueryExpansionEnabled(Boolean isQueryExpansionEnabled) {
        this.isQueryExpansionEnabled = isQueryExpansionEnabled;
    }

    public Boolean isQuerySplittingEnabled() {
        return isQuerySplittingEnabled;
    }

    public void setIsQuerySplittingEnabled(Boolean isQuerySplittingEnabled) {
        this.isQuerySplittingEnabled = isQuerySplittingEnabled;
    }

}