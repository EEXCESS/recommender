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

import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

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

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * 
 * @author hziak
 *
 */
public class DBPediaGraphJGraph implements
		DBPediaGraphInterface<SimpleWeightedGraph<String, DefaultEdge>> {
	private static final String DBPEDIAFAILURE = "DBPedia index could not be searched for keyword";
	private static final Logger logger = Logger
			.getLogger(DBPediaGraphJGraph.class.getName());
	private DbPediaSolrIndex dbPediaIndex = null;
	private ExecutorService threadPool;

	public DBPediaGraphJGraph(DbPediaSolrIndex dbPediaSolrIndex) {
		threadPool = Executors.newFixedThreadPool(10); // TODO: value in the
														// config file
		this.dbPediaIndex = dbPediaSolrIndex;
	}

	public ContextKeyword searchKeyNodes(SimpleWeightedGraph<String, DefaultEdge> g,
			ContextKeyword keyword, List<String> keynodes,
			List<String> vistedNodes, int hitsLimit, int depthLimit)
			throws FederatedRecommenderException {
		List<DbPediaIndexBean> results = null;
		try {

			String query = "entity_en:\"" + keyword.getText() + "\""; // \" AND edge:\"http://xmlns.com/foaf/0.1/name\"";
			// logger.log(Level.INFO,"Query:_"+query);
			results = dbPediaIndex.search(hitsLimit, query);
		} catch (FederatedRecommenderException e) {
			logger.log(Level.SEVERE, DBPEDIAFAILURE);
			throw new FederatedRecommenderException(DBPEDIAFAILURE, e);
		}

		// logger.log(Level.INFO,"Result:" +results.toString());
		synchronized (g) {
			g.addVertex(keyword.getText());
			for (DbPediaIndexBean item : results) {
				try {
					if (item.entity_en.replace(",.*", "").toLowerCase().equals(
							keyword.getText().toLowerCase().trim())) {
						keyword.setReason("unique");
						logger.log(Level.INFO, item.entity_en +" "+ keyword.getText());
					}
					g.addVertex(item.parentNode);
					g.addEdge(keyword.getText(), item.parentNode);
					buildFromKeyword(g, item.parentNode, vistedNodes,
							hitsLimit, depthLimit);
				} catch (FederatedRecommenderException e) {
					logger.log(Level.SEVERE, DBPEDIAFAILURE);
					throw new FederatedRecommenderException(DBPEDIAFAILURE, e);
				}
			}
			
			// g.removeVertex(keyword);
			keynodes.add(keyword.getText());
		}
		return keyword;

	}

	/**
	 * Adds related keywords from dbPedia to an existing graph - single keyword
	 * 
	 * @param keyNodes
	 * @throws FederatedRecommenderException
	 * 
	 */
	public void buildFromKeyword(SimpleWeightedGraph<String, DefaultEdge> g,
			String keyword, List<String> visitedNodes, int hitsLimit,
			int depthLimit) throws FederatedRecommenderException {

		if (depthLimit > 0) {
			List<DbPediaIndexBean> results;
			try {
				results = dbPediaIndex.search(hitsLimit, "parentNode:\""
						+ keyword + "\"");
			} catch (FederatedRecommenderException e1) {
				logger.log(Level.SEVERE, DBPEDIAFAILURE);
				throw new FederatedRecommenderException(DBPEDIAFAILURE, e1);
			}

			for (DbPediaIndexBean doc : results) {

				String object = doc.childNode;
				String subject = doc.parentNode;
				addVertex(g, visitedNodes, hitsLimit, depthLimit, object,
						subject);
			}

		}
	}

	private void addVertex(SimpleWeightedGraph<String, DefaultEdge> g,
			List<String> visitedNodes, int hitsLimit, int depthLimit,
			String object, String subject) throws FederatedRecommenderException {
		if (object != null && !object.equals(subject)) {
			g.addVertex(object);
			g.addVertex(subject);
			g.addEdge(object, subject);
			if (!visitedNodes.contains(subject)) {
				visitedNodes.add(subject);
				buildFromKeyword(g, subject, visitedNodes, hitsLimit,
						depthLimit - 1);
			} else {
				logger.log(Level.FINEST, "GRAPH: Node " + subject
						+ " allready in graph ");
			}
		}
	}

	@Override
	public SimpleWeightedGraph<String, DefaultEdge> getGraphFromKeywords(
			List<ContextKeyword> profileKeywords, List<String> keynodes,
			int hitsLimit, int depthLimit) throws FederatedRecommenderException {
		final SimpleWeightedGraph<String, DefaultEdge> g = new SimpleWeightedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		
		final List<String> visitedNodes = new ArrayList<String>();
		List<ContextKeyword> tmpKeywords = new ArrayList<ContextKeyword>();
		for(int i = 0; i < profileKeywords.size(); i++){
			if(i+1<profileKeywords.size())
				tmpKeywords.add(new ContextKeyword(profileKeywords.get(i).getText()+ " "+profileKeywords.get(i+1).getText()));
			if(i+2<profileKeywords.size())
				tmpKeywords.add(new ContextKeyword(profileKeywords.get(i).getText()+ " "+profileKeywords.get(i+1).getText()+ " "+profileKeywords.get(i+2).getText()));
		}
		profileKeywords.addAll(tmpKeywords);
		Map<String, Future<ContextKeyword>> futures = new HashMap<String, Future<ContextKeyword>>();
		for (final ContextKeyword keyword : profileKeywords) {
			Future<ContextKeyword> future = threadPool.submit(new Callable<ContextKeyword>() {
				@Override
				public ContextKeyword call() throws Exception {
					ContextKeyword result = null;
					try {
						result =searchKeyNodes(g, keyword, keynodes, visitedNodes,
								hitsLimit, depthLimit);
					} catch (FederatedRecommenderException e) {
						logger.log(Level.SEVERE, DBPEDIAFAILURE);
						throw new FederatedRecommenderException(DBPEDIAFAILURE,
								e);
					}
					return result;
				}
			});
			futures.put(keyword.getText(), future);

		}
		for (Entry<String, Future<ContextKeyword>> entry : futures.entrySet()) {
			try {
				ContextKeyword keyword =entry.getValue().get();
				System.out.println(keyword.toString());
			} catch (Exception e) {
				logger.log(Level.SEVERE,
						"Failed to get the graph for keywords: "
								+ profileKeywords, e);
			}
		}
		
		return g;
	}

}
