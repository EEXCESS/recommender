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

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import grph.Grph;
import grph.path.ArrayListPath;
import grph.properties.NumericalProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Estimate the relevance of a sub category c being related to all categories m
 * ∈ M of the given set of main categories M.
 * 
 * @author Raoul Rubien
 *
 */
public class MainCategoryRelevanceEstimator {

	public static class NodeData {
		int nodeId = -1;
		int numOutgoingEdges = -1;
	}

	public static String[] MAIN_CATEGORIES = { "Agriculture‎", "Architecture‎", "Arts‎", "Behavior‎", "Chronology‎",
					"Concepts‎", "Creativity‎", "Culture‎", "Disciplines‎", "Education‎", "Environment‎", "Geography‎",
					"Government", "Health‎", "History", "Humanities‎", "Humans‎", "Industry‎", "Information‎",
					"Knowledge‎", "Language‎", "Law‎", "Life‎", "Mathematics‎", "Matter‎", "Medicine‎", "Mind‎",
					"Nature", "Objects", "People‎", "Politics", "Science‎", "Society‎", "Sports‎", "Structure‎",
					"Systems‎", "Technology‎", "Universe‎", "World‎" };

	private Grph graph;
	private HashMap<String, Integer> categoryIds;
	private String[] topCategoryLabels;
	int[] topCategories;
	int topKShortestPathes;
	private HashMap<Integer, NodeData> cachedNodes;

	public MainCategoryRelevanceEstimator(Grph completeCategoryGraph, HashMap<String, Integer> categoryIdDictionary,
					String[] mainCategories, int topKShortestPathes) {
		this.graph = completeCategoryGraph;
		this.categoryIds = categoryIdDictionary;
		this.topCategoryLabels = mainCategories;
		this.topCategories = new int[topCategoryLabels.length];

		int idx = 0;
		for (String category : topCategoryLabels) {
			topCategories[idx++] = categoryIds.get(category).intValue();
		}

		this.topKShortestPathes = topKShortestPathes;
		this.cachedNodes = new HashMap<>();
	}

	public Map<Integer, HashMap<String, Double>> estimateProbabilities(int[] startCategories) {
		return estimateProbabilities(startCategories, topKShortestPathes);
	}

	public Map<Integer, HashMap<String, Double>> estimateProbabilities(int[] startCategories, int topKShortestPathes) {
		this.topKShortestPathes = topKShortestPathes;
		HashMap<Integer, HashMap<String, Double>> results = new HashMap<>();

		// get top k shortest paths
		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = yenTopKShortestPaths(graph, startCategories,
						topCategories, this.topKShortestPathes, null);

		// disperse probability over paths
		int sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			System.out.println("estimate for source id [" + startCategories[sourceIdx] + "]");

			HashMap<String, Double> probs = disperseProbabilityOverPathes(startCategories.length,
							startCategories[sourceIdx], sourceToTargetsPathes);

			// bring logarithmic probabilities back to decimal
			for (Map.Entry<String, Double> entry : probs.entrySet()) {
				entry.setValue(Math.pow(Math.E, entry.getValue()));
			}
			int sourceId = startCategories[sourceIdx];
			results.put(sourceId, probs);
			sourceIdx++;
		}

		return results;
	}

	private HashMap<String, Double> disperseProbabilityOverPathes(int numTotalStartCategories, int sourceCategoryId,
					List<List<ArrayListPath>> sourceToTargetsPaths) {

		HashMap<String, Double> maintopicsProbability = new HashMap<>(topCategories.length);
		for (String category : topCategoryLabels) {
			maintopicsProbability.put(category, Double.NaN);
		}

		double categoryProbability = Math.log(1) - Math.log(numTotalStartCategories);
		double startPathProbability = categoryProbability - Math.log(getNodeData(sourceCategoryId).numOutgoingEdges);
		System.out.println("at id [" + sourceCategoryId + "] with total categories of [" + numTotalStartCategories
						+ "] and outgoing notes of [" + getNodeData(sourceCategoryId).numOutgoingEdges + "] P(c)=["
						+ categoryProbability + "] p_start=[" + startPathProbability + "]");

		int targetIdx = 0;
		for (List<ArrayListPath> targetPaths : sourceToTargetsPaths) {
			for (ArrayListPath targetPath : targetPaths) {
				double pathProbability = startPathProbability;
				System.out.println("new path starts at id [" + sourceCategoryId + "] with p [" + pathProbability + "]");

				// distribute probability over path
				int[] foo = targetPath.toVertexArray();
				for (int idx = 0; idx < (foo.length - 1); idx++) {
					Integer categoryId = foo[idx];
					pathProbability = pathProbability - Math.log(getNodeData(categoryId).numOutgoingEdges);
					System.out.println("at id [" + categoryId + "] p [" + pathProbability + "]");
				}

				// update main topic probability with currently calculated
				String mainTopic = topCategoryLabels[targetIdx];
				Double previousProbability = maintopicsProbability.get(mainTopic);
				if (previousProbability.isNaN()) {
					maintopicsProbability.put(mainTopic, pathProbability);
					System.out.println("topic p was [" + previousProbability + "] is now [" + pathProbability
									+ "] store it to id [" + mainTopic + "]");
				} else {
					maintopicsProbability.put(mainTopic, previousProbability + pathProbability);
					System.out.println("topic p was [" + previousProbability + "] is now ["
									+ (previousProbability + pathProbability) + "]");
				}
			}
			targetIdx++;
		}

		return maintopicsProbability;
	}

	private NodeData getNodeData(Integer nodeId) {
		NodeData nd = cachedNodes.get(nodeId);
		if (null == nd) {
			nd = new NodeData();
			nd.nodeId = nodeId.intValue();
			nd.numOutgoingEdges = graph.getOutEdges(nd.nodeId).size();
			cachedNodes.put(nodeId, nd);
		}
		return nd;
	}

	List<List<List<ArrayListPath>>> yenTopKShortestPaths(Grph g, int[] sources, int[] targets, int topK,
					NumericalProperty weights) {

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

		List<List<List<ArrayListPath>>> sourcesTotargetsPaths = new ArrayList<List<List<ArrayListPath>>>();

		for (int source : sources) {
			List<List<ArrayListPath>> sourceTargetsPaths = new ArrayList<List<ArrayListPath>>();
			for (int target : targets) {

				// TODO: does get_shortest_paths() perform its work read only on
				// the graph h?
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
				sourceTargetsPaths.add(grphPaths);
			}
			sourcesTotargetsPaths.add(sourceTargetsPaths);
		}
		return sourcesTotargetsPaths;
	}
}
