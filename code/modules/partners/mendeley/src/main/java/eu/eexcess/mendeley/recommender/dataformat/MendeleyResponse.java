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

import javax.xml.bind.annotation.XmlElementWrapper;

public class MendeleyResponse {
	@XmlElementWrapper(name="documents")
    public List<MendeleyDocs> documents = new ArrayList<MendeleyDocs>();
	public Integer total_results;
	public Integer total_pages;
	public Integer current_page;
	public Integer items_per_page;
	@Override
	public String toString() {
		return "MendeleyResponse [documents=" + documents + ", total_results="
				+ total_results + ", total_pages=" + total_pages
				+ ", current_page=" + current_page + ", items_per_page="
				+ items_per_page + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((current_page == null) ? 0 : current_page.hashCode());
		result = prime * result
				+ ((documents == null) ? 0 : documents.hashCode());
		result = prime * result
				+ ((items_per_page == null) ? 0 : items_per_page.hashCode());
		result = prime * result
				+ ((total_pages == null) ? 0 : total_pages.hashCode());
		result = prime * result
				+ ((total_results == null) ? 0 : total_results.hashCode());
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
		MendeleyResponse other = (MendeleyResponse) obj;
		if (current_page == null) {
			if (other.current_page != null)
				return false;
		} else if (!current_page.equals(other.current_page))
			return false;
		if (documents == null) {
			if (other.documents != null)
				return false;
		} else if (!documents.equals(other.documents))
			return false;
		if (items_per_page == null) {
			if (other.items_per_page != null)
				return false;
		} else if (!items_per_page.equals(other.items_per_page))
			return false;
		if (total_pages == null) {
			if (other.total_pages != null)
				return false;
		} else if (!total_pages.equals(other.total_pages))
			return false;
		if (total_results == null) {
			if (other.total_results != null)
				return false;
		} else if (!total_results.equals(other.total_results))
			return false;
		return true;
	}
	
}
