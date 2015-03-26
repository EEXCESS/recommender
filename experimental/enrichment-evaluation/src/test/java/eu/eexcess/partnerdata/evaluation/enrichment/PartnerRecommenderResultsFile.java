package eu.eexcess.partnerdata.evaluation.enrichment;

import java.util.ArrayList;

public class PartnerRecommenderResultsFile {

	private String filename="";
	private String filenamePath="";
	
	public String getFilenamePath() {
		return filenamePath;
	}

	public void setFilenamePath(String filenamePath) {
		this.filenamePath = filenamePath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	private ArrayList<PartnerRecommenderResultLogEntry> results = new ArrayList<PartnerRecommenderResultLogEntry>();

	public ArrayList<PartnerRecommenderResultLogEntry> getResults() {
		return results;
	}

	public void setResults(ArrayList<PartnerRecommenderResultLogEntry> results) {
		this.results = results;
	}
	
	public void addResult(PartnerRecommenderResultLogEntry result) {
		if (this.results == null)
			this.results = new ArrayList<PartnerRecommenderResultLogEntry>();
		this.results.add(result);
	}
}
