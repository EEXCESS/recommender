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

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.io.AbstractGraphReader;
import grph.io.AbstractGraphWriter;
import grph.io.GraphBuildException;
import grph.io.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GrphTupleCollector implements CategoryTupleCollector {

	// private Logger logger = PianoLogger.getLogger(GrphTupleCollector.class);
	private int initialCategoryCapacity = 350000;
	// private int logAllNTakeCalls = 200000;
	private Grph graph;
	private Map<String, Integer> categoryIds;

	public static class Statistics {
		public int numSelfLinked = 0;
		public int numDuplicates = 0;
		public int numTotalCalls = 0;
		public int numTotalTaken = 0;

		@Override
		public String toString() {
			return "edges inflated to graph: nodes [" + numTotalTaken + "] edge duplicates [" + numDuplicates
							+ "] self linked [" + numSelfLinked + "] total collecting calls [" + numTotalTaken + "]";
		}
	}

	private Statistics stats;

	private HashSet<String> seenTuples;

	public GrphTupleCollector(int initialCategoryCapacity) {
		this.initialCategoryCapacity = initialCategoryCapacity;
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
		clear();
	}

	private synchronized int categoryId(String category) {
		Integer id = categoryIds.get(category);
		if (id == null) {
			id = categoryIds.size() + 1;
			categoryIds.put(category, id);
		}
		return id;
	}

	@Override
	public void takeTuple(String parent, String child) throws SQLException {

		stats.numTotalCalls++;
		if (parent.hashCode() == child.hashCode()) {
			// logger.warning("skipping loop to self with parent[" + parent +
			// "] subcategory[" + child + "]");
			stats.numSelfLinked++;
			return;
		}
		if (seenTuples.contains(child + parent)) {
			// logger.warning("skipping seen tuple parent[" + parent +
			// "] subcategory[" + child + "]");
			stats.numDuplicates++;
			return;
		}
		stats.numTotalTaken++;

		int childId = categoryId(child);
		int parentId = categoryId(parent);

		if (!graph.containsVertex(childId)) {
			graph.addVertex(childId);
		}

		if (!graph.containsVertex(parentId)) {
			graph.addVertex(parentId);
		}

		graph.addDirectedSimpleEdge(childId, parentId);
		seenTuples.add(child + parent);

		// if (0 == stats.numTotalCalls % logAllNTakeCalls) {
		// logger.info("total taken tuples [" + stats.numTotalTaken +
		// "] out of [" + stats.numTotalCalls + "]");
		// if (stats.numDuplicates > 0 || stats.numSelfLinked > 0) {
		// logger.warning("skipped tuples: duplicates [" + stats.numDuplicates +
		// "] self linked ["
		// + stats.numSelfLinked + "]");
		// }
		// }
	}

	/**
	 * releases references to state dependent resources
	 */
	public void clear() {
		graph = new InMemoryGrph();
		categoryIds = new HashMap<>(initialCategoryCapacity);
		seenTuples = new HashSet<>();
		stats = new Statistics();
	}

	public Statistics getStatistics() {
		return stats;
	}

	public Grph getGraph() {
		return graph;
	}

	public Map<String, Integer> getCategoryMap() {
		return categoryIds;
	}

	public int getCategoryId(String category) {
		System.out.println("called id for [" + category + "]");
		int id = categoryIds.get(category).intValue();
		System.out.println("id [" + id + "]");
		return id;
	}

	public String getCategory(int id) {
		for (Map.Entry<String, Integer> entry : categoryIds.entrySet()) {
			if (entry.getValue().compareTo(id) == 0) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void storeToFile(File graphFile, AbstractGraphWriter writer) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(graphFile);
		writer.writeGraph(graph, fos);
		fos.flush();
		fos.close();
	}

	public static Grph readFromFile(File graphFile, AbstractGraphReader reader) throws FileNotFoundException,
					IOException, GraphBuildException, ParseException {
		FileInputStream fis = new FileInputStream(graphFile);
		Grph g = reader.readGraph(fis);
		fis.close();
		return g;
	}
}
