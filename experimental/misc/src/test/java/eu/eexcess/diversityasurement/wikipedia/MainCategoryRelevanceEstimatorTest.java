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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.ArrayListPath;

import java.util.ArrayList;
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
	public void yenTopKShortestPaths_testModification_expectCorrectPathes() {
		Grph g = newTestGraph();
		HashMap<String, Integer> categoryIds = newMainCategoryIdDictionary();

		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(g, categoryIds,
						newMaincategoryIdArray(), 12);

		int startCategories[] = new int[] { 21, 22, 23, 24 };
		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = estimator.yenTopKShortestPaths(g, startCategories,
						estimator.topCategories, estimator.topKShortestPathes, null);

		int sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			int sourceId = startCategories[sourceIdx++];
			int targetIdx = 0;
			for (List<ArrayListPath> sourceToTargetPathes : sourceToTargetsPathes) {
				int targetId = estimator.topCategories[targetIdx++];
				assertPathes(sourceId, targetId, sourceToTargetPathes);
			}
		}
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
	public void disperseProbability_givenTestGraph_expectCorrectProbabilities() {
		MainCategoryRelevanceEstimator estimator = new MainCategoryRelevanceEstimator(newTestGraph(),
						newMainCategoryIdDictionary(), newMaincategoryIdArray(), 50);

		double pTotal = 0.0;
		Map<Integer, HashMap<Integer, Double>> estimation = estimator.estimateProbabilities(new int[] { 21 });

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
}
