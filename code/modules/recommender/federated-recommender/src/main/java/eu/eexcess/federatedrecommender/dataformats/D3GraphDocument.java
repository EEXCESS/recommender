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
package eu.eexcess.federatedrecommender.dataformats;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
/**
 * VisualisationDataType (Just to get a json out of the results of the graph)
 * @author hziak
 *
 */
public class D3GraphDocument implements Serializable{
	private static final long serialVersionUID = 1L;



	@XmlElement(name="node")
	@XmlElementWrapper(name="nodes")
	public ArrayList<String> nodes;

	@XmlElement(name="edge")
	@XmlElementWrapper(name="edges")
	public ArrayList<D3GraphDocumentEdge> edges;
	
	
	public D3GraphDocument(SimpleWeightedGraph<String, DefaultEdge> graph) throws FederatedRecommenderException {
		if(graph==null)
			throw new FederatedRecommenderException("graph was null");
		if(graph.vertexSet()==null)
			throw new FederatedRecommenderException("graph was null");
		
		if(graph.edgeSet()==null)
			throw new FederatedRecommenderException("graph was null");
	
		nodes = new ArrayList<String>(graph.vertexSet());
		edges = new ArrayList<D3GraphDocumentEdge>();
		for (DefaultEdge currentEdge : graph.edgeSet()) {
			
			 edges.add(new D3GraphDocumentEdge(graph.getEdgeSource(currentEdge),graph.getEdgeTarget(currentEdge)));
		}
		
		
	}		


	}
