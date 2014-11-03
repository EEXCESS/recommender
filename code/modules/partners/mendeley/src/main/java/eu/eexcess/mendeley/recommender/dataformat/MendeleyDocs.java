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
package eu.eexcess.mendeley.recommender.dataformat;
/**
 * 
 * @author hziak
 *
 */
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class MendeleyDocs {
	
    @XmlElement
	public String uuid;
    @XmlElement
	public String title;
    @XmlElement
	public String publication_outlet;
    @XmlElement
	public Integer year;
    @XmlElement
	public String mendeley_url;
    @XmlElement
	public String doi;
    @XmlElementWrapper(name="authors")
    @XmlElement(name="authors")
	public List<MendeleyAuthors> authors = new ArrayList<MendeleyAuthors>();
	public String authorsString;
	public String publicationOutlet;
	public String description;
	public String website;
	@Override
	public String toString() {
		return "MendeleyDocs [uuid=" + uuid + ", title=" + title
				+ ", publication_outlet=" + publication_outlet + ", year="
				+ year + ", mendeley_url=" + mendeley_url + ", doi=" + doi
				+ ", authors=" + authors + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result + ((doi == null) ? 0 : doi.hashCode());
		result = prime * result
				+ ((mendeley_url == null) ? 0 : mendeley_url.hashCode());
		result = prime
				* result
				+ ((publication_outlet == null) ? 0 : publication_outlet
						.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		MendeleyDocs other = (MendeleyDocs) obj;
		if (authors == null) {
			if (other.authors != null)
				return false;
		} else if (!authors.equals(other.authors))
			return false;
		if (doi == null) {
			if (other.doi != null)
				return false;
		} else if (!doi.equals(other.doi))
			return false;
		if (mendeley_url == null) {
			if (other.mendeley_url != null)
				return false;
		} else if (!mendeley_url.equals(other.mendeley_url))
			return false;
		if (publication_outlet == null) {
			if (other.publication_outlet != null)
				return false;
		} else if (!publication_outlet.equals(other.publication_outlet))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
	
	
}
