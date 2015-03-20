package eu.eexcess.diversityeasurement.ndcg;

public class NDCGIACategory {

	private String categoryName;
	private Double documentWeight;
	private Double queryWeight;

	public NDCGIACategory(String categoryName, Double documentWeight,Double queryWeight) {
		this.categoryName = categoryName;
		this.documentWeight = documentWeight;
		this.queryWeight = queryWeight;
	}
	
	
	public Double getWeight() {
		return documentWeight;
	}
	public void setWeight(Double weight) {
		this.documentWeight = weight;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Double getQueryWeight() {
		return queryWeight;
	}
	public void setQueryWeight(Double queryWeight) {
		this.queryWeight = queryWeight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
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
		NDCGIACategory other = (NDCGIACategory) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "NDCGIACategory [categoryName=" + categoryName + ", weight="
				+ documentWeight + ", queryWeight=" + queryWeight + "]";
	}
	


	
}
