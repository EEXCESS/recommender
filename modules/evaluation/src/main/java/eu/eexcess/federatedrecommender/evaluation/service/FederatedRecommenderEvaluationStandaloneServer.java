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
package eu.eexcess.federatedrecommender.evaluation.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class FederatedRecommenderEvaluationStandaloneServer {
    private static Server server;

    private FederatedRecommenderEvaluationStandaloneServer() {
    }

    public static synchronized void start(int port) throws Exception {
        if (server != null) {
            throw new IllegalStateException("Server is already running");
        }

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/eexcess-federated-recommender-web-service-evaluation-1.0-SNAPSHOT");
        Map<String, Object> initMap = new HashMap<String, Object>();
        initMap.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        initMap.put("com.sun.jersey.config.property.packages", " eu.eexcess.federatedrecommender.evaluation.service");

        context.addServlet(new ServletHolder(new ServletContainer(new PackagesResourceConfig(initMap))), "/*");

        ResourceHandler resourcehandler = new ResourceHandler();
        resourcehandler.setDirectoriesListed(true);
        resourcehandler.setWelcomeFiles(new String[] { "index.html" });

        resourcehandler.setResourceBase(".");

        // EnrichmentServer.configProvider = configProvider;

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourcehandler);
        handlers.addHandler(context);
        server = new Server(port);
        // server.setHandler(resourcehandler);

        server.setHandler(handlers);

        server.start();
    }

    public static synchronized void stop() throws Exception {
        if (server == null) {
            throw new IllegalStateException("Server not running");
        }
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("FederatedRecommenderStandaloneServer <port-number>");
            System.exit(-1);
        }

        start(Integer.parseInt(args[0]));
    }
}
