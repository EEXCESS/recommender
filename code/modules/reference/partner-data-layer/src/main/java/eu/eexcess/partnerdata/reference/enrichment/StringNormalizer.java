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


public class StringNormalizer {
	
	public static String removeStopMarks(String s)
	{
		s=s.replace("&qout", "");
		if (s.indexOf("'")==0)
		{
			s=s.substring(1);
		}
		if (s.indexOf("(")==0)
		{
			s=s.substring(1);
		}
		
		if (s.indexOf("?")!=-1)
		{
			s=s.substring(0, s.indexOf("?"));
		}
		if (s.indexOf(".")!=-1)
		{
			s=s.substring(0, s.indexOf("."));
		}
		if (s.indexOf(",")!=-1)
		{
			s=s.substring(0, s.indexOf(","));
		}
		if (s.indexOf("-")!=-1)
		{
			s=s.substring(0, s.indexOf("-"));
		}
		if (s.indexOf("!")!=-1)
		{
			s=s.substring(0, s.indexOf("!"));
		}
		if (s.indexOf(";")!=-1)
		{
			s=s.substring(0, s.indexOf(";"));
		}
		if (s.indexOf(":")!=-1)
		{
			s=s.substring(0, s.indexOf(":"));
		}
		if (s.indexOf(")")!=-1)
		{
			s=s.substring(0, s.indexOf(")"));
		}	
		//remove end of the word after '
		if (s.indexOf("'")!=-1)
		{
			s=s.substring(0, s.indexOf("'"));
		}
		
		return s;
	}
}
