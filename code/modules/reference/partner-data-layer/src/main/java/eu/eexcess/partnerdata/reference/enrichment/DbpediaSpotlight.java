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
package eu.eexcess.partnerdata.reference.enrichment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.PartnerdataTracer.FILETYPE;


public class DbpediaSpotlight extends EnrichmentServiceBase{

	//protected static final String DBPEDIA_URL = "http://spotlight.dbpedia.org/rest/candidates?text=";
	protected static final String DBPEDIA_URL = "http://spotlight.dbpedia.org/rest/annotate?text=";
	protected int timeout = 5000;
	public DbpediaSpotlight(PartnerConfiguration config) {
		super(config);
	}

	public boolean isEntityDbpediaSpotlight(String word, PartnerdataLogger logger)
	{
        long startTime = logger.getActLogEntry().getTimeNow();
		try {
			String URL=DBPEDIA_URL;
			URL += word.replaceAll(" ", "%20");
	
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			
	//		HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(URL);
			request.setHeader("Accept", "text/xml");
			HttpResponse response;
	
			try {
				response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == 200)
				{
					InputStream is= response.getEntity().getContent();
//					Set<DbpediaSpotlightResult> entities=XmlParser.getEntitiesDbpediaSpotlightCandidatesXML(this.partnerConfig,is);
					Set<DbpediaSpotlightResult> entities=XmlParser.getEntitiesDbpediaSpotlightAnnotateXML(this.partnerConfig,is, logger);
					is.close();
					logger.getActLogEntry().addEnrichmentDbpediaSpotlightResults(entities.size());
					logger.getActLogEntry().addEnrichmentDbpediaSpotlightServiceCalls(1);
					logger.getActLogEntry().addEnrichmentDbpediaSpotlightServiceCallDuration(startTime);

					return entities.size()>0;
				}
	
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

		return false;

	}

	public Set<DbpediaSpotlightResult> selectEntitiesDbpediaSpotlight(Set<String> words, PartnerdataLogger logger)
	{

        long startTime = logger.getActLogEntry().getTimeNow();
		String urlBase=DBPEDIA_URL;
		Set<DbpediaSpotlightResult> entities=new HashSet<DbpediaSpotlightResult>();

		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			

			for (String w : words)
			{
				String url=urlBase+w.replaceAll(" ", "%20");
				HttpGet request = new HttpGet(new URI(url).toString());
				request.setHeader("Accept", "text/xml");

				HttpResponse response = client.execute(request);
		        PartnerdataTracer.dumpFile(DbpediaSpotlight.class, this.partnerConfig, response.getEntity().toString(), "dbpedia-response", FILETYPE.XML, logger);

//				entities.addAll(XmlParser.getEntitiesDbpediaSpotlightCandidatesXML(this.partnerConfig, response.getEntity().getContent()));
				entities.addAll(XmlParser.getEntitiesDbpediaSpotlightAnnotateXML(this.partnerConfig, response.getEntity().getContent(), logger));
			}
			return entities;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.getActLogEntry().addEnrichmentDbpediaSpotlightResults(entities.size());
		logger.getActLogEntry().addEnrichmentDbpediaSpotlightServiceCalls(1);
		logger.getActLogEntry().addEnrichmentDbpediaSpotlightServiceCallDuration(startTime);

		return entities;

	}
	
	public DbpediaSpotlightResult selectEntityDbpediaSpotlight(String word, PartnerdataLogger logger)
	{
		
		Set<String> words=new HashSet<String>();
		words.add(word);

		Iterator<DbpediaSpotlightResult> iterator= selectEntitiesDbpediaSpotlight(words,logger).iterator();
		
		if (iterator.hasNext())
		{
			return iterator.next();
		}
		else
		{
			return null;
		}
		
	}
}
