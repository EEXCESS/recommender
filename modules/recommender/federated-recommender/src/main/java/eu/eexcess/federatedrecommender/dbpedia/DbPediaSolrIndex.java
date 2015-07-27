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

/**
 * Handles the connection and creation of the Solr DBPedia Index
 * 
 * @author hziak
 *
 */
public class DbPediaSolrIndex {
    private static final String COULD_COT_COMMIT_TO_DB_PEDIA_INDEX = "Could cot commit to DBPedia Index";
    private static final String PARENT_NODE = "parentNode";
    private static final String EDGE = "edge";
    private static final String ENITY_DE = "enity_de";
    private static final String ENITY_EN = "enity_en";
    private static final String CHILD_NODE = "childNode";
    private static final Logger LOGGER = Logger.getLogger(DbPediaSolrIndex.class.getName());
    private final String solrServer;
    private final SolrServer server;

    public DbPediaSolrIndex(FederatedRecommenderConfiguration federatedRecommenderConfiguration) {
        this.solrServer = federatedRecommenderConfiguration.getSolrServerUri();
        this.server = new HttpSolrServer(solrServer);

    }

    /**
     * 
     * @param dbPediaFile
     * @param startCounter
     * @param wipeIndex
     * @throws FederatedRecommenderException
     */
    public void createIndex(String dbPediaFile, int startCounter, boolean wipeIndex) throws FederatedRecommenderException {

        if (wipeIndex) {
            try {
                server.deleteByQuery("*:*");
                server.commit();
            } catch (SolrServerException e1) {
                LOGGER.log(Level.SEVERE, "Tried to wipe the index, could not commit", e1);
                throw new FederatedRecommenderException("Tried to wipe the index, could not commit");
            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, "Tried to wipe the index, could not commit", e1);
                throw new FederatedRecommenderException("Tried to wipe the index, could not commit");
            }
        }

