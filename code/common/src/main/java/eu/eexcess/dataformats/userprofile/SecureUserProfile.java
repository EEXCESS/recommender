/* Copyright (C) 2010 
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
package eu.eexcess.dataformats.userprofile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

//import cc.mallet.types.Instance;
import eu.eexcess.dataformats.PartnerBadge;

/**
 * Basic information about an EEXCESS secure user profile.
 * 
 * @author rkern@know-center.at
 */
@XmlRootElement(name = "eexcess-secure-user-profile")
public class SecureUserProfile implements Serializable {
	private static final long serialVersionUID = 1344062464911638930L;
	
	@XmlElementWrapper(name="partnerList")
    @XmlElement(name="partnerList")
    public List<PartnerBadge> partnerList = new ArrayList<PartnerBadge>(); //
	@XmlElementWrapper(name="protectedPartnerList")
    @XmlElement(name="protectedPartnerList")
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
    @XmlElement(name="address")
    public Address address;
    
    
    
    @XmlElementWrapper(name="languages")
    @XmlElement(name="languages")
    public List<Language> languages = new ArrayList<Language>();
    
    @XmlElementWrapper(name="userLocations")
    @XmlElement(name="userLocations")
    public List<UserLocation> userLocations = new ArrayList<UserLocation>();
    
    
    @XmlElementWrapper(name="userCredentials")
    @XmlElement(name="userCredentials")
    public List<UserCredentials> userCredentials = new ArrayList<UserCredentials>();
    
    @XmlElementWrapper(name="history")
    @XmlElement(name="history")
    public List<History> history = new ArrayList<History>();
    
    
    @XmlElementWrapper(name="interests")
    @XmlElement(name="interests")
    public List<Interest> interestList = new ArrayList<Interest>();
    
    @XmlElementWrapper(name="contextKeywords")
    @XmlElement(name="contextKeywords")
    public List<ContextKeyword> contextKeywords = new ArrayList<ContextKeyword>();
    
    @XmlElement(name="context")
    public Context context = new Context();
    
    @XmlElement(name="contextNamedEntities")
    public ContextNamedEntity contextNamedEntities;





	@Override
	public String toString() {
		return "SecureUserProfile [partnerList=" + partnerList
				+ ", protectedPartnerList=" + protectedPartnerList
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", birthDate=" + birthDate + ", gender=" + gender
				+ ", address=" + address + ", languages=" + languages
				+ ", userLocations=" + userLocations + ", userCredentials="
				+ userCredentials + ", history=" + history + ", interestList="
				+ interestList + ", contextKeywords=" + contextKeywords
				+ ", contextNamedEntities=" + contextNamedEntities + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result
				+ ((contextKeywords == null) ? 0 : contextKeywords.hashCode());
		result = prime
				* result
				+ ((contextNamedEntities == null) ? 0 : contextNamedEntities
						.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		result = prime * result
				+ ((interestList == null) ? 0 : interestList.hashCode());
		result = prime * result
				+ ((languages == null) ? 0 : languages.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((partnerList == null) ? 0 : partnerList.hashCode());
		result = prime
				* result
				+ ((protectedPartnerList == null) ? 0 : protectedPartnerList
						.hashCode());
		result = prime * result
				+ ((userCredentials == null) ? 0 : userCredentials.hashCode());
		result = prime * result
				+ ((userLocations == null) ? 0 : userLocations.hashCode());
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
		if (contextKeywords == null) {
			if (other.contextKeywords != null)
				return false;
		} else if (!contextKeywords.equals(other.contextKeywords))
			return false;
		if (contextNamedEntities == null) {
			if (other.contextNamedEntities != null)
				return false;
		} else if (!contextNamedEntities.equals(other.contextNamedEntities))
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
		if (history == null) {
			if (other.history != null)
				return false;
		} else if (!history.equals(other.history))
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
		if (userCredentials == null) {
			if (other.userCredentials != null)
				return false;
		} else if (!userCredentials.equals(other.userCredentials))
			return false;
		if (userLocations == null) {
			if (other.userLocations != null)
				return false;
		} else if (!userLocations.equals(other.userLocations))
			return false;
		return true;
	}
    
    
 
}
