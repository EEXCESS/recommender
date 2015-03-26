package eu.eexcess.diversityasurement.ndcg;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.dataformats.result.Result;

/**
 * 
 * @author hziak
 *
 */
public class NDCGResult extends Result{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double nDCGRelevance;
	public List<NDCGIACategory> categories = new ArrayList<NDCGIACategory>();
	@Override
	public String toString() {
		return "NDCGResult ["+super.title+"]";
	}
	
}
