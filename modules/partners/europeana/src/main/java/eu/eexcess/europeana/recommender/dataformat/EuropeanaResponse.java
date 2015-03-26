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
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class EuropeanaResponse {
	@XmlElementWrapper(name="items")
	public List<EuropeanaDoc> items = new ArrayList<EuropeanaDoc>();
	@XmlElement(name="action")
	public String action;
	@XmlElement(name="apikey")
	public String apikey;
	@XmlElement(name="requestNumber")
	public Integer requestNumber;
	@XmlElement(name="itemsCount")
	public Integer itemsCount;
	@XmlElement(name="totalResults")
	public Integer totalResults;
	@Override
	public String toString() {
		return "EuropeanaResponse [documents=" + items + ", action="
				+ action + ", apikey=" + apikey + ", requestNumber="
				+ requestNumber + ", itemsCount=" + itemsCount
				+ ", totalResults=" + totalResults + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((apikey == null) ? 0 : apikey.hashCode());
		result = prime * result
				+ ((items == null) ? 0 : items.hashCode());
		result = prime * result
				+ ((itemsCount == null) ? 0 : itemsCount.hashCode());
		result = prime * result
				+ ((requestNumber == null) ? 0 : requestNumber.hashCode());
		result = prime * result
				+ ((totalResults == null) ? 0 : totalResults.hashCode());
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
		EuropeanaResponse other = (EuropeanaResponse) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (apikey == null) {
			if (other.apikey != null)
				return false;
		} else if (!apikey.equals(other.apikey))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (itemsCount == null) {
			if (other.itemsCount != null)
				return false;
		} else if (!itemsCount.equals(other.itemsCount))
			return false;
		if (requestNumber == null) {
			if (other.requestNumber != null)
				return false;
		} else if (!requestNumber.equals(other.requestNumber))
			return false;
		if (totalResults == null) {
			if (other.totalResults != null)
				return false;
		} else if (!totalResults.equals(other.totalResults))
			return false;
		return true;
	}
	
		
}
