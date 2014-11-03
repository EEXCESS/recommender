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


public class Anchor {

	private String id;
	private String fileName;
	private String anchorStartTime;
	private String anchorEndTime;
	private String itemStartTime;
	private String itemEndTime;
	
		
	public Anchor(String id, String fileName, String anchorStartTime,
			String anchorEndTime, String itemStartTime, String itemEndTime) {
		this.id = id;
		this.fileName = fileName;
		this.anchorStartTime = anchorStartTime;
		this.anchorEndTime = anchorEndTime;
		this.itemStartTime = itemStartTime;
		this.itemEndTime = itemEndTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAnchorStartTime() {
		return anchorStartTime;
	}
	public void setAnchorStartTime(String anchorStartTime) {
		this.anchorStartTime = anchorStartTime;
	}
	public String getAnchorEndTime() {
		return anchorEndTime;
	}
	public void setAnchorEndTime(String anchorEndTime) {
		this.anchorEndTime = anchorEndTime;
	}
	public String getItemStartTime() {
		return itemStartTime;
	}
	public void setItemStartTime(String itemStartTime) {
		this.itemStartTime = itemStartTime;
	}
	public String getItemEndTime() {
		return itemEndTime;
	}
	public void setItemEndTime(String itemEndTime) {
		this.itemEndTime = itemEndTime;
	}
	
}
