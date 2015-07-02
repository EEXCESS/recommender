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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement(name = "eexcess-partner-badge")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) 
public class PartnerBadge implements Serializable{
	
	private static final long serialVersionUID = -6411801334911587483L;

	
	@XmlElement(name="systemId")
    public String systemId;
	
	@XmlElement(name="queryGeneratorClass")
	public String queryGeneratorClass; // = LuceneQueryGenerator.class.getName();   
    
	@XmlElement(name="partnerKey") //has to be the same value than in SecureUserProfile
    public String partnerKey;
	@XmlElement(name="description")
    private String description;
	@XmlElement(name="favIconURI")
    private String favIconURI;
	@XmlElement(name="partnerConnectorEndpoint")
    private String partnerConnectorEndpoint;
	
	@XmlElement(name="shortTimeStats", required=false) 
	public PartnerBadgeStats shortTimeStats = new PartnerBadgeStats();
	//TODO: Statistics should be moved somewere else! (Specially the logic for it)

	@XmlElement(name="longTimeStats", required=false)
	public PartnerBadgeStats longTimeStats = new PartnerBadgeStats();	

	public Long getShortTimeResponseTime() {
		return shortTimeStats.shortTimeResponseTime;
	}
	public void setShortTimeResponseTime(Long shortTimeResponseTime) {
		this.shortTimeStats.shortTimeResponseTime = shortTimeResponseTime;
	}

	@XmlElement(name="tag")
	@XmlElementWrapper(name="tags")
    private List<String> tags;


    @XmlElement(name="languageContent")
    private List<String> languageContent = new ArrayList<String>();
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
	public Stack<Long> getLastResponseTimes() {
		return this.shortTimeStats.lastResponseTimes;
	}
	public void pushLastResponseTimes(Long lastResponseTime) {
		while(this.shortTimeStats.lastResponseTimes.size()>50)
			this.shortTimeStats.lastResponseTimes.pop();
		this.shortTimeStats.lastResponseTimes.push(lastResponseTime);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((partnerConnectorEndpoint == null) ? 0 : partnerConnectorEndpoint.hashCode());
		result = prime * result
				+ ((partnerKey == null) ? 0 : partnerKey.hashCode());
		result = prime * result
				+ ((systemId == null) ? 0 : systemId.hashCode());
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (partnerConnectorEndpoint == null) {
			if (other.partnerConnectorEndpoint != null)
				return false;
		} else if (!partnerConnectorEndpoint.equals(other.partnerConnectorEndpoint))
			return false;
		if (partnerKey == null) {
			if (other.partnerKey != null)
				return false;
		} else if (!partnerKey.equals(other.partnerKey))
			return false;
		if (systemId == null) {
			if (other.systemId != null)
				return false;
		} else if (!systemId.equals(other.systemId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PartnerBadge [systemId=" + systemId + ", partnerKey="
				+ partnerKey + ", description=" + description + ", endpoint="
				+ partnerConnectorEndpoint + ", tags=" + tags + ", languageContent=" + languageContent + "]";
	}
	public void setShortTimeResponsDeviation() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * updates the partner response times (shortTime and longTime) and short time deviation
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
//		if (getLongTimeResponseTime() != null)
//			setLongTimeResponseTime((getLongTimeResponseTime() + respTime) / 2);
//		else
//			setLongTimeResponseTime(respTime);
		double[] values = new double[getLastResponseTimes().toArray().length];

//		StandardDeviation standartDeviation = new StandardDeviation();
		Object[] respTimes = getLastResponseTimes().toArray();
		int count = 0;
		for (Object long1 : respTimes) {
			if (long1 != null)
				values[count] = ((Long) long1).doubleValue();
			count++;
		}
//		setShortTimeResponsDeviation(new Double(standartDeviation.evaluate(values)).longValue());
	}
	public LinkedList<ResultStats> getLastQueries() {
		return this.shortTimeStats.lastQueries;
	}
	public void addLastQueries(ResultStats lastQuerie) {
		this.shortTimeStats.lastQueries.addLast(lastQuerie);
		if(this.shortTimeStats.lastQueries.size()>50)
			this.shortTimeStats.lastQueries.removeFirst();
	}
	
	
	
	
	
}
