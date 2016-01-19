package eu.eexcess.federatedrecommender.dbpedia;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

import java.util.List;

public interface  DBPediaGraphInterface<E>  {

	/**
	 * Builds a new graph of related keywords according to dbPedia - multiple keywords
	 * @throws FederatedRecommenderException 
	 * 
	 */
	E getGraphFromKeywords(List<ContextKeyword> profileKeywords, List<String> keynodes, int hitsLimit, int depthLimit) throws FederatedRecommenderException;

}