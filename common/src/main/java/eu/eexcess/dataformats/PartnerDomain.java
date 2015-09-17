/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.dataformats;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * Reflects a partner domain given in partner-configuration.
 * 
 * @author Raoul Rubien
 *
 */
public class PartnerDomain implements Serializable {

    private static final long serialVersionUID = -3022570752393633726L;

    @XmlElement(name = "domainName")
    private String domainName;

    @XmlElement(name = "weight")
    private Double weight;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public PartnerDomain(String domainName, double weight) {
        this.domainName = domainName;
        this.weight = weight;
    }

    public PartnerDomain() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domainName == null) ? 0 : domainName.hashCode());
        result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
        PartnerDomain other = (PartnerDomain) obj;
        if (domainName == null) {
            if (other.domainName != null)
                return false;
        } else if (!domainName.equals(other.domainName))
            return false;
        if (weight == null) {
            if (other.weight != null)
                return false;
        } else if (!weight.equals(other.weight))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartnerDomain [domainName=" + domainName + ", weight=" + weight + "]";
    }

}
