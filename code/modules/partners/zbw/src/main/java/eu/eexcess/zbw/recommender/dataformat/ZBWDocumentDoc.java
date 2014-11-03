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
package eu.eexcess.zbw.recommender.dataformat;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * @author hziak@know-center.at
 *
 */
public class ZBWDocumentDoc {
	@XmlElement(name="_score")
	public Double score;
	
	@XmlElement(name="date")
	public String date;
	
	@XmlElement(name="abstract")
	public String abstractString;
	
	@XmlElement(name="publisher")
	public String publisher;
	
	@XmlElement(name="isn")
	public Integer	isn;
	
	
	@XmlElement(name="conference_place")
	public String conferencePlace;
	
	@XmlElement(name="econbiz_created")
	public List<Date> econbiz_created;
	
	@XmlElement(name="id")
	public String id;
	
	
	@XmlElement(name="institution")
	public String institution;
	
	@XmlElement(name="isPartOf")
	public String isPartOf;
	
	
	@XmlElement(name="source")
	public String source;
	
	@XmlElement(name="title")
	public String title;

	@XmlElement(name="type")
	public String type;

	@XmlElement(name="subject")
	public List<String> subject;

	@XmlElement(name="type_genre")
	public List<String> type_genre;
	
	@XmlElement(name="person")
	public List<String> person;
	
	@XmlElement(name="creator")
	public List<String> creator;

	@XmlElement(name="identifier_url")
	public List<String> identifierUrl;
	@XmlElement(name="lng")
	public String lng;
	@XmlElement(name="lat")
	public String lat;
	
}
