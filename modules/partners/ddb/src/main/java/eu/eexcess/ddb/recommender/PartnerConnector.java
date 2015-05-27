/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

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
package eu.eexcess.ddb.recommender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.text.StrSubstitutor;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.XMLTools;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;
import eu.eexcess.utils.URLParamEncoder;

/**
 * Query generator for DDB.
 * 
 * @author thomas.orgel@joanneum.at
 */

public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {
	private final Logger log = Logger.getLogger(PartnerConnector.class.getName());
    private QueryGeneratorApi queryGenerator;
    

    //private boolean makeDetailRequests = false;
    
    public PartnerConnector(){
    }
    
    
    
    /**
     * Returns the query generator for the partner search engine.
     * @return the query generator
     */
    protected QueryGeneratorApi getQueryGenerator() {
        return queryGenerator;
    }
    
    @Override
    public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger)
    				throws IOException {
    	return null;
    }
    
	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException {

//	       final String url = "https://api.deutsche-digitale-bibliothek.de/items/OAXO2AGT7YH35YYHN3YKBXJMEI77W3FF/view";
	        final String key = PartnerConfigurationCache.CONFIG.getPartnerConfiguration().apiKey;
	         
	        // get XML data via HTTP request header authentication
	         /*
	        // get JSON data via HTTP request header authentication
	        String httpJsonResult = httpGet(url, new HashMap<String, String>() {
	            {
	                put("Authorization", "OAuth oauth_consumer_key=\"" + key + "\"");
	                put("Accept", "application/json");
	            }
	        });
	        logger.info(httpJsonResult); // print results
	         
	        // get JSON data via query parameter authentication
	        // remember: use URL encoded Strings online -> URLEncoder.encode(s, enc)
	        String queryJsonURL = url + "?oauth_consumer_key=" + URLEncoder.encode(key, "UTF-8");
	        String queryJsonResult = httpGet(queryJsonURL, null);
	        logger.info(queryJsonResult); // print results
*/
		
		//
		//
		// EUROPEANA Impl
		//
		//
		
		// Configure
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
	    
//        ClientConfig config = new DefaultClientConfig();
//        config.getClasses().add(JacksonJsonProvider.class);
//        
        //final Client client = new Client(PartnerConfigurationEnum.CONFIG.getClientJacksonJson());
        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass);
		String query = getQueryGenerator().toQuery(userProfile);
        long start = System.currentTimeMillis();
		
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("query", URLParamEncoder.encode(query));
        int numResultsRequest = 10;
        if (userProfile.numResults!=null && userProfile.numResults != 0)
        	numResultsRequest  = userProfile.numResults;
        valuesMap.put("numResults", numResultsRequest+"");
        String searchRequest = StrSubstitutor.replace(partnerConfiguration.searchEndpoint, valuesMap);
        String httpJSONResult = callDDBAPI(key, searchRequest, "application/json"); // print results
        //ObjectMapper mapper = new ObjectMapper();
        //DDBResponse ddbResponse = mapper.readValue(httpJSONResult, DDBResponse.class);

