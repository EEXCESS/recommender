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

    private String country;
    private Integer zipCode;
    private String city;
    private String line1;
    private String line2;

    public Address(String country, Integer zipCode, String city, String line1, String line2) {
        super();
        this.setCountry(country);
        this.setZipCode(zipCode);
        this.setCity(city);
        this.setLine1(line1);
        this.setLine2(line2);
    }

    public Address() {
    }

    @Override
    public String toString() {
        return "Address [country=" + getCountry() + ", zipCode=" + getZipCode() + ", city=" + getCity() + ", line1=" + getLine1() + ", line2=" + getLine2() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
        result = prime * result + ((getCountry() == null) ? 0 : getCountry().hashCode());
        result = prime * result + ((getLine1() == null) ? 0 : getLine1().hashCode());
        result = prime * result + ((getLine2() == null) ? 0 : getLine2().hashCode());
        result = prime * result + ((getZipCode() == null) ? 0 : getZipCode().hashCode());
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
        if (getCity() == null) {
            if (other.getCity() != null)
                return false;
        } else if (!getCity().equals(other.getCity()))
            return false;
        if (getCountry() == null) {
            if (other.getCountry() != null)
                return false;
        } else if (!getCountry().equals(other.getCountry()))
            return false;
        if (getLine1() == null) {
            if (other.getLine1() != null)
                return false;
        } else if (!getLine1().equals(other.getLine1()))
            return false;
        if (getLine2() == null) {
            if (other.getLine2() != null)
                return false;
        } else if (!getLine2().equals(other.getLine2()))
            return false;
        if (getZipCode() == null) {
            if (other.getZipCode() != null)
                return false;
        } else if (!getZipCode().equals(other.getZipCode()))
            return false;
        return true;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

}
