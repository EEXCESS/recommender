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
package eu.eexcess.dataformats.userprofile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.eexcess.dataformats.PartnerBadge;

@XmlRootElement(name = "eexcess-secure-user-profile")
public class SecureUserProfileEvaluation extends SecureUserProfile implements Serializable {
    private static final long serialVersionUID = 2359464317396073509L;

    private ArrayList<PartnerBadge> queryExpansionSourcePartner = new ArrayList<PartnerBadge>();
    @XmlTransient
    private ArrayList<ArrayList<ContextKeyword>> contextKeywordsGroups = new ArrayList<ArrayList<ContextKeyword>>();
    private String picker;
    private String decomposer;
    private String sourceSelect;
    private String description;
    private List<History> history = new ArrayList<History>();

    @XmlTransient
    public String getContextKeywordConcatenation() {
        StringBuilder builder = new StringBuilder();
        for (ContextKeyword contextKeyword : getContextKeywords()) {
            builder.append(contextKeyword.getText());
            builder.append(" ");
        }
        return builder.toString();
    }

    public ArrayList<ArrayList<ContextKeyword>> getContextKeywordsGroups() {
        return contextKeywordsGroups;
    }

    public void setContextKeywordsGroups(ArrayList<ArrayList<ContextKeyword>> contextKeywordsGroups) {
        this.contextKeywordsGroups = contextKeywordsGroups;
    }

    @XmlElement
    public ArrayList<PartnerBadge> getQueryExpansionSourcePartner() {
        return queryExpansionSourcePartner;
    }

    public void setQueryExpansionSourcePartner(ArrayList<PartnerBadge> queryExpansionSourcePartner) {
        this.queryExpansionSourcePartner = queryExpansionSourcePartner;
    }

    @XmlElement
    public String getPicker() {
        return picker;
    }

    public void setPicker(String picker) {
        this.picker = picker;
    }

    @XmlElement
    public String getDecomposer() {
        return decomposer;
    }

    public void setDecomposer(String decomposer) {
        this.decomposer = decomposer;
    }

    @XmlElement
    public String getSourceSelect() {
        return sourceSelect;
    }

    public void setSourceSelect(String sourceSelect) {
        this.sourceSelect = sourceSelect;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SecureUserProfileEvaluation [queryExpansionSourcePartner=" + queryExpansionSourcePartner + ", contextKeywordsGroups=" + contextKeywordsGroups + ", picker="
                + picker + ", decomposer=" + decomposer + ", sourceSelect=" + sourceSelect + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((contextKeywordsGroups == null) ? 0 : contextKeywordsGroups.hashCode());
        result = prime * result + ((decomposer == null) ? 0 : decomposer.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((picker == null) ? 0 : picker.hashCode());
        result = prime * result + ((queryExpansionSourcePartner == null) ? 0 : queryExpansionSourcePartner.hashCode());
        result = prime * result + ((sourceSelect == null) ? 0 : sourceSelect.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SecureUserProfileEvaluation other = (SecureUserProfileEvaluation) obj;
        if (contextKeywordsGroups == null) {
            if (other.contextKeywordsGroups != null)
                return false;
        } else if (!contextKeywordsGroups.equals(other.contextKeywordsGroups))
            return false;
        if (decomposer == null) {
            if (other.decomposer != null)
                return false;
        } else if (!decomposer.equals(other.decomposer))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (picker == null) {
            if (other.picker != null)
                return false;
        } else if (!picker.equals(other.picker))
            return false;
        if (queryExpansionSourcePartner == null) {
            if (other.queryExpansionSourcePartner != null)
                return false;
        } else if (!queryExpansionSourcePartner.equals(other.queryExpansionSourcePartner))
            return false;
        if (sourceSelect == null) {
            if (other.sourceSelect != null)
                return false;
        } else if (!sourceSelect.equals(other.sourceSelect))
            return false;
        return true;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }

}
