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
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;

public class EnrichmentServicesProxy {
	
	protected PartnerConfiguration partnerConfig;
	protected WordFilter wordFilter;

	public EnrichmentServicesProxy(PartnerConfiguration config)
	{
		this.partnerConfig = config;
		wordFilter = new WordFilter(this.partnerConfig);
	}

	
	public Set<EnrichmentResult> enrich(String text, PartnerdataLogger logger){
		if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("------------------------------------------------------------");
		Set<EnrichmentResult> words = new HashSet<EnrichmentResult>();
		
		
		FreeBase freebase = new FreeBase(this.partnerConfig);
		GeoNames geonames = new GeoNames(this.partnerConfig);

		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreElements()) {
			String actWord = st.nextElement().toString();
			actWord = StringNormalizer.removeStopMarks(actWord);
			if (wordFilter.isKeyWord(actWord)) 
			{
				if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("query freebase with:"+actWord);
				ArrayList<FreebaseResult> responseFreebase = freebase.getEntitiesFreeBase(actWord, logger);
				for (FreebaseResult freebaseResult : responseFreebase) {
					EnrichmentResult enrichmentResult = new EnrichmentResult();
					enrichmentResult.setWord(freebaseResult.getName());
					enrichmentResult.setUri(freebaseResult.getURI());
					enrichmentResult.setLanguage(freebaseResult.getLanguage());
					words.add(enrichmentResult);
					if (freebaseResult.getNotableId()!= null && ! freebaseResult.getNotableId().isEmpty() &&
							(freebaseResult.getNotableId().toLowerCase().startsWith("/location") ) )
//							.contains("city")||
//							responseFreebase.contains("town")||
//							responseFreebase.contains("village")||
//							responseFreebase.contains("river")||
//							responseFreebase.contains("mountain")||
//							responseFreebase.contains("location"))
					{
						if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("query geonames with:"+actWord);
						Set<EnrichmentResult> responseGeonames = geonames.getLocationHierarchy(actWord, logger);
						words.addAll(responseGeonames);
					}
				}
			}
		}

		if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("enriching: input:" + text +"\n\n\n" + words.toString());
		if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("------------------------------------------------------------");

		return words;
	}
}
