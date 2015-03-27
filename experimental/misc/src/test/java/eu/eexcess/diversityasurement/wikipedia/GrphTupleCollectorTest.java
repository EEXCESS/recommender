/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.diversityasurement.wikipedia;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import eu.eexcess.diversityasurement.wikipedia.config.Settings;
import grph.Grph;
import grph.io.GraphBuildException;
import grph.io.GrphBinaryReader;
import grph.io.GrphBinaryWriter;
import grph.io.ParseException;
import grph.path.ArrayListPath;
import grph.properties.NumericalProperty;

public class GrphTupleCollectorTest {

	@Test
	public void collectAndCalculateSomeKNearestPathes_givenDBtPediaDB_notExceptional() throws IOException {
		if (!Settings.RDFCategories.isCategoryFileAvailable()) {
			return;
		}

		GrphTupleCollector collector = new GrphTupleCollector(330000);
		RDFCategoryExtractor extractor = new RDFCategoryExtractor(new File(Settings.RDFCategories.PATH), collector);
		extractor.extract();

		Grph g = collector.getGraph();
		collector.clear();
		System.out.println(g);
		// vertices [747248] edges [1678939] simple edges [1678939] undirected
		// edges [0] undirected simple edges [0] directed edges [1678939]
		// directed simple edges [1678939]
		System.out.println("vertices [" + g.getNumberOfVertices() + "] edges [" + g.getNumberOfEdges()
						+ "] simple edges [" + g.getNumberOfSimpleEdges() + "] undirected edges ["
						+ g.getNumberOfUndirectedEdges() + "] undirected simple edges ["
						+ g.getNumberOfUndirectedSimpleEdges() + "] directed edges [" + g.getNumberOfDirectedEdges()
						+ "] directed simple edges [" + g.getNumberOfDirectedSimpleEdges() + "]");
		List<List<ArrayListPath>> targetPaths = compute(g, 3, new int[] { 4, 5, 6 }, 4, null);

		for (List<ArrayListPath> targetPath : targetPaths) {
			int i = 0;
			for (ArrayListPath paths : targetPath) {
				System.out.println("p[" + i++ + "] " + paths.toString());
			}
		}
	}

	@Test
	public void collectAndWriteToFile_givenDBtPediaDB_notExceptional() throws IOException {
		if (!Settings.RDFCategories.isCategoryFileAvailable()) {
			return;
		}
		if (!Settings.RDFCategories.isCategoryFileAvailable()) {
			return;
		}

		GrphTupleCollector collector = new GrphTupleCollector(330000);
		RDFCategoryExtractor extractor = new RDFCategoryExtractor(new File(Settings.RDFCategories.PATH), collector);
		extractor.extract();

		Grph g = collector.getGraph();
		System.out.println(g);
		collector.storeToFile(new File(Settings.Grph.PATH), new GrphBinaryWriter());
		collector.clear();
		// vertices [747248] edges [1678939] simple edges [1678939] undirected
		// edges [0] undirected simple edges [0] directed edges [1678939]
		// directed simple edges [1678939]
		System.out.println("vertices [" + g.getNumberOfVertices() + "] edges [" + g.getNumberOfEdges()
						+ "] simple edges [" + g.getNumberOfSimpleEdges() + "] undirected edges ["
						+ g.getNumberOfUndirectedEdges() + "] undirected simple edges ["
						+ g.getNumberOfUndirectedSimpleEdges() + "] directed edges [" + g.getNumberOfDirectedEdges()
						+ "] directed simple edges [" + g.getNumberOfDirectedSimpleEdges() + "]");
	}

	@Test
	public void readFromFile_givenPrebuiltDBPEdiaDBGraph_expectCorectNumbersOfEdgesAndVertices()
					throws FileNotFoundException, ParseException, IOException, GraphBuildException {

		if (!Settings.Grph.isGrphFileAvailable()) {
			return;
		}

		long startTimestamp = System.currentTimeMillis();
		Grph g = GrphTupleCollector.readFromFile(new File(Settings.Grph.PATH), new GrphBinaryReader());

		System.out.println("read in [" + (System.currentTimeMillis() - startTimestamp) + "]ms vertices ["
						+ g.getNumberOfVertices() + "] edges [" + g.getNumberOfEdges() + "] simple edges ["
						+ g.getNumberOfSimpleEdges() + "] undirected edges [" + g.getNumberOfUndirectedEdges()
						+ "] undirected simple edges [" + g.getNumberOfUndirectedSimpleEdges() + "] directed edges ["
						+ g.getNumberOfDirectedEdges() + "] directed simple edges ["
						+ g.getNumberOfDirectedSimpleEdges() + "]");
		// vertices [747248] edges [1678939] simple edges [1678939] undirected
		// edges [0] undirected simple edges [0] directed edges [1678939]
		// directed simple edges [1678939]
		assertEquals(747248, g.getNumberOfVertices());
		assertEquals(1678939, g.getNumberOfEdges());
		assertEquals(1678939, g.getNumberOfSimpleEdges());
		assertEquals(0, g.getNumberOfUndirectedEdges());
		assertEquals(0, g.getNumberOfUndirectedSimpleEdges());
		assertEquals(1678939, g.getNumberOfDirectedSimpleEdges());
	}

	private List<List<ArrayListPath>> compute(Grph g, int source, int[] targets, int topK, NumericalProperty weights) {

		if (weights != null)
			throw new IllegalArgumentException("unsupported");

		g = g.clone();

		while (g.getVertices().getDensity() < 1) {
			g.addVertex();
		}

		Graph h = new Graph();

		for (int v : g.getVertices().toIntArray()) {
			h.addVertex(v);
		}

		for (int e : g.getEdges().toIntArray()) {
			int a = g.getOneVertex(e);
			int b = g.getTheOtherVertex(e, a);
			h.add_edge(a, b, 1);
		}

		YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(h);

		List<List<ArrayListPath>> targetPaths = new ArrayList<List<ArrayListPath>>();

		for (int target : targets) {
			List<edu.asu.emit.qyan.alg.model.Path> paths = alg.get_shortest_paths(h.get_vertex(source),
							h.get_vertex(target), topK);

			List<ArrayListPath> grphPaths = new ArrayList<>();
			for (edu.asu.emit.qyan.alg.model.Path p : paths) {
				ArrayListPath pb = new ArrayListPath();

				for (BaseVertex v : p.get_vertices()) {
					pb.extend(v.get_id());
				}

				grphPaths.add(pb);
			}
			targetPaths.add(grphPaths);
		}
		return targetPaths;
	}
}
