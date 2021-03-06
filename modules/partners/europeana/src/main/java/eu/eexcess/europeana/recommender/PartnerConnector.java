/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.europeana.recommender;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.europeana.recommender.dataformat.EuropeanaDoc;
import eu.eexcess.europeana.recommender.dataformat.EuropeanaResponse;
import eu.eexcess.europeana.recommender.dataformat.details.EuropeanaDocDetail;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;

/**
 * Query generator for Europeana.
 * 
 * @author plopez@know-center.at
 */

public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {
    private static final Logger LOGGER = Logger.getLogger(PartnerConnector.class.getName());

    private boolean makeDetailRequests = false;

    public PartnerConnector() {

    }

    @Override
    public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException {

        // Configure
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // ClientConfig config = new DefaultClientConfig();
        // config.getClasses().add(JacksonJsonProvider.class);
        //
        final Client client = new Client(PartnerConfigurationCache.CONFIG.getClientJacksonJson());
        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.getQueryGeneratorClass());
        String query = getQueryGenerator().toQuery(userProfile);
        long start = System.currentTimeMillis();

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("query", URLEncoder.encode(query, "UTF-8"));
        valuesMap.put("apiKey", partnerConfiguration.getApiKey()); // add API
                                                                   // key
        // searchEndpoint:
        // "http://www.europeana.eu/api/v2/search.json?wskey=${apiKey}&query=${query}"
        Integer numResultsRequest = 10;
        if (userProfile.getNumResults() != null && userProfile.getNumResults() != 0)
            numResultsRequest = userProfile.getNumResults();
        valuesMap.put("numResults", numResultsRequest.toString());
        String searchRequest = StrSubstitutor.replace(partnerConfiguration.getSearchEndpoint(), valuesMap);
        LOGGER.log(Level.INFO, "SEARCHREQUEST: " + searchRequest);
        WebResource service = client.resource(searchRequest);
        ObjectMapper mapper = new ObjectMapper();
        Builder builder = service.accept(MediaType.APPLICATION_JSON);
        EuropeanaResponse response = builder.get(EuropeanaResponse.class);
        if (response.items.size() > numResultsRequest)
            response.items = response.items.subList(0, numResultsRequest);
        PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, response.toString(), "service-response", PartnerdataTracer.FILETYPE.JSON, logger);
        client.destroy();
        if (makeDetailRequests) {
            HashMap<EuropeanaDoc, Future<Void>> futures = new HashMap<EuropeanaDoc, Future<Void>>();
            final HashMap<EuropeanaDoc, EuropeanaDocDetail> docDetails = new HashMap<EuropeanaDoc, EuropeanaDocDetail>();
            final PartnerConfiguration partnerConfigLocal = partnerConfiguration;
            final String eexcessRequestId = logger.getActLogEntry().getRequestId();
            for (int i = 0; i < response.items.size(); i++) {
                final EuropeanaDoc item = response.items.get(i);

                Future<Void> future = threadPool.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        EuropeanaDocDetail details = null;
                        try {
                            details = fetchDocumentDetails(item.id, partnerConfigLocal, eexcessRequestId);
                        } catch (EEXCESSDataTransformationException e) {
                            LOGGER.log(Level.INFO, "Error getting item with id" + item.id, e);
                            return null;
                        }
                        docDetails.put(item, details);
                        return null;
                    }
                });
                futures.put(item, future);
            }

            for (EuropeanaDoc doc : futures.keySet()) {
                try {
                    futures.get(doc).get(start + 15 * 500 - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    LOGGER.log(Level.WARNING, "Detail thread for " + doc.id + " did not responses in time", e);
                }

                // item.edmConcept.addAll(details.concepts);
                // item.edmConcept = details.concepts;
                // item.edmCountry = details.edmCountry;
                // item.edmPlace = details.places;
            }
        }

        // long end = System.currentTimeMillis();

        // long startXML = System.currentTimeMillis();

        Document newResponse = null;
        try {
            newResponse = this.transformJSON2XML(mapper.writeValueAsString(response));
        } catch (EEXCESSDataTransformationException e) {
            LOGGER.log(Level.INFO, "Error Transforming Json to xml", e);
        }
        // long endXML = System.currentTimeMillis();
        // LOGGER.log(Level.INFO, "millis " + (endXML - startXML) + "   " + (end
        // - start));

        threadPool.shutdownNow();

        return newResponse;

    }

    protected EuropeanaDocDetail fetchDocumentDetails(String objectId, PartnerConfiguration partnerConfiguration, String eexcessRequestId)
            throws EEXCESSDataTransformationException {
        try {
            Client client = new Client(PartnerConfigurationCache.CONFIG.getClientJacksonJson());
            Map<String, String> valuesMap = new HashMap<String, String>();
            valuesMap.put("objectId", objectId);
            valuesMap.put("apiKey", partnerConfiguration.getApiKey());
            String detailEndpoint = "http://europeana.eu/api/v2/record/${objectId}.json?wskey=${apiKey}";
            String detailRequest = StrSubstitutor.replace(detailEndpoint, valuesMap);
            WebResource service = client.resource(detailRequest);
            Builder builder = service.accept(MediaType.APPLICATION_JSON);
            EuropeanaDocDetail jsonResponse = builder.get(EuropeanaDocDetail.class);
            PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, jsonResponse.toString(), "detail-response", PartnerdataTracer.FILETYPE.JSON, eexcessRequestId);
            client.destroy();
            return jsonResponse;
        } catch (Exception e) {
            throw new EEXCESSDataTransformationException(e);
        }
    }

    @Override
    public Document queryPartnerDetails(PartnerConfiguration partnerConfiguration, DocumentBadge document, PartnerdataLogger logger) throws IOException {
        // Configure
        try {
            Client client = new Client(PartnerConfigurationCache.CONFIG.getClientDefault());

            queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.getQueryGeneratorClass());

            String detailQuery = getQueryGenerator().toDetailQuery(document);

            Map<String, String> valuesMap = new HashMap<String, String>();
            valuesMap.put("detailQuery", detailQuery);
            valuesMap.put("apiKey", partnerConfiguration.getApiKey()); // add
                                                                       // API
                                                                       // key

            String searchRequest = StrSubstitutor.replace(partnerConfiguration.getDetailEndpoint(), valuesMap);

            WebResource service = client.resource(searchRequest);

            Builder builder = service.accept(MediaType.APPLICATION_JSON);

            client.destroy();
            String httpJSONResult = builder.get(String.class);
            Document newResponse = null;
            try {
                newResponse = this.transformJSON2XML(httpJSONResult);
            } catch (EEXCESSDataTransformationException e) {
                LOGGER.log(Level.INFO, "Error Transforming Json to xml", e);
            }
            return newResponse;
        } catch (Exception e) {
            throw new IOException("Cannot query partner REST API!", e);
        }
    }

}

