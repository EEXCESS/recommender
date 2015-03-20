package eu.eexcess.dataformats;

import javax.xml.bind.annotation.XmlElement;

public class RecommenderStats {
	@XmlElement(name="averageGlobalTime")
	private long averageGlobalTime;
	@XmlElement(name="averageAggregationTime")
	private long averageAggregationTime;
	public long getAverageAggregationTime() {
		return averageAggregationTime;
	}

	public void setAverageAggregationTime(long averageAggregationTime) {
		if(this.averageAggregationTime!=0)
			this.averageAggregationTime = (this.averageAggregationTime +averageAggregationTime)/2;
		else
			this.averageAggregationTime = averageAggregationTime;
	}

	public long getAverageGlobalTime() {
		return averageGlobalTime;
	}

	public void setAverageGlobalTime(long averageGlobalTime) {
		if(this.averageGlobalTime!=0)
			this.averageGlobalTime = (this.averageGlobalTime +averageGlobalTime)/2;
		else
			this.averageGlobalTime = averageGlobalTime;
		
	}
}
