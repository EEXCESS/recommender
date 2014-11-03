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


public class TimeString {

	public static int MinSecToSec(String timeString) {
		int dotIndex=timeString.indexOf(".");
		int minutes=Integer.parseInt(timeString.substring(0,dotIndex));
		int seconds=Integer.parseInt(timeString.substring(dotIndex+1,timeString.length()));
		
		return seconds+minutes*60;		
	}
	
	public static int SecMiliToSec(String timeString) {
		int dotIndex=timeString.indexOf(".");
		if (dotIndex!=-1)
		{
			return Integer.parseInt(timeString.substring(0,dotIndex));
		}
		else
		{
			return Integer.parseInt(timeString);
		}
	}
	
	public static int HoursMinSecToSec(String timeString){
		String[] timeStringArray=timeString.split(":");
		int hours=Integer.parseInt(timeStringArray[0]);
		int minutes=Integer.parseInt(timeStringArray[1]);
		int seconds=SecMiliToSec(timeStringArray[2]);
		
		
		return hours*60*60+minutes*60+seconds;
	}
}
