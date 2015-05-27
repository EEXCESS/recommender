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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.XML;
import org.w3c.dom.Document;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.result.ResultStats;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.api.IEnrichment;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.XMLTools;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
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
    private static PartnerConfiguration partnerConfiguration =PartnerConfigurationCache.CONFIG.getPartnerConfiguration();
    private static PartnerConnectorApi partnerConnector =PartnerConfigurationCache.CONFIG.getPartnerConnector();
    private static ITransformer transformer= PartnerConfigurationCache.CONFIG.getTransformer();
    private static IEnrichment enricher= PartnerConfigurationCache.CONFIG.getEnricher();
    
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
        	PartnerConfiguration currentPartnerConfiguration = partnerConfiguration;
        	if(userProfile.partnerList!=null)
        		if(userProfile.partnerList.size()>0)
        			for (PartnerBadge pC: userProfile.partnerList) {
						if(pC.systemId.equals(partnerConfiguration.systemId)){
							currentPartnerConfiguration.queryGeneratorClass=pC.queryGeneratorClass;
						}	
					}
        	ResultList nativeResult = partnerConnector.queryPartnerNative(currentPartnerConfiguration, userProfile, partnerdataLogger);
        	if (nativeResult != null) {
        		  long endCallPartnerApi = System.currentTimeMillis();
        		  nativeResult.setResultStats(new ResultStats(PartnerConfigurationCache.CONFIG.getQueryGenerator(null).toQuery(userProfile),endCallPartnerApi-startCallPartnerApi,0,0,0,nativeResult.totalResults));
                
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
        	Document enrichedResultsExcess = null;
        	boolean queryHasResults= transformer.hasEEXCESSRDFResponseResults(searchResultsEexcess);
            if (queryHasResults)
            	enrichedResultsExcess = enricher.enrichResultList(searchResultsEexcess, partnerdataLogger);
        	partnerdataLogger.getActLogEntry().enrichEnd();
            long endEnrich = System.currentTimeMillis();
        	/*
        	 *  Pack into ResultList simple format
        	 */
            long startTransform2 = System.currentTimeMillis();
            ResultList recommendations = new ResultList();
            if (queryHasResults)
            	recommendations = transformer.toResultList(searchResultsNative, enrichedResultsExcess, partnerdataLogger);
            else 
            	recommendations.results = new LinkedList<Result>();
            partnerdataLogger.addResults(recommendations);
        	partnerdataLogger.getActLogEntry().end();
            partnerdataLogger.save();
            long endTransform2 = System.currentTimeMillis();
            log.log(Level.INFO,"Call Parnter Api:"+(endCallPartnerApi-startCallPartnerApi)+"ms; First Transformation:"+(endTransform1-startTransform1)+"ms; Enrichment:"+(endEnrich-startEnrich)+"ms; Second Transformation:"+(endTransform2-startTransform2)+"ms");
            //TODO: refactor the next line!
            recommendations.setResultStats(new ResultStats(PartnerConfigurationCache.CONFIG.getQueryGenerator(null).toQuery(userProfile),endCallPartnerApi-startCallPartnerApi,endTransform1-startTransform1,endTransform2-startTransform2,endEnrich-startEnrich,recommendations.totalResults));
            PartnerdataTracer.dumpFile(this.getClass(), this.partnerConfiguration, recommendations, "partner-recommender-results", PartnerdataTracer.FILETYPE.XML, partnerdataLogger);
            return recommendations;
            
            // TODO rrubien: end impl of PartnerRecommender
        } catch (Exception e) {
            throw new IOException("Partner system is not working correctly ", e);
        }
    }
    /**
     * Fetch the details of the documents.
     * @returns list of DocumentBadges including the details
     * @throws IOException
     */
	@Override
	public DocumentBadgeList getDetails(DocumentBadgeList documents)
			throws IOException {
        PartnerdataLogger partnerdataLogger = new PartnerdataLogger(partnerConfiguration);
    	partnerdataLogger.getActLogEntry().start();

    	
    	for (int i = 0; i < documents.documentBadges.size(); i++) {
            try {
	    		DocumentBadge document = documents.documentBadges.get(i);
	    		Document detailResultNative = partnerConnector.queryPartnerDetails(partnerConfiguration, document, partnerdataLogger);
	        	/*
	        	 *  Transform Document in partner format to EEXCESS RDF format
	        	 */
	            long startTransform1 = System.currentTimeMillis();
				//partnerdataLogger.addQuery(userProfile);
				Document detailResultEexcess = transformer.transformDetail(detailResultNative, partnerdataLogger);
				String rdfXML = XMLTools.getStringFromDocument(detailResultEexcess);
				
//		   		StringReader stream = new StringReader(document.details);
//		   		OntModel model = ModelFactory.createOntologyModel(); 
//				model.read(stream,null);
//				document.detailsRDF = XMLTools.writeModel(model);
//				document.detailsJSONLD = XMLTools.writeModelJsonLD(model);
				
				String json = XML.toJSONObject(rdfXML).toString();
				json = json.replaceAll("\"rdf:", "\"rdf");
				json = json.replaceAll("\"rdfs:", "\"rdfs");
				json = json.replaceAll("\"eexcess:", "\"eexcess");
				json = json.replaceAll("\"dc:", "\"dc");
				json = json.replaceAll("\"dcterms:", "\"dcterms");
				json = json.replaceAll("\"edm:", "\"edm");
				json = json.replaceAll("\"ore:", "\"ore");
				json = json.replaceAll("\"owl:", "\"owl");
				json = json.replaceAll("\"foaf:", "\"foaf");
				json = json.replaceAll("\"xsd:", "\"xsd");
				json = json.replaceAll("\"xmlns:", "\"xmlns");
				json = json.replaceAll("\"xml:", "\"xml");
				json = json.replaceAll("\"wgs84:", "\"wgs84");

				document.details = json; 
						
				/*
				String contextJSON = "{\"@context\": {\"aggregatedCHO\": {\"@id\": \"http://www.europeana.eu/schemas/edm/aggregatedCHO\", \"@type\": \"@id\"}, \"collectionName\": \"http://www.europeana.eu/schemas/edm/collectionName\", \"dataProvider\": {\"@id\": \"http://www.europeana.eu/schemas/edm/dataProvider\", \"@type\": \"@id\"}, \"imports\": {\"@id\": \"http://www.w3.org/2002/07/owl#imports\", \"@type\": \"@id\"}, \"isShownAt\": {\"@id\": \"http://www.europeana.eu/schemas/edm/isShownAt\", \"@type\": \"@id\"}, \"isShownBy\": {\"@id\": \"http://www.europeana.eu/schemas/edm/isShownBy\", \"@type\": \"@id\"}, \"preview\": {\"@id\": \"http://www.europeana.eu/schemas/edm/preview\", \"@type\": \"@id\"}, \"provider\": {\"@id\": \"http://www.europeana.eu/schemas/edm/provider\", \"@type\": \"@id\"}, \"rights\": {\"@id\": \"http://www.europeana.eu/schemas/edm/rights\", \"@type\": \"@id\"} } }";
				final Object contextJson = JSONObject.fromObject(contextJSON) ;

				final com.github.jsonldjava.core.JsonLdOptions options = new com.github.jsonldjava.core.JsonLdOptions();
				options.format = "application/jsonld";

				Object compact = JsonLdProcessor.compact(new ByteArrayInputStream(XMLTools.writeModelJsonLD(model).getBytes("UTF-8")), contextJson, options);
				
				System.out.println(JSONUtils.valueToString(compact));
				document.details = JSONUtils.valueToString(compact);
				*/
	            long endTransform1 = System.currentTimeMillis();
			} catch (EEXCESSDataTransformationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
    	
    	partnerdataLogger.getActLogEntry().end();
        partnerdataLogger.save();
/*
		DocumentBadgeList returnList = new DocumentBadgeList();
		returnList.documentBadges = new LinkedList<DocumentBadge>();
		DocumentBadge e =  new DocumentBadge("id", "uri", "provider");
		returnList.documentBadges.add(e );
		DocumentBadge e2 =  new DocumentBadge("id1", "uri1", "provider1");
		returnList.documentBadges.add(e2 );
		return returnList ;
		*/
        PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, documents, "partner-recommender-results-details", PartnerdataTracer.FILETYPE.XML, partnerdataLogger);

        return documents;
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