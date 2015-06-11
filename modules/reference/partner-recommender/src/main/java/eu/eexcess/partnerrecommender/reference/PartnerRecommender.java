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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SerializationUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.ontology.OntModel;

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
//        	partnerdataLogger.getActLogEntry().start();
        	long startCallPartnerApi = System.currentTimeMillis();
        	// use native untransformed result primarily
        	PartnerConfiguration currentPartnerConfiguration = (PartnerConfiguration) SerializationUtils.clone(partnerConfiguration);
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
        		  nativeResult.setResultStats(new ResultStats(PartnerConfigurationCache.CONFIG.getQueryGenerator(currentPartnerConfiguration.queryGeneratorClass).toQuery(userProfile),endCallPartnerApi-startCallPartnerApi,0,0,0,nativeResult.totalResults));
                
        		return nativeResult;
        	}
            
        	/*
        	 *  Call remote API from partner
        	 */
        
//        	partnerdataLogger.getActLogEntry().queryPartnerAPIStart();
        	Document searchResultsNative = partnerConnector.queryPartner(currentPartnerConfiguration, userProfile, partnerdataLogger);
//            partnerdataLogger.getActLogEntry().queryPartnerAPIEnd();
            long endCallPartnerApi = System.currentTimeMillis();
        	/*
        	 *  Transform Document in partner format to EEXCESS RDF format
        	 */
            long startTransform1 = System.currentTimeMillis();
//            partnerdataLogger.addQuery(userProfile);
            // TODO rrubien: begin impl of PartnerRecommender
            Document searchResultsEexcess = transformer.transform(searchResultsNative, partnerdataLogger);
            long endTransform1 = System.currentTimeMillis();
        	/*
        	 *  Enrich results
        	 */
            /*
            long startEnrich = System.currentTimeMillis();
//        	partnerdataLogger.getActLogEntry().enrichStart();
        	Document enrichedResultsExcess = null;
        	boolean queryHasResults= transformer.hasEEXCESSRDFResponseResults(searchResultsEexcess);
            if (queryHasResults)
            	enrichedResultsExcess = enricher.enrichResultList(searchResultsEexcess, partnerdataLogger);
//        	partnerdataLogger.getActLogEntry().enrichEnd();
            long endEnrich = System.currentTimeMillis();
            */
        	/*
        	 *  Pack into ResultList simple format
        	 */
            long startTransform2 = System.currentTimeMillis();
            ResultList recommendations = new ResultList();
        	boolean queryHasResults= transformer.hasEEXCESSRDFResponseResults(searchResultsEexcess);
            if (queryHasResults)
            	recommendations = transformer.toResultList(searchResultsNative, searchResultsEexcess, partnerdataLogger);
            else 
            	recommendations.results = new LinkedList<Result>();
//            partnerdataLogger.addResults(recommendations);
//        	partnerdataLogger.getActLogEntry().end();
//            partnerdataLogger.save();
            long endTransform2 = System.currentTimeMillis();
            log.log(Level.INFO,"Call Parnter Api:"+(endCallPartnerApi-startCallPartnerApi)+"ms; First Transformation:"+(endTransform1-startTransform1)+"ms; Second Transformation:"+(endTransform2-startTransform2)+"ms");
            //TODO: refactor the next line!
            recommendations.setResultStats(new ResultStats(PartnerConfigurationCache.CONFIG.getQueryGenerator(currentPartnerConfiguration.queryGeneratorClass).toQuery(userProfile),endCallPartnerApi-startCallPartnerApi,endTransform1-startTransform1,endTransform2-startTransform2,0,recommendations.totalResults));
//            PartnerdataTracer.dumpFile(this.getClass(), this.partnerConfiguration, recommendations, "partner-recommender-results", PartnerdataTracer.FILETYPE.XML, partnerdataLogger);
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
//	            long startTransform1 = System.currentTimeMillis();
				//partnerdataLogger.addQuery(userProfile);
				Document detailResultEexcess = transformer.transformDetail(detailResultNative, partnerdataLogger);
				
	            long startEnrich = System.currentTimeMillis();
//	        	partnerdataLogger.getActLogEntry().enrichStart();
	        	Document enrichedDetailResultEexcess = null;
//	        	boolean queryHasResults= transformer.hasEEXCESSRDFResponseResults(searchResultsEexcess);
//	            if (queryHasResults)
	            	enrichedDetailResultEexcess = enricher.enrichResultList(detailResultEexcess, partnerdataLogger);
//	        	partnerdataLogger.getActLogEntry().enrichEnd();
	            long endEnrich = System.currentTimeMillis();

				String rdfXML = XMLTools.getStringFromDocument(enrichedDetailResultEexcess);

