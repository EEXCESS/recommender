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
package eu.eexcess.dataformats;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "eexcess-partner-badge")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartnerBadge implements Serializable{
	
	private static final long serialVersionUID = -6411801334911587483L;

	
	@XmlElement(name="systemId")
    public String systemId;
	
	@XmlElement(name="partnerKey") //has to be the same value than in SecureUserProfile
    public String partnerKey;
	@XmlElement(name="description")
    private String description;
	@XmlElement(name="endpoint")
    private String endpoint;
	
	//Begin response time
	@XmlTransient
	private Stack<Long> lastResponseTimes= new Stack<Long>() ;
	@XmlElement(name="shortTimeResponseTimes")
	private Long shortTimeResponseTime;
	@XmlElement(name="longTimeResponseTimes")
	private Long longTimeResponseTime;
	//End response time	
	
	public Long getShortTimeResponseTime() {
		return shortTimeResponseTime;
	}
	public void setShortTimeResponseTime(Long shortTimeResponseTime) {
		this.shortTimeResponseTime = shortTimeResponseTime;
	}
	public Long getLongTimeResponseTime() {
		return longTimeResponseTime;
	}
	public void setLongTimeResponseTime(Long longTimeResponseTime) {
		this.longTimeResponseTime = longTimeResponseTime;
	}

	@XmlElement(name="tag")
	@XmlElementWrapper(name="tags")
    private List<String> tags;
	
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
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
		return lastResponseTimes;
	}
	public void pushLastResponseTimes(Long lastResponseTime) {
		while(lastResponseTimes.size()>10)
			lastResponseTimes.pop();
		this.lastResponseTimes.push(lastResponseTime);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((endpoint == null) ? 0 : endpoint.hashCode());
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
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
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
				+ endpoint + ", tags=" + tags + "]";
	}
	
	
	
}
