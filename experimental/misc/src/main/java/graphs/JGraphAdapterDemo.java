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
package graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Adapted from: http://jgrapht.org/visualizations.html
 */

public class JGraphAdapterDemo extends JApplet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7652737594125927551L;
	private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
    private static final Dimension DEFAULT_SIZE = new Dimension( 530, 320 );

    // 
    private JGraphModelAdapter<String, DefaultWeightedEdge> m_jgAdapter;

    /**
     * @see java.applet.Applet#init().
     */
    @Override
    public void init() {
    	
		String vertexA = JGraphPerformanceTest.randomString();
		String vertexB = JGraphPerformanceTest.randomString();

		String vertexI = JGraphPerformanceTest.randomString(); // Vertex of interest to ensure it is
											// in both graphs and there is a
											// path

		SimpleWeightedGraph<String, DefaultWeightedEdge> graphA = JGraphPerformanceTest.randomGraph(vertexA, vertexI);
		SimpleWeightedGraph<String, DefaultWeightedEdge> graphB = JGraphPerformanceTest.randomGraph(vertexB, vertexI);

		Graphs.addGraph(graphA, graphB); // Adds graphB to graphA - Merging.
											// Expensive operation?

		DijkstraShortestPath<String, DefaultWeightedEdge> path = new DijkstraShortestPath<String, DefaultWeightedEdge>(graphA, vertexA, vertexB);

		if (path.getPathLength() == Double.POSITIVE_INFINITY) {
			System.out.println("No path between " + vertexA + " and " + vertexB	+ " found.");
		}
		else
		{
			System.out.println("Path between " + vertexA + " and " + vertexB + " found!");
			System.out.println("Length: " + path.getPathLength());

			System.out.println(path.getPathEdgeList().toString());
		}
    	
    	
    	
    	// create a JGraphT graph
        //ListenableGraph g = new ListenableDirectedGraph( DefaultEdge.class );

        // create a visualization using JGraph, via an adapter
        m_jgAdapter = new JGraphModelAdapter<String, DefaultWeightedEdge>( graphA );

        JGraph jgraph = new JGraph( m_jgAdapter );

        adjustDisplaySettings( jgraph );
        getContentPane(  ).add( jgraph );
        resize( DEFAULT_SIZE );

    }


    private void adjustDisplaySettings( JGraph jg ) {
        jg.setPreferredSize( DEFAULT_SIZE );

        Color  c        = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter( "bgcolor" );
        }
         catch( Exception e ) {}

        if( colorStr != null ) {
            c = Color.decode( colorStr );
        }

        jg.setBackground( c );
    }


    
    @SuppressWarnings("unused")
	private void positionVertexAt( Object vertex, int x, int y ) {
        DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
        Map<?, ?>              attr = cell.getAttributes(  );
        Rectangle2D      b    =  GraphConstants.getBounds(attr);

        GraphConstants.setBounds( attr, new Rectangle( x, y, b.getBounds().width, b.getBounds().height ) );

        Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>(  );
        cellAttr.put( cell, attr );
        m_jgAdapter.edit( cellAttr, null, null, null );
    }
}
