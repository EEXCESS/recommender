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

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.*;
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
import eu.eexcess.partnerrecommender.api.SpecialFieldsQueryGeneratorApi;
import org.apache.commons.lang.SerializationUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author rkern@know-center.at
 * @author plopez@know-center.at
 * @author thomas.orgel@joanneum.at
 */
public class PartnerRecommender implements PartnerRecommenderApi {
    private static final Logger LOGGER = Logger.getLogger(PartnerRecommender.class.getName());
    private PartnerConfiguration partnerConfiguration = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();
    private PartnerConnectorApi partnerConnector = PartnerConfigurationCache.CONFIG.getPartnerConnector();
    private ITransformer transformer = PartnerConfigurationCache.CONFIG.getTransformer();
//    private static IEnrichment enricher = PartnerConfigurationCache.CONFIG.getEnricher();

    private ExecutorService threadPoolDetailCalls;


    /**
     * Creates a new instance of this class.
     */
    public PartnerRecommender() {
        super();
        threadPoolDetailCalls = Executors.newFixedThreadPool(20);
        this.partnerConfiguration = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();
        this.partnerConnector = PartnerConfigurationCache.CONFIG.getPartnerConnector();
        this.transformer = PartnerConfigurationCache.CONFIG.getTransformer();
        //		enricher = PartnerConfigurationCache.CONFIG.getEnricher();
    }

    public PartnerRecommender( PartnerConfiguration partnerConfiguration, PartnerConnectorApi partnerConnector, ITransformer transformer ) {
        super();
        threadPoolDetailCalls = Executors.newFixedThreadPool(20);

        this.partnerConfiguration = partnerConfiguration;
        this.partnerConnector = partnerConnector;
        this.transformer = transformer;
        //		enricher = PartnerConfigurationCache.CONFIG.getEnricher();
    }


    @Override
    public void initialize() throws IOException {
        /**
         * Nothing to do here
         */
    }

