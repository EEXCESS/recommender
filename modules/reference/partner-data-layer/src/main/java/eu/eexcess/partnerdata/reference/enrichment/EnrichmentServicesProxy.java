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
import java.util.Iterator;
import java.util.Set;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.enrichment.EnrichmentResult.EnrichmentSource;

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
		Set<EnrichmentResult> enrichmentResults = new HashSet<EnrichmentResult>();
		
		
		GeoNames geonames = new GeoNames(this.partnerConfig);
		DbpediaSpotlight dbpediaSpotlight = new DbpediaSpotlight(partnerConfig);
		Set<DbpediaSpotlightResult> dbpediaSpotlightResults = dbpediaSpotlight.searchDbpediaSpotlight(text, logger);
		for (Iterator<DbpediaSpotlightResult> iterator = dbpediaSpotlightResults.iterator(); iterator.hasNext();) {
			DbpediaSpotlightResult dbpediaSpotlightResult = (DbpediaSpotlightResult) iterator.next();
			EnrichmentResult enrichmentResult = new EnrichmentResult(EnrichmentSource.DBPEDIA);
			enrichmentResult.setWord(dbpediaSpotlightResult.getName());
			enrichmentResult.setUri(dbpediaSpotlightResult.getURI());
			if (dbpediaSpotlightResult.types != null && !dbpediaSpotlightResult.types.isEmpty() ) {
				enrichmentResult.setTypes(dbpediaSpotlightResult.getAllConcepts());
				enrichmentResult.setType(enrichmentResult.getTypes().get(0));
				if (enrichmentResult.isTypeGeographicResource())
				{
					Set<EnrichmentResult> responseGeonames = geonames.getLocationHierarchy(enrichmentResult.getWord(), logger);
					enrichmentResults.addAll(responseGeonames);
				}
			}
			enrichmentResults.add(enrichmentResult);
		}
		
		/*
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreElements()) {
			String actWord = st.nextElement().toString();
			actWord = StringNormalizer.removeStopMarks(actWord);
			if (wordFilter.isKeyWord(actWord, logger)) 
			{
				if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("wordnet with:"+actWord);
				Set<String> synonyms = wordFilter.getSynonymsWordNet(actWord);
				for (Iterator<String> iterator = synonyms.iterator(); iterator.hasNext();) {
					String actSynonym = (String) iterator.next();
					if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("wordnet synonym:"+actSynonym);
					
					if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("query dbpedia with:"+actSynonym);
					DbpediaSpotlightResult dbpediaResults = dbpediaSpotlight.selectEntityDbpediaSpotlight(actSynonym, logger);
					if ( dbpediaResults != null && dbpediaResults.isDBpediaConcept() ) {
						ArrayList<String> dbPediaConcepts = dbpediaResults.getDBpediaConcept();
//						ArrayList<String> freebaseConcepts = dbpediaResults.getFreebaseConcept();
//						ArrayList<String> schemaConcepts = dbpediaResults.getSchemaConcept();
				        PartnerdataTracer.dumpFile(DbpediaSpotlight.class, this.partnerConfig, dbpediaResults.toString(), "dbpedia-response-concept", FILETYPE.TXT, logger);
						if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("query dbpedia result:"+dbpediaResults.toString());
						for (int i = 0; i < dbPediaConcepts.size(); i++) {
							EnrichmentResult enrichmentResult = new EnrichmentResult();
							enrichmentResult.setWord(dbpediaResults.getName());
							enrichmentResult.setUri(dbpediaResults.getURI());
							enrichmentResult.setType(dbPediaConcepts.get(i));
							enrichmentResults.add(enrichmentResult);
						}

					}
					if (useFreebase)
					{
						ArrayList<FreebaseResult> responseFreebase = freebase.getEntitiesFreeBase(actSynonym, logger);
						for (FreebaseResult freebaseResult : responseFreebase) {
							EnrichmentResult enrichmentResult = new EnrichmentResult();
							enrichmentResult.setWord(freebaseResult.getName());
							enrichmentResult.setUri(freebaseResult.getURI());
							enrichmentResult.setLanguage(freebaseResult.getLanguage());
							enrichmentResults.add(enrichmentResult);
							if (freebaseResult.getNotableId()!= null && ! freebaseResult.getNotableId().isEmpty() &&
									(freebaseResult.getNotableId().toLowerCase().startsWith("/location") ) )
		//							.contains("city")||
		//							responseFreebase.contains("town")||
		//							responseFreebase.contains("village")||
		//							responseFreebase.contains("river")||
		//							responseFreebase.contains("mountain")||
		//							responseFreebase.contains("location"))
							{
								if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("query geonames with:"+actSynonym);
								Set<EnrichmentResult> responseGeonames = geonames.getLocationHierarchy(actSynonym, logger);
								enrichmentResults.addAll(responseGeonames);
							}
						}
					}
				}
			}
		}
*/
		if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("enriching: input:" + text +"\n\n\n" + enrichmentResults.toString());
		if (this.partnerConfig.partnerDataRequestsTrace) System.out.println("------------------------------------------------------------");

		return enrichmentResults;
	}
}
