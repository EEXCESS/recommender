/* Copyright (C) 2010 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
