package eu.eexcess.federatedrecommender.evaluation.schloett.dataformats;

import java.io.Serializable;
import java.net.URL;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SchloettHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("visit_id")
	Integer visitID;
	@JsonProperty("task_id")
	Integer taskID;
	@JsonProperty("url")
	URL uRL;
	@JsonProperty("task_name")
	String taskName;
	@Override
	public String toString() {
		return "SchloettHistory [visitID=" + visitID + ", taskID=" + taskID
				+ ", uRL=" + uRL + ", taskName=" + taskName + "]";
	}
	
}