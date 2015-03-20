package eu.eexcess.diversityeasurement.ndcg;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.dataformats.result.Result;


public class NDCGResult extends Result{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double nDCGRelevance;
	public List<NDCGIACategory> categories = new ArrayList<NDCGIACategory>();
}