        LangNTriples lntriples = null;
        try {
            lntriples = RiotReader.createParserNTriples(new FileInputStream(dbPediaFile), null);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "DBPedia Triples File name \"" + dbPediaFile + "\" could not be found in current directory", e);
            throw new FederatedRecommenderException("DBPedia Triples File name \"" + dbPediaFile + "\" could not be found in current directory", e);
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
                addAndCommitDocs(docs);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "error ", e);
            }

        }

        addAndCommitToSolr(docs);

    }

    private void addAndCommitToSolr(Collection<SolrInputDocument> docs) throws FederatedRecommenderException {
        try {
            if (!docs.isEmpty()) {
                server.add(docs);
                server.commit();
            }

        } catch (SolrServerException e) {
            LOGGER.log(Level.SEVERE, "Could not commit to index", e);
            throw new FederatedRecommenderException("Could not commit to DBPediaSolrIndex: " + solrServer, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not commit to index", e);
            throw new FederatedRecommenderException("Could not commit to DBPediaSolrIndex: " + solrServer, e);
        }
    }

    private void addAndCommitDocs(Collection<SolrInputDocument> docs) throws FederatedRecommenderException {
        if (docs.size() % 1000 == 0) {
            addAndCommitToSolr(docs);
            docs.clear();
        }
    }

    /**
     * Creates solrInputDocument out of RDF Triple
     * 
     * @param counter
     * @param t
     * @return
     * @throws Exception
     */
    private SolrInputDocument createDocument(int counter, Triple t) throws FederatedRecommenderException {
        // TODO: Refactoring Use the Beans
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "Doc " + counter);
        doc.addField(PARENT_NODE, t.getSubject().toString());
        doc.addField(EDGE, t.getPredicate().toString());
        doc.addField("relevance", 0);
        if (getIndexableObject(t.getObject(), doc)) {

            String query = generateSolrQuery(doc);
            ModifiableSolrParams solrParams = new ModifiableSolrParams();
            solrParams.set("q", query);
            QueryResponse queryResponse = null;
            try {
                queryResponse = server.query(solrParams);
            } catch (SolrServerException e) {
                LOGGER.log(Level.INFO, "", e);

            }
            if (queryResponse == null)
                return doc;
            else if (queryResponse.getResults() == null)
                return doc;
            else if (queryResponse.getResults().isEmpty())
                return doc;
            else {
                List<DbPediaIndexBean> dbPediaIndexBeans = queryResponse.getBeans(DbPediaIndexBean.class);
                for (DbPediaIndexBean dbPediaDoc : dbPediaIndexBeans) {
                    dbPediaDoc.relevance += 1;
                }

                try {
                    server.addBeans(dbPediaIndexBeans);
                } catch (SolrServerException | IOException e) {
                    LOGGER.log(Level.SEVERE, "Some exception happened while adding the solr beans", e);
                    throw new FederatedRecommenderException("Some exception happened while adding the solr beans", e);
                }
                return null;
            }
        }
        return null;

    }

    /**
     * generates the solr query for the new node
     * 
     * @param doc
     * @return
     */
    private String generateSolrQuery(SolrInputDocument doc) {
        String query = "";
        if (doc.getField(CHILD_NODE) != null)
            query += doc.getField(CHILD_NODE).getName() + ":\"" + doc.getField(CHILD_NODE).getFirstValue() + "\" AND ";
        if (doc.getField(ENITY_EN) != null)
            query += doc.getField(ENITY_EN).getName() + ":\"" + doc.getField(ENITY_EN).getFirstValue() + "\" AND ";
        if (doc.getField(ENITY_DE) != null)
            query += doc.getField(ENITY_DE).getName() + ":\"" + doc.getField(ENITY_DE).getFirstValue() + "\" AND ";
        query += doc.getField(EDGE).getName() + ":\"" + doc.getField(EDGE).getFirstValue() + "\" AND ";
        query += doc.getField(PARENT_NODE).getName() + ":\"" + doc.getField(PARENT_NODE).getFirstValue() + "\"";
        return query;
    }

    /**
     * checks if the object is of the correct type (some rdf connections are
     * filtered)
     * 
     * @param object
     * @param doc
     * @return
     */
    private boolean getIndexableObject(Node object, SolrInputDocument doc) {
        try {
            try {
                String dataTypeUri = object.toString();
                if (dataTypeUri.contains("resource")) {
                    doc.addField(CHILD_NODE, object.toString());
                    return true;
                } else if (dataTypeUri.contains("XMLSchema")) {
                    return false;
                }
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "Could not parse node correctly", e);
                return false;
            }
            doc.addField("entity_" + object.getLiteralLanguage(), object.getLiteralLexicalForm()); // t.getObject().getLiteralLanguage()
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Could not parse literal node correctly", e);
            return false;

        }

    }

    /**
     * searches in the dbpediaIndex
     * 
     * @param hitsLimit
     * @param query
     * @return
     * @throws FederatedRecommenderException
     */
    public List<DbPediaIndexBean> search(int hitsLimit, String query) throws FederatedRecommenderException {

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("q", query);
        solrParams.set("rows", hitsLimit);
        solrParams.set("sort", "referringParent DESC");

        QueryResponse queryResponse = null;

        try {
            queryResponse = server.query(solrParams);
        } catch (SolrServerException e) {
            LOGGER.log(Level.SEVERE, "Query " + query + " was not found in DBPediaIndex", e);
            throw new FederatedRecommenderException("Query " + query + " was not foung in DBPediaIndex, server is perhabs not running", e);
        }
        return queryResponse.getBeans(DbPediaIndexBean.class);
    }

    /**
     * post processing to get a better ranking of the nodes
     * 
     * @throws FederatedRecommenderException
     */
    public void createReferenceCounts() throws FederatedRecommenderException {
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        Long steps = new Long(10000);
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("q", "*:*  -referringChild:* -referringParent:*");
        solrParams.set("rows", 0);
        QueryResponse parentResponse = null;
        try {
            parentResponse = server.query(solrParams);
        } catch (SolrServerException e) {
            LOGGER.log(Level.INFO, "No Results in the index for query *:* with all docs", e);
        }
        long docCount = 0;
        if (parentResponse != null)
            docCount = parentResponse.getResults().getNumFound();

        Map<Long, Future<Void>> futures = new HashMap<Long, Future<Void>>();
        for (long i = steps; i <= docCount; i += steps) {
            solrParams.set("rows", steps.toString());
            solrParams.set("start", String.valueOf(i - steps));
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
                LOGGER.log(Level.SEVERE, "Failed to retrieve results from a parter system '" + entry.getKey() + "'", e);
            }
        }

    }

    /**
     * post processing to get a better ranking of the nodes counting the
     * references
     * 
     * @param solrParams
     * @throws FederatedRecommenderException
     */
    private void createReferenceCountsSegment(ModifiableSolrParams solrParams) throws FederatedRecommenderException {
        QueryResponse queryResponse = null;
        try {
            queryResponse = server.query(solrParams);
        } catch (SolrServerException e) {
            LOGGER.log(Level.INFO, "Query *:* could not be processed", e);
        }
        List<DbPediaIndexBean> allDocs = null;
        if (queryResponse != null)
            allDocs = queryResponse.getBeans(DbPediaIndexBean.class);
        List<DbPediaIndexBean> processedDocs = new ArrayList<DbPediaIndexBean>();
        if (allDocs != null)
            for (DbPediaIndexBean dbPediaIndexBean : allDocs) {

                ModifiableSolrParams parentParam = new ModifiableSolrParams();
                String parentQuery = "childNode:\"" + dbPediaIndexBean.parentNode + "\"";
                parentParam.set("q", parentQuery);
                parentParam.set("rows", 0);

                QueryResponse parentResponse = null;
                try {
                    parentResponse = server.query(parentParam);
                } catch (SolrServerException e) {
                    LOGGER.log(Level.INFO, "No Results in the index for query " + parentQuery, e);
                }
                dbPediaIndexBean.referringParent = 0;
                if (parentResponse != null && parentResponse.getResults() != null)
                    dbPediaIndexBean.referringParent = parentResponse.getResults().getNumFound();

                ModifiableSolrParams childParam = new ModifiableSolrParams();
                String childQuery = "parentNode:\"" + dbPediaIndexBean.childNode + "\"";
                childParam.set("q", childQuery);
                parentParam.set("rows", 0);
                QueryResponse childResponse = null;
                try {
                    childResponse = server.query(childParam);
                } catch (SolrServerException e) {
                    LOGGER.log(Level.INFO, "No Results in the index for query " + childQuery, e);
                }
                dbPediaIndexBean.referringChild = 0;
                if (childResponse != null && childResponse.getResults() != null)
                    dbPediaIndexBean.referringChild = childResponse.getResults().getNumFound();

                processedDocs.add(dbPediaIndexBean);
                commitBeans(processedDocs);
            }
        try {
            server.addBeans(processedDocs);
            server.commit();
        } catch (SolrServerException e) {
            LOGGER.log(Level.SEVERE, COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
            throw new FederatedRecommenderException(COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
            throw new FederatedRecommenderException(COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
        }
    }

    private void commitBeans(List<DbPediaIndexBean> processedDocs) throws FederatedRecommenderException {
        if (processedDocs.size() % 100000 == 0) {
            try {
                server.addBeans(processedDocs);
                server.commit();
            } catch (SolrServerException e) {
                LOGGER.log(Level.SEVERE, COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
                throw new FederatedRecommenderException(COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
                throw new FederatedRecommenderException(COULD_COT_COMMIT_TO_DB_PEDIA_INDEX, e);
            }
            processedDocs.clear();
        }
    }

}
