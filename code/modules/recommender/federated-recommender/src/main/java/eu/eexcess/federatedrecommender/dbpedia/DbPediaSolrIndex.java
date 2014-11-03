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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import org.apache.jena.riot.RiotReader;
import org.apache.jena.riot.lang.LangNTriples;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

public class DbPediaSolrIndex {
	private static final Logger logger = Logger
			.getLogger(DbPediaSolrIndex.class.getName());
	private final String solrServer;
	private final SolrServer server;

	public DbPediaSolrIndex(FederatedRecommenderConfiguration federatedRecommenderConfiguration) {
		this.solrServer = federatedRecommenderConfiguration.solrServerUri;
		this.server = new HttpSolrServer(solrServer);

	}
	/**
	 * 
	 * @param dbPediaFile
	 * @param startCounter
	 * @param wipeIndex
	 * @throws FederatedRecommenderException
	 */
	public void createIndex(String dbPediaFile, int startCounter,boolean wipeIndex)
			throws FederatedRecommenderException {

		if(wipeIndex){
		 try {
			 server.deleteByQuery("*:*");
			 server.commit();
			 } catch (SolrServerException e1) {
				 logger.log(Level.SEVERE,"Tried to wipe the index, could not commit",e1);
				 	throw new FederatedRecommenderException("Tried to wipe the index, could not commit");
			 } catch (IOException e1) {
				 logger.log(Level.SEVERE,"Tried to wipe the index, could not commit",e1);
				 	throw new FederatedRecommenderException("Tried to wipe the index, could not commit");
			 }
		}
		// String dbPediaFile = "mappingbased_properties_en.nt";

		LangNTriples lntriples = null;
		try {
			lntriples = RiotReader.createParserNTriples(new FileInputStream(
					dbPediaFile), null);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "DBPedia Triples File name \""
					+ dbPediaFile
					+ "\" could not be found in current directory", e);
			throw new FederatedRecommenderException(
					"DBPedia Triples File name \"" + dbPediaFile
							+ "\" could not be found in current directory", e);
		}
		int counter = startCounter;
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		while (lntriples.hasNext()) {
			try {
				Triple t = lntriples.next();
				SolrInputDocument doc = createDocument(counter++, t);

				if (doc != null) {
					docs.add(doc);
				}
				if (docs.size() % 1000 == 0) {
					try {
						if (!docs.isEmpty()) {
							server.add(docs);
							server.commit();
						}
					} catch (SolrServerException e) {
						logger.log(Level.SEVERE, "Could not commit to index", e);
						throw new FederatedRecommenderException(
								"Could not commit to DBPediaSolrIndex: "
										+ solrServer, e);
					} catch (IOException e) {
						logger.log(Level.SEVERE, "Could not commit to index", e);
						throw new FederatedRecommenderException(
								"Could not commit to DBPediaSolrIndex: "
										+ solrServer, e);
					}
					docs.clear();
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "error ", e);
			}

		}

