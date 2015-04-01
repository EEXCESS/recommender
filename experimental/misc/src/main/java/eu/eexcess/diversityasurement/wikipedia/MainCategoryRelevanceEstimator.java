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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Estimate the relevance of a sub category c being related to all categories m
 * ∈ M of the given set of main categories M.
 * 
 * @author Raoul Rubien
 *
 */
public class MainCategoryRelevanceEstimator {

	public static class Statistics {
		public int cacheHits = 0;
		public int cacheEntries = 0;

		@Override
		public String toString() {
			return "cache hits [" + cacheHits + "] cache total entries [" + cacheEntries + "]";
		}
	}

	public static class NodeData {
		public int nodeId = -1;
		public int numOutgoingEdges = -1;
	}

	public static class KShortestPaths {
		public int k = -1;
		List<ArrayListPath> paths;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + k;
			result = prime * result + ((paths == null) ? 0 : paths.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KShortestPaths other = (KShortestPaths) obj;
			if (k != other.k)
				return false;
			if (paths == null) {
				if (other.paths != null)
					return false;
			} else if (!paths.equals(other.paths))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "k [" + k + "] paths [" + paths + "]";
		}
	}

	public static class PathMapKey {
		public int source = -1;
		public int target = -1;

		public PathMapKey(int source, int target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + source;
			result = prime * result + target;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PathMapKey other = (PathMapKey) obj;
			if (source != other.source)
				return false;
			if (target != other.target)
				return false;
			return true;
		}
	}

	private static class Worker implements Runnable {

		private List<Integer> startCategories;
		private int topK;
		private MainCategoryRelevanceEstimator relevanceEstimator;
		static Map<Integer, List<List<ArrayListPath>>> topKShortestPaths = Collections
						.synchronizedMap(new HashMap<Integer, List<List<ArrayListPath>>>());
		public static int numCategoriesToCalculateBundled = 5;

		public Worker(List<Integer> startCategories, int topK, MainCategoryRelevanceEstimator estimator) {
			System.out.println("new worker constructed @ remaining #categories [" + startCategories.size() + "]");
			this.topK = topK;
			this.relevanceEstimator = estimator;
			this.startCategories = startCategories;
		}

