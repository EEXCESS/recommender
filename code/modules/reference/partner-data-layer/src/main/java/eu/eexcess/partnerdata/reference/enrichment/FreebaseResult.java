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

public class FreebaseResult {

	protected String uri;
	protected String name;
	protected String id;
	protected String notableName;
	protected String notableId;
	protected String language;
	
	public String getURI() {
		return "http://www.freebase.com" + this.id;
	}
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNotableName() {
		return notableName;
	}
	public void setNotableName(String notableName) {
		this.notableName = notableName;
	}
	public String getNotableId() {
		return notableId;
	}
	public void setNotableId(String notableId) {
		this.notableId = notableId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
