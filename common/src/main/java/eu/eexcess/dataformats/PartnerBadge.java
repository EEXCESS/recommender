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

import eu.eexcess.dataformats.result.ResultStats;
import eu.eexcess.dataformats.userprofile.FeatureVector;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@XmlRootElement(name = "eexcess-partner-badge") @XmlAccessorType(XmlAccessType.FIELD) @JsonIgnoreProperties(ignoreUnknown = true) @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL) public class PartnerBadge
        implements Serializable {

    private static final long serialVersionUID = -6411801334911587483L;
    public PartnerBadgeStats longTimeStats = new PartnerBadgeStats();
    private String description;
    private String favIconURI;
    private String partnerConnectorEndpoint;
    private Integer ageRange;
    private String lowerDateLimit;
    private String upperDateLimit;
    private List<String> tags;
    private List<PartnerDomain> domainContent = new ArrayList<PartnerDomain>();
    private List<String> languageContent = new ArrayList<String>();
    private String systemId;
    private String queryGeneratorClass;
    private String specialFieldQueryGeneratorClass;
    private Boolean isQueryExpansionEnabled;
    private Boolean isQuerySplittingEnabled;
    private String partnerKey;


    public double expertLevel;




    private FeatureVector featureVector;
    // TODO: Statistics should be moved somewhere else! (Specially the logic for
    // it)
    private PartnerBadgeStats shortTimeStats = new PartnerBadgeStats();


    public double getExpertLevel() {
        return expertLevel;
    }

    public void setExpertLevel(double expertLevel) {
        this.expertLevel = expertLevel;
    }
    public String getSpecialFieldQueryGeneratorClass() {
        return specialFieldQueryGeneratorClass;
    }

    public void setSpecialFieldQueryGeneratorClass(String specialFieldQueryGeneratorClass) {
        this.specialFieldQueryGeneratorClass = specialFieldQueryGeneratorClass;
    }

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
        this.getShortTimeStats().lastResponseTimes.addLast(lastResponseTime);
    }

    /**
     * updates the partner response times (shortTime and longTime) and short
     * time deviation
     *
     * @param respTime
     */
    public synchronized void updatePartnerResponseTime(long respTime) {
        pushLastResponseTimes(respTime);
        boolean first = true;
        for (Long tmpTime : getLastResponseTimes()) {
            if (first) {
                setShortTimeResponseTime(tmpTime);
                first = false;
            } else {
                setShortTimeResponseTime((tmpTime + getShortTimeResponseTime()) / 2);
            }
        }
    }

    public List<ResultStats> getLastQueries() {
        return this.getShortTimeStats().lastQueries;
    }

    public synchronized void addLastQueries(ResultStats lastQuery) {
        this.getShortTimeStats().lastQueries.add(lastQuery);
        if (this.getShortTimeStats().lastQueries.size() > 30)
            this.getShortTimeStats().lastQueries.remove(0);
    }

    public String getFavIconURI() {
        return favIconURI;
    }

    public void setFavIconURI(String favIconURI) {
        this.favIconURI = favIconURI;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
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

    @Override public String toString() {
        return "PartnerBadge [description=" + description + ", favIconURI=" + favIconURI + ", partnerConnectorEndpoint=" + partnerConnectorEndpoint + ", tags=" + tags
                + ", domainContent=" + domainContent + ", languageContent=" + languageContent + ", systemId=" + systemId + ", queryGeneratorClass=" + getQueryGeneratorClass()
                + ", partnerKey=" + getPartnerKey() + "]";
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

    public String getLowerDateLimit() {
        return lowerDateLimit;
    }

    public void setLowerDateLimit(String lowerDateLimit) {
        this.lowerDateLimit = lowerDateLimit;
    }

    public String getUpperDateLimit() {
        return upperDateLimit;
    }

    public void setUpperDateLimit(String upperDateLimit) {
        this.upperDateLimit = upperDateLimit;
    }

    public FeatureVector getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(FeatureVector featureVector) {
        this.featureVector = featureVector;
    }


    public Integer getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(Integer ageRange) {
        this.ageRange = ageRange;
    }

}
