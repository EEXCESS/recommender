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

public class DbpediaSpotlightResult {

	private static final String CONCEPT_DBPEDIA = "DBpedia:";
	private static final String CONCEPT_FREEBASE = "Freebase:";
	private static final String CONCEPT_SCHEMA = "Schema:";
	protected String uri;
	protected String name;
	
	protected ArrayList<String> types;
	
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
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getURI() {
		return this.uri;
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isConcept()
	{
		if (this.uri != null && this.types != null && this.types.size() > 0 ) return true;
		return false;
	}
	
	public boolean isDBpediaConcept()
	{
		if (isConcept()) 
		{
			for (int i = 0; i < this.types.size(); i++) {
				String type = this.types.get(i);
				if (type.startsWith(CONCEPT_DBPEDIA))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	public ArrayList<String> getAllConcepts()
	{
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(getDBpediaConcept());
		ret.addAll(getFreebaseConcept());
		ret.addAll(getSchemaConcept());
		return ret;
	}

	public ArrayList<String> getDBpediaConcept()
	{
		ArrayList<String> ret = new ArrayList<String>();
		if (isConcept()) 
		{
			for (int i = 0; i < this.types.size(); i++) {
				String type = this.types.get(i);
				if (type.startsWith(CONCEPT_DBPEDIA))
				{
					ret.add(type.replace(CONCEPT_DBPEDIA, "http://dbpedia.org/ontology/"));
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getFreebaseConcept()
	{
		ArrayList<String> ret = new ArrayList<String>();
		if (isConcept()) 
		{
			for (int i = 0; i < this.types.size(); i++) {
				String type = this.types.get(i);
				if (type.startsWith(CONCEPT_FREEBASE))
				{
					ret.add(type.replace(CONCEPT_FREEBASE, "http://www.freebase.com"));
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getSchemaConcept()
	{
		ArrayList<String> ret = new ArrayList<String>();
		if (isConcept()) 
		{
			for (int i = 0; i < this.types.size(); i++) {
				String type = this.types.get(i);
				if (type.startsWith(CONCEPT_SCHEMA))
				{
					ret.add(type.replace(CONCEPT_SCHEMA, "http://schema.org/"));
				}
			}
		}
		return ret;
	}

	public String toString()
    {
		String ret = "";
		ret += "uri:" + (this.uri== null ? "" : this.getURI()) + "\n";
		ret += "name:" + (this.name== null ? "" : this.name) + "\n";
		ret += "types:";
		if (this.types == null)
			ret += "null";
		else {
			for (int i = 0; i < this.types.size(); i++) {
				ret += "\n  " + this.types.get(i);
			}
		}
		return ret;
    }
}
