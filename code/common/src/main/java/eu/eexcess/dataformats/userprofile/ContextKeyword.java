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
