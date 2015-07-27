/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

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
package eu.eexcess.dataformats.result;

import java.io.Serializable;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Encapsulation of Document Badges in EEXCESS data format.
 * 
 * @author thomas.orgel@joanneum.at
 */
@XmlRootElement(name = "eexcess-document-badges-list")
public class DocumentBadgeList implements Serializable {

    private static final long serialVersionUID = 5326398793008341024L;
    @XmlElement(name = "documentBadge")
    public LinkedList<DocumentBadge> documentBadges = new LinkedList<DocumentBadge>();

    @Override
    public String toString() {
        return "DocumentBadgeList [documentBadges=" + documentBadges + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((documentBadges == null) ? 0 : documentBadges.hashCode());
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
        DocumentBadgeList other = (DocumentBadgeList) obj;
        if (documentBadges == null) {
            if (other.documentBadges != null)
                return false;
        } else if (!documentBadges.equals(other.documentBadges))
            return false;
        return true;
    }

}
