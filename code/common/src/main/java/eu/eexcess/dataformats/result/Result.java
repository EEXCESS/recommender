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

}
