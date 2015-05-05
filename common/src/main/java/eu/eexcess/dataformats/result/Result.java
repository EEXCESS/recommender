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
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object representation of the returned documents bei the federated recommender
 * @author hziak@know-center.at
 */
@XmlRootElement(name = "eexcess-result")
public class Result implements Serializable {

	private static final long serialVersionUID = -3095633945245542398L;

    @XmlElement(name="resultGroup")
    public LinkedList<Result> resultGroup = new LinkedList<Result>();
	
    @XmlElement(name="documentBadge")
    public DocumentBadge documentBadge = new DocumentBadge();

	@XmlElement(name = "mediaType")
	public String mediaType;
	
	@XmlElement(name = "previewImage")
	public String previewImage;
	
	@XmlElement(name = "title")
	public String title;

	@XmlElement(name = "description")
	public String description;

	@XmlElement(name = "date")
	public String date;
	
	@XmlElement(name = "language")
	public String language;
	
	@XmlElement(name = "licence")
	public String licence;

	@Override
	public String toString() {
		return "Result [resultGroup=" + resultGroup + ", documentBadge="
				+ documentBadge + ", mediaType=" + mediaType
				+ ", previewImage=" + previewImage + ", title=" + title
				+ ", description=" + description + ", date=" + date
				+ ", language=" + language + ", licence=" + licence + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((documentBadge == null) ? 0 : documentBadge.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((licence == null) ? 0 : licence.hashCode());
		result = prime * result
				+ ((mediaType == null) ? 0 : mediaType.hashCode());
		result = prime * result
				+ ((previewImage == null) ? 0 : previewImage.hashCode());
		result = prime * result
				+ ((resultGroup == null) ? 0 : resultGroup.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (documentBadge == null) {
			if (other.documentBadge != null)
				return false;
		} else if (!documentBadge.equals(other.documentBadge))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (licence == null) {
			if (other.licence != null)
				return false;
		} else if (!licence.equals(other.licence))
			return false;
		if (mediaType == null) {
			if (other.mediaType != null)
				return false;
		} else if (!mediaType.equals(other.mediaType))
			return false;
		if (previewImage == null) {
			if (other.previewImage != null)
				return false;
		} else if (!previewImage.equals(other.previewImage))
			return false;
		if (resultGroup == null) {
			if (other.resultGroup != null)
				return false;
		} else if (!resultGroup.equals(other.resultGroup))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}
	
	
	

	


	

}
