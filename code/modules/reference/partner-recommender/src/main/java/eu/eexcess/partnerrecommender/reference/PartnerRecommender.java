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
package eu.eexcess.partnerrecommender.reference;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParser;
import org.w3c.dom.Document;

//import com.hp.hpl.jena.ontology.OntModel;
//import com.hp.hpl.jena.query.Query;
//import com.hp.hpl.jena.query.QueryExecution;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.query.QueryFactory;
//import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet;
//import com.hp.hpl.jena.query.ResultSetFormatter;
//import com.hp.hpl.jena.rdf.model.Literal;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.rdf.model.Statement;



import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.api.IEnrichment;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.Enrichment;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerRecommenderApi;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;

/**
 * 
 * 
 * @author rkern@know-center.at
 * @author plopez@know-center.at
 */
public class PartnerRecommender implements PartnerRecommenderApi {

    private PartnerConfiguration partnerConfiguration;
    
    private PartnerConnectorApi partnerConnector;
    
 //   private Client client;
    
    private ITransformer transformer;

    private IEnrichment enricher;
    
    /**
     * Creates a new instance of this class.
     */
    public PartnerRecommender() {
        super();
    }

    @Override
    public void initialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
        	
        	/*
        	 * Read partner configuration file
        	 * 
        	 */
        	
        	URL resource = getClass().getResource("/partner-config.json");
        	mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        	partnerConfiguration = mapper.readValue(new File(resource.getFile()), PartnerConfiguration.class);
                      
        	/*
        	 * Configure the partner connector
        	 */   
            
            partnerConnector = (PartnerConnectorApi)Class.forName(partnerConfiguration.partnerConnectorClass).newInstance();
            
            /*
             * Configure data transformer
             * 
             */
            transformer = (ITransformer)Class.forName(partnerConfiguration.transformerClass).newInstance();       
            transformer.init(partnerConfiguration);
            
            /*
             * Configure data enricher
             * 
             */
            
            enricher = new Enrichment();
            enricher.init(partnerConfiguration);
            
        } catch (Exception e) {
            throw new IOException("Cannot initialize recommender", e);
        }
    }

    
    /**
     * Recommend items from a partner system matching to a given user profile.
     * @param userProfile a secure user profile containing the current users information need
     * @return an XML document in the EEXCESS partner result format
     * @throws IOException
     */
    @Override
    public ResultList recommend(SecureUserProfile userProfile) throws IOException {
        try {
            PartnerdataLogger partnerdataLogger = new PartnerdataLogger(partnerConfiguration);
        	partnerdataLogger.getActLogEntry().start();
            
        	/*
        	 *  Call remote API from partner
        	 */
        	partnerdataLogger.getActLogEntry().queryPartnerAPIStart();

            Document searchResultsNative = partnerConnector.queryPartner(partnerConfiguration, userProfile);
            partnerdataLogger.getActLogEntry().queryPartnerAPIEnd();
        	/*
        	 *  Transform Document in partner format to EEXCESS RDF format
        	 */

            partnerdataLogger.addQuery(userProfile);
            Document searchResultsEexcess = transformer.transform(searchResultsNative, partnerdataLogger);
            
        	/*
        	 *  Enrich results
        	 */
            
            Document enrichedResultsExcess = enricher.enrichResultList(searchResultsEexcess, partnerdataLogger);
            
        	/*
        	 *  Pack into ResultList simple format
        	 */
            
            ResultList recommendations = transformer.toResultList(searchResultsNative, enrichedResultsExcess, partnerdataLogger);
            partnerdataLogger.addResults(recommendations);
        	partnerdataLogger.getActLogEntry().end();
            partnerdataLogger.save();
            return recommendations;
            
        } catch (Exception e) {
            throw new IOException("Partner system is not working correctly ", e);
        }
    }
    

    /** 
     * Returns the EEXCESS user profile for a given user.
     * @return
     * @throws IOException
     */
    @Override
    public Document getUserProfile(String userId) throws IOException {
        return null;
    }



}