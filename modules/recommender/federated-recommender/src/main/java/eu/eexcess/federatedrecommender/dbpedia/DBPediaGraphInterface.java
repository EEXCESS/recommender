package eu.eexcess.federatedrecommender.dbpedia;

import java.util.List;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

public interface  DBPediaGraphInterface<E>  {

	/**
	 * Builds a new graph of related keywords according to dbPedia - multiple keywords
	 * @throws FederatedRecommenderException 
	 * 
	 */
	public E getGraphFromKeywords(List<ContextKeyword> profileKeywords, List<String> keynodes,int hitsLimit, int depthLimit) throws FederatedRecommenderException;

}