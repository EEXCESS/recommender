package eu.eexcess.dataformats;

import java.util.LinkedList;
import java.util.Stack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import eu.eexcess.dataformats.result.ResultStats;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerBadgeStats {
	
	
	@XmlElement(name="requestCount")
	public int requestCount=0;

	@XmlElement(name="failedRequestCount")
	public int failedRequestCount=0;

	@XmlElement(name="failedRequestTimeoutCount")
	public int failedRequestTimeoutCount=0;
	
	@XmlElement(name="lastQueries")
	public LinkedList<ResultStats> lastQueries = new LinkedList<ResultStats>();

	//Begin response time
	@XmlTransient
	public Stack<Long> lastResponseTimes= new Stack<Long>() ;
	@XmlElement(name="shortTimeResponseTimes")
	public Long shortTimeResponseTime;
	
	//End response time	

}
