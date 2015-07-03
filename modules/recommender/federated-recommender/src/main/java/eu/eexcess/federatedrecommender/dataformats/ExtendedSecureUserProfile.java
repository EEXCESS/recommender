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
package eu.eexcess.federatedrecommender.dataformats;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.utils.esuputils.ESUPPair;
import eu.eexcess.federatedrecommender.utils.esuputils.ESUPSource;

public class ExtendedSecureUserProfile extends SecureUserProfile {
    private static final long serialVersionUID = -4983458034155356055L;

    private List<ESUPPair> extendedInterestList = new ArrayList<ESUPPair>();
    private List<ESUPPair> extendedContextList = new ArrayList<ESUPPair>();

    public ExtendedSecureUserProfile(SecureUserProfile secureUserProfile) {
        this.contextKeywords = secureUserProfile.contextKeywords;
        this.interestList = secureUserProfile.interestList;
    }

    public void addInterestClass(String interest, List<ESUPSource> sourceClasses) {
        getExtendedInterestList().add(new ESUPPair(interest, sourceClasses));
    }

    public void addContextClass(String context, List<ESUPSource> sourceClasses) {
        getExtendedContextList().add(new ESUPPair(context, sourceClasses));
    }

    public List<ESUPPair> getExtendedContextList() {
        return extendedContextList;
    }

    public List<ESUPPair> getExtendedInterestList() {
        return extendedInterestList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((extendedContextList == null) ? 0 : extendedContextList.hashCode());
        result = prime * result + ((extendedInterestList == null) ? 0 : extendedInterestList.hashCode());
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
        ExtendedSecureUserProfile other = (ExtendedSecureUserProfile) obj;
        if (extendedContextList == null) {
            if (other.extendedContextList != null)
                return false;
        } else if (!extendedContextList.equals(other.extendedContextList))
            return false;
        if (extendedInterestList == null) {
            if (other.extendedInterestList != null)
                return false;
        } else if (!extendedInterestList.equals(other.extendedInterestList))
            return false;
        return true;
    }

}
