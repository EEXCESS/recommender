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
