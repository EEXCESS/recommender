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

import eu.eexcess.logger.PianoLogger;
import grph.Grph;
import grph.path.ArrayListPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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

	public static class NodeData implements Serializable {
		private static final long serialVersionUID = 1879632141881061251L;
		public int nodeId = -1;
		public int numOutgoingEdges = -1;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + nodeId;
			result = prime * result + numOutgoingEdges;
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
			NodeData other = (NodeData) obj;
			if (nodeId != other.nodeId)
				return false;
			if (numOutgoingEdges != other.numOutgoingEdges)
				return false;
			return true;
		}

	}

	public static class SerializableArrayListPath extends ArrayListPath implements Serializable {
		private static final long serialVersionUID = 3977234866306205056L;

		private void writeObject(ObjectOutputStream out) throws IOException {
			ArrayList<Integer> verticesList = new ArrayList<Integer>();
			for (Integer v : toVertexArray()) {
				verticesList.add(v);
			}
			out.writeObject(verticesList);
		}

		@SuppressWarnings("unchecked")
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			for (int v : (ArrayList<Integer>) in.readObject()) {
				extend(v);
			}
		}
	}

	public static class KShortestPaths implements Serializable {
		private static final long serialVersionUID = -1993436599702546472L;
		public int k = -1;
		List<SerializableArrayListPath> paths;

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

	public static class PathMapKey implements Serializable {
		private static final long serialVersionUID = 7377946546508470786L;
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

		private static Logger logger = PianoLogger.getLogger(Worker.class);
		private ArrayList<Integer> startCategories;
		private int topK;
		private MainCategoryRelevanceEstimator relevanceEstimator;
		static Map<Integer, List<List<ArrayListPath>>> topKShortestPaths = new HashMap<Integer, List<List<ArrayListPath>>>();
		public static int numCategoriesToCalculateBundled = 5;

		private static Object sourceLock = new Object();
		public static Object sinkLock = new Object();

		public Worker(ArrayList<Integer> startCategories, int topK, MainCategoryRelevanceEstimator estimator) {
			logger.info("new worker constructed @ remaining #categories [" + startCategories.size() + "]");
			this.topK = topK;
			this.relevanceEstimator = estimator;
			this.startCategories = startCategories;
		}

		@Override
		public void run() {
			while (!startCategories.isEmpty()) {

				ArrayList<Integer> fetchedCategories = new ArrayList<>();
				StringBuilder fetchedString = new StringBuilder();
				synchronized (sourceLock) {
					int taken = 0;
					while (!startCategories.isEmpty() && taken < numCategoriesToCalculateBundled) {
						Integer category = startCategories.remove(0);
						fetchedCategories.add(category);
						fetchedString.append("" + category + " ");
						taken++;
					}
				}

				if (fetchedCategories.size() <= 0) {
					break;
				}

				logger.info("[" + Thread.currentThread().getName() + "] took [" + fetchedCategories.size()
								+ "] categories: [ " + fetchedString + "], remaining [" + startCategories.size() + "]");

				List<List<List<ArrayListPath>>> sourcesToTargetsPathes = relevanceEstimator.yenTopKShortestPaths(
								fetchedCategories, topK);

				// collect results to map with arbitrary order
				synchronized (sinkLock) {
					int idx = 0;
					logger.info("collect results to map with arbitrary order");
					for (List<List<ArrayListPath>> sourceToTargetsPathes : sourcesToTargetsPathes) {
						topKShortestPaths.put(fetchedCategories.get(idx++), sourceToTargetsPathes);
					}
				}
			}
			logger.info("thread [" + Thread.currentThread().getName() + "] exited: no work scheduled");
		}
	}

	private Logger logger = PianoLogger.getLogger(MainCategoryRelevanceEstimator.class);
	private final Grph graph;
	private final int[] topCategories;
	// private File outLuceneIndexDirectory = new
	// File("/opt/iaselect/results/category-relevance-index");
	// private IndexWriter indexWriter;
	HashMap<Integer, NodeData> cachedNodes;
	HashMap<PathMapKey, KShortestPaths> cachedTopKShortestPaths;
	private int numberOfKClosestNeighbors = 0;
	private Statistics statistics;

	public MainCategoryRelevanceEstimator(Grph completeCategoryGraph, int[] mainCategories) throws IOException {
		this.graph = completeCategoryGraph;
		this.topCategories = mainCategories;
		clear();
		clearCache();
		// openOutIndex();
	}

	/**
	 * use k closest neighbors for relevance estimation
	 * 
	 * @param useKClosestNeighbors
	 *            > 0 if {@link #estimateRelevances(int[], int)} is to be
	 *            performed on a subgraph with source node centered instead of
	 *            the whole
	 */
	public void setKClosestNeighborsSubgraph(int useKClosestNeighbors) {
		numberOfKClosestNeighbors = useKClosestNeighbors;
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
	 * calculate the relevance of categories c ∈ startCategories to all m ∈
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
	 * @param distributeOverSiblingCategories
	 *            {@code #disperseProbabilityOverPathes(int, List, boolean)}
	 */
	public Map<Integer, HashMap<Integer, Double>> estimateRelevances(ArrayList<Integer> startCategories,
					int topKShortestPathes, boolean distributeOverSiblingCategories) {

		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = yenTopKShortestPaths(startCategories,
						topKShortestPathes);

		HashMap<Integer, HashMap<Integer, Double>> relevances = calculateProbabilites(startCategories,
						sourcesToTargetsPathes, distributeOverSiblingCategories);

		// writeRelevancesToIndex(relevances);

		return relevances;
	}

	/**
	 * concurrent implementation of {@link #estimateRelevances(int[], int)}
	 * 
	 * @param startCategories
	 *            see {@link #estimateRelevances(int[], int)}
	 * @param topKShortestPathes
	 *            see {@link #estimateRelevances(int[], int)}
	 * @param numTotalThreads
	 *            if > 1 runs multiple threads, else calls
	 *            {@link #estimateRelevances(ArrayList, int, boolean)}
	 * @param numRunningThreadsAtOnce
	 *            must be less than numTotalThreads
	 * @param distributeOverSiblingCategories
	 *            {@code #calculateProbabilites(int[], List, boolean)}
	 * @return
	 */
	public Map<Integer, HashMap<Integer, Double>> estimateRelevancesConcurrent(ArrayList<Integer> startCategories,
					int topKShortestPathes, int numTotalThreads, boolean distributeOverSiblingCategories) {

		if (numTotalThreads <= 1) {
			return estimateRelevances(startCategories, topKShortestPathes, distributeOverSiblingCategories);
		}

		ArrayList<Integer> toBeConsumed = new ArrayList<Integer>();
		for (int startCategory : startCategories) {
			toBeConsumed.add(startCategory);
		}

		ExecutorService executor = Executors.newFixedThreadPool(numTotalThreads);
		for (int i = 0; i < numTotalThreads; i++) {
			Runnable worker = new Worker(toBeConsumed, topKShortestPathes, this);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				executor.awaitTermination(7, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				logger.severe("failed to wait for all threads finished " + e.getMessage());
			}
		}

		// order results as they are not ordered
		List<List<List<ArrayListPath>>> sourcesToTargetsPathes = new ArrayList<>();
		for (int sourceIdx = 0; sourceIdx < startCategories.size(); sourceIdx++) {
			sourcesToTargetsPathes.add(Worker.topKShortestPaths.get(startCategories.get(sourceIdx)));
		}

		logger.info("probabilities for [" + startCategories.size() + "] nodes ready");
		HashMap<Integer, HashMap<Integer, Double>> relevances = calculateProbabilites(startCategories,
						sourcesToTargetsPathes, distributeOverSiblingCategories);
		// writeRelevancesToIndex(relevances);

		return relevances;
	}

	// private void writeRelevancesToIndex(HashMap<Integer, HashMap<Integer,
	// Double>> relevances) {
	//
	// for (HashMap.Entry<Integer, HashMap<Integer, Double>> entry :
	// relevances.entrySet()) {
	// Integer sourceCategoryId = entry.getKey();
	// for (HashMap.Entry<Integer, Double> topCategoryRelevanceEntry :
	// entry.getValue().entrySet()) {
	// Integer topCategoryId = topCategoryRelevanceEntry.getKey();
	// Double topCategoryRelevance = topCategoryRelevanceEntry.getValue();
	//
	// if (null != sourceCategoryId) {
	// if (null != topCategoryRelevance && !Double.isNaN(topCategoryRelevance))
	// {
	// Document doc = new Document();
	// doc.add(new StringField("categoryId", sourceCategoryId.toString(),
	// Field.Store.YES));
	//
	// if (null != topCategoryId) {
	// doc.add(new StringField("topCategoryId", topCategoryId.toString(),
	// Field.Store.YES));
	// }
	//
	// doc.add(new StringField("topCategoryRelevance",
	// topCategoryRelevance.toString(),
	// Field.Store.YES));
	// try {
	// indexWriter.addDocument(doc);
	// } catch (IOException e) {
	// logger.severe("failed storing document to index: " + e.getMessage());
	// }
	// }
	// } else {
	// logger.severe("failed reading relevance details");
	// }
	// }
	// }
	//
	// }

	/**
	 * Number of category relevances to calculate in a thread sequentially. Only
	 * considered when calculation is multithreaded.
	 * 
	 * @param num
	 */
	public static void setNumCategoriesToCalculateBundled(int num) {
		Worker.numCategoriesToCalculateBundled = num;
	}

	/**
	 * Disperses the probability 1.0 over all start categories over all their
	 * k-paths all n-top categories.
	 * 
	 * @param startCategories
	 *            all start categories
	 * @param sourcesToTargetsPaths
	 *            for each start category a list of all k-paths from start
	 *            categories to each n-top categories; in total k*n paths for
	 *            each source
	 * @param distributeOverSiblingCategories
	 *            {@code #disperseProbabilityOverPathes(int, List, boolean)}
	 * @return
	 */
	private HashMap<Integer, HashMap<Integer, Double>> calculateProbabilites(ArrayList<Integer> startCategories,
					List<List<List<ArrayListPath>>> sourcesToTargetsPaths, boolean distributeOverSiblingCategories) {
		HashMap<Integer, HashMap<Integer, Double>> results = new HashMap<>();
		int sourceIdx = 0;
		// for each source get all paths
		for (List<List<ArrayListPath>> sourceToTargetsPaths : sourcesToTargetsPaths) {
			// distribute probabilities over all paths of one source
			HashMap<Integer, Double> probs = disperseProbabilityOverPathes(startCategories.size(),
							sourceToTargetsPaths, distributeOverSiblingCategories);

			int sourceId = startCategories.get(sourceIdx);
			results.put(sourceId, probs);
			sourceIdx++;
		}
		return results;
	}

	/**
	 * if (true == distributeOverSiblingCategories): Disperse probability 1.0
	 * over {@code numTotalStartCategories} ∀ k paths p ∈
	 * {@code sourceToTargetPaths} (∀ p from {@code sourceCategoryId}
	 * <P>
	 * else: Disperse probability 1.0 over all k paths p ∈
	 * {@code sourceToTargetPaths} (∀ p from {@code sourceCategoryId} to ∀
	 * mainmainCategories).
	 * 
	 * @param numTotalStartCategories
	 *            number of total start categories to consider
	 * @param sourceToTargetsPaths
	 *            to vertices id
	 * @param distributeOverSiblingCategories
	 *            whether to divide start distribution over all siblings or not
	 * @return mapping from vertex id to probability
	 */
	private HashMap<Integer, Double> disperseProbabilityOverPathes(int numTotalStartCategories,
					List<List<ArrayListPath>> sourceToTargetsPaths, boolean distributeOverSiblingCategories) {

		// initialize result
		HashMap<Integer, Double> maintopicsProbability = new HashMap<>(topCategories.length);
		for (Integer category : topCategories) {
			maintopicsProbability.put(category, Double.NaN);
		}
		// probability at path start
		double categoryProbability = 1.0;

		if (true == distributeOverSiblingCategories) {
			// probability at path start: distributed over all start nodes
			categoryProbability = 1.0 / (double) numTotalStartCategories;
		}

		// System.out.println("[" + numTotalStartCategories +
		// "] total siblings");
		// for each target distribute remaining probability over all paths to
		// the target
		int targetIdx = 0;
		for (List<ArrayListPath> targetPaths : sourceToTargetsPaths) {

			// distribute remaining probability over all paths
			for (ArrayListPath targetPath : targetPaths) {
				double pathProbability = categoryProbability;
				int[] vertexArray = targetPath.toVertexArray();
				// System.out.println("p[" + categoryProbability + "] path [" +
				// targetPath + "]");
				// distribute remaining probability over current path
				for (int idx = 0; idx < (vertexArray.length - 1); idx++) {
					Integer categoryId = vertexArray[idx];
					pathProbability = pathProbability / (double) (getNodeData(categoryId).numOutgoingEdges);
					// System.out.println("p[" + pathProbability + "] @id[" +
					// categoryId + "]");
				}

				// update top category probability with currently calculated
				int mainTopic = topCategories[targetIdx];
				Double previousProbability = maintopicsProbability.get(mainTopic);
				if (previousProbability.isNaN()) {
					maintopicsProbability.put(mainTopic, pathProbability);
					// System.out.println("new probability [" + pathProbability
					// + "]");
				} else {
					maintopicsProbability.put(mainTopic, previousProbability + pathProbability);
					// System.out.println("probability [" + previousProbability
					// + pathProbability + "] was ["
					// + previousProbability + pathProbability + "]");
				}
			}
			targetIdx++;
		}

		return maintopicsProbability;
	}

	/**
	 * convert form Grph to Graph
	 * 
	 * @param g
	 * @return
	 */
	// private Graph fromGrph(Grph g) {
	//
	// Graph h = new Graph();
	//
	// for (int v : g.getVertices().toIntArray()) {
	// h.addVertex(v);
	// }
	//
	// for (int e : g.getEdges().toIntArray()) {
	// int a = g.getOneVertex(e);
	// int b = g.getTheOtherVertex(e, a);
	// h.add_edge(a, b, 1);
	// }
	//
	// return h;
	// }

	private NodeData getNodeData(Integer nodeId) {
		NodeData nd = null;
		synchronized (cachedNodes) {
			nd = cachedNodes.get(nodeId);
			if (null == nd) {
				nd = new NodeData();
				nd.nodeId = nodeId.intValue();
				nd.numOutgoingEdges = graph.getOutEdges(nd.nodeId).size();
				cachedNodes.put(nodeId, nd);
			}
		}
		return nd;
	}

	/**
	 * calculate the k-shortest paths from sources to all top categories in
	 * {@code #topCategories}
	 * 
	 * @param sources
	 *            sources where to start calculation
	 * @param topKShortestPathes
	 *            number of top shortest paths to calculate
	 * @return for each source a list of k-paths to each top category; the list
	 *         is in same order as @param sources
	 */
	List<List<List<ArrayListPath>>> yenTopKShortestPaths(ArrayList<Integer> sources, int topKShortestPathes) {

		List<List<List<ArrayListPath>>> sourcesTotargetsPaths = new ArrayList<List<List<ArrayListPath>>>();

		long totalstartTimestamp = System.currentTimeMillis();
		for (int source : sources) {
			List<List<ArrayListPath>> sourceTargetsPaths = new ArrayList<List<ArrayListPath>>();
			for (int target : topCategories) {

				// long startTimestamp = System.currentTimeMillis();

				List<ArrayListPath> grphPaths = getCachedOrCalculate(graph, source, target, topKShortestPathes);
				// logger.info("kshortest in [" + (System.currentTimeMillis() -
				// startTimestamp) + "]ms for [" + source
				// + "] to target [" + target + "] k [" + topKShortestPathes +
				// "]");
				sourceTargetsPaths.add(grphPaths);
			}
			sourcesTotargetsPaths.add(sourceTargetsPaths);
		}
		logger.info("total duration of yenTopKShortestPaths with [" + sources.size() + "] start nodes to ["
						+ topCategories.length + "] targets with k [" + topKShortestPathes + "]: ["
						+ (System.currentTimeMillis() - totalstartTimestamp) + "]ms ");
		return sourcesTotargetsPaths;
	}

	/**
	 * return cached result or performs a new calculation of top k shortest
	 * paths
	 * 
	 * @param h
	 *            graph as Graph
	 * @param g
	 *            same graph as Grph
	 * @param alg
	 *            top k algorithm implementation
	 * @param source
	 *            where to start
	 * @param target
	 *            to which target
	 * @param topK
	 *            number of top shortest paths to return
	 * @return a list of paths where list.size() <= topK
	 */
	private List<ArrayListPath> getCachedOrCalculate(Grph g, int source, int target, int topK) {

		PathMapKey pathOfInterest = new PathMapKey(source, target);
		List<ArrayListPath> paths = null;

		synchronized (cachedTopKShortestPaths) {
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

		// Graph h = getSubGraph(g, source);
		Grph h = getSubGraph(g, source);
		// YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(h);
		// kPaths.paths = getShortestKPaths(h, alg, source, target, topK);

		kPaths.paths = new ArrayList<MainCategoryRelevanceEstimator.SerializableArrayListPath>();
		for (ArrayListPath p : h.getKShortestPaths(source, target, topK, null)) {
			SerializableArrayListPath sp = new SerializableArrayListPath();
			for (int v : p.toVertexArray()) {
				sp.extend(v);
			}
			kPaths.paths.add(sp);
		}

		synchronized (cachedTopKShortestPaths) {
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

	/**
	 * create a subgraph with numberOfKClosestNeighbors closest neighbors or a
	 * whole clone prepared for yenTopKShortestPaths()
	 * 
	 * @param g
	 * @param source
	 * @return
	 */
	private Grph getSubGraph(Grph g, int source) {
		// g = g.clone();

		// take only a subgraph if specified to do so
		if (numberOfKClosestNeighbors > 0) {

			toools.set.IntSet kClosest = g.getKClosestNeighbors(source, numberOfKClosestNeighbors, null);
			kClosest.add(source);
			Grph subgraph = g.getSubgraphInducedByVertices(kClosest);
			// logger.info("trim to [" + numberOfKClosestNeighbors +
			// "] max nodes graph from [" + g + "] center at ["
			// + source + "] to subgraph [" + subgraph + "]");
			g = subgraph;
		}

		// for (int p : g.getVertices().toIntArray()) {
		// System.out.println(numberOfKClosestNeighbors + " subgraph path of " +
		// source + ": " + p);
		// }
		return g;
	}

	/**
	 * takes a cached result and returns a copy. caches results with k >= topK
	 * are trimmed and returned
	 * 
	 * @param topK
	 *            k that was used to calculate the result
	 * @param pathOfInterest
	 *            describes the start and destination node id
	 * @return null if no cached result found
	 */
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

	// /**
	// * invoke algorithm implementation of get_shortest_paths
	// *
	// * @param h
	// * the graph
	// * @param alg
	// * algorithm implementation
	// * @param source
	// * where to start from
	// * @param target
	// * to which target
	// * @param topK
	// * number of top shortest paths to calculate
	// * @return a list of top shortest paths from source to target
	// */
	// private List<SerializableArrayListPath> getShortestKPaths(Graph h,
	// YenTopKShortestPathsAlg alg, int source,
	// int target, int topK) {
	//
	// List<edu.asu.emit.qyan.alg.model.Path> paths =
	// alg.get_shortest_paths(h.get_vertex(source),
	// h.get_vertex(target), topK);
	//
	// List<SerializableArrayListPath> grphPaths = new ArrayList<>();
	// for (edu.asu.emit.qyan.alg.model.Path p : paths) {
	// SerializableArrayListPath pb = new SerializableArrayListPath();
	// for (BaseVertex v : p.get_vertices()) {
	// pb.extend(v.get_id());
	// }
	// grphPaths.add(pb);
	// }
	// return grphPaths;
	// }

	public void writeCachedPaths(File cacheFile) throws FileNotFoundException, IOException {
		synchronized (cachedTopKShortestPaths) {
			FileOutputStream fos = new FileOutputStream(cacheFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cachedTopKShortestPaths);
			oos.close();
			fos.close();
			logger.info("stored [" + cachedTopKShortestPaths.size() + "] paths from cache to ["
							+ cacheFile.getAbsolutePath() + "]");
		}
	}

	@SuppressWarnings("unchecked")
	public void readCachedPaths(File cacheFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(cacheFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		cachedTopKShortestPaths = (HashMap<PathMapKey, KShortestPaths>) ois.readObject();
		ois.close();
		fis.close();
		logger.info("read [" + cachedTopKShortestPaths.size() + "] paths from file [" + cacheFile.getAbsolutePath()
						+ "]");
	}

	public void writeCachedNodes(File cacheFile) throws FileNotFoundException, IOException {
		synchronized (cachedNodes) {
			FileOutputStream fos = new FileOutputStream(cacheFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cachedNodes);
			oos.close();
			fos.close();
			logger.info("stored [" + cachedNodes.size() + "] nodes from cache to [" + cacheFile.getAbsolutePath() + "]");
		}
	}

	@SuppressWarnings("unchecked")
	public void readCachedNodes(File cacheFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(cacheFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		cachedNodes = (HashMap<Integer, NodeData>) ois.readObject();
		ois.close();
		fis.close();
		logger.info("read [" + cachedNodes.size() + "] nodes from file [" + cacheFile.getAbsolutePath() + "]");
	}

	// private void openOutIndex() throws IOException {
	// try {
	// Directory indexDirectory = FSDirectory.open(outLuceneIndexDirectory);
	// Analyzer analyzer = new EnglishAnalyzer();
	// IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST,
	// analyzer);
	// writerConfig.setOpenMode(OpenMode.CREATE);
	// // writerConfig.setRAMBufferSizeMB(ramBufferSizeMB);
	// indexWriter = new IndexWriter(indexDirectory, writerConfig);
	// } catch (IOException e) {
	// logger.log(Level.SEVERE, "unable to open/create index at [" +
	// outLuceneIndexDirectory + "]", e);
	// throw e;
	// }
	// }

	// private void closeOutIndex() throws IOException {
	// try {
	// indexWriter.close();
	// } catch (IOException e) {
	// logger.log(Level.SEVERE, "index writer closed erroneous", e);
	// } catch (NullPointerException npe) {
	// logger.log(Level.SEVERE, "index writer already closed");
	// }
	// indexWriter = null;
	// }

	// @Override
	// public void close() throws IOException {
	// closeOutIndex();
	// }

}
