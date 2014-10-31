/* Copyright (C) 2014 
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
package eu.eexcess.dataformats.userprofile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class ContextNamedEntitiesElement   implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8014896415191666525L;
	public ContextNamedEntitiesElement(String text, Double weight,
			Double confidence, String uri) {
		super();
		this.text = text;
		this.weight = weight;
		this.confidence = confidence;
		this.uri = uri;
	}
	public ContextNamedEntitiesElement(){
	}
	@XmlElement(name="text")
	public String text;
	@XmlElement(name="weight")
	public Double weight;
	@XmlElement(name="confidence")
	public Double confidence;
	@XmlElement(name="uri")
	public String uri;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((confidence == null) ? 0 : confidence.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
		ContextNamedEntitiesElement other = (ContextNamedEntitiesElement) obj;
		if (confidence == null) {
			if (other.confidence != null)
				return false;
		} else if (!confidence.equals(other.confidence))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ContextNamedEntitiesElement [text=" + text + ", weight="
				+ weight + ", confidence=" + confidence + ", uri=" + uri + "]";
	}
	
}
