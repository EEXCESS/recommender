/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

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
package eu.eexcess.partnerdata.reference.enrichment;

import java.util.ArrayList;
import java.util.List;


public class WordTiming {

	private String word;
	private List<String> timings;
	
	public WordTiming(String word) {

		this.word = word;
		timings=new ArrayList<String>();
	}
	
	public void addTiming(String timing)
	{
		timings.add(timing);
	}
	
	public List<String> getTimings()
	{
		return timings;
	}
	
	public String getWord()
	{
		return word;
	}
	
	
}
