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
package eu.eexcess.europeana.recommender.dataformat;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EuropeanaDocPlace {

	@XmlElement(name="about")
	public String about=new String();
	
	@XmlElement(name="latitude")
	public Double latitude;
	
	@XmlElement(name="longitude")
	public Double longitude;
	
	@XmlElementWrapper(name="en")
	public ArrayList<String> en = new ArrayList<String>();

	@Override
	public String toString() {
		return "EuropeanaDocPlace [about=" + about + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", en=" + en + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((about == null) ? 0 : about.hashCode());
		result = prime * result + ((en == null) ? 0 : en.hashCode());
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
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
		EuropeanaDocPlace other = (EuropeanaDocPlace) obj;
		if (about == null) {
			if (other.about != null)
				return false;
		} else if (!about.equals(other.about))
			return false;
		if (en == null) {
			if (other.en != null)
				return false;
		} else if (!en.equals(other.en))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}
	


}