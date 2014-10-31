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
/* ==========================================

 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* -----------------
 * HelloJGraphT.java
 * -----------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 27-Jul-2003 : Initial revision (BN);
 *
 */
package graphs;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.*;


/**
 * Performance test of JGraphT.
 *
 * @author Pablo López-García
 * @since Dec 16, 2013
 */
public final class JGraphPerformanceTest
{
    

    private JGraphPerformanceTest()
    {
    } // ensure non-instantiability.

    

    public static void main(String [] args)
 {

		String vertexA = randomString();
		String vertexB = randomString();

		String vertexI = randomString(); // Vertex of interest to ensure it is
											// in both graphs and there is a
											// path

		SimpleWeightedGraph<String, DefaultWeightedEdge> graphA = randomGraph(vertexA, vertexI);
		SimpleWeightedGraph<String, DefaultWeightedEdge> graphB = randomGraph(vertexB, vertexI);

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

		

	}

    public static SimpleWeightedGraph<String, DefaultWeightedEdge> randomGraph(String sourceVertex, String interestVertex)
    {
    	SimpleWeightedGraph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    	
    	// Root node
        g.addVertex(sourceVertex);
        
        for (int i = 0; i < 5; i++)
        {
        	String randomVertex = randomString();
        	g.addVertex(randomVertex);

            DefaultWeightedEdge e = g.addEdge(sourceVertex, randomVertex);
            g.setEdgeWeight(e, randomInt());
        }
        
           
    	     
    	return g;
    	
    }
    
    public static int randomInt()
    {
    	// Random int for weight edge
    	int MIN = 1, MAX = 5;
    	int w = MIN + (int)(Math.random() * ((MAX - MIN) + 1));
    	return w;
    }
    
    public static String randomString()
    {
    	// Random string for vertix
    	SecureRandom random = new SecureRandom();
    	String s = new BigInteger(130, random).toString(32);
    	return s;
    }
    
}

