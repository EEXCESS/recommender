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
 * Interests of the user
 * @author hziak
 *
 */

public class Interest  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2510863722705747232L;

	public Interest(String text, Double weight, Double confidence,
			Double competenceLevel, String source, String uri) {
		super();
		this.text = text;
		this.weight = weight;
		this.confidence = confidence;
		this.competenceLevel = competenceLevel;
		this.source = source;
		this.uri = uri;
	}

	public Interest(String text) {
		this.text=text;
	}
	public Interest() {
	}
	@XmlElement(name="text")
	public String text;
	@XmlElement(name="weight")
	public Double weight;
	@XmlElement(name="confidence")
	public Double confidence;
	@XmlElement(name="competenceLevel")
	public Double competenceLevel;
	@XmlElement(name="source")
	public String source;
	@XmlElement(name="uri")
	public String uri;
	
	@Override
	public String toString() {
		return "Interest [text=" + text + ", weight=" + weight
				+ ", confidence=" + confidence + ", competenceLevel="
				+ competenceLevel + ", source=" + source + ", uri=" + uri + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((competenceLevel == null) ? 0 : competenceLevel.hashCode());
		result = prime * result
				+ ((confidence == null) ? 0 : confidence.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		Interest other = (Interest) obj;
		if (competenceLevel == null) {
			if (other.competenceLevel != null)
				return false;
		} else if (!competenceLevel.equals(other.competenceLevel))
			return false;
		if (confidence == null) {
			if (other.confidence != null)
				return false;
		} else if (!confidence.equals(other.confidence))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
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
	
}