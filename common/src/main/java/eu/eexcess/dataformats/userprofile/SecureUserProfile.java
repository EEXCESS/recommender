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

    private List<PartnerBadge> partnerList = new ArrayList<PartnerBadge>(); //
    private List<PartnerBadge> protectedPartnerList = new ArrayList<PartnerBadge>();
    private String queryID;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Integer numResults;
    private String gender;
    private Address address;
    private TimeRange timeRange;
    private List<Language> languages = new ArrayList<Language>();
    private List<UserCredentials> userCredentials = new ArrayList<UserCredentials>();
    private List<Interest> interestList = new ArrayList<Interest>();
    private List<ContextKeyword> contextKeywords = new ArrayList<ContextKeyword>();
    private Context context = new Context();

    @Override
    public String toString() {
        return "SecureUserProfile [partnerList=" + getPartnerList() + ", protectedPartnerList=" + getProtectedPartnerList() + ", queryID=" + getQueryID() + ", firstName="
                + getFirstName() + ", lastName=" + getLastName() + ", birthDate=" + getBirthDate() + ", numResults=" + getNumResults() + ", gender=" + getGender() + ", address="
                + getAddress() + ", timeRange=" + getTimeRange() + ", languages=" + getLanguages() + ",  userCredentials=" + getUserCredentials() + ", interestList="
                + getInterestList() + ", contextKeywords=" + getContextKeywords() + ", context=" + getContext() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getBirthDate() == null) ? 0 : getBirthDate().hashCode());
        result = prime * result + ((getContext() == null) ? 0 : getContext().hashCode());
        result = prime * result + ((getContextKeywords() == null) ? 0 : getContextKeywords().hashCode());
        result = prime * result + ((getFirstName() == null) ? 0 : getFirstName().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getInterestList() == null) ? 0 : getInterestList().hashCode());
        result = prime * result + ((getLanguages() == null) ? 0 : getLanguages().hashCode());
        result = prime * result + ((getLastName() == null) ? 0 : getLastName().hashCode());
        result = prime * result + ((getNumResults() == null) ? 0 : getNumResults().hashCode());
        result = prime * result + ((getPartnerList() == null) ? 0 : getPartnerList().hashCode());
        result = prime * result + ((getProtectedPartnerList() == null) ? 0 : getProtectedPartnerList().hashCode());
        result = prime * result + ((getQueryID() == null) ? 0 : getQueryID().hashCode());
        result = prime * result + ((getTimeRange() == null) ? 0 : getTimeRange().hashCode());
        result = prime * result + ((getUserCredentials() == null) ? 0 : getUserCredentials().hashCode());
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
        if (getAddress() == null) {
            if (other.getAddress() != null)
                return false;
        } else if (!getAddress().equals(other.getAddress()))
            return false;
        if (getBirthDate() == null) {
            if (other.getBirthDate() != null)
                return false;
        } else if (!getBirthDate().equals(other.getBirthDate()))
            return false;
        if (getContext() == null) {
            if (other.getContext() != null)
                return false;
        } else if (!getContext().equals(other.getContext()))
            return false;
        if (getContextKeywords() == null) {
            if (other.getContextKeywords() != null)
                return false;
        } else if (!getContextKeywords().equals(other.getContextKeywords()))
            return false;
        if (getFirstName() == null) {
            if (other.getFirstName() != null)
                return false;
        } else if (!getFirstName().equals(other.getFirstName()))
            return false;
        if (getGender() == null) {
            if (other.getGender() != null)
                return false;
        } else if (!getGender().equals(other.getGender()))
            return false;
        if (getInterestList() == null) {
            if (other.getInterestList() != null)
                return false;
        } else if (!getInterestList().equals(other.getInterestList()))
            return false;
        if (getLanguages() == null) {
            if (other.getLanguages() != null)
                return false;
        } else if (!getLanguages().equals(other.getLanguages()))
            return false;
        if (getLastName() == null) {
            if (other.getLastName() != null)
                return false;
        } else if (!getLastName().equals(other.getLastName()))
            return false;
        if (getNumResults() == null) {
            if (other.getNumResults() != null)
                return false;
        } else if (!getNumResults().equals(other.getNumResults()))
            return false;
        if (getPartnerList() == null) {
            if (other.getPartnerList() != null)
                return false;
        } else if (!getPartnerList().equals(other.getPartnerList()))
            return false;
        if (getProtectedPartnerList() == null) {
            if (other.getProtectedPartnerList() != null)
                return false;
        } else if (!getProtectedPartnerList().equals(other.getProtectedPartnerList()))
            return false;
        if (getQueryID() == null) {
            if (other.getQueryID() != null)
                return false;
        } else if (!getQueryID().equals(other.getQueryID()))
            return false;
        if (getTimeRange() == null) {
            if (other.getTimeRange() != null)
                return false;
        } else if (!getTimeRange().equals(other.getTimeRange()))
            return false;
        if (getUserCredentials() == null) {
            if (other.getUserCredentials() != null)
                return false;
        } else if (!getUserCredentials().equals(other.getUserCredentials()))
            return false;
        return true;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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
}
