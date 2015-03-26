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

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MendeleyDocs {
    @JsonProperty("id")
	public String id;

    @JsonProperty("title")
	public String title;

    @JsonProperty("source")
	public String source;

    @JsonProperty("year")
	public Integer year;

    @JsonProperty("link")
	public String link;

    @JsonProperty("authors")
	public List<MendeleyAuthors> authors = new ArrayList<MendeleyAuthors>();
	public String authorsString;

    @JsonProperty("abstract")
	public String description;

	@Override
	public String toString() {
		return "MendeleyDocs [id=" + id + ", title=" + title
				+ ", source=" + source + ", year="
				+ year + ", link=" + link
				+ ", authors=" + authors
                + ", description=" + description + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result
				+ ((link == null) ? 0 : link.hashCode());
		result = prime
				* result
				+ ((source == null) ? 0 : source
						.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
		return true;
	}
}
