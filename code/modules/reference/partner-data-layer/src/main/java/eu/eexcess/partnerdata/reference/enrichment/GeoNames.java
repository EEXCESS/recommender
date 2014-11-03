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

import java.util.HashSet;
import java.util.Set;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.PartnerdataTracer.FILETYPE;


public class GeoNames extends EnrichmentServiceBase {

	public GeoNames(PartnerConfiguration config) {
		super(config);
	}

	public Set<EnrichmentResult> getLocationHierarchy(String location, PartnerdataLogger logger)
	{
		Set<EnrichmentResult> resultSet=new HashSet<EnrichmentResult>();
		try {
			WebService.setUserName("geonamesmichal"); // add your username here

			ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
			searchCriteria.setQ(location);
			ToponymSearchResult searchResult;
			searchResult = WebService.search(searchCriteria);
			//			for (Toponym toponym : searchResult.getToponyms()) {
			//				System.out.println(toponym.getName()+" "+ toponym.getCountryName());
			//			}
	        PartnerdataTracer.dumpFile(GeoNames.class, this.partnerConfig, toStringToponymSearchResult(searchResult), "geonames-response", FILETYPE.TXT);

			if (searchResult.getToponyms().size()>0)
			{
				Toponym topo=searchResult.getToponyms().get(0);
				
				String[] topoWordArray=topo.getName().toLowerCase().split(" ");
				for (String t: topoWordArray)
				{
					t=StringNormalizer.removeStopMarks(t);
					EnrichmentResult result = new EnrichmentResult();
					result.setWord(t);
					resultSet.add(result);
				}
				
				topoWordArray=topo.getCountryName().toLowerCase().split(" ");
				for (String t: topoWordArray)
				{
					t=StringNormalizer.removeStopMarks(t);
					EnrichmentResult result = new EnrichmentResult();
					result.setWord(t);
					resultSet.add(result);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.getActLogEntry().addEnrichmentGeonamesResults(resultSet.size());
		logger.getActLogEntry().addEnrichmentGeonamesServiceCalls(1);

		return resultSet;
	}
	
	public String toStringToponymSearchResult(ToponymSearchResult searchResult)
	{
		String ret = "";
		ret += "TotalResultsCount:" + searchResult.getTotalResultsCount() + "\n";
		if (searchResult.getToponyms() != null)
		{
			for (int i = 0; i < searchResult.getToponyms().size(); i++) {
				ret += "Toponyms:\n" + searchResult.getToponyms().get(i) + "\n";
				ret += "\n" + toStringToponym(searchResult.getToponyms().get(i)) + "\n";
				
			}	
		}
		
		return ret;
	}

	public String toStringToponym(Toponym searchResult)
	{
		String ret = "";
		ret += "Name:" + searchResult.getName() + "\n";
		ret += "CountryName:" + searchResult.getCountryName() + "\n";
		
		return ret;
	}
}
