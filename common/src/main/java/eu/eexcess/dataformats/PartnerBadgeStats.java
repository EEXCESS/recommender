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
package eu.eexcess.dataformats;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.eexcess.dataformats.result.ResultStats;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PartnerBadgeStats implements Serializable {
	
	private static final long serialVersionUID = -227986625685031717L;

	@XmlElement(name="requestCount")
	public Integer requestCount=0;

	@XmlElement(name="failedRequestCount")
	public Integer failedRequestCount=0;

	@XmlElement(name="failedRequestTimeoutCount")
	public Integer failedRequestTimeoutCount=0;
	
	@XmlElement(name="lastQueries")
	public LinkedList<ResultStats> lastQueries = new LinkedList<ResultStats>();

	//Begin response time
	@XmlTransient
	public Stack<Long> lastResponseTimes= new Stack<Long>() ;
	@XmlElement(name="shortTimeResponseTimes")
	public Long shortTimeResponseTime;
	
	//End response time	

}
