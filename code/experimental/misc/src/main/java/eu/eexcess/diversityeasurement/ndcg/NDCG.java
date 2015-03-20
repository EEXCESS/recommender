package eu.eexcess.diversityeasurement.ndcg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import eu.eexcess.dataformats.result.ResultList;

/**
 * Normalized Discounted cumulative gain
 * 
 * @author hziak
 *
 */
public class NDCG {

	/**
	 * Calculates the NDCG for a given input List
	 * 
	 * @param resultList
	 * @param category for ndcg-ia else null
	 * @param at 
	 * @return
	 */
	public Double calcNDCG(NDCGResultList resultList, NDCGIACategory category, int at) {
		Double ndcg = 0.0;

		if (resultList.results.size() > 1) {
			Double rel1 = resultList.results.get(0).nDCGRelevance;
			Double dCG = rel1;
			NDCGResultList sortedByRelevance = getRelevanceSortedResultList(resultList);
			Double iDCG = sortedByRelevance.results.get(0).nDCGRelevance;
			for (int i = 0; i < resultList.results.size() && i<at; i++) {
				boolean categoryFlagRel = true;
				boolean categoryFlagNRel = true;
				if (category != null) {
					if (!resultList.results.get(i).categories.contains(category))
						categoryFlagRel = false;
					if (!sortedByRelevance.results.get(i).categories.contains(category))
						categoryFlagNRel = false;

				}
					Double relI = 0.0;
					Double nrelI = 0.0;
					Double 	log2I = Math.log(i + 2) / Math.log(2.0);
					if (categoryFlagRel) {
						relI = resultList.results.get(i).nDCGRelevance;
					}
					if(categoryFlagNRel){
						nrelI = sortedByRelevance.results.get(i).nDCGRelevance;
					}
					
					if (log2I != null && log2I != 0.0) {
						double d = (Math.pow(2, relI) - 1) / log2I;
						dCG += d;
						double e = (Math.pow(2, nrelI) - 1) / log2I;
						iDCG += e;
					}
				}
			System.out.println("DCG " +dCG + " IDCG " + iDCG);
			ndcg = dCG / iDCG;
		}
		return ndcg;
	}

	protected NDCGResultList getRelevanceSortedResultList(
			NDCGResultList resultList) {
		NDCGResultList sorted = new NDCGResultList();
		Comparator<NDCGResult> resultListComperator = new Comparator<NDCGResult>() {

			@Override
			public int compare(NDCGResult o1, NDCGResult o2) {
				if (o1.nDCGRelevance < o2.nDCGRelevance)
					return 1;
				if (o1.nDCGRelevance > o2.nDCGRelevance)
					return -1;
				else
					return 0;
			}
		};
		sorted.results = new ArrayList<NDCGResult>(resultList.results);
		Collections.sort(sorted.results, resultListComperator);
		return sorted;
	}

}
