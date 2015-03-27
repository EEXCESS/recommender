package eu.eexcess.diversityasurement.ndcg;

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
//			Double rel1 = resultList.results.get(0).nDCGRelevance;
			Double dCG = 0.0;//rel1;
			NDCGResultList sortedByRelevance = getRelevanceSortedResultList(resultList,category,at);
			Double iDCG = 0.0;// sortedByRelevance.results.get(0).nDCGRelevance;
			for (int i = 0; i < resultList.results.size() && i<at && i<sortedByRelevance.results.size(); i++) {
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
					int a = i + 2;
					Double 	log2I = Math.log10(a)/ Math.log10(2.0);
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
			ndcg = dCG / iDCG;
		}
		return ndcg;
	}

	protected NDCGResultList getRelevanceSortedResultList(
			NDCGResultList resultList, NDCGIACategory category, int at) {
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
		
		if(category!=null){ //TODO: that is not performant at all
			ArrayList<NDCGResult> categorySortedResult =new ArrayList<NDCGResult>();
			for (NDCGResult ndcgResult : sorted.results) {
				if(ndcgResult.categories.contains(category) && categorySortedResult.size()<at)
					categorySortedResult.add(ndcgResult);
			}
			while(categorySortedResult.size()<at){
				NDCGResult empty = new NDCGResult();
				empty.categories.add(category);
				empty.nDCGRelevance=0.0;
				empty.title="empty";
				categorySortedResult.add(empty );
			}
				
			sorted.results= categorySortedResult;
		}
		return sorted;
	}

}