/*
        JAXBContext jaxbContext = JAXBContext.newInstance(DDBDocument.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ZBWDocument zbwResponse = (DDBDocument) jaxbUnmarshaller.unmarshal(respStringReader);
        for (ZBWDocumentHit hit : zbwResponse.hits.hit) {
        	try{
*/
        /*
        WebResource service = client.resource(searchRequest);
        ObjectMapper mapper = new ObjectMapper();
        Builder builder = service.accept(MediaType.APPLICATION_JSON);
        EuropeanaResponse response= builder.get(EuropeanaResponse.class);
        if (response.items.size() > numResultsRequest)
        	response.items = response.items.subList(0, numResultsRequest);
        PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, response.toString(), "service-response", PartnerdataTracer.FILETYPE.JSON);
        client.destroy();     
        if (makeDetailRequests) 
        {
	        HashMap<EuropeanaDoc, Future<Void>> futures= new HashMap<EuropeanaDoc, Future<Void>>();
	        final HashMap<EuropeanaDoc, EuropeanaDocDetail> docDetails= new HashMap<EuropeanaDoc,EuropeanaDocDetail>();
	        final PartnerConfiguration partnerConfigLocal = partnerConfiguration;
	        for (int i = 0;i<response.items.size()  ;i++) {
	        	final EuropeanaDoc item = response.items.get(i);
	        	
	        	Future<Void> future = threadPool.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						EuropeanaDocDetail details = null;
						try {
							details = fetchDocumentDetails( item.id, partnerConfigLocal);
						} catch (EEXCESSDataTransformationException e) {
							logger.log(Level.INFO,"Error getting item with id"+item.id,e);
							return null;
						}		
						docDetails.put(item,details);
						return null;
					}
				});
				futures.put(item, future);
			}
	
			for (EuropeanaDoc doc : futures.keySet()) {
				try {
					futures.get(doc).get(start + 15 * 500 - System.currentTimeMillis(),
							TimeUnit.MILLISECONDS);
				} catch (InterruptedException | ExecutionException
						| TimeoutException e) {
					logger.log(Level.WARNING,"Detail thread for "+doc.id+" did not responses in time",e);
				}
	
				
				//item.edmConcept.addAll(details.concepts);
	//			item.edmConcept = details.concepts; TODO: copy into doc
	//			item.edmCountry = details.edmCountry;
	//			item.edmPlace = details.places;
			}
        }
		*/
        long end = System.currentTimeMillis();
		
		long startXML = System.currentTimeMillis();
		
    		Document newResponse = null;
			try {
				newResponse = this.transformJSON2XML(httpJSONResult);
			} catch (EEXCESSDataTransformationException e) {
				// TODO logger
				
				log.log(Level.INFO,"Error Transforming Json to xml",e );
				
			}
			long endXML = System.currentTimeMillis();
			System.out.println("millis "+(endXML-startXML) + "   "+ (end-start));
			
			threadPool.shutdownNow();
		
        return newResponse;
		
	}



	private String callDDBAPI(final String key, String searchRequest,String acceptType)
			throws IOException {
		String httpJSONResult = httpGet(searchRequest, new HashMap<String, String>() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -5911519512191023737L;

			{
                put("Authorization", "OAuth oauth_consumer_key=\"" + key + "\"");
//                put("Accept", );
				put("Accept", acceptType);

            }
        });
        log.info(httpJSONResult);
		return httpJSONResult;
	}
	/*
	protected EuropeanaDocDetail fetchDocumentDetails( String objectId, PartnerConfiguration partnerConfiguration) throws EEXCESSDataTransformationException {
		try {
			Client client = new Client(PartnerConfigurationEnum.CONFIG.getClientJacksonJson());
	        Map<String, String> valuesMap = new HashMap<String, String>();
	        valuesMap.put("objectId", objectId);
	        valuesMap.put("apiKey", partnerConfiguration.apiKey);		
	        String detailEndpoint = "http://europeana.eu/api/v2/record/${objectId}.json?wskey=${apiKey}";
	        String detailRequest = StrSubstitutor.replace(detailEndpoint, valuesMap);
		    WebResource service = client.resource(detailRequest);
		    Builder builder = service.accept(MediaType.APPLICATION_JSON);
		    EuropeanaDocDetail jsonResponse = builder.get(EuropeanaDocDetail.class);
		    PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, jsonResponse.toString(), "detail-response", PartnerdataTracer.FILETYPE.JSON);
		    client.destroy();
		    return jsonResponse;
		} catch (Exception e) {
			throw new EEXCESSDataTransformationException(e);
		}
	}

*/
	/**
	 * Opens a HTTP connection, gets the response and converts into to a String.
	 * 
	 * @param urlStr Servers URL
	 * @param properties Keys and values for HTTP request properties
	 * @return Servers response
	 * @throws IOException  If connection could not be established or response code is !=200
	 */
	public static String httpGet(String urlStr, HashMap<String, String> properties) throws IOException {
		if (properties == null) {
			properties = new HashMap<String, String>();
		}
		// open HTTP connection with URL
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// set properties if any do exist
		for (String key : properties.keySet()) {
			conn.setRequestProperty(key, properties.get(key));
		}
		// test if request was successful (status 200)
		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}
		// buffer the result into a string
		InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		isr.close();
		conn.disconnect();
		return sb.toString();
	}

	
	@Override
	public Document queryPartnerDetails(PartnerConfiguration partnerConfiguration,
			DocumentBadge document, PartnerdataLogger logger)
			throws IOException {
		// Configure
		try {	
	    	String key= PartnerConfigurationCache.CONFIG.getPartnerConfiguration().apiKey;

	        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass);;
			
	        String detailQuery = getQueryGenerator().toDetailQuery(document);
	        
	        Map<String, String> valuesMap = new HashMap<String, String>();
	        valuesMap.put("detailQuery", detailQuery);
	        
	        String searchRequest = StrSubstitutor.replace(partnerConfiguration.detailEndpoint, valuesMap);
	        
	        String httpXMLResult = callDDBAPI(key, searchRequest, "application/xml"); // print results
	       
	        // HOFFIX TODO_REMOVE
	        httpXMLResult = httpXMLResult.replaceAll("<ns4:", "<ns4");
	        httpXMLResult = httpXMLResult.replaceAll("</ns4:", "</ns4");
	        
	        return XMLTools.convertStringToDocument(httpXMLResult);
		}
		catch (Exception e) {
				throw new IOException("Cannot query partner REST API!", e);
		}
	}

}



