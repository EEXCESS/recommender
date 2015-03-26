package eu.eexcess.federatedrecommender.evaluation.dataformats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchloettQuery implements Serializable {
	private static final long serialVersionUID = 4433607137755412112L;
	public SchloettQuery(){
	}
	
	@JsonProperty("timestamp")
	public String timeStamp;
	public List<SchloettContext> context = new ArrayList<SchloettContext>();
	@JsonProperty("task_name")
	public String taskName;
	public Integer id;
	public String query;
	@JsonProperty("task_id")
	public String taskId;
	@Override
	public String toString() {
		return "SchloettQuery [timeStamp=" + timeStamp + ",  query=" + query+ "]";
	}
	
}
