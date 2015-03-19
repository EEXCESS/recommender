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
package eu.eexcess.dataformats.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Basic information about a recommended item in EEXCESS data format.
 * 
 * @author plopez@know-center.at
 */
@XmlRootElement(name = "eexcess-result")
public class Result implements Serializable {

	private static final long serialVersionUID = -3095633945245542398L;

	@XmlElement(name = "id")
	public String id;

	
	@XmlElement(name = "title")
	public String title;

	@XmlElement(name = "previewImage")
	public String previewImage;

	@XmlElement(name = "uri")
	public String uri;

	@XmlElement(name = "eexcessURI")
	public String eexcessURI;

	@XmlElement(name = "creator")
	public String creator;

	@XmlElement(name = "description")
	public String description;

	@XmlElement(name = "collectionName")
	public String collectionName;

	/* Fields that should be returned as facets (provider, type, language, year) */

	@XmlElement(name = "facets")
	public ResultFacets facets = new ResultFacets();

	@XmlElement(name = "rdf")
	public Object rdf = null;

	@Override
	public String toString() {
		return id + " " + title + " " + previewImage + " " + uri + " "
				+ creator + " " + description + " " + collectionName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collectionName == null) ? 0 : collectionName.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((eexcessURI == null) ? 0 : eexcessURI.hashCode());
		result = prime * result + ((facets == null) ? 0 : facets.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((previewImage == null) ? 0 : previewImage.hashCode());
		result = prime * result + ((rdf == null) ? 0 : rdf.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Result other = (Result) obj;
		if (collectionName == null) {
			if (other.collectionName != null)
				return false;
		} else if (!collectionName.equals(other.collectionName))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (eexcessURI == null) {
			if (other.eexcessURI != null)
				return false;
		} else if (!eexcessURI.equals(other.eexcessURI))
			return false;
		if (facets == null) {
			if (other.facets != null)
				return false;
		} else if (!facets.equals(other.facets))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (previewImage == null) {
			if (other.previewImage != null)
				return false;
		} else if (!previewImage.equals(other.previewImage))
			return false;
		if (rdf == null) {
			if (other.rdf != null)
				return false;
		} else if (!rdf.equals(other.rdf))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}


	

}