		@Override
		public void run() {
			while (!startCategories.isEmpty()) {
				// get top k shortest paths

				System.out.println("get top k shortest paths");

				ArrayList<Integer> fetchedCategories = new ArrayList<>();
				int taken = 0;
				while (!startCategories.isEmpty() && taken < numCategoriesToCalculateBundled) {
					fetchedCategories.add(startCategories.remove(0));
					taken++;
				}

				if (fetchedCategories.size() <= 0) {
					return;
				}
				System.out.println("took [" + fetchedCategories.size() + "] categories, remaining ["
								+ startCategories.size() + "]");
				int idx = 0;
				int[] sources = new int[fetchedCategories.size()];
				for (Integer id : fetchedCategories) {
					sources[idx++] = id;
				}

				List<List<List<ArrayListPath>>> sourcesToTargetsPathes = relevanceEstimator.yenTopKShortestPaths(
								sources, topK);

				System.out.println("collect results to map with arbitrary order");
				// collect results to map with arbitrary order
				idx = 0;
				for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
					topKShortestPaths.put(sources[idx++], sourceToTargetsPathes);
				}
			}
		}
	}

	private final Grph graph;
	private final int[] topCategories;
	private HashMap<Integer, NodeData> cachedNodes;
	private HashMap<PathMapKey, KShortestPaths> cachedTopKShortestPaths;
	private Statistics statistics;

	public MainCategoryRelevanceEstimator(Grph completeCategoryGraph, int[] mainCategories) {
		this.graph = completeCategoryGraph;
		this.topCategories = mainCategories;
		clear();
		clearCache();
	}

	/**
	 * clears state dependent but no cache resources
	 */
	public void clear() {
		statistics = new Statistics();
	}

	/**
	 * clear cache resources
	 */
	public void clearCache() {
		cachedNodes = new HashMap<>();
		cachedTopKShortestPaths = new HashMap<>();
	}

	public Statistics getStatistics() {
		statistics.cacheEntries = cachedTopKShortestPaths.size();
		return statistics;
	}

	/**
	 * Estimates the relevance of categories c ∈ startCategories to all m ∈
	 * mainCategories {@link #MainCategoryRelevanceEstimator(Grph, int[], int)}.
	 * Each c gets the probability 1.0/|startCategories|. This probability is
	 * spread over each of the topKShortestPahtes to all mainCategories.
	 * Probability dispersion is performed on each node where p is current
	 * probability and p' is the new probability as: p' = p /
	 * node.numberOfOutgoingEdges().
	 * 
	 * @param startCategories
	 *            categories to find topKShortests paths to all topCategories
	 * @param topKShortestPathes
	 *            number of max shortest paths to consider
	 * @return a map entry for each c containing a map of topCategories and
	 *         their relevance
	 */
	public Map<Integer, HashMap<Integer, Double>> estimateRelevances(int[] startCategories, int topKShortestPathes) {
		// get top k shortest paths
		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = yenTopKShortestPaths(startCategories,
						topKShortestPathes);
		return calculateProbabilites(startCategories, sourcesToTargetsPathes);
	}

	/**
	 * concurrent implementation of {@link #estimateRelevances(int[], int)}
	 * 
	 * @param startCategories
	 *            see {@link #estimateRelevances(int[], int)}
	 * @param topKShortestPathes
	 *            see {@link #estimateRelevances(int[], int)}
	 * @param numTotalThreads
	 *            must be greater than numRunningThreadsAtOnce
	 * @param numRunningThreadsAtOnce
	 *            must be less than numTotalThreads
	 * @return
	 */
	public Map<Integer, HashMap<Integer, Double>> estimateRelevancesConcurrent(int[] startCategories,
					int topKShortestPathes, int numTotalThreads, int numRunningThreadsAtOnce) {

		List<Integer> toBeProcessed = Collections.synchronizedList(new ArrayList<Integer>());
		for (int startCategory : startCategories) {
			toBeProcessed.add(startCategory);
		}

		ExecutorService executor = Executors.newFixedThreadPool(numRunningThreadsAtOnce);
		for (int i = 0; i < numTotalThreads; i++) {
			Runnable worker = new Worker(toBeProcessed, topKShortestPathes, this);
			executor.execute(worker);
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				executor.awaitTermination(7, TimeUnit.DAYS);
			} catch (InterruptedException e) {
			}
		}

		// order results that are arbitrarily ordered
		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = new ArrayList<>();
		for (int sourceIdx = 0; sourceIdx < startCategories.length; sourceIdx++) {
			sourcesToTargetsPathes.add(Worker.topKShortestPaths.get(startCategories[sourceIdx]));
		}

		return calculateProbabilites(startCategories, sourcesToTargetsPathes);
	}

	public void setNumCategoriesToCalculateBundled(int num) {
		Worker.numCategoriesToCalculateBundled = num;
	}

	private HashMap<Integer, HashMap<Integer, Double>> calculateProbabilites(int[] startCategories,
					List<List<List<ArrayListPath>>> sourcesToTargetsPathes) {
		HashMap<Integer, HashMap<Integer, Double>> results = new HashMap<>();
		// disperse probability over paths
		int sourceIdx = 0;
		for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
			System.out.println("estimate for source id [" + startCategories[sourceIdx] + "]");

			HashMap<Integer, Double> probs = disperseProbabilityOverPathes(startCategories.length,
							startCategories[sourceIdx], sourceToTargetsPathes);

			int sourceId = startCategories[sourceIdx];
			results.put(sourceId, probs);
			sourceIdx++;
		}
		return results;
	}

	/**
	 * Disperse probability 1.0 over {@code numTotalStartCategories} ∀ k paths p
	 * ∈ {@code sourceToTargetPaths} (∀ p from {@code sourceCategoryId} to ∀
	 * mainmainCategories).
	 * 
	 * @param numTotalStartCategories
	 *            number of total start categories to consider
	 * @param sourceCategoryId
	 *            from vertex id
	 * @param sourceToTargetsPaths
	 *            to vertices id
	 * @return mapping from vertex id to probability
	 */
	private HashMap<Integer, Double> disperseProbabilityOverPathes(int numTotalStartCategories, int sourceCategoryId,
					List<List<ArrayListPath>> sourceToTargetsPaths) {

		HashMap<Integer, Double> maintopicsProbability = new HashMap<>(topCategories.length);
		for (Integer category : topCategories) {
			maintopicsProbability.put(category, Double.NaN);
		}

		double categoryProbability = 1.0 / (double) numTotalStartCategories;
		// System.out.println("at id [" + sourceCategoryId +
		// "] with total categories of [" + numTotalStartCategories
		// + "] and outgoing notes of [" +
		// getNodeData(sourceCategoryId).numOutgoingEdges + "] P(c)=["
		// + categoryProbability + "] p_start=[" + "startPathProbability" +
		// "]");

		int targetIdx = 0;
		for (List<ArrayListPath> targetPaths : sourceToTargetsPaths) {
			for (ArrayListPath targetPath : targetPaths) {
				double pathProbability = categoryProbability;
				// System.out.println("new path starts at id [" +
				// sourceCategoryId + "] with p [" + pathProbability + "]");

				// distribute probability over path
				int[] foo = targetPath.toVertexArray();
				for (int idx = 0; idx < (foo.length - 1); idx++) {
					Integer categoryId = foo[idx];
					pathProbability = pathProbability / (double) (getNodeData(categoryId).numOutgoingEdges);
					// System.out.println("at id [" + categoryId + "] p [" +
					// pathProbability + "]");
				}

				// update main topic probability with currently calculated
				int mainTopic = topCategories[targetIdx];
				Double previousProbability = maintopicsProbability.get(mainTopic);
				if (previousProbability.isNaN()) {
					maintopicsProbability.put(mainTopic, pathProbability);
					// System.out.println("topic p was [" + previousProbability
					// + "] is now [" + pathProbability
					// + "] store it to id [" + mainTopic + "]");
				} else {
					maintopicsProbability.put(mainTopic, previousProbability + pathProbability);
					// System.out.println("topic p was [" + previousProbability
					// + "] is now ["
					// + maintopicsProbability.get(mainTopic) +
					// "] store it to id [" + mainTopic + "]");
				}
			}
			targetIdx++;
		}

		return maintopicsProbability;
	}

	synchronized private NodeData getNodeData(Integer nodeId) {
		NodeData nd = cachedNodes.get(nodeId);
		if (null == nd) {
			nd = new NodeData();
			nd.nodeId = nodeId.intValue();
			nd.numOutgoingEdges = graph.getOutEdges(nd.nodeId).size();
			cachedNodes.put(nodeId, nd);
		}
		return nd;
	}

	List<List<List<ArrayListPath>>> yenTopKShortestPaths(int[] sources, int topKShortestPathes) {

		Grph g = graph.clone();

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

		long totalstartTimestamp = System.currentTimeMillis();
		for (int source : sources) {
			List<List<ArrayListPath>> sourceTargetsPaths = new ArrayList<List<ArrayListPath>>();
			for (int target : topCategories) {

				// TODO: does get_shortest_paths() perform its work read only on
				// the graph h?
				long startTimestamp = System.currentTimeMillis();
//				System.out.println("started get kshortest for [" + source + "] to target [" + target + "] k ["
//								+ topKShortestPathes + "]");

				List<ArrayListPath> grphPaths = getCachedOrCalculate(h, alg, source, target, topKShortestPathes);
				System.out.println("done in [" + (System.currentTimeMillis() - startTimestamp)
								+ "]ms get kshortest for [" + source + "] to target [" + target + "] k ["
								+ topKShortestPathes + "]");

				sourceTargetsPaths.add(grphPaths);
			}
			sourcesTotargetsPaths.add(sourceTargetsPaths);
		}
		System.out.println("total duration of yenTopKShortestPaths with [" + sources.length + "] start nodes to ["
						+ topCategories.length + "] targets with k [" + topKShortestPathes + "]: ["
						+ (System.currentTimeMillis() - totalstartTimestamp) + "]ms ");
		return sourcesTotargetsPaths;
	}

	private List<ArrayListPath> getCachedOrCalculate(Graph h, YenTopKShortestPathsAlg alg, int source, int target,
					int topK) {
		PathMapKey pathOfInterest = new PathMapKey(source, target);
		List<ArrayListPath> paths = null;

		synchronized (this) {
			paths = copyOfCachedPaths(topK, pathOfInterest);
			if (null != paths) {
				statistics.cacheHits++;
				return paths;
			}
		}

		// O(get_shortest_paths(h,source,target, K)) = O(K * h.nodes
		// (m+n*log(n)))
		KShortestPaths kPaths = new KShortestPaths();
		kPaths.k = topK;
		kPaths.paths = getShortestKPaths(h, alg, source, target, topK);

		synchronized (this) {
			KShortestPaths doubleChecedKPaths = cachedTopKShortestPaths.get(pathOfInterest);
			if (null != doubleChecedKPaths) {
				if (kPaths.k < doubleChecedKPaths.k) {
					statistics.cacheHits++;
					return copyOfShortestPaths(kPaths.paths.size(), doubleChecedKPaths);
				}
			}
			cachedTopKShortestPaths.put(pathOfInterest, kPaths);
			statistics.cacheEntries++;
			return copyOfCachedPaths(topK, pathOfInterest);
		}
	}

	private List<ArrayListPath> copyOfCachedPaths(int topK, PathMapKey pathOfInterest) {

		KShortestPaths cachedKPaths = cachedTopKShortestPaths.get(pathOfInterest);
		if (null != cachedKPaths) {
			// cached paths can only be used if topK <= kPaths.k
			if (topK <= cachedKPaths.k) {
				return copyOfShortestPaths(topK, cachedKPaths);
			}
		}
		return null;
	}

	/**
	 * copies the shortest topK paths from kPaths
	 * 
	 * @param topK
	 *            number of paths to copy
	 * @param paths
	 *            where to copy paths from
	 */
	private List<ArrayListPath> copyOfShortestPaths(int topK, KShortestPaths kPaths) {
		List<ArrayListPath> pathList = new ArrayList<>();
		for (int idx = 0; idx < topK && idx < kPaths.paths.size(); idx++) {
			ArrayListPath pathCopy = new ArrayListPath();
			for (int vertex : kPaths.paths.get(idx).toVertexArray()) {
				pathCopy.extend(vertex);
			}
			pathList.add(pathCopy);
		}
		return pathList;
	}

	private List<ArrayListPath> getShortestKPaths(Graph h, YenTopKShortestPathsAlg alg, int source, int target, int topK) {
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
		return grphPaths;
	}
}
