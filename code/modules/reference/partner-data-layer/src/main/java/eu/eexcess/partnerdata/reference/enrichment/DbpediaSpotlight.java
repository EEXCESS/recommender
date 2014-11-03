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
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.PartnerdataTracer.FILETYPE;


public class DbpediaSpotlight extends EnrichmentServiceBase{

	protected int timeout = 5000;
	public DbpediaSpotlight(PartnerConfiguration config) {
		super(config);
	}

	public boolean isEntityDbpediaSpotlight(String word)
	{
		try {
			String URL="http://spotlight.dbpedia.org/rest/spot?text="+word;
	
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			
	//		HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(URL);
			HttpResponse response;
	
			try {
				response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == 200)
				{
					InputStream is= response.getEntity().getContent();
					Set<String> entities=XmlParser.getEntitiesDbpediaSpotlightXML(this.partnerConfig,is);
					is.close();
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

	public Set<String> selectEntitiesDbpediaSpotlight(Set<String> words)
	{

		String urlBase="http://spotlight.dbpedia.org/rest/spot?text=";
		Set<String> entities=new HashSet<String>();

		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
			HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			

			for (String w : words)
			{
				String url=urlBase+w;
				HttpGet request = new HttpGet(new URI(url).toString());
				HttpResponse response = client.execute(request);
		        PartnerdataTracer.dumpFile(DbpediaSpotlight.class, this.partnerConfig, response.getEntity().toString(), "dbpedia-response", FILETYPE.TXT);

				entities.addAll(XmlParser.getEntitiesDbpediaSpotlightXML(this.partnerConfig, response.getEntity().getContent()));
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

		return entities;

	}
	
	public String selectEntityDbpediaSpotlight(String word)
	{
		
		Set<String> words=new HashSet<String>();
		words.add(word);

		Iterator<String> iterator= selectEntitiesDbpediaSpotlight(words).iterator();
		
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
