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
package eu.eexcess.dataformats.userprofile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.eexcess.dataformats.PartnerBadge;

/**
 * Basic information about an EEXCESS secure user profile.
 * 
 * @author rkern@know-center.at
 */
@XmlRootElement(name = "eexcess-secure-user-profile")
public class SecureUserProfile implements Serializable {
    private static final long serialVersionUID = 1344062464911638930L;

    private List<PartnerBadge> partnerList;
    private List<PartnerBadge> protectedPartnerList = new ArrayList<PartnerBadge>();
    private String queryID;
    private Integer ageRange;
    private Integer numResults;
    private String gender;
    private Address address;
    private TimeRange timeRange;
    private List<Language> languages = new ArrayList<Language>();
    private List<UserCredentials> userCredentials = new ArrayList<UserCredentials>();
    private List<ContextKeyword> contextKeywords = new ArrayList<ContextKeyword>();
    private List<Interest> interestList = new ArrayList<Interest>();
    private Context context = new Context();


    @XmlElementWrapper(name = "partnerList")
    @XmlElement(name = "partnerList")
    public List<PartnerBadge> getPartnerList() {
        return partnerList;
    }

    public void setPartnerList(List<PartnerBadge> partnerList) {
        this.partnerList = partnerList;
    }

    @XmlElementWrapper(name = "protectedPartnerList")
    @XmlElement(name = "protectedPartnerList")
    public List<PartnerBadge> getProtectedPartnerList() {
        return protectedPartnerList;
    }

    public void setProtectedPartnerList(List<PartnerBadge> protectedPartnerList) {
        this.protectedPartnerList = protectedPartnerList;
    }

	public Integer getNumResults() {
        return numResults;
    }

    public void setNumResults(Integer numResults) {
        this.numResults = numResults;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getQueryID() {
        return queryID;
    }

    public void setQueryID(String queryID) {
        this.queryID = queryID;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    @XmlElementWrapper(name = "languages")
    @XmlElement(name = "languages")
    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    @XmlElementWrapper(name = "userCredentials")
    @XmlElement(name = "userCredentials")
    public List<UserCredentials> getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(List<UserCredentials> userCredentials) {
        this.userCredentials = userCredentials;
    }

    @XmlElementWrapper(name = "interests")
    @XmlElement(name = "interests")
    public List<Interest> getInterestList() {
        return interestList;
    }

    public void setInterestList(List<Interest> interestList) {
        this.interestList = interestList;
    }

    @XmlElementWrapper(name = "contextKeywords")
    public List<ContextKeyword> getContextKeywords() {
        return contextKeywords;
    }

    public void setContextKeywords(List<ContextKeyword> contextKeywords) {
        this.contextKeywords = contextKeywords;
    }

    @XmlElement(name = "context")
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

	public Integer getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(Integer ageRange) {
		this.ageRange = ageRange;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((ageRange == null) ? 0 : ageRange.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result
				+ ((contextKeywords == null) ? 0 : contextKeywords.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result
				+ ((interestList == null) ? 0 : interestList.hashCode());
		result = prime * result
				+ ((languages == null) ? 0 : languages.hashCode());
		result = prime * result
				+ ((numResults == null) ? 0 : numResults.hashCode());
		result = prime * result
				+ ((partnerList == null) ? 0 : partnerList.hashCode());
		result = prime
				* result
				+ ((protectedPartnerList == null) ? 0 : protectedPartnerList
						.hashCode());
		result = prime * result + ((queryID == null) ? 0 : queryID.hashCode());
		result = prime * result
				+ ((timeRange == null) ? 0 : timeRange.hashCode());
		result = prime * result
				+ ((userCredentials == null) ? 0 : userCredentials.hashCode());
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
		SecureUserProfile other = (SecureUserProfile) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (ageRange == null) {
			if (other.ageRange != null)
				return false;
		} else if (!ageRange.equals(other.ageRange))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (contextKeywords == null) {
			if (other.contextKeywords != null)
				return false;
		} else if (!contextKeywords.equals(other.contextKeywords))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (interestList == null) {
			if (other.interestList != null)
				return false;
		} else if (!interestList.equals(other.interestList))
			return false;
		if (languages == null) {
			if (other.languages != null)
				return false;
		} else if (!languages.equals(other.languages))
			return false;
		if (numResults == null) {
			if (other.numResults != null)
				return false;
		} else if (!numResults.equals(other.numResults))
			return false;
		if (partnerList == null) {
			if (other.partnerList != null)
				return false;
		} else if (!partnerList.equals(other.partnerList))
			return false;
		if (protectedPartnerList == null) {
			if (other.protectedPartnerList != null)
				return false;
		} else if (!protectedPartnerList.equals(other.protectedPartnerList))
			return false;
		if (queryID == null) {
			if (other.queryID != null)
				return false;
		} else if (!queryID.equals(other.queryID))
			return false;
		if (timeRange == null) {
			if (other.timeRange != null)
				return false;
		} else if (!timeRange.equals(other.timeRange))
			return false;
		if (userCredentials == null) {
			if (other.userCredentials != null)
				return false;
		} else if (!userCredentials.equals(other.userCredentials))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecureUserProfile [partnerList=" + partnerList
				+ ", protectedPartnerList=" + protectedPartnerList
				+ ", queryID=" + queryID + ", ageRange=" + ageRange
				+ ", numResults=" + numResults + ", gender=" + gender
				+ ", address=" + address + ", timeRange=" + timeRange
				+ ", languages=" + languages + ", userCredentials="
				+ userCredentials + ", contextKeywords=" + contextKeywords
				+ ", interestList=" + interestList + ", context=" + context
				+ "]";
	}
	
	
}