		try {
			if (!docs.isEmpty()) {
				server.add(docs);
				server.commit();
			}

		} catch (SolrServerException e) {
			logger.log(Level.SEVERE, "Could not commit to index", e);
			throw new FederatedRecommenderException(
					"Could not commit to DBPediaSolrIndex: " + solrServer, e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not commit to index", e);
			throw new FederatedRecommenderException(
					"Could not commit to DBPediaSolrIndex: " + solrServer, e);
		}

	}
	/**
	 * Creates solrInputDocument out of RDF Triple
	 * @param counter
	 * @param t
	 * @return
	 * @throws Exception
	 */
	private SolrInputDocument createDocument(int counter, Triple t) throws Exception {
		//TODO: Refactoring Use the Beans
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "Doc " + counter);
		doc.addField("parentNode", t.getSubject().toString());
		doc.addField("edge", t.getPredicate().toString());
		doc.addField("relevance", 0);
		if (getIndexableObject(t.getObject(), doc)) {
//			if (doc.getField("childNode") != null) {
			String query =""; 
			if(doc.getField("childNode")!=null)
				query+= doc.getField("childNode").getName() + ":\""+ doc.getField("childNode").getFirstValue() + "\" AND ";
			if(doc.getField("enity_en")!=null)
				query+=  doc.getField("enity_en").getName() + ":\""+ doc.getField("enity_en").getFirstValue() + "\" AND ";
			if(doc.getField("enity_de")!=null)
				query+=doc.getField("enity_de").getName() + ":\""+ doc.getField("enity_de").getFirstValue() + "\" AND ";
			query+=doc.getField("edge").getName() + ":\""+ doc.getField("edge").getFirstValue() + "\" AND ";
			query+=doc.getField("parentNode").getName() + ":\"" +doc.getField("parentNode").getFirstValue() + "\"";
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				QueryResponse queryResponse = null;
				try {
					queryResponse = server.query(solrParams);
				} catch (SolrServerException e) { // Exception is ignored on
													// purpose
					// logger.log(Level.INFO);
					// e.printStackTrace();
				}
				if (queryResponse == null)
					return doc;
				else if (queryResponse.getResults() == null)
					return doc;
				else if (queryResponse.getResults().size() == 0)
					return doc;
				else{
					 List<DbPediaIndexBean> dbPediaIndexBeans =  queryResponse.getBeans(DbPediaIndexBean.class);
					 for (DbPediaIndexBean 	dbPediaDoc :  dbPediaIndexBeans) {
						 	dbPediaDoc.relevance+=1;
					}
		 
						server.addBeans(dbPediaIndexBeans);
						return null;
				}
		}
		return null;

	}
	/**
	 * checks if the object is of the correct type (some rdf connections are filtered)
	 * @param object
	 * @param doc
	 * @return
	 */
	private boolean getIndexableObject(Node object, SolrInputDocument doc) {
		try {

			try {
				String dataTypeUri = object.toString();
				if (dataTypeUri.contains("resource")) {
					doc.addField("childNode", object.toString());
					return true;

				} else if (dataTypeUri.contains("XMLSchema")) {
					return false;
				}
			} catch (Exception e) {
				// logger.log(Level.INFO, "Could not parse node correctly", e);
				return false;
			}

			doc.addField("entity_" + object.getLiteralLanguage(),
					object.getLiteralLexicalForm()); // t.getObject().getLiteralLanguage()
			return true;
		} catch (Exception e) {
			// logger.log(Level.INFO, "Could not parse literal node correctly",
			// e);
			return false;
		}
		// doc.addField("entity_"+languageAbbreviation,object.getLiteralLexicalForm());
		// return true;

	}
/**
 * searches in the dbpediaIndex
 * @param hitsLimit
 * @param query
 * @return
 * @throws FederatedRecommenderException
 */
	public List<DbPediaIndexBean> search(int hitsLimit, String query) throws FederatedRecommenderException {

		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("q", query);
		solrParams.set("rows",hitsLimit);
		solrParams.set("sort","referringParent DESC , referringChild DESC");
		
		QueryResponse queryResponse = null;

		try {
			queryResponse = server.query(solrParams);
		} catch (SolrServerException e) {
			logger.log(Level.SEVERE, "Query " + query
					+ " was not foung in DBPediaIndex", e);
			throw new FederatedRecommenderException("Query "+ query
					+ " was not foung in DBPediaIndex, server is perhabs not running", e);
		}
		return queryResponse.getBeans(DbPediaIndexBean.class);
	}
