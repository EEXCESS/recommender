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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import eu.eexcess.diversityasurement.wikipedia.MainCategoryRelevanceEstimator.KShortestPaths;
import eu.eexcess.diversityasurement.wikipedia.MainCategoryRelevanceEstimator.NodeData;
import eu.eexcess.diversityasurement.wikipedia.MainCategoryRelevanceEstimator.PathMapKey;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.ArrayListPath;
import grph.properties.NumericalProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MainCategoryRelevanceEstimatorTest {

	private static class MyListPath extends ArrayListPath {

		MyListPath(ArrayListPath p) {
			for (int id : p.toVertexArray()) {
				extend(id);
			}
		}

		MyListPath() {
		}

		@Override
		public boolean equals(ArrayListPath p) {
			if (null == p) {
				return false;
			}

			if (this.getNumberOfVertices() != p.getNumberOfVertices()) {
				return false;
			}

			for (int i = 0; i < p.getNumberOfVertices(); i++) {
				if (p.getVertexAt(i) != this.getVertexAt(i)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public int hashCode() {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < getNumberOfVertices(); i++) {
				b.append(Integer.toString(getVertexAt(i)));
			}
			return b.hashCode();
		}
	}

	@Test
	public void equality() {
		// ArrayListPath p = new ArrayListPath();
		ArrayListPath p = new MyListPath();
		p.extend(21);
		p.extend(24);
		p.extend(101);

		// ArrayListPath q = new ArrayListPath();
		ArrayListPath q = new MyListPath();
		q.extend(21);
		q.extend(24);
		q.extend(101);

		// assertTrue(p.equals(new int[] {21,24,101}));
		assertTrue(p.equals(q));
	}

	@Test
	public void yenTopKShortestPaths_testArrayInputModification_expectCorrectPaths() throws IOException {
		Grph g = newTestGraph();
		int[] mainCategoryIdArray = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategoryIdArray);
		ArrayList<Integer> startCategories = new ArrayList<>(Arrays.asList(21, 22, 23, 24));
		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = estimator.yenTopKShortestPaths(startCategories, 12);

		int sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			int sourceId = startCategories.get(sourceIdx++);
			int targetIdx = 0;
			for (List<ArrayListPath> sourceToTargetPathes : sourceToTargetsPathes) {
				int targetId = mainCategoryIdArray[targetIdx++];
				assertPathes(sourceId, targetId, sourceToTargetPathes);
			}
		}
	}

	@Test
	public void yenTopKShortestPaths_testCacheModification_expectCorrectPaths() throws IOException {
		Grph g = newTestGraph();
		int[] mainCategoryIdArray = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategoryIdArray);
		ArrayList<Integer> startCategories = new ArrayList<Integer>(Arrays.asList(21));
		Map<Integer, HashMap<Integer, Double>> sourcesToTargetsRelevances = estimator.estimateRelevances(
						startCategories, 12, true);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : sourcesToTargetsRelevances.entrySet()) {
			System.out.println("[" + entry.getKey() + "] estimates:");
			for (HashMap.Entry<Integer, Double> estimate : entry.getValue().entrySet()) {
				System.out.println("id [" + estimate.getKey() + "] relevance [" + estimate.getValue() + "]");
			}
		}

		// recalculate using cache
		sourcesToTargetsRelevances = estimator.estimateRelevances(startCategories, 11, true);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : sourcesToTargetsRelevances.entrySet()) {
			System.out.println("[" + entry.getKey() + "] estimates:");
			for (HashMap.Entry<Integer, Double> estimate : entry.getValue().entrySet()) {
				System.out.println("id [" + estimate.getKey() + "] relevance [" + estimate.getValue() + "]");
			}
		}

		assertEquals(1, sourcesToTargetsRelevances.size());
		HashMap<Integer, Double> estimates = sourcesToTargetsRelevances.get(21);
		assertEquals(0.375, estimates.get(101), 0.0001);
		assertEquals(0.25, estimates.get(102), 0.0001);
		assertEquals(0.06640625, estimates.get(103), 0.0001);
		assertEquals(0.234375, estimates.get(104), 0.0001);
	}

	@Test
	public void yenTopKShortestPaths_testCacheModification_expectCorrectCacheStats() throws IOException {
		Grph g = newTestGraph();
		int[] mainCategoryIdArray = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategoryIdArray);
		ArrayList<Integer> startCategories = new ArrayList<Integer>(Arrays.asList(21, 22, 23, 24));

		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = estimator.yenTopKShortestPaths(startCategories, 12);
		int sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			int sourceId = startCategories.get(sourceIdx++);
			int targetIdx = 0;
			for (List<ArrayListPath> sourceToTargetPathes : sourceToTargetsPathes) {
				int targetId = mainCategoryIdArray[targetIdx++];
				assertPathes(sourceId, targetId, sourceToTargetPathes);
			}
		}

		sourcesToTargetsPathes = estimator.yenTopKShortestPaths(startCategories, 10);
		sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			int sourceId = startCategories.get(sourceIdx++);
			int targetIdx = 0;
			for (List<ArrayListPath> sourceToTargetPathes : sourceToTargetsPathes) {
				int targetId = mainCategoryIdArray[targetIdx++];
				assertPathes(sourceId, targetId, sourceToTargetPathes);
			}
		}
		assertEquals(4 * 39, estimator.getStatistics().cacheEntries);
		assertEquals(1 * 4 * 39, estimator.getStatistics().cacheHits);

		sourcesToTargetsPathes = estimator.yenTopKShortestPaths(startCategories, 7);
		sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			int sourceId = startCategories.get(sourceIdx++);
			int targetIdx = 0;
			for (List<ArrayListPath> sourceToTargetPathes : sourceToTargetsPathes) {
				int targetId = mainCategoryIdArray[targetIdx++];
				assertPathes(sourceId, targetId, sourceToTargetPathes);
			}
		}
		assertEquals(4 * 39, estimator.getStatistics().cacheEntries);
		assertEquals(2 * 4 * 39, estimator.getStatistics().cacheHits);

		sourcesToTargetsPathes = estimator.yenTopKShortestPaths(startCategories, 5);
		sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			int sourceId = startCategories.get(sourceIdx++);
			int targetIdx = 0;
			for (List<ArrayListPath> sourceToTargetPathes : sourceToTargetsPathes) {
				int targetId = mainCategoryIdArray[targetIdx++];
				assertPathes(sourceId, targetId, sourceToTargetPathes);
			}
		}
		assertEquals(4 * 39, estimator.getStatistics().cacheEntries);
		assertEquals(3 * 4 * 39, estimator.getStatistics().cacheHits);
		System.out.println("cache statistics: " + estimator.getStatistics());
	}

	@Test
	public void yenTopKShortestPathsConcurrent_oneThreadPerSourceNode_expectCorrectPaths() throws IOException {

		Grph g = newTestGraph();
		int[] mainCategoryIdArray = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategoryIdArray);
		MainCategoryRelevanceEstimator.setNumCategoriesToCalculateBundled(1);
		ArrayList<Integer> startCategories = new ArrayList<>(Arrays.asList(21, 22, 23, 24));

		Map<Integer, HashMap<Integer, Double>> sourcesToTargetsRelevances = estimator.estimateRelevancesConcurrent(
						startCategories, 12, 4, true);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : sourcesToTargetsRelevances.entrySet()) {
			System.out.println("[" + entry.getKey() + "] estimates:");
			for (HashMap.Entry<Integer, Double> estimate : entry.getValue().entrySet()) {
				System.out.println("id [" + estimate.getKey() + "] relevance [" + estimate.getValue() + "]");
			}
		}

		// recalculate using cache
		sourcesToTargetsRelevances = estimator.estimateRelevances(startCategories, 11, true);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : sourcesToTargetsRelevances.entrySet()) {
			System.out.println("[" + entry.getKey() + "] estimates:");
			for (HashMap.Entry<Integer, Double> estimate : entry.getValue().entrySet()) {
				System.out.println("id [" + estimate.getKey() + "] relevance [" + estimate.getValue() + "]");
			}
		}

		assertEquals(4, sourcesToTargetsRelevances.size());
		HashMap<Integer, Double> estimates = sourcesToTargetsRelevances.get(21);
		assertEquals(0.09375, estimates.get(101), 0.0001);
		assertEquals(0.0625, estimates.get(102), 0.0001);
		assertEquals(0.0166015625, estimates.get(103), 0.0001);
		assertEquals(0.05859375, estimates.get(104), 0.0001);

	}

	@Test
	public void yenTopKShortestPathtsConcurrent_withKneighborhood_expectCorrectPaths() throws IOException {

		Grph g = newTestGraph();

		int[] mainCategoryIdArray = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategoryIdArray);
		MainCategoryRelevanceEstimator.setNumCategoriesToCalculateBundled(1);
		ArrayList<Integer> startCategories = new ArrayList<>(Arrays.asList(21, 22, 23, 24));

		estimator.setKClosestNeighborsSubgraph(4);
		Map<Integer, HashMap<Integer, Double>> sourcesToTargetsRelevances = estimator.estimateRelevancesConcurrent(
						startCategories, 1, 4, false);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : sourcesToTargetsRelevances.entrySet()) {
			System.out.println("[" + entry.getKey() + "] estimates:");
			for (HashMap.Entry<Integer, Double> estimate : entry.getValue().entrySet()) {
				System.out.println("id [" + estimate.getKey() + "] relevance [" + estimate.getValue() + "]");
			}
		}

		assertEquals(4, sourcesToTargetsRelevances.size());
		HashMap<Integer, Double> estimates = sourcesToTargetsRelevances.get(21);
		assertEquals(0.25, estimates.get(101), 0.0001);
		// assertEquals(0.03125, estimates.get(102), 0.0001);
		// assertEquals(0.0625, estimates.get(103), 0.0001);
		// assertEquals(0.03125, estimates.get(104), 0.0001);

		// estimates = sourcesToTargetsRelevances.get(22);
		assertEquals(0.25, estimates.get(101), 0.0001);
		// assertEquals(0.0625, estimates.get(102), 0.0001);
		// assertEquals(0.125, estimates.get(103), 0.0001);
		// assertEquals(0.0625, estimates.get(104), 0.0001);

		estimates = sourcesToTargetsRelevances.get(23);
		// assertEquals(0.125, estimates.get(101), 0.0001);
		// assertEquals(0.03125, estimates.get(102), 0.0001);
		// assertEquals(0.0625, estimates.get(103), 0.0001);
		assertEquals(0.5, estimates.get(104), 0.0001);

		estimates = sourcesToTargetsRelevances.get(24);
		assertEquals(0.5, estimates.get(101), 0.0001);
		assertEquals(0.25, estimates.get(102), 0.0001);
		// assertEquals(0.0078125, estimates.get(103), 0.0001);
		// assertEquals(0.125, estimates.get(104), 0.0001);

		// recalculate using cache
		sourcesToTargetsRelevances = estimator.estimateRelevancesConcurrent(startCategories, 1, 4, false);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : sourcesToTargetsRelevances.entrySet()) {
			System.out.println("[" + entry.getKey() + "] estimates:");
			for (HashMap.Entry<Integer, Double> estimate : entry.getValue().entrySet()) {
				System.out.println("id [" + estimate.getKey() + "] relevance [" + estimate.getValue() + "]");
			}
		}

		assertEquals(4, sourcesToTargetsRelevances.size());
		estimates = sourcesToTargetsRelevances.get(21);
		assertEquals(0.25, estimates.get(101), 0.0001);
		// assertEquals(0.03125, estimates.get(102), 0.0001);
		// assertEquals(0.0625, estimates.get(103), 0.0001);
		// assertEquals(0.03125, estimates.get(104), 0.0001);

		// estimates = sourcesToTargetsRelevances.get(22);
		assertEquals(0.25, estimates.get(101), 0.0001);
		// assertEquals(0.0625, estimates.get(102), 0.0001);
		// assertEquals(0.125, estimates.get(103), 0.0001);
		// assertEquals(0.0625, estimates.get(104), 0.0001);

		estimates = sourcesToTargetsRelevances.get(23);
		// assertEquals(0.125, estimates.get(101), 0.0001);
		// assertEquals(0.03125, estimates.get(102), 0.0001);
		// assertEquals(0.0625, estimates.get(103), 0.0001);
		assertEquals(0.5, estimates.get(104), 0.0001);

		estimates = sourcesToTargetsRelevances.get(24);
		assertEquals(0.5, estimates.get(101), 0.0001);
		assertEquals(0.25, estimates.get(102), 0.0001);
		// assertEquals(0.0078125, estimates.get(103), 0.0001);
		// assertEquals(0.125, estimates.get(104), 0.0001);

	}

	@Test
	public void yenTopKShortestPathsConcurrent_testCacheModification_expectCorrectCacheStats() throws IOException {
		Grph g = newTestGraph();
		int[] mainCategoryIdArray = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategoryIdArray);
		MainCategoryRelevanceEstimator.setNumCategoriesToCalculateBundled(1);
		ArrayList<Integer> startCategories = new ArrayList<>(Arrays.asList(21, 22, 23, 24));

		estimator.estimateRelevancesConcurrent(startCategories, 12, 4, true);
		System.out.println("cache statistics#1: " + estimator.getStatistics());
		assertEquals(0 * 4 * 39, estimator.getStatistics().cacheHits);
		assertEquals(4 * 39, estimator.getStatistics().cacheEntries);

		// recalculate using cache
		estimator.estimateRelevancesConcurrent(startCategories, 11, 4, true);
		System.out.println("cache statistics#2: " + estimator.getStatistics());
		assertEquals(4 * 39, estimator.getStatistics().cacheHits);
		assertEquals(4 * 39, estimator.getStatistics().cacheEntries);

		// recalculate using cache
		estimator.estimateRelevancesConcurrent(new ArrayList<Integer>(Arrays.asList(21, 25)), 11, 4, true);
		System.out.println("cache statistics#3: " + estimator.getStatistics());
		assertEquals(4 * 39 + 1 * 39, estimator.getStatistics().cacheHits);
		assertEquals(4 * 39 + 1 * 39, estimator.getStatistics().cacheEntries);

	}

	@Test
	public void getKCLosestNeighbours() {
		Grph g = newTestGraph();
		toools.set.IntSet f = g.getKClosestNeighbors(27, 6, new NumericalProperty("asdf"));
		Grph h = g.getSubgraphInducedByVertices(f);
		System.out.println("f:" + f);
		System.out.println("grphf:" + h);
	}

	/**
	 * compare given paths to known paths
	 */
	private void assertPathes(int sourceId, int targetId, List<ArrayListPath> sourceToTargetPathes) {
		List<MyListPath> knownPaths = getKnownPathsFromSrcToMainCategories(sourceId);
		List<MyListPath> toTargetPathes = new ArrayList<>();

		// convert to paths that support .equals() as expected
		for (ArrayListPath path : sourceToTargetPathes) {
			toTargetPathes.add(new MyListPath(path));
		}

		// remove all paths from sourceId to targetId
		for (ArrayListPath path : toTargetPathes) {
			knownPaths.remove(path);
		}

		// assert no paths from sourceId to targetId are left over
		for (ArrayListPath p : knownPaths) {
			if (p.getVertexAt(p.getLength() - 1) == targetId && p.getVertexAt(0) == sourceId) {
				assertTrue(false);
			}
		}
	}

	private List<MyListPath> getKnownPathsFromSrcToMainCategories(int src) {
		List<MyListPath> pathes = new ArrayList<>();

		switch (src) {
		case 21:
			MyListPath p = new MyListPath();
			p.extend(21);
			p.extend(24);
			p.extend(101);
			pathes.add(p);

			p = new MyListPath();
			p.extend(21);
			p.extend(22);
			p.extend(24);
			p.extend(25);
			p.extend(26);
			p.extend(102);
			pathes.add(p);

			p = new MyListPath();
			p.extend(17);
			p.extend(22);
			p.extend(27);
			p.extend(25);
			p.extend(26);
			p.extend(102);
			pathes.add(p);

			p = new MyListPath();
			p.extend(21);
			p.extend(22);
			p.extend(27);
			p.extend(26);
			p.extend(102);
			pathes.add(p);

			p = new MyListPath();
			p.extend(21);
			p.extend(22);
			p.extend(27);
			p.extend(103);
			pathes.add(p);

			p = new MyListPath();
			p.extend(21);
			p.extend(22);
			p.extend(27);
			p.extend(28);
			p.extend(104);
			pathes.add(p);

			p = new MyListPath();
			p.extend(21);
			p.extend(22);
			p.extend(27);
			p.extend(28);
			p.extend(23);
			p.extend(104);
			pathes.add(p);
			break;
		}
		return pathes;
	}

	private int[] newMaincategoryIdArray() {
		return new int[] { 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
						119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137,
						138, 139 };
	}

	private String mapCategoryIdToName(Integer id) {
		for (Map.Entry<String, Integer> entry : newMainCategoryIdDictionary().entrySet()) {
			if (entry.getValue().compareTo(id) == 0) {
				return entry.getKey();
			}
		}
		return null;
	}

	private HashMap<String, Integer> newMainCategoryIdDictionary() {
		HashMap<String, Integer> categoryIds = new HashMap<>();
		categoryIds.put("Agriculture‎", 101);
		categoryIds.put("Architecture‎", 102);
		categoryIds.put("Arts‎", 103);
		categoryIds.put("Behavior‎", 104);
		categoryIds.put("Chronology‎", 105);
		categoryIds.put("Concepts‎", 106);
		categoryIds.put("Creativity‎", 107);
		categoryIds.put("Culture‎", 108);
		categoryIds.put("Disciplines‎", 109);
		categoryIds.put("Education‎", 110);
		categoryIds.put("Environment‎", 111);
		categoryIds.put("Geography‎", 112);
		categoryIds.put("Government", 113);
		categoryIds.put("Health‎", 114);
		categoryIds.put("History", 115);
		categoryIds.put("Humanities‎", 116);
		categoryIds.put("Humans‎", 117);
		categoryIds.put("Industry‎", 118);
		categoryIds.put("Information‎", 119);
		categoryIds.put("Knowledge‎", 120);
		categoryIds.put("Language‎", 121);
		categoryIds.put("Law‎", 122);
		categoryIds.put("Life‎", 123);
		categoryIds.put("Mathematics‎", 124);
		categoryIds.put("Matter‎", 125);
		categoryIds.put("Medicine‎", 126);
		categoryIds.put("Mind‎", 127);
		categoryIds.put("Nature", 128);
		categoryIds.put("Objects", 129);
		categoryIds.put("People‎", 130);
		categoryIds.put("Politics", 131);
		categoryIds.put("Science‎", 132);
		categoryIds.put("Society‎", 133);
		categoryIds.put("Sports‎", 134);
		categoryIds.put("Structure‎", 135);
		categoryIds.put("Systems‎", 136);
		categoryIds.put("Technology‎", 137);
		categoryIds.put("Universe‎", 138);
		categoryIds.put("World‎", 139);
		return categoryIds;
	}

	private Grph newTestGraph() {
		Grph g = new InMemoryGrph();

		// main categories
		g.addVertex(101); // Agriculture‎
		g.addVertex(102); // Architecture‎
		g.addVertex(103); // Arts‎
		g.addVertex(104); // Behavior‎
		g.addVertex(105); // Chronology‎
		g.addVertex(106); // Concepts‎

		// some nodes
		g.addVertex(21);
		g.addVertex(22);
		g.addVertex(23);
		g.addVertex(24);
		g.addVertex(25);
		g.addVertex(26);
		g.addVertex(27);
		g.addVertex(28);

		g.addDirectedSimpleEdge(21, 1, 24);
		g.addDirectedSimpleEdge(21, 2, 22);
		g.addDirectedSimpleEdge(24, 15, 25);
		g.addDirectedSimpleEdge(24, 16, 101);
		g.addDirectedSimpleEdge(22, 17, 27);
		g.addDirectedSimpleEdge(22, 3, 24);
		g.addDirectedSimpleEdge(23, 6, 104);
		g.addDirectedSimpleEdge(23, 4, 22);
		g.addDirectedSimpleEdge(25, 14, 26);
		g.addDirectedSimpleEdge(27, 8, 25);
		g.addDirectedSimpleEdge(27, 9, 26);
		g.addDirectedSimpleEdge(27, 11, 103);
		g.addDirectedSimpleEdge(27, 7, 28);
		g.addDirectedSimpleEdge(26, 13, 102);
		g.addDirectedSimpleEdge(26, 10, 28);
		g.addDirectedSimpleEdge(28, 12, 104);
		g.addDirectedSimpleEdge(28, 5, 23);

		assertEquals(14, g.getNumberOfVertices());
		assertEquals(17, g.getNumberOfDirectedEdges());
		return g;
	}

	@Test
	public void disperseProbability_givenTestGraph_expectCorrectProbabilities() throws IOException {
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(newTestGraph(),
						newMaincategoryIdArray());

		double pTotal = 0.0;
		Map<Integer, HashMap<Integer, Double>> estimation = estimator.estimateRelevances(
						new ArrayList<Integer>(Arrays.asList(21)), 50, true);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : estimation.entrySet()) {
			System.out.println("estimations for [" + entry.getKey() + "]:");

			for (Map.Entry<Integer, Double> estimationEntry : entry.getValue().entrySet()) {
				int topCategoryId = estimationEntry.getKey();
				System.out.println("[" + topCategoryId + "=" + mapCategoryIdToName(topCategoryId) + "] estimates to ["
								+ estimationEntry.getValue() + "] ");

				if (!estimationEntry.getValue().isNaN()) {
					pTotal += estimationEntry.getValue();
				}

				switch (topCategoryId) {
				case 101:
					assertEquals(0.375, estimationEntry.getValue(), 0.0001);
					break;
				case 102:
					assertEquals(0.25, estimationEntry.getValue(), 0.0001);
					break;
				case 103:
					assertEquals(0.06641, estimationEntry.getValue(), 0.0001);
					break;
				case 104:
					assertEquals(0.2344, estimationEntry.getValue(), 0.0001);
					break;
				}
			}
		}
		System.out.println("P total [" + pTotal + "]");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void readWriteCachedNodes_expectExactlyRestoredCache() throws IOException, ClassNotFoundException {

		Grph g = newTestGraph();

		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, new int[] { 101, 102, 103,
						104, 105, 106 });

		estimator.estimateRelevances(new ArrayList<Integer>(Arrays.asList(21, 22, 23, 24)), 12, true);

		File temp = File.createTempFile("temp-nodes-cache", ".txt");
		estimator.writeCachedNodes(temp);

		FileInputStream fis = new FileInputStream(temp);
		ObjectInputStream ois = new ObjectInputStream(fis);
		HashMap<Integer, NodeData> readCachedNodes = (HashMap<Integer, NodeData>) ois.readObject();
		ois.close();

		assertEquals(8, readCachedNodes.size());
		assertEquals(readCachedNodes.size(), estimator.cachedNodes.size());

		for (Map.Entry<Integer, NodeData> entry : estimator.cachedNodes.entrySet()) {
			NodeData nData = readCachedNodes.get(entry.getKey());
			assertNotNull(nData);
			assertEquals(entry.getValue(), nData);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void readWriteCachedPaths_expectExactlyRestoredCache() throws IOException, ClassNotFoundException {

		Grph g = newTestGraph();

		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, new int[] { 101, 102, 103,
						104, 105, 106 });

		estimator.estimateRelevances(new ArrayList<Integer>(Arrays.asList(21, 22, 23, 24)), 12, true);

		File temp = File.createTempFile("temp-paths-cache", ".txt");
		estimator.writeCachedPaths(temp);

		FileInputStream fis = new FileInputStream(temp);
		ObjectInputStream ois = new ObjectInputStream(fis);
		HashMap<PathMapKey, KShortestPaths> readCachedPaths = (HashMap<PathMapKey, KShortestPaths>) ois.readObject();
		ois.close();

		assertEquals(24, readCachedPaths.size());
		assertEquals(readCachedPaths.size(), estimator.cachedTopKShortestPaths.size());

		for (Map.Entry<PathMapKey, KShortestPaths> cachedEntry : estimator.cachedTopKShortestPaths.entrySet()) {
			KShortestPaths kShortestPaths = readCachedPaths.get(cachedEntry.getKey());

			System.out.println("cached [" + cachedEntry.getValue().paths + "] reread ["
							+ readCachedPaths.get(cachedEntry.getKey()).paths + "]");
			assertNotNull(kShortestPaths);
			assertEquals(cachedEntry.getValue().paths, kShortestPaths.paths);
		}
	}

	@Test
	public void disperseProbability_doNotConsiderSiblings_expect_correctRelevance() throws IOException {
		Grph g = newTestGraph();
		int[] mainCategories = newMaincategoryIdArray();
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, mainCategories);

		// TODO: multithreaded impl has bug/s
		ArrayList<Integer> startCategories = new ArrayList<Integer>(Arrays.asList(21, 25, 22, 26, 23, 27, 24, 28));
		Map<Integer, HashMap<Integer, Double>> relevances = estimator.estimateRelevancesConcurrent(startCategories, 1,
						4, false);

		// test works for:
		// Map<Integer, HashMap<Integer, Double>> relevances =
		// estimator.estimateRelevancesConcurrent(startCategories, 1, 4, false);

		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : relevances.entrySet()) {
			System.out.println("source [" + entry.getKey() + "] ");
			for (HashMap.Entry<Integer, Double> rel : entry.getValue().entrySet()) {
				System.out.println("relevance to [" + rel.getKey() + "] = [" + rel.getValue() + "]");
			}
		}

		// wrong result: multi threaded
		// assertEquals(0.5, relevances.get(21).get(101));
		// assertEquals(0.125, relevances.get(21).get(102));

		// correct result: single threaded
		assertEquals(1.0 / 4.0, relevances.get(21).get(101));
		assertEquals((1.0 / 16.0) / 2.0, relevances.get(21).get(102));

		assertEquals(0.0625, relevances.get(21).get(103));
		assertEquals(0.03125, relevances.get(21).get(104));
		assertEquals(Double.NaN, relevances.get(21).get(105));
		assertEquals(Double.NaN, relevances.get(21).get(106));

		assertEquals(0.25, relevances.get(22).get(101));
		assertEquals(0.0625, relevances.get(22).get(102));
		assertEquals(0.125, relevances.get(22).get(103));
		assertEquals(0.0625, relevances.get(22).get(104));
		assertEquals(Double.NaN, relevances.get(22).get(105));
		assertEquals(Double.NaN, relevances.get(22).get(106));

		assertEquals(0.125, relevances.get(23).get(101));
		assertEquals(0.03125, relevances.get(23).get(102));
		assertEquals(0.0625, relevances.get(23).get(103));
		assertEquals(0.5, relevances.get(23).get(104));
		assertEquals(Double.NaN, relevances.get(23).get(105));
		assertEquals(Double.NaN, relevances.get(23).get(106));

		assertEquals(0.5, relevances.get(24).get(101));
		assertEquals(0.25, relevances.get(24).get(102));
		assertEquals(0.0078125, relevances.get(24).get(103));
		assertEquals(0.125, relevances.get(24).get(104));
		assertEquals(Double.NaN, relevances.get(24).get(105));
		assertEquals(Double.NaN, relevances.get(24).get(106));
	}

}
