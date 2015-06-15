#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.webservice.tool;

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
 * @author thomas.orgel@joanneum.at
 */
public class PartnerStandaloneServer {
	private static Server server;
	
	public static synchronized void start(int port) throws Exception {
		if (server != null) {
			throw new IllegalStateException("Server is already running");
		}
		
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/${artifactId}-${version}");
		Map<String,Object> initMap = new HashMap<String, Object>();
	    initMap.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
	    initMap.put("com.sun.jersey.config.property.packages", "${groupId}.partnerwebservice");	    
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
