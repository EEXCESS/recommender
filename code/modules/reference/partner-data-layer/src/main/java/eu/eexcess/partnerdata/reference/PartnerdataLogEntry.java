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
package eu.eexcess.partnerdata.reference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

public class PartnerdataLogEntry {
	private static String CSVSeperator = ";";
	
	protected Date date;
	
	protected int resultsFound = 0;
	protected int resultsReturnend = 0;

	protected String systemId = "";

	protected int enrichmentServicesCalled = 0;
	protected int enrichmentServicesResults = 0;
	
	protected int enrichmentGeonamesServiceCalls = 0;
	protected int enrichmentGeonamesResults = 0;
	protected long enrichmentGeonamesServiceCallduration = 0;
	
	protected int enrichmentFreebaseServiceCalls = 0;
	protected int enrichmentFreebaseResults = 0;
	protected long enrichmentFreebaseServiceCallduration = 0;
	
	protected long start;
	protected long queryPartnerAPIStart;
	protected long queryPartnerAPIEnd;
	protected long end;
	
	public PartnerdataLogEntry() {
		super();
		date = Calendar.getInstance().getTime();
	}
	
	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemid) {
		this.systemId = systemid;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void start(){
		this.start = System.currentTimeMillis();
	}
	
	public void end() {
		this.end = System.currentTimeMillis();
	}
	
	public void queryPartnerAPIStart()
	{
		this.queryPartnerAPIStart = System.currentTimeMillis();
	}
	
	public void queryPartnerAPIEnd()
	{
		this.queryPartnerAPIEnd = System.currentTimeMillis();
	}
	
	
	public int getEnrichmentServicesResults() {
		return enrichmentServicesResults;
	}

//	public void addEnrichmentServicesResults(int enrichmentServicesResults) {
//		this.enrichmentServicesResults = this.enrichmentServicesResults + enrichmentServicesResults;
//	}

	public int getEnrichmentGeonamesServiceCalls() {
		return enrichmentGeonamesServiceCalls;
	}

	public void addEnrichmentGeonamesServiceCalls(int enrichmentGeonamesServiceCalls) {
		this.enrichmentServicesCalled = this.enrichmentServicesCalled + enrichmentGeonamesServiceCalls;
		this.enrichmentGeonamesServiceCalls = this.enrichmentGeonamesServiceCalls+ enrichmentGeonamesServiceCalls;
	}

	
	public int getResultsFound() {
		return resultsFound;
	}

	public int getEnrichmentServicesCalled() {
		return enrichmentServicesCalled;
	}

//	public void addEnrichmentServicesCalled(int enrichmentServicesCalled) {
//		this.enrichmentServicesCalled = this.enrichmentServicesCalled + enrichmentServicesCalled;
//	}

	public int getEnrichmentGeonamesResults() {
		return enrichmentGeonamesResults;
	}

	public void addEnrichmentGeonamesResults(int enrichmentGeonamesResults) {
		this.enrichmentServicesResults = this.enrichmentServicesResults + enrichmentGeonamesResults;
		this.enrichmentGeonamesResults = this.enrichmentGeonamesResults + enrichmentGeonamesResults;
	}

	public int getEnrichmentFreebaseServiceCalls() {
		return enrichmentFreebaseServiceCalls;
	}

	public void addEnrichmentFreebaseServiceCalls(int enrichmentFreebaseServiceCalls) {
		this.enrichmentServicesCalled = this.enrichmentServicesCalled + enrichmentFreebaseServiceCalls;
		this.enrichmentFreebaseServiceCalls = this.enrichmentFreebaseServiceCalls + enrichmentFreebaseServiceCalls;
	}

	public int getEnrichmentFreebaseResults() {
		return enrichmentFreebaseResults;
	}

	public void addEnrichmentFreebaseResults(int enrichmentFreebaseResults) {
		this.enrichmentServicesResults = this.enrichmentServicesResults + enrichmentFreebaseResults;
		this.enrichmentFreebaseResults = this.enrichmentFreebaseResults + enrichmentFreebaseResults;
	}

	public void addResultsFound(int resultsFound) {
		this.resultsFound = this.resultsFound + resultsFound;
	}

	public int getResultsReturnend() {
		return resultsReturnend;
	}

	public void addResultsReturnend(int resultsReturnend) {
		this.resultsReturnend = this.resultsReturnend + resultsReturnend;
	}
	
	public String getCSVHeader(){
		String ret ="";
		ret += "date" + CSVSeperator;
		ret += "systemId" + CSVSeperator;
		ret += "resultsFound" + CSVSeperator;
		ret += "resultsReturnend" + CSVSeperator;
		ret += "enrichmentServicesCalled" + CSVSeperator;
		ret += "enrichmentServicesResults" + CSVSeperator;
		ret += "enrichmentGeonamesServiceCalls" + CSVSeperator;
		ret += "enrichmentGeonamesResults" + CSVSeperator;
		ret += "enrichmentFreebaseServiceCalls" + CSVSeperator;
		ret += "enrichmentFreebaseResults" + CSVSeperator;
		ret += "duration" + CSVSeperator;
		ret += "partnerAPI";
		return ret;
	}
	
	public String getCVSValues() {
		String ret ="";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String reportDate = df.format(date);
		ret += escapeForCSV(reportDate) + CSVSeperator;
		ret += escapeForCSV(systemId) + CSVSeperator;
		ret += escapeForCSV(resultsFound) + CSVSeperator;
		ret += escapeForCSV(resultsReturnend) + CSVSeperator;
		ret += escapeForCSV(enrichmentServicesCalled) + CSVSeperator;
		ret += escapeForCSV(enrichmentServicesResults) + CSVSeperator;
		ret += escapeForCSV(enrichmentGeonamesServiceCalls) + CSVSeperator;
		ret += escapeForCSV(enrichmentGeonamesResults) + CSVSeperator;
		ret += escapeForCSV(enrichmentFreebaseServiceCalls) + CSVSeperator;
		ret += escapeForCSV(enrichmentFreebaseResults) + CSVSeperator;
		ret += (this.end - this.start)  + CSVSeperator;
		ret += (this.queryPartnerAPIEnd - this.queryPartnerAPIStart);
 		return ret;
	}
	
	private String escapeForCSV(String value)
	{
		return "\"" + value + "\"";
	}
	
	private String escapeForCSV(int value)
	{
		return "\"" + value + "\"";
	}

	public void addQuery(SecureUserProfile userProfile) {
		
	}

	public void addResults(ResultList recommendations) {
		this.resultsFound = recommendations.totalResults;
		if (recommendations.results != null)
			this.resultsReturnend = recommendations.results.size();
	}

	public long getEnrichmentGeonamesServiceCallduration() {
		return enrichmentGeonamesServiceCallduration;
	}

	public void setEnrichmentGeonamesServiceCallduration(
			long newDuration) {
		this.enrichmentGeonamesServiceCallduration = newDuration;
	}

	public void addEnrichmentGeonamesServiceCallduration(
			long newDuration) {
		this.enrichmentGeonamesServiceCallduration = this.enrichmentGeonamesServiceCallduration + newDuration;
	}

	public long getEnrichmentFreebaseServiceCallduration() {
		return enrichmentFreebaseServiceCallduration;
	}

	public void setEnrichmentFreebaseServiceCallduration(
			long newDuration) {
		this.enrichmentFreebaseServiceCallduration = newDuration;
	}

	public void addEnrichmentFreebaseServiceCallduration(
			long newDuration) {
		this.enrichmentFreebaseServiceCallduration = this.enrichmentFreebaseServiceCallduration + newDuration;
	}
}
