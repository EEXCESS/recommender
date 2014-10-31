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
/**
 * Keywords out of the context
 * @author hziak
 *
 */
public class ContextKeyword implements Serializable{
	
	private static final long serialVersionUID = -4047662119669146571L;
	public ContextKeyword(String reason, String text, Double weight) {
		super();
		
		this.text = text;
		this.weight = weight;
		this.expansion=false;
	}
	//Is used for query expansion
	@XmlElement(name="expansion",required=false)
	public Boolean expansion;
	@XmlElement(name="reason")
	public String reason;	
	@XmlElement(name="text")
	public String text;
	@XmlElement(name="weight")
	public Double weight;
	public ContextKeyword() {

	}
	public ContextKeyword(String text) {
		this.text = text;
	}
	public ContextKeyword(String text,Double weight) {
		this.text = text;
		this.weight=weight;
	}
	public ContextKeyword(String text, boolean expansion) {
		this.text = text;
		this.expansion=expansion;
	}
	public ContextKeyword(String text, Double weight, boolean expansion) {
		this.text = text;
		this.weight = weight;
		this.expansion = expansion;
		
	}
	@Override
	public String toString() {
		return "ContextKeyword [expansion=" + expansion + ", reason=" + reason
				+ ", text=" + text + ", weight=" + weight + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expansion == null) ? 0 : expansion.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		ContextKeyword other = (ContextKeyword) obj;
		if (expansion == null) {
			if (other.expansion != null)
				return false;
		} else if (!expansion.equals(other.expansion))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
	
	
	
	
	
}
