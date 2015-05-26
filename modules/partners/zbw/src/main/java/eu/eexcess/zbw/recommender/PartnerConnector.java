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
package eu.eexcess.zbw.recommender;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.text.StrSubstitutor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
import eu.eexcess.zbw.recommender.dataformat.ZBWDocument;
import eu.eexcess.zbw.recommender.dataformat.ZBWDocumentHit;

/**
 * Query generator for ZBW.
 * 
 * @author thomas.orgel@joanneum.at
 */

public class PartnerConnector implements PartnerConnectorApi {
	private static final Logger log = Logger.getLogger(PartnerConnector.class.getName());
    private QueryGeneratorApi queryGenerator;

	
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
		
		// Configure
		try {	
			Client client = new Client(PartnerConfigurationCache.CONFIG.getClientJAXBContext());
	        queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass);
			
	        String query = getQueryGenerator().toQuery(userProfile);
	        query=query.replaceAll("\"", "");
	        query=query.replaceAll("\\(", " ");
	        query=query.replaceAll("\\)", " ");
	        query = URLEncoder.encode(query,"UTF-8");
	        Map<String, String> valuesMap = new HashMap<String, String>();
	        valuesMap.put("query", query);
	        if(userProfile.numResults!=null)
	        	valuesMap.put("size", userProfile.numResults.toString());
	        else
	        	valuesMap.put("size", "10");
	        String searchRequest = StrSubstitutor.replace(partnerConfiguration.searchEndpoint, valuesMap);
	        
	        WebResource service = client.resource(searchRequest);
	        log.log(Level.INFO,"SearchRequest: "+searchRequest);
	      
	        
	        Builder builder = service.accept(MediaType.APPLICATION_XML);
	        String response = builder.get(String.class);
	        StringReader respStringReader = new StringReader(response) ;
	        client.destroy();
	        JAXBContext jaxbContext = JAXBContext.newInstance(ZBWDocument.class);
	        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	        ZBWDocument zbwResponse = (ZBWDocument) jaxbUnmarshaller.unmarshal(respStringReader);
	        for (ZBWDocumentHit hit : zbwResponse.hits.hit) {
	        	try{
	        		if(hit.element.type.equals("event")){
	        			Document detail =fetchDocumentDetails( hit.element.id);
	        			PartnerdataTracer.dumpFile(this.getClass(), partnerConfiguration, detail, "detail-response", logger);
	        			String latValue = getValueWithXPath("/doc/record/geocode/lat", detail);
	        			String longValue = getValueWithXPath("/doc/record/geocode/lng", detail);
	        			hit.element.lat=latValue;
	        			hit.element.lng=longValue;
	        		}
	        	}	catch(Exception e){
	        			log.log(Level.WARNING,"Could not get longitude and latitude for event element "+hit.element.id,e);
	        	}
	        	// put all creators in the creatorString
	        	if (hit.element.creator != null)
	        	{
		        	for (String creator : hit.element.creator) {
		        		if (hit.element.creatorString == null)
		        			hit.element.creatorString = "";
		        		if (hit.element.creatorString.length() > 0) 
		        			hit.element.creatorString += ", ";
		        		hit.element.creatorString += creator;
		        	}
	        	}
	        }
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document document = db.newDocument();
	        Marshaller marshaller = jaxbContext.createMarshaller();
	        marshaller.marshal(zbwResponse, document);
	
	        return document;
		}	catch (Exception e) {
			throw new IOException("Cannot query partner REST API!", e);
		}
        
	}

	
	protected Document fetchDocumentDetails( String id) {
		Client client = PartnerConfigurationCache.CONFIG.getClientJAXBContext();
		String request = "https://api.econbiz.de/v1/record/"+id+"?xml=true";
		Document returnValue = null;
		try {
	
			returnValue = client.resource(request)
					.accept(MediaType.APPLICATION_XML_TYPE)
					.get(Document.class);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.log(Level.INFO,"No Detailed information on document " +id+ " found" ,e);
		}
		client.destroy();
		return returnValue; 	
}

	
	protected String getValueWithXPath(String xpath, Document orgPartnerResult) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList)xPath.evaluate(xpath,
					orgPartnerResult.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength();) {
			    Element e = (Element) nodes.item(i);
			    return e.getTextContent();
			}
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}
		return "";
	}
	@Override
	public Document queryPartnerDetails(
			PartnerConfiguration partnerConfiguration,
			List<DocumentBadge> documents, PartnerdataLogger logger)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
