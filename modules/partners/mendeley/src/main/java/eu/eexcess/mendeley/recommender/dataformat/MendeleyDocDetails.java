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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MendeleyDocDetails {
	@JsonProperty("publication_outlet")
	public String publicationoutlet;
	public String website;
	@JsonProperty("abstract")
	public String detailsAbstract;
	@Override
	public String toString() {
		return "MendeleyDocDetails [publicationoutlet=" + publicationoutlet
				+ ", website=" + website + ", detailsAbstract="
				+ detailsAbstract + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((detailsAbstract == null) ? 0 : detailsAbstract.hashCode());
		result = prime
				* result
				+ ((publicationoutlet == null) ? 0 : publicationoutlet
						.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
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
		MendeleyDocDetails other = (MendeleyDocDetails) obj;
		if (detailsAbstract == null) {
			if (other.detailsAbstract != null)
				return false;
		} else if (!detailsAbstract.equals(other.detailsAbstract))
			return false;
		if (publicationoutlet == null) {
			if (other.publicationoutlet != null)
				return false;
		} else if (!publicationoutlet.equals(other.publicationoutlet))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		return true;
	}
	
	
	
}
