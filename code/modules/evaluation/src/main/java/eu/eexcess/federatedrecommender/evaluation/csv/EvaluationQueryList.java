package eu.eexcess.federatedrecommender.evaluation.csv;

import java.util.ArrayList;

import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;

public class EvaluationQueryList {

	ArrayList<EvaluationQuery> queries= new ArrayList<EvaluationQuery>();
		
	public ArrayList<EvaluationQuery> getQueries() {
		return queries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((queries == null) ? 0 : queries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvaluationQueryList other = (EvaluationQueryList) obj;
		if (queries == null) {
			if (other.queries != null)
				return false;
		} else if (!queries.equals(other.queries))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EvaluationQueryList [queries=" + queries + "]";
	}
	
}