// String jsonResponse = builder.get(String.class);
// logger.log(Level.INFO,"SearchRequest:" + searchRequest);
// logger.log(Level.INFO,"JsonResponse:" + jsonResponse);
// PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration,
// jsonResponse, "service-response", PartnerdataTracer.FILETYPE.JSON);
// JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonResponse );
//
// JSONArray items = json.getJSONArray("items");
// for(Object item : items){
// JSONObject itemm = (JSONObject) item;
//
// String objectId = itemm.getString("id");
// JSONObject details = fetchDocumentDetails(client, objectId,
// partnerConfiguration);
//
// if (details.has("object")) {
// JSONObject object = details.getJSONObject("object");
// if (object.has("europeanaAggregation")) {
// JSONObject europeanaAggregation =
// object.getJSONObject("europeanaAggregation");
// if (europeanaAggregation.has("edmCountry")) {
// JSONObject edmCountry = europeanaAggregation.getJSONObject("edmCountry");
// if (edmCountry.has("def")) {
// String country = edmCountry.getString("def");
//
// itemm.put("edmCountry", country);
// }
// }
// }
// if (object.has("concepts")) {
// JSONArray concepts = object.getJSONArray("concepts");
// JSONArray newConcepts = new JSONArray();
// for (int i = 0; i < concepts.size(); i++) {
// if (((JSONObject) concepts.get(i)).has("prefLabel")) {
// JSONObject prefLabel = ((JSONObject)
// concepts.get(i)).getJSONObject("prefLabel");
// if (prefLabel.has("en")) {
// String concept = prefLabel.getString("en");
// JSONObject newConcept = new JSONObject();
// newConcept.put("en", concept);
// newConcepts.add(newConcept);
// }
// }
// }
// if (newConcepts.size() > 0)
// itemm.put("edmConcept", newConcepts);
//
// }
// if (object.has("places")) {
// JSONArray places = object.getJSONArray("places");
// JSONArray newPlaces = new JSONArray();
// for (int i = 0; i < places.size(); i++) {
// JSONObject myPlace = (JSONObject) places.get(i);
// JSONObject newPlace = new JSONObject();
// if (myPlace.has("about")) {
// newPlace.put("about", myPlace.get("about"));
// }
// if (myPlace.has("altLabel")) {
// JSONObject altLabel = myPlace.getJSONObject("altLabel");
// if (altLabel.has("en")) {
// String placeLabel = altLabel.getString("en");
// newPlace.put("en", placeLabel);
// }
// }
// /*
// "latitude": 47.06667,
// "longitude": 15.45,
// "note": {
// "def": [
// "http://ru.wikipedia.org/wiki/%D0%93%D1%80%D0%B0%D1%86",
// "http://en.wikipedia.org/wiki/Graz"
// ]
// },
//
// */
//
// if (myPlace.has("latitude")) {
// newPlace.put("latitude", myPlace.get("latitude"));
// }
// if (myPlace.has("longitude")) {
// newPlace.put("longitude", myPlace.get("longitude"));
// }
// newPlaces.add(newPlace);
// }
// if (newPlaces.size() > 0)
// itemm.put("edmPlace", newPlaces);
//
// }
// }

// }
// PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration,
// json.toString(), "service-response-added-values",
// PartnerdataTracer.FILETYPE.JSON);
