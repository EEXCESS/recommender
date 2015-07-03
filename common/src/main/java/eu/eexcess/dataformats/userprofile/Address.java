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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Address class, holds information about the users address
 * 
 * @author hziak
 *
 */
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Serializable {

    private static final long serialVersionUID = -4948461957571529961L;

    @XmlAttribute
    public String country;
    @XmlAttribute
    public Integer zipCode;
    @XmlAttribute
    public String city;
    @XmlAttribute
    public String line1;
    @XmlAttribute
    public String line2;

    public Address(String country, Integer zipCode, String city, String line1, String line2) {
        super();
        this.country = country;
        this.zipCode = zipCode;
        this.city = city;
        this.line1 = line1;
        this.line2 = line2;
    }

    public Address() {
    }

    @Override
    public String toString() {
        return "Address [country=" + country + ", zipCode=" + zipCode + ", city=" + city + ", line1=" + line1 + ", line2=" + line2 + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
        result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
        result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
        Address other = (Address) obj;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (line1 == null) {
            if (other.line1 != null)
                return false;
        } else if (!line1.equals(other.line1))
            return false;
        if (line2 == null) {
            if (other.line2 != null)
                return false;
        } else if (!line2.equals(other.line2))
            return false;
        if (zipCode == null) {
            if (other.zipCode != null)
                return false;
        } else if (!zipCode.equals(other.zipCode))
            return false;
        return true;
    }

}
