package eu.eexcess.diversityeasurement.ndcg;

import java.util.ArrayList;
import java.util.List;
/**
 * NDCG-IA implementation
 * @author hziak
 *
 */
public class NDCGIA extends NDCG {
	/**
	 * 
	 * @param resultList
	 * @param at e.g. NDCG@5
	 * @return
	 */
	public Double calcNDCGIA(NDCGResultList resultList, int at){
		Double nDCGIA=0.0;
		List<NDCGIACategory> categories = new ArrayList<NDCGIACategory>();
		for (NDCGResult result : resultList.results) {
			for (NDCGIACategory cat : result.categories) {
				if(!categories.contains(cat)){
					categories.add(cat);
				}
			}
		}
		for (NDCGIACategory cat : categories) {
			Double nDCG = calcNDCG(resultList, cat, at);
			nDCGIA += nDCG* cat.getQueryWeight();
			System.out.println(nDCG + " IA " + nDCG* cat.getQueryWeight() );
		}
		return nDCGIA;
	}
	
}