    /**
     * Recommend items from a partner system matching to a given user profile.
     * 
     * @param userProfile
     *            a secure user profile containing the current users information
     *            need
     * @return an XML document in the EEXCESS partner result format
     * @throws IOException
     */
    @Override
    public ResultList recommend(SecureUserProfile userProfile) throws IOException {
        try {
            PartnerdataLogger partnerdataLogger = new PartnerdataLogger(partnerConfiguration);
            long startCallPartnerApi = System.currentTimeMillis();
            // use native untransformed result primarily
            /*
             * Call remote API from partner
             */
            PartnerConfiguration currentPartnerConfiguration = (PartnerConfiguration) SerializationUtils.clone(partnerConfiguration);
            if (userProfile.getPartnerList() != null && !userProfile.getPartnerList().isEmpty())
                for (PartnerBadge pC : userProfile.getPartnerList()) {
                    if (pC.getSystemId().equals(partnerConfiguration.getSystemId())) {
                        currentPartnerConfiguration.setQueryGeneratorClass(pC.getQueryGeneratorClass());
                    }
                }
            String finalFormulatedQuery = PartnerConfigurationCache.CONFIG.getQueryGenerator(currentPartnerConfiguration.getQueryGeneratorClass()).toQuery(userProfile);
            if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSpecialFieldQueryGeneratorClass() != null) {
                SpecialFieldsQueryGeneratorApi specialFieldsGenerator = (SpecialFieldsQueryGeneratorApi) Class
                        .forName(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSpecialFieldQueryGeneratorClass()).newInstance();
                finalFormulatedQuery += specialFieldsGenerator.toQuery(userProfile);
            }
            ResultList nativeResultList = getNativePartnerResult(userProfile, partnerdataLogger, startCallPartnerApi, currentPartnerConfiguration, finalFormulatedQuery);
            if (nativeResultList != null)
                return nativeResultList;
            return getPartnerResult(userProfile, partnerdataLogger, startCallPartnerApi, currentPartnerConfiguration, finalFormulatedQuery);

        } catch (Exception e) {
            throw new IOException("Partner system is not working correctly ", e);
        }
    }

    /**
     * gets the transformed partner result
     * 
     * @param userProfile
     * @param partnerdataLogger
     * @param startCallPartnerApi
     * @param currentPartnerConfiguration
     * @param finalFormulatedQuery
     * @return
     * @throws IOException
     * @throws EEXCESSDataTransformationException
     */
    private ResultList getPartnerResult(SecureUserProfile userProfile, PartnerdataLogger partnerdataLogger, long startCallPartnerApi,
            PartnerConfiguration currentPartnerConfiguration, String finalFormulatedQuery) throws IOException, EEXCESSDataTransformationException {
        Document searchResultsNative = partnerConnector.queryPartner(currentPartnerConfiguration, userProfile, partnerdataLogger);
        // partnerdataLogger.getActLogEntry().queryPartnerAPIEnd();
        long endCallPartnerApi = System.currentTimeMillis();
        /*
         * Transform Document in partner format to EEXCESS RDF format
         */
        long startTransform1 = System.currentTimeMillis();
        Document searchResultsEexcess = transformer.transform(searchResultsNative, partnerdataLogger);
        long endTransform1 = System.currentTimeMillis();
        /*
         * Pack into ResultList simple format
         */
        long startTransform2 = System.currentTimeMillis();
        ResultList recommendations = new ResultList();
        boolean queryHasResults = transformer.hasEEXCESSRDFResponseResults(searchResultsEexcess);
        if (queryHasResults)
            recommendations = transformer.toResultList(searchResultsNative, searchResultsEexcess, partnerdataLogger);
        else
            recommendations.results = new LinkedList<Result>();

        long endTransform2 = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "Call Parnter Api:" + (endCallPartnerApi - startCallPartnerApi) + "ms; First Transformation:" + (endTransform1 - startTransform1)
                + "ms; Second Transformation:" + (endTransform2 - startTransform2) + "ms");
        finalFormulatedQuery = URLDecoder.decode(finalFormulatedQuery, "UTF-8");
        recommendations.setResultStats(new ResultStats(finalFormulatedQuery, endCallPartnerApi - startCallPartnerApi, endTransform1 - startTransform1, endTransform2
                - startTransform2, 0, recommendations.totalResults));
        recommendations = addQueryToResultDocuments(recommendations, finalFormulatedQuery);
        PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, recommendations, "partner-recommender-results", PartnerdataTracer.FILETYPE.XML,
                partnerdataLogger);
        return recommendations;
    }

    /**
     * calls the native implementation of the partner recommender
     * 
     * @param userProfile
     * @param partnerdataLogger
     * @param startCallPartnerApi
     * @param currentPartnerConfiguration
     * @param finalFormulatedQuery
     * @return
     * @throws IOException
     */
    private ResultList getNativePartnerResult(SecureUserProfile userProfile, PartnerdataLogger partnerdataLogger, long startCallPartnerApi,
            PartnerConfiguration currentPartnerConfiguration, String finalFormulatedQuery) throws IOException {
        ResultList nativeResult = partnerConnector.queryPartnerNative(currentPartnerConfiguration, userProfile, partnerdataLogger);
        if (nativeResult != null) {
            long endCallPartnerApi = System.currentTimeMillis();
            nativeResult.setResultStats(new ResultStats(finalFormulatedQuery, endCallPartnerApi - startCallPartnerApi, 0, 0, 0, nativeResult.totalResults));
            nativeResult = addQueryToResultDocuments(nativeResult, finalFormulatedQuery);
            return nativeResult;
        }
        return nativeResult;
    }

    /**
     * Adds the generating query to each document in the resultlist
     * 
     * @param resultList
     * @param finalFormulatedQuery
     * @return
     */
    private ResultList addQueryToResultDocuments(ResultList resultList, String finalFormulatedQuery) {
        for (Result resultDocument : resultList.results) {
            resultDocument.generatingQuery = finalFormulatedQuery;
        }
        return resultList;
    }

    /**
     * Fetch the details of the documents.
     * 
     * @returns list of DocumentBadges including the details
     * @throws IOException
     */
    @Override
    public DocumentBadgeList getDetails(DocumentBadgeList documents) throws IOException {
        PartnerdataLogger partnerdataLogger = new PartnerdataLogger(partnerConfiguration);
        partnerdataLogger.getActLogEntry().start();
        DocumentBadgeList returnList = new DocumentBadgeList();
        ArrayList<Future<DocumentBadge>> futures = new ArrayList<Future<DocumentBadge>>();
        for (int i = 0; i < documents.documentBadges.size(); i++) {
                DocumentBadge currentDoc = documents.documentBadges.get(i);
                
                Future<DocumentBadge> future = threadPoolDetailCalls.submit(new Callable<DocumentBadge>() {
                    @Override
                    public DocumentBadge call() throws Exception {
                        try {
                            long startTime = System.currentTimeMillis();

	                        Document detailResultNative = partnerConnector.queryPartnerDetails(partnerConfiguration, currentDoc, partnerdataLogger);
	                        /*
	                         * Transform Document in partner format to EEXCESS RDF format
	                         */
	                        Document detailResultEexcess = transformer.transformDetail(detailResultNative, partnerdataLogger);
	
	                        Document enrichedDetailResultEexcess = null;
	                        IEnrichment enricher = PartnerConfigurationCache.CONFIG.getEnricher();

	                        enrichedDetailResultEexcess = enricher.enrichResultList(detailResultEexcess, partnerdataLogger);
	                        String rdfXML = XMLTools.getStringFromDocument(enrichedDetailResultEexcess);
	
//	                        PartnerdataTracer.dumpFile(this.getClass(), PartnerRecommender.partnerConfiguration, rdfXML, "partner-recommender-results-details-before-reduce-" + i,
//	                                PartnerdataTracer.FILETYPE.XML, partnerdataLogger);
	                        currentDoc.details = transformRDFXMLToResponseDetail(rdfXML, partnerdataLogger, 0);
	                        long endTime = System.currentTimeMillis();
	                        long respTime = endTime - startTime;
	                        LOGGER.log(Level.FINER,currentDoc.id + " finished:"+respTime );
//	                        PartnerdataTracer.dumpFile(this.getClass(), PartnerRecommender.partnerConfiguration, currentDoc.details, "partner-recommender-results-details-" + i,
//	                                PartnerdataTracer.FILETYPE.JSON, partnerdataLogger);
	                    } catch (EEXCESSDataTransformationException e) {
	                        LOGGER.log(Level.INFO, "", e);
	                    }
                        return currentDoc;
                    }

                });
                futures.add(future);

        }
        for (Future<DocumentBadge> actFuture : futures) {
        	try {
				returnList.documentBadges.add(actFuture.get(3000, TimeUnit.MILLISECONDS));
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "InterruptedException:", e);
			} catch (ExecutionException e) {
				LOGGER.log(Level.WARNING, "ExecutionException:", e);
			} catch (TimeoutException e) {
				LOGGER.log(Level.WARNING, "TimeoutException during getDetails call");
			}
		}
        partnerdataLogger.getActLogEntry().end();
        partnerdataLogger.save();

        PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, returnList, "partner-recommender-results-details", PartnerdataTracer.FILETYPE.XML, partnerdataLogger);

        return returnList;
    }
    
    /*
    public DocumentBadgeList getDetailsSingleThreaded(DocumentBadgeList documents) throws IOException {
        PartnerdataLogger partnerdataLogger = new PartnerdataLogger(partnerConfiguration);
        partnerdataLogger.getActLogEntry().start();

        for (int i = 0; i < documents.documentBadges.size(); i++) {
            try {
                DocumentBadge document = documents.documentBadges.get(i);
                Document detailResultNative = partnerConnector.queryPartnerDetails(partnerConfiguration, document, partnerdataLogger);
                
                //Transform Document in partner format to EEXCESS RDF format
                 
                Document detailResultEexcess = transformer.transformDetail(detailResultNative, partnerdataLogger);

                Document enrichedDetailResultEexcess = null;
                enrichedDetailResultEexcess = enricher.enrichResultList(detailResultEexcess, partnerdataLogger);

                String rdfXML = XMLTools.getStringFromDocument(enrichedDetailResultEexcess);

                PartnerdataTracer.dumpFile(this.getClass(), PartnerRecommender.partnerConfiguration, rdfXML, "partner-recommender-results-details-before-reduce-" + i,
                        PartnerdataTracer.FILETYPE.XML, partnerdataLogger);
                document.details = transformRDFXMLToResponseDetail(rdfXML, partnerdataLogger, i);
                PartnerdataTracer.dumpFile(this.getClass(), PartnerRecommender.partnerConfiguration, document.details, "partner-recommender-results-details-" + i,
                        PartnerdataTracer.FILETYPE.JSON, partnerdataLogger);

            } catch (EEXCESSDataTransformationException e) {
                LOGGER.log(Level.INFO, "", e);
            }

        }

        partnerdataLogger.getActLogEntry().end();
        partnerdataLogger.save();

        PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, documents, "partner-recommender-results-details", PartnerdataTracer.FILETYPE.XML, partnerdataLogger);

        return documents;
    }
*/
    private String transformRDFXMLToResponseDetail(String rdfXML, PartnerdataLogger partnerdataLogger, int index) {
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
//        PartnerdataTracer.dumpFile(this.getClass(), PartnerRecommender.partnerConfiguration, json, "partner-recommender-results-details-before-reduce-" + index,
//                PartnerdataTracer.FILETYPE.JSON, partnerdataLogger);

        JSONObject ret = new JSONObject();
        try {
            JSONObject rdf = new JSONObject(json);
            if (rdf.has("rdfRDF")) {
                JSONObject rdfRDF = (JSONObject) rdf.get("rdfRDF");
                String eexcessProxyKey = "eexcessProxy";
                if (rdfRDF.has(eexcessProxyKey)) {
                    if (rdfRDF.get(eexcessProxyKey) instanceof JSONArray) {
                        JSONArray eexcessProxyArray = (JSONArray) rdfRDF.get("eexcessProxy");
                        for (int i = 0; i < eexcessProxyArray.length(); i++) {
                            JSONObject eexcessProxyItem = (JSONObject) eexcessProxyArray.get(i);
                            processEEXCESSProxyItemJSON(ret, eexcessProxyItem,rdfRDF);
                        }
                    } else {
                        if (rdfRDF.get(eexcessProxyKey) instanceof JSONObject) {
                            JSONObject eexcessProxyItem = (JSONObject) rdfRDF.get("eexcessProxy");
                            processEEXCESSProxyItemJSON(ret, eexcessProxyItem,rdfRDF);
                        }

                    }
                }
            }
        } catch (JSONException e) {
            LOGGER.log(Level.INFO, "", e);
        }
        return ret.toString();
    }

    private void processEEXCESSProxyItemJSON(JSONObject ret, JSONObject eexcessProxyItem, JSONObject rdfRDF) throws JSONException {
        String rdfaboutKey = "rdfabout";
        if (eexcessProxyItem.has(rdfaboutKey)) {
            String rdfabout = eexcessProxyItem.getString(rdfaboutKey);
            String oreProxyInKey = "oreproxyIn";
            String oreAggregationKey = "oreAggregation";
//            if (eexcessProxyItem.has(oreProxyInKey)) {
//                JSONObject oreProxyIn = (JSONObject) eexcessProxyItem.get(oreProxyInKey);
                if (rdfRDF.has(oreAggregationKey)) {
                    JSONObject oreAggregation = (JSONObject) rdfRDF.get(oreAggregationKey);
                    String edmCollectionNameKey = "edmcollectionName";
                    if (oreAggregation.has(edmCollectionNameKey)) {
                        eexcessProxyItem.put(edmCollectionNameKey, oreAggregation.get(edmCollectionNameKey));
                    }
                    String edmpreviewKey = "edmpreview";
                    if (oreAggregation.has(edmpreviewKey)) {
                        eexcessProxyItem.put(edmpreviewKey, oreAggregation.get(edmpreviewKey));
                    }
                }
//            }
            if (rdfabout.endsWith("/enrichedProxy/")) {
                eexcessProxyItem.remove(rdfaboutKey);
                eexcessProxyItem.remove("oreproxyFor");
                eexcessProxyItem.remove("xmlnseexcess");
                eexcessProxyItem.remove(oreProxyInKey);
                eexcessProxyItem = simplify(eexcessProxyItem);
                ret.put("eexcessProxyEnriched", eexcessProxyItem);
            }
            if (rdfabout.endsWith("/proxy/")) {
                eexcessProxyItem.remove(rdfaboutKey);
                eexcessProxyItem.remove("oreproxyFor");
                eexcessProxyItem.remove("xmlnseexcess");
                eexcessProxyItem.remove(oreProxyInKey);
                eexcessProxyItem = simplify(eexcessProxyItem);
                ret.put("eexcessProxy", eexcessProxyItem);
            }
        }
    }

    private JSONObject simplify(JSONObject eexcessProxyItem) {
        Iterator<?> keys = eexcessProxyItem.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                if (eexcessProxyItem.get(key) instanceof JSONObject) {
                    JSONObject myChild = (JSONObject) eexcessProxyItem.get(key);
                    if (myChild.has("content")) {
                        String content = myChild.getString("content");
                        eexcessProxyItem.put(key, content);
                    }
                    if (myChild.has("rdfresource")) {
                        String content = myChild.getString("rdfresource");
                        eexcessProxyItem.put(key, content);
                    }
                    Iterator<?> keysChild = myChild.keys();

                    ArrayList<String> keysToRemove = new ArrayList<String>();
                    while (keysChild.hasNext()) {
                        String keyChild = (String) keysChild.next();
                        if (keyChild.toLowerCase().startsWith("xmlns"))
                            keysToRemove.add(keyChild);
                    }
                    for (String removeKey : keysToRemove) {
                        myChild.remove(removeKey);
                    }
                }
                if (eexcessProxyItem.get(key) instanceof JSONArray) {
                    JSONArray myNewChilds = new JSONArray();
                    JSONArray myChilds = (JSONArray) eexcessProxyItem.get(key);
                    for (int i = 0; i < myChilds.length(); i++) {
                        if (myChilds.get(i) instanceof JSONObject) {
                            JSONObject myChild = (JSONObject) myChilds.get(i);
                            if (myChild.has("content")) {
                                String content = myChild.getString("content");
                                myNewChilds.put(content);
                            }
                            if (myChild.has("rdfresource")) {
                                String rdfresource = myChild.getString("rdfresource");
                                myNewChilds.put(rdfresource);
                            }
                            Iterator<?> keysChild = myChild.keys();

                            ArrayList<String> keysToRemove = new ArrayList<String>();
                            while (keysChild.hasNext()) {
                                String keyChild = (String) keysChild.next();
                                if (keyChild.toLowerCase().startsWith("xmlns"))
                                    keysToRemove.add(keyChild);
                                if (keyChild.toLowerCase().equalsIgnoreCase("rdfDescription")) {
                                    myNewChilds.put(myChild.get("rdfDescription"));
                                }

                            }
                            for (String removeKey : keysToRemove) {
                                myChild.remove(removeKey);
                            }
                            myNewChilds.put(myChild);
                        }

                    }
                    eexcessProxyItem.put(key, myNewChilds);
                }
            } catch (JSONException e) {
                LOGGER.log(Level.INFO, "", e);
            }
        }
        return eexcessProxyItem;
    }

    /**
     * Returns the EEXCESS user profile for a given user.
     * 
     * @return
     * @throws IOException
     */
    @Override
    public Document getUserProfile(String userId) throws IOException {
        return null;
    }

}
