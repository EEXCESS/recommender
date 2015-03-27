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

import javax.xml.bind.annotation.XmlElement;

public class RecommenderStats {
	@XmlElement(name="averageGlobalTime")
	private long averageGlobalTime;
	@XmlElement(name="averageAggregationTime")
	private long averageAggregationTime;
	public long getAverageAggregationTime() {
		return averageAggregationTime;
	}

	public void setAverageAggregationTime(long averageAggregationTime) {
		if(this.averageAggregationTime!=0)
			this.averageAggregationTime = (this.averageAggregationTime +averageAggregationTime)/2;
		else
			this.averageAggregationTime = averageAggregationTime;
	}

	public long getAverageGlobalTime() {
		return averageGlobalTime;
	}

	public void setAverageGlobalTime(long averageGlobalTime) {
		if(this.averageGlobalTime!=0)
			this.averageGlobalTime = (this.averageGlobalTime +averageGlobalTime)/2;
		else
			this.averageGlobalTime = averageGlobalTime;
		
	}
}
