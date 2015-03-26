package eu.eexcess.dataformats.result;

import java.io.Serializable;

/**
 * Partner statistics for each query result contains the constructed query and
 * processing times
 * 
 * @author hziak
 *
 */
public class ResultStats implements Serializable {

	private static final long serialVersionUID = 1L;

	private String partnerQuery;
	private long partnerCallTime;
	private long firstTransformationTime;
	private long secondTransformationTime;
	private long enrichmentTime;

	private int resultCount;

	public ResultStats(String partnerQuery, long partnerCallTime, long firstTransformationTime,
					long secondTransformationTime, long enrichmentTime, int resultCount) {
		this.partnerQuery = partnerQuery;
		this.partnerCallTime = partnerCallTime;
		this.firstTransformationTime = firstTransformationTime;
		this.secondTransformationTime = secondTransformationTime;
		this.enrichmentTime = enrichmentTime;
		this.resultCount= resultCount;
	}

	public ResultStats() {
	}
	
	public String getPartnerQuery() {
		return partnerQuery;
	}

	public void setPartnerQuery(String partnerQuery) {
		this.partnerQuery = partnerQuery;
	}

	public long getPartnerCallTime() {
		return partnerCallTime;
	}

	public void setPartnerCallTime(long partnerCallTime) {
		this.partnerCallTime = partnerCallTime;
	}

	public long getFirstTransformationTime() {
		return firstTransformationTime;
	}

	public void setFirstTransformationTime(long firstTransformationTime) {
		this.firstTransformationTime = firstTransformationTime;
	}

	public long getSecondTransformationTime() {
		return secondTransformationTime;
	}

	public void setSecondTransformationTime(long secondTransformationTime) {
		this.secondTransformationTime = secondTransformationTime;
	}

	public long getEnrichmentTime() {
		return enrichmentTime;
	}

	public void setEnrichmentTime(long enrichmentTime) {
		this.enrichmentTime = enrichmentTime;
	}

	public String toString() {
		return "ResultStats [partnerQuery=" + partnerQuery + ", partnerCallTime=" + partnerCallTime
						+ ", firstTransformationTime=" + firstTransformationTime + ", secondTransformationTime="
						+ secondTransformationTime + ", enrichmentTime=" + enrichmentTime + "]";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (enrichmentTime ^ (enrichmentTime >>> 32));
		result = prime * result + (int) (firstTransformationTime ^ (firstTransformationTime >>> 32));
		result = prime * result + (int) (partnerCallTime ^ (partnerCallTime >>> 32));
		result = prime * result + ((partnerQuery == null) ? 0 : partnerQuery.hashCode());
		result = prime * result + (int) (secondTransformationTime ^ (secondTransformationTime >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultStats other = (ResultStats) obj;
		if (enrichmentTime != other.enrichmentTime)
			return false;
		if (firstTransformationTime != other.firstTransformationTime)
			return false;
		if (partnerCallTime != other.partnerCallTime)
			return false;
		if (partnerQuery == null) {
			if (other.partnerQuery != null)
				return false;
		} else if (!partnerQuery.equals(other.partnerQuery))
			return false;
		if (secondTransformationTime != other.secondTransformationTime)
			return false;
		return true;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

}