//		   		StringReader stream = new StringReader(document.details);
//		   		OntModel model = ModelFactory.createOntologyModel(); 
//				model.read(stream,null);
//				document.detailsRDF = XMLTools.writeModel(model);
				/*
				OntModel modelEnriched = XMLTools.createModel(enrichedDetailResultEexcess);
				document.detailsJSONLD = XMLTools.writeModelJsonLD(modelEnriched);
				
				
//				document.details = transformRDFXMLToResponseDetailJSONLD(rdfXML); 
				document.detailsJSONLDCompacted = transformJSONLDToResponseDetailJSONLDCompact(document.detailsJSONLD);
				document.detailXMLJSON = transformRDFXMLToResponseDetail(rdfXML);
				*/
				document.details = transformRDFXMLToResponseDetail(rdfXML); 
						
				/*
				String contextJSON = "{\"@context\": {\"aggregatedCHO\": {\"@id\": \"http://www.europeana.eu/schemas/edm/aggregatedCHO\", \"@type\": \"@id\"}, \"collectionName\": \"http://www.europeana.eu/schemas/edm/collectionName\", \"dataProvider\": {\"@id\": \"http://www.europeana.eu/schemas/edm/dataProvider\", \"@type\": \"@id\"}, \"imports\": {\"@id\": \"http://www.w3.org/2002/07/owl#imports\", \"@type\": \"@id\"}, \"isShownAt\": {\"@id\": \"http://www.europeana.eu/schemas/edm/isShownAt\", \"@type\": \"@id\"}, \"isShownBy\": {\"@id\": \"http://www.europeana.eu/schemas/edm/isShownBy\", \"@type\": \"@id\"}, \"preview\": {\"@id\": \"http://www.europeana.eu/schemas/edm/preview\", \"@type\": \"@id\"}, \"provider\": {\"@id\": \"http://www.europeana.eu/schemas/edm/provider\", \"@type\": \"@id\"}, \"rights\": {\"@id\": \"http://www.europeana.eu/schemas/edm/rights\", \"@type\": \"@id\"} } }";
				final Object contextJson = JSONObject.fromObject(contextJSON) ;

				final com.github.jsonldjava.core.JsonLdOptions options = new com.github.jsonldjava.core.JsonLdOptions();
				options.format = "application/jsonld";

				Object compact = JsonLdProcessor.compact(new ByteArrayInputStream(XMLTools.writeModelJsonLD(model).getBytes("UTF-8")), contextJson, options);
				
				System.out.println(JSONUtils.valueToString(compact));
				document.details = JSONUtils.valueToString(compact);
				*/
//	            long endTransform1 = System.currentTimeMillis();
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

	private String transformRDFXMLToResponseDetailNew(String rdfXML) {
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
		return json;
	}
	
	private String transformRDFXMLToResponseDetail(String rdfXML) {
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
		JSONObject ret = new JSONObject();
		try {
			JSONObject rdf = new JSONObject(json);
			if (rdf.has("rdfRDF")) {
				JSONObject rdfRDF = (JSONObject)rdf.get("rdfRDF");
				if (rdfRDF.has("eexcessProxy")) {
					JSONArray eexcessProxyArray = (JSONArray)rdfRDF.get("eexcessProxy");
					for (int i = 0; i < eexcessProxyArray.length(); i++) {
						JSONObject eexcessProxyItem = (JSONObject) eexcessProxyArray.get(i);
						String rdfaboutKey = "rdfabout";
						if (eexcessProxyItem.has(rdfaboutKey)) {
							String rdfabout = eexcessProxyItem.getString(rdfaboutKey);
							if (rdfabout.endsWith("/enrichedProxy/")) {
								eexcessProxyItem.remove(rdfaboutKey);
								eexcessProxyItem.remove("oreproxyFor");
								eexcessProxyItem.remove("oreproxyIn");
								ret.put("eexcessProxyEnriched", eexcessProxyItem);
							}
							if (rdfabout.endsWith("/proxy/")) {
								eexcessProxyItem.remove(rdfaboutKey);
								eexcessProxyItem.remove("oreproxyFor");
								eexcessProxyItem.remove("oreproxyIn");
								ret.put("eexcessProxy", eexcessProxyItem);
							}
						}			
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret.toString();
	}

	
	private String transformRDFXMLToResponseDetailJSONLD(String rdfXML) {
		try {
			System.out.println(XML.toJSONObject(rdfXML).toString());
			// Open a valid json(-ld) input file
			InputStream inputStream = new FileInputStream("d:\\input.json");
			// Read the file into an Object (The type of this object will be a List, Map, String, Boolean,
			// Number or null depending on the root object in the file).
			Object jsonObject = JsonUtils.fromInputStream(inputStream);

			// Create a context JSON map containing prefixes and definitions
			Map context = new HashMap();
			// Customise context...
			// Create an instance of JsonLdOptions with the standard JSON-LD options
			JsonLdOptions options = new JsonLdOptions();
// xxx			options.format = 
			// Customise options...
//			options.setCompactArrays(true);
			// Call whichever JSONLD function you want! (e.g. compact)
			Object compact;
				compact = JsonLdProcessor.fromRDF(rdfXML,options);
			// Print out the result (or don't, it's your call!)
			String json = JsonUtils.toPrettyString(compact);
			
			return json;
		} catch (JsonLdError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String transformJSONLDToResponseDetailJSONLDCompact(String jsonLD) {
		try {
//			Object jsonObject = JsonUtils.fromInputStream(inputStream);
			// Create a context JSON map containing prefixes and definitions
			Map context = new HashMap();
			// Customise context...
			// Create an instance of JsonLdOptions with the standard JSON-LD options
			JsonLdOptions options = new JsonLdOptions();
			// Customise options...
			// Call whichever JSONLD function you want! (e.g. compact)
			Object compact = JsonLdProcessor.compact(jsonLD, context, options);
			// Print out the result (or don't, it's your call!)
			System.out.println(JsonUtils.toPrettyString(compact));
			String json = JsonUtils.toPrettyString(compact);
			
			return json;
		} catch (JsonLdError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
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