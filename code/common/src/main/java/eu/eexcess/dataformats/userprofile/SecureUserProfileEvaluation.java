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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.eexcess.dataformats.PartnerBadge;
@XmlRootElement(name = "eexcess-secure-user-profile")
public class SecureUserProfileEvaluation extends SecureUserProfile implements Serializable{
	private static final long serialVersionUID = 2359464317396073509L;
	
	@XmlElementWrapper(name="queryExpansionSourcePartner")
	public ArrayList<PartnerBadge> queryExpansionSourcePartner = new ArrayList<PartnerBadge>();
	@XmlTransient
    public ArrayList<ArrayList<ContextKeyword>> contextKeywordsGroups = new ArrayList<ArrayList<ContextKeyword>>();
    
    
	@XmlAttribute
	public String picker;
	@XmlAttribute
	public String decomposer;
	@XmlAttribute
	public String sourceSelect;
}