/**
 * post processing to get a better ranking of the nodes
 * @throws FederatedRecommenderException
 */
	public void createReferenceCounts() throws FederatedRecommenderException {
        ExecutorService threadPool  = Executors.newFixedThreadPool(8);
		Long steps = new Long(10000);
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("q", "*:*  -referringChild:* -referringParent:*");
		solrParams.set("rows",0);
		QueryResponse parentResponse = null;
		try {
			parentResponse = server.query(solrParams);
		} catch (SolrServerException e) {
			logger.log(Level.INFO ,"No Results in the index for query *:* with all docs" ,e);
		}
		long docCount=0;
		if(parentResponse!=null)
			docCount = parentResponse.getResults().getNumFound();
		
		Map<Long, Future<Void>> futures = new HashMap<Long, Future<Void>>();
		for(long i = steps ; i<=docCount ;i+=steps){
			solrParams.set("rows",steps.toString());
			solrParams.set("start",String.valueOf(i-steps));
			final ModifiableSolrParams finalModifiableSolrParams = new ModifiableSolrParams(solrParams);
			 Future<Void> future = threadPool.submit(new Callable<Void>() {
	                @Override
	                public Void call() throws Exception {
	                    createReferenceCountsSegment(finalModifiableSolrParams);
						return null;
	                }
	            });
	            futures.put(i, future);
		
		}
		
		for (Entry<Long, Future<Void>> entry : futures.entrySet()) {
		    
		    try {
                entry.getValue().get();
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to retrieve results from a parter system '" + entry.getKey() + "'", e);
            }
		}
		
	

	}
	/**
	 * post processing to get a better ranking of the nodes counting the references
	 * @param solrParams
	 * @throws FederatedRecommenderException
	 */
	private void createReferenceCountsSegment(ModifiableSolrParams solrParams)
			throws FederatedRecommenderException {
		QueryResponse queryResponse = null;
		try {
			queryResponse = server.query(solrParams);
		} catch (SolrServerException e) {
			logger.log(Level.INFO, "Query *:* could not be processed", e);
		}
		List<DbPediaIndexBean> allDocs= null;
		if(queryResponse!=null)
			allDocs= queryResponse.getBeans(DbPediaIndexBean.class);
		List<DbPediaIndexBean> processedDocs = new ArrayList<DbPediaIndexBean>();
		if(allDocs!=null)
		for (DbPediaIndexBean dbPediaIndexBean : allDocs) {
			
			ModifiableSolrParams parentParam = new ModifiableSolrParams();
			String parentQuery = "childNode:\""+dbPediaIndexBean.parentNode+"\"";
			parentParam.set("q",parentQuery );
			parentParam.set("rows",0);
			
			QueryResponse parentResponse = null;
			try {
				parentResponse = server.query(parentParam);
			} catch (SolrServerException e) {
				logger.log(Level.INFO ,"No Results in the index for query "+parentQuery ,e);
			}
			dbPediaIndexBean.referringParent = 0;
			if(parentResponse!=null)
				if(parentResponse.getResults()!=null)
					dbPediaIndexBean.referringParent = parentResponse.getResults().getNumFound();
				
			ModifiableSolrParams childParam = new ModifiableSolrParams();
			String childQuery = "parentNode:\""+dbPediaIndexBean.childNode+"\"";
			childParam.set("q", childQuery);
			parentParam.set("rows",0);
			QueryResponse childResponse = null;
			try {
				childResponse = server.query(childParam);
			} catch (SolrServerException e) {
				logger.log(Level.INFO,"No Results in the index for query "+childQuery ,e);
			}
			dbPediaIndexBean.referringChild = 0;
			if(childResponse!=null)
				if(childResponse.getResults()!=null)
					dbPediaIndexBean.referringChild = childResponse.getResults().getNumFound();
			
			processedDocs.add(dbPediaIndexBean);
			if(processedDocs.size()%100000==0){
				try {
					server.addBeans(processedDocs);
					server.commit();
				} catch (SolrServerException e) {
					logger.log(Level.SEVERE,"Could cot commit to DBPedia Index",e);
					throw new FederatedRecommenderException("Could cot commit to DBPedia Index", e);
				} catch (IOException e) {
					logger.log(Level.SEVERE,"Could cot commit to DBPedia Index",e);
					throw new FederatedRecommenderException("Could cot commit to DBPedia Index", e);
				}
				processedDocs.clear();
			}
		}
		try {
			server.addBeans(processedDocs);
			server.commit();
		} catch (SolrServerException e) {
			logger.log(Level.SEVERE,"Could cot commit to DBPedia Index",e);
			throw new FederatedRecommenderException("Could cot commit to DBPedia Index", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Could cot commit to DBPedia Index",e);
			throw new FederatedRecommenderException("Could cot commit to DBPedia Index", e);
		}
	}


}
