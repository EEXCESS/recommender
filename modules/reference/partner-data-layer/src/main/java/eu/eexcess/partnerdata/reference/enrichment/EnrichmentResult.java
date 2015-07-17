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

public class EnrichmentResult {
	public enum EnrichmentSource {
	    GEONAMES, DBPEDIA 
	}
	
	protected EnrichmentSource enrichmentSource;
	
	public EnrichmentResult(EnrichmentSource source) 
	{
		this.enrichmentSource = source;
	}
	
//	private EnrichmentResult()
//	{
//		
//	}
	
	public String getEnrichmentSource() {
		return enrichmentSource.toString();
	}

	public void setEnrichmentSource(EnrichmentSource enrichmentSource) {
		this.enrichmentSource = enrichmentSource;
	}

	protected String word;
	
	protected String language;

	protected String uri;
	
	protected String type;
	
	protected double longitude;
	protected double latitude;
	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	protected ArrayList<String> types = new ArrayList<String>();

	public String getType() {
		return type;
	}

	public ArrayList<String> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}

	public void addType(String type) {
		if (this.types == null)
			this.types = new ArrayList<String>();
		this.types.add(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	private boolean containsInList(String search, String[] list)
	{
		search = search.toLowerCase();
		for(String s : list)
		    if(s.toLowerCase().contains(search)) return true;
		return false;		
	}
	
	String[] geoConcepts = new String[] {
			"http://dbpedia.org/ontology/City", 
			"http://dbpedia.org/ontology/Settlement", 
			"http://dbpedia.org/ontology/PopulatedPlace", 
			"http://dbpedia.org/ontology/Place", 
			"http://www.freebase.com/location/location", 
			"http://www.freebase.com/location", 
			"http://www.freebase.com/people/place_of_interment", 
			"http://www.freebase.com/people", 
			"http://www.freebase.com/location/statistical_region", 
			"http://www.freebase.com/location/dated_location",
			"http://www.freebase.com/location/de_city", 
			"http://www.freebase.com/location/de_urban_district", 
			"http://www.freebase.com/location/citytown", 
			"http://schema.org/Place", 
			"http://schema.org/City"
	};
	
	public boolean isTypeGeographicResource()
	{
		for (int i = 0; i < getTypes().size(); i++) {
			if (this.containsInList(getTypes().get(i), this.geoConcepts))
				return true;
		}
		return false;
	}
	
}
