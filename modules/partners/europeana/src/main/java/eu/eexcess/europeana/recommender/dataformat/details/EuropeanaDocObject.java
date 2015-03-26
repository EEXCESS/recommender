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
package eu.eexcess.europeana.recommender.dataformat.details;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class EuropeanaDocObject {

	@XmlElementWrapper(name="places")
	public List<EuropeanaDocPlaceDetails> places = new ArrayList<EuropeanaDocPlaceDetails>();
	@XmlElementWrapper(name="concepts")
	public List<EuropeanaDocConceptDetails> concepts = new ArrayList<EuropeanaDocConceptDetails>();
	@XmlElementWrapper(name="edmCountry")
	public List<EuropeanaDocCountryDetails> edmCountry = new ArrayList<EuropeanaDocCountryDetails>();
	@Override
	public String toString() {
		return "EuropeanaDocObject [places=" + places + ", concepts="
				+ concepts + ", edmCountry=" + edmCountry + "]";
	}
	
}
