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
package eu.eexcess.ddb.webservice.tool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Standalone server to test the web service. 
 * 
 * @author rkern@know-center.at
 */
public class PartnerStandaloneServer {
	private static Server server;
	
	public static synchronized void start(int port) throws Exception {
		if (server != null) {
			throw new IllegalStateException("Server is already running");
		}
		
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/eexcess-partner-ddb-1.0-SNAPSHOT");
		Map<String,Object> initMap = new HashMap<String, Object>();
	    initMap.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
	    initMap.put("com.sun.jersey.config.property.packages", "eu.eexcess.partnerwebservice");	    
		context.addServlet(new ServletHolder(new ServletContainer(new PackagesResourceConfig(initMap))), "/*");
		
//		EnrichmentServer.configProvider = configProvider;
		server = new Server(port);
		server.setHandler(context);
		server.start();
	}

	public static synchronized void stop() throws Exception {
		if (server == null) {
			throw new IllegalStateException("Server not running");
		}
		server.stop();		
	}	
	
	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.out.println("PartnerStandaloneServer <port-number>");
			System.exit(-1);
		}
		
		start(Integer.parseInt(args[0]));
	}

}
