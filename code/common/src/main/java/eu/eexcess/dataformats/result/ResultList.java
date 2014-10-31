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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Encapsulation of results in EEXCESS data format.
 * 
 * @author plopez@know-center.at
 */
@XmlRootElement(name = "eexcess-results")
public class ResultList {
	
	@XmlAttribute
    public String provider;
    
    @XmlAttribute
    public int totalResults;

    @XmlElement(name="result")
    public List<Result> results = new ArrayList<Result>();
    
    @XmlElement(name="resultsRDF")
    public Object resultsRDF = null;
    @XmlTransient //TODO: Should be @XmlAttribute but not for the stable version
	public String queryID;
    
    @Override
    public String toString(){
    	return provider +" "+ totalResults;
    }
}
