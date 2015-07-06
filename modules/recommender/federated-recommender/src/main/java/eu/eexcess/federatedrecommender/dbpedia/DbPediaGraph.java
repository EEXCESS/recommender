/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.eexcess.federatedrecommender.dbpedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * 
 * @author hziak
 *
 */
public class DbPediaGraph {
	private static final String DBPEDIAFAILURE = "DBPedia index could not be searched for keyword";
    private static final Logger logger = Logger.getLogger(DbPediaGraph.class.getName());
	private DbPediaSolrIndex dbPediaIndex = null; 
    private ExecutorService threadPool;
	
	public DbPediaGraph(DbPediaSolrIndex dbPediaSolrIndex) {
		 threadPool = Executors.newFixedThreadPool(10); //TODO: value in the config file
			this.dbPediaIndex  =dbPediaSolrIndex;
	}
	
	/**
	 * Builds a new graph of related keywords according to dbPedia - multiple keywords
	 * @throws FederatedRecommenderException 
	 * 
	 */
	public SimpleWeightedGraph<String,DefaultEdge> getFromKeywords (List<ContextKeyword> profileKeywords, final List<String> keynodes, final int hitsLimit, final int depthLimit) throws FederatedRecommenderException
	{
		final  SimpleWeightedGraph<String, DefaultEdge> g = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		final List<String> visitedNodes = new ArrayList<String>();
		Map<String,Future<Void>> futures= new HashMap<String, Future<Void>>();
		for (final ContextKeyword keyword : profileKeywords) {
            Future<Void> future = threadPool.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                	try {
        				searchKeyNodes(g, keyword.text, keynodes, visitedNodes, hitsLimit, depthLimit);
        			} catch (FederatedRecommenderException e) {
        				logger.log(Level.SEVERE,DBPEDIAFAILURE);
        				throw new FederatedRecommenderException(DBPEDIAFAILURE, e);
        			}                        
                     return null;
                }
            });
            futures.put(keyword.text, future);
			
		}
		for (Entry<String, Future<Void>> entry : futures.entrySet()) {
		    try {
                entry.getValue().get();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to the graph for keywords: "+profileKeywords, e);
            }
		}
		return g;
		
	}
	public  void searchKeyNodes(SimpleWeightedGraph<String, DefaultEdge> g, String keyword, List<String> keynodes,List<String> vistedNodes, int hitsLimit, int depthLimit) throws FederatedRecommenderException{
		List<DbPediaIndexBean> results = null;
		try {
			results = dbPediaIndex.search(hitsLimit, "entity_en:\"" + keyword + "\" AND edge:\"http://xmlns.com/foaf/0.1/name\"");
		} catch (FederatedRecommenderException e) {
			logger.log(Level.SEVERE,DBPEDIAFAILURE);
			throw new FederatedRecommenderException(DBPEDIAFAILURE, e);
		}
		synchronized (this) {
			g.addVertex(keyword);
			for (DbPediaIndexBean item : results) {
				try {
					g.addVertex(item.parentNode);
					g.addEdge(keyword, item.parentNode);
					buildFromKeyword(g, item.parentNode , vistedNodes, hitsLimit, depthLimit);
				} catch (FederatedRecommenderException e) {
					logger.log(Level.SEVERE,DBPEDIAFAILURE);
					throw new FederatedRecommenderException(DBPEDIAFAILURE, e);
				}
			}
			keynodes.add(keyword);
		}
		
	}
	/**
	 * Adds related keywords from dbPedia to an existing graph - single keyword
	 * @param keyNodes 
	 * @throws FederatedRecommenderException 
	 * 
	 */
	public void buildFromKeyword(	SimpleWeightedGraph<String, DefaultEdge> g,
							 				String keyword,
							 				List<String> visitedNodes,int hitsLimit,
							 				int depthLimit) throws FederatedRecommenderException {
		
		if (depthLimit > 0)
		{
			List<DbPediaIndexBean> results;
			try {
				results = dbPediaIndex.search(hitsLimit, "parentNode:\"" + keyword + "\"");
			} catch (FederatedRecommenderException e1) {
				logger.log(Level.SEVERE,DBPEDIAFAILURE);
				throw new FederatedRecommenderException(DBPEDIAFAILURE, e1);
			}
			
			for (DbPediaIndexBean doc : results) {
				
				String object = doc.childNode;
				String subject = doc.parentNode;
				addVertex(g, visitedNodes, hitsLimit, depthLimit, object, subject);
				}
				
	
		}
	}

    private void addVertex(SimpleWeightedGraph<String, DefaultEdge> g, List<String> visitedNodes, int hitsLimit, int depthLimit, String object, String subject)
            throws FederatedRecommenderException {
        if(object!=null)
        if(!object.equals(subject)){
        	g.addVertex(object);
        	g.addVertex(subject);
        	g.addEdge(object, subject);
        	if(!visitedNodes.contains(subject)){
        		visitedNodes.add(subject);
        		buildFromKeyword(g, subject, visitedNodes, hitsLimit, depthLimit - 1);
        	}else{
        		logger.log(Level.FINEST,"GRAPH: Node "+subject+" allready in graph ");
        	}
        }
    }
	
}
