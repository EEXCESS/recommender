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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EuropeanaDoc {

	@XmlElement(name="id")
	public String id = new String();
	
	@XmlElement(name="score")
	public Double score;
	
	@XmlElement(name="type")
	public String type=new String();
	
	@XmlElementWrapper(name="dataProvider")
	public List<String> dataProvider = new ArrayList<String>();
	
	  
	@XmlElementWrapper(name="edmPlaceLatitude")
	public List<Double> edmPlaceLatitude = new ArrayList<Double>();
	
	@XmlElementWrapper(name="edmPlaceLongitude")
	public List<Double> edmPlaceLongitude = new ArrayList<Double>();
	
	@XmlElementWrapper(name="title")
	public List<String> title = new ArrayList<String>();
	
	@XmlElementWrapper(name="language")
	public List<String> language = new ArrayList<String>();
	
	@XmlElementWrapper(name="edmIsShownAt")
	public List<String> edmIsShownAt = new ArrayList<String>();

	@XmlElementWrapper(name="guid")
	public String guid = new String();
	
	@XmlElementWrapper(name="edmPreview")
	public List<String> edmPreview = new ArrayList<String>();

	@XmlElementWrapper(name="year")
	public List<String> year = new ArrayList<String>();
	
	@XmlElementWrapper(name="europeanaCollectionName")
	public List<String> europeanaCollectionName = new ArrayList<String>();	
	
	@XmlElementWrapper(name="provider")
	public List<String> provider = new ArrayList<String>();
	
	@XmlElementWrapper(name="rights")
	public List<String> rights = new ArrayList<String>();
	
	@XmlElementWrapper(name="dcCreator")
	public List<String> dcCreator = new ArrayList<String>();
	
	
	@XmlElementWrapper(name="edmPlaces")
	@JsonIgnore
	public List<EuropeanaDocPlace> edmPlace = new ArrayList<EuropeanaDocPlace>();
	
	@XmlElementWrapper(name="edmConcept")
	@JsonIgnore
	public List<EuropeanaDocConcept> edmConcept = new ArrayList<EuropeanaDocConcept>();
	
	@XmlElementWrapper(name="edmCountry")
	@JsonIgnore
	public List<String> edmCountry = new ArrayList<String>();
	
	@Override
	public String toString() {
		return "EuropeanaDoc [id=" + id + ", score=" + score + ", type=" + type
				+ ", dataProvider=" + dataProvider + ", edmCountry="
				+ edmCountry + ", edmPlaceLatitude=" + edmPlaceLatitude
				+ ", edmPlaceLongitude=" + edmPlaceLongitude + ", title="
				+ title + ", language=" + language + ", provider=" + provider
				+ ", rights=" + rights + ", dcCreator=" + dcCreator
				+ ", edmPLace=" + edmPlace + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataProvider == null) ? 0 : dataProvider.hashCode());
		result = prime * result
				+ ((dcCreator == null) ? 0 : dcCreator.hashCode());
		result = prime * result
				+ ((edmCountry == null) ? 0 : edmCountry.hashCode());
		result = prime * result
				+ ((edmPlace == null) ? 0 : edmPlace.hashCode());
		result = prime
				* result
				+ ((edmPlaceLatitude == null) ? 0 : edmPlaceLatitude.hashCode());
		result = prime
				* result
				+ ((edmPlaceLongitude == null) ? 0 : edmPlaceLongitude
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		EuropeanaDoc other = (EuropeanaDoc) obj;
		if (dataProvider == null) {
			if (other.dataProvider != null)
				return false;
		} else if (!dataProvider.equals(other.dataProvider))
			return false;
		if (dcCreator == null) {
			if (other.dcCreator != null)
				return false;
		} else if (!dcCreator.equals(other.dcCreator))
			return false;
		if (edmCountry == null) {
			if (other.edmCountry != null)
				return false;
		} else if (!edmCountry.equals(other.edmCountry))
			return false;
		if (edmPlace == null) {
			if (other.edmPlace != null)
				return false;
		} else if (!edmPlace.equals(other.edmPlace))
			return false;
		if (edmPlaceLatitude == null) {
			if (other.edmPlaceLatitude != null)
				return false;
		} else if (!edmPlaceLatitude.equals(other.edmPlaceLatitude))
			return false;
		if (edmPlaceLongitude == null) {
			if (other.edmPlaceLongitude != null)
				return false;
		} else if (!edmPlaceLongitude.equals(other.edmPlaceLongitude))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (rights == null) {
			if (other.rights != null)
				return false;
		} else if (!rights.equals(other.rights))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}	
