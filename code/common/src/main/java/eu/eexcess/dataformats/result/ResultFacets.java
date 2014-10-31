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


/* Fields that should be returned as facets (provider, type, language, year) */

@XmlRootElement(name = "facets")
public class ResultFacets implements Serializable{
	
	private static final long serialVersionUID = -3642368065602424847L;

	@XmlElement(name="provider")
    public String provider;
	
	@XmlElement(name="type")
    public String type;
	
	@XmlElement(name="language")
    public String language;

    @XmlElement(name="year")
    public String year;
    
    @XmlElement(name="license")
    public String license;
    
}
