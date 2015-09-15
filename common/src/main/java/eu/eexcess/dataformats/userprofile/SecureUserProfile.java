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

import javax.xml.bind.annotation.XmlAttribute;
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

    @XmlElementWrapper(name = "partnerList")
    @XmlElement(name = "partnerList")
    public List<PartnerBadge> partnerList = new ArrayList<PartnerBadge>(); //
    @XmlElementWrapper(name = "protectedPartnerList")
    @XmlElement(name = "protectedPartnerList")
    public List<PartnerBadge> protectedPartnerList = new ArrayList<PartnerBadge>();
    @XmlAttribute
    public String queryID;
    @XmlAttribute
    public String firstName;
    @XmlAttribute
    public String lastName;
    @XmlAttribute
    public Date birthDate;
    @XmlAttribute
    public Integer numResults;
    @XmlAttribute
    public String gender;
    @XmlElement(name = "address")
    public Address address;
    @XmlElement(name = "timeRange")
    public TimeRange timeRange;

    @XmlElementWrapper(name = "languages")
    @XmlElement(name = "languages")
    public List<Language> languages = new ArrayList<Language>();

    //@XmlElementWrapper(name = "userLocations")
    //@XmlElement(name = "userLocations")
    //public List<UserLocation> userLocations = new ArrayList<UserLocation>();

    @XmlElementWrapper(name = "userCredentials")
    @XmlElement(name = "userCredentials")
    public List<UserCredentials> userCredentials = new ArrayList<UserCredentials>();

    //@XmlElementWrapper(name = "history")
    //@XmlElement(name = "history")
   // public List<History> history = new ArrayList<History>();

    @XmlElementWrapper(name = "interests")
    @XmlElement(name = "interests")
    public List<Interest> interestList = new ArrayList<Interest>();

    @XmlElementWrapper(name = "contextKeywords")
    // @XmlElement(name="contextKeywords")
    public List<ContextKeyword> contextKeywords = new ArrayList<ContextKeyword>();

    @XmlElement(name = "context")
    public Context context = new Context();

   // @XmlElement(name = "contextNamedEntities")
   // public ContextNamedEntity contextNamedEntities;

    @Override
    public String toString() {
        return "SecureUserProfile [partnerList=" + partnerList + ", protectedPartnerList=" + protectedPartnerList + ", queryID=" + queryID + ", firstName=" + firstName
                + ", lastName=" + lastName + ", birthDate=" + birthDate + ", numResults=" + numResults + ", gender=" + gender + ", address=" + address + ", timeRange=" + timeRange
                + ", languages=" + languages + ",  userCredentials=" + userCredentials  + ", interestList="
                + interestList + ", contextKeywords=" + contextKeywords + ", context=" + context +  "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((contextKeywords == null) ? 0 : contextKeywords.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((interestList == null) ? 0 : interestList.hashCode());
        result = prime * result + ((languages == null) ? 0 : languages.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((numResults == null) ? 0 : numResults.hashCode());
        result = prime * result + ((partnerList == null) ? 0 : partnerList.hashCode());
        result = prime * result + ((protectedPartnerList == null) ? 0 : protectedPartnerList.hashCode());
        result = prime * result + ((queryID == null) ? 0 : queryID.hashCode());
        result = prime * result + ((timeRange == null) ? 0 : timeRange.hashCode());
        result = prime * result + ((userCredentials == null) ? 0 : userCredentials.hashCode());
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
        if (birthDate == null) {
            if (other.birthDate != null)
                return false;
        } else if (!birthDate.equals(other.birthDate))
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
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
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
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
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
}
