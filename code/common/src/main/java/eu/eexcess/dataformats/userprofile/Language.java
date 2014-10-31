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

import javax.xml.bind.annotation.XmlAttribute;
/**
 * Information which language the user knows and their competence level
 * @author hziak
 *
 */
public class Language  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 388649425945017482L;

	public Language(String iso2, Double competenceLevel) {
		this.iso2 = iso2;
		this.competenceLevel = competenceLevel;
	}
	public Language(){
		
	}
	@XmlAttribute
    public String iso2;
	@XmlAttribute
    public Double competenceLevel;
	
	@Override
	public String toString() {
		return "Language [iso2=" + iso2 + ", competenceLevel="
				+ competenceLevel + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((competenceLevel == null) ? 0 : competenceLevel.hashCode());
		result = prime * result + ((iso2 == null) ? 0 : iso2.hashCode());
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
		Language other = (Language) obj;
		if (competenceLevel == null) {
			if (other.competenceLevel != null)
				return false;
		} else if (!competenceLevel.equals(other.competenceLevel))
			return false;
		if (iso2 == null) {
			if (other.iso2 != null)
				return false;
		} else if (!iso2.equals(other.iso2))
			return false;
		return true;
	}
	
}
