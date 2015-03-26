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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import eu.eexcess.dataformats.result.ResultStats;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.api.IEnrichment;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationEnum;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.api.PartnerRecommenderApi;

/**
 * 
 * 
 * @author rkern@know-center.at
 * @author plopez@know-center.at
 */
public class PartnerRecommender implements PartnerRecommenderApi {
	Logger log = Logger.getLogger(PartnerRecommender.class.getName());
    private static PartnerConfiguration partnerConfiguration =PartnerConfigurationEnum.CONFIG.getPartnerConfiguration();
    private static PartnerConnectorApi partnerConnector =PartnerConfigurationEnum.CONFIG.getPartnerConnector();
    private static ITransformer transformer= PartnerConfigurationEnum.CONFIG.getTransformer();
    private static IEnrichment enricher= PartnerConfigurationEnum.CONFIG.getEnricher();
    
    /**
     * Creates a new instance of this class.
     */
    public PartnerRecommender() {
        super();
    }

    @Override
    public void initialize() throws IOException {
       
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
        	long startCallPartnerApi = System.currentTimeMillis();
        	// use native untransformed result primarily
        	ResultList nativeResult = partnerConnector.queryPartnerNative(partnerConfiguration, userProfile, partnerdataLogger);
        	if (nativeResult != null) {
        		  long endCallPartnerApi = System.currentTimeMillis();
        		  nativeResult.setResultStats(new ResultStats(PartnerConfigurationEnum.CONFIG.getQueryGenerator().toQuery(userProfile),endCallPartnerApi-startCallPartnerApi,0,0,0,nativeResult.totalResults));
                
        		return nativeResult;
        	}
            
        	/*
        	 *  Call remote API from partner
        	 */
        
        	partnerdataLogger.getActLogEntry().queryPartnerAPIStart();
        	
            Document searchResultsNative = partnerConnector.queryPartner(partnerConfiguration, userProfile, partnerdataLogger);
            partnerdataLogger.getActLogEntry().queryPartnerAPIEnd();
            long endCallPartnerApi = System.currentTimeMillis();
        	/*
        	 *  Transform Document in partner format to EEXCESS RDF format
        	 */
            long startTransform1 = System.currentTimeMillis();
            partnerdataLogger.addQuery(userProfile);
            // TODO rrubien: begin impl of PartnerRecommender
            Document searchResultsEexcess = transformer.transform(searchResultsNative, partnerdataLogger);
            long endTransform1 = System.currentTimeMillis();
        	/*
        	 *  Enrich results
        	 */
            long startEnrich = System.currentTimeMillis();
        	partnerdataLogger.getActLogEntry().enrichStart();
            Document enrichedResultsExcess = enricher.enrichResultList(searchResultsEexcess, partnerdataLogger);
        	partnerdataLogger.getActLogEntry().enrichEnd();
            long endEnrich = System.currentTimeMillis();
        	/*
        	 *  Pack into ResultList simple format
        	 */
            long startTransform2 = System.currentTimeMillis();
            ResultList recommendations = transformer.toResultList(searchResultsNative, enrichedResultsExcess, partnerdataLogger);
            partnerdataLogger.addResults(recommendations);
        	partnerdataLogger.getActLogEntry().end();
            partnerdataLogger.save();
            long endTransform2 = System.currentTimeMillis();
            log.log(Level.INFO,"Call Parnter Api:"+(endCallPartnerApi-startCallPartnerApi)+"ms; First Transformation:"+(endTransform1-startTransform1)+"ms; Enrichment:"+(endEnrich-startEnrich)+"ms; Second Transformation:"+(endTransform2-startTransform2)+"ms");
            recommendations.setResultStats(new ResultStats(PartnerConfigurationEnum.CONFIG.getQueryGenerator().toQuery(userProfile),endCallPartnerApi-startCallPartnerApi,endTransform1-startTransform1,endTransform2-startTransform2,endEnrich-startEnrich,recommendations.totalResults));
            PartnerdataTracer.dumpFile(this.getClass(), this.partnerConfiguration, recommendations, "partner-recommender-results", PartnerdataTracer.FILETYPE.XML, partnerdataLogger);
            return recommendations;
            
            // TODO rrubien: end impl of PartnerRecommender
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