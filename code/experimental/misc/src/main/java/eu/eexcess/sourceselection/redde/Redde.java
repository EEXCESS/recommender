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

package eu.eexcess.sourceselection.redde;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.sf.extjwnl.JWNLException;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.config.Settings.TestIndexSettings;
import eu.eexcess.sourceselection.redde.dbsampling.DBSampler;
import eu.eexcess.sourceselection.redde.dbsampling.DatabaseDetails;
import eu.eexcess.sourceselection.redde.logger.PianoLogger;

/**
 * the class samples, re-samples test sets and use this data for estimating
 * query relevance as described in REDDE (Relevant Document Distribution
 * Estimation Method for Resource Selection by Luo S. and Jamie C.)
 * 
 * @author Raoul Rubien
 */
public class Redde {

	/**
	 * default Redde settings
	 */
	static class Parameters {
		/**
		 * queries used to sample database, i.e. 300
		 */
		int numQueries = 300;
		/**
		 * number of documents stored to sample database for each query, i.e. 4
		 */
		int numSamplesPerQuery = 4;
		/**
		 * probabilityOfRelevance(...) considers the top n documents:
		 * <p>
		 * n = probabilityOfRelevanceRatio_1
		 * estimatedCentralizedCompleteDatabase;
		 * estimatedCentralizedCompleteDatabase = est. number of documents of
		 * all sources;
		 * 
		 * default: 0.0005
		 */
		double probabilityOfRelevanceRatio_1 = 0.0005;
		/**
		 * relevance distribution threshold when to apply
		 * probabilityOfRelevanceRatio_2
		 * <p>
		 * use probabilityOfRelevanceRatio_1 if
		 * (queryRelevanceDistribution(query, r1) >= backofftThreshold) else use
		 * probabilityOfRelevanceRatio_2
		 */
		double backoffThreshold = 0.1;
		/**
		 * see {@link probabilityOfRelevanceRatio_1}
		 * <p>
		 * default: 0.003
		 */
		double probabilityOfRelevanceRatio_2 = 0.003;
		/**
		 * amount of re-samples (db size estimation) performed that were
		 * averaged to smooth the estimation
		 */
		int numSizeEstimationResamples = 5;
	}

	private Logger logger = PianoLogger.getLogger(Redde.class.getCanonicalName());
	private Version luceneVersion;
	private String wordnetPath;
	private Set<TestIndexSettings> testSets;

	private int numQueries;
	private int numSamplesPerQuery;
	private int numSizeEstimationResamples = 5;

	/**
	 * contains all pre-calculated values of {@link #estimateSourcesSize()}
	 */
	HashMap<String, DatabaseDetails> sourceDetails;

	private MultiReader generalizedSampleDatabaseReader;

	private double probabilityOfRelevanceRatio_1 = 0.0005;
	private double probabilityOfRelevanceRatio_2 = 0.003;
	private double backoffThreshold = 0.1;

	/**
	 * factory method creating a default Parameter needed for class constructor
	 * 
	 * @return default parameters
	 */
	public static Parameters newDefaultParameters() {
		return new Parameters();
	}

	/**
	 * @param testSets
	 *            sets to be used for re-sampling and estimating query relevance
	 * @param wordnetPath
	 *            path to wordnet used for generating random queries (words)
	 * @param luceneVersion
	 * @param numQueries
	 *            number of queries to be sent (sampled) to the test set
	 * @param numSamplesPerQuery
	 *            number of samples to take from the top documents returned for
	 *            a query
	 */
	public Redde(Set<TestIndexSettings> testSets, String wordnetPath, Version luceneVersion, Parameters params) {
		this.luceneVersion = luceneVersion;
		this.testSets = testSets;
		this.wordnetPath = wordnetPath;
		this.numQueries = params.numQueries;
		this.numSamplesPerQuery = params.numSamplesPerQuery;
		this.numSizeEstimationResamples = params.numSizeEstimationResamples;
		this.probabilityOfRelevanceRatio_1 = params.probabilityOfRelevanceRatio_1;
		this.probabilityOfRelevanceRatio_2 = params.probabilityOfRelevanceRatio_2;
		this.backoffThreshold = params.backoffThreshold;
	}

	/**
	 * estimate database (source) sizes and error rate from sampled databases
	 */
	public void estimateSourcesSize() throws IOException {

		sourceDetails = new HashMap<String, DatabaseDetails>();
		for (TestIndexSettings set : testSets) {
			DBSampler sampler = null;
			try {
				sampler = new DBSampler(set.baseIndexPath, set.sampledIndexPath, luceneVersion, wordnetPath);
				sampler.open();
				sampler.sample(numQueries, numSamplesPerQuery);

				DatabaseDetails result = new DatabaseDetails();
				result.estimatedDBSize = sampler.estimateSize(numSizeEstimationResamples);
				result.absoluteErrorRate = sampler.absoluteErrorRate();
				result.sampledDBSize = sampler.size();
				result.estimatedToSampledRatio = result.estimatedDBSize / result.sampledDBSize;
				result.sampledIndexPath = set.sampledIndexPath;
				result.indexName = set.testSetName;
				sourceDetails.put(set.testSetName, result);

			} catch (IOException e) {
				throw new IOException("failed opening/sampling/estimating db [" + set.baseIndexPath + "]", e);
			} catch (ParseException e) {
				throw new IOException("failed estimating size of [" + set.baseIndexPath + "]", e);
			} catch (JWNLException e) {
				throw new IOException("failed sampling [" + set.baseIndexPath + "]", e);
			} catch (IllegalArgumentException e) {
				throw new IOException("failed estimating size of [" + set.baseIndexPath + "] due to [" + e.getMessage()
								+ "]", e);
			} finally {
				if (sampler != null) {
					sampler.close();
				}
				logger.info("resampled [" + set.testSetName + "]");
			}
		}
	}

	public Map<String, DatabaseDetails> getSourceDatabaseDetails() {
		return sourceDetails;
	}

	private MultiReader openGeneralizedSampleDatabase() throws IOException {

		if (generalizedSampleDatabaseReader != null) {
			return generalizedSampleDatabaseReader;
		}
		IndexReader[] readers = new IndexReader[Settings.testSets().size()];
		int idx = 0;
		for (TestIndexSettings setting : testSets) {
			readers[idx] = DirectoryReader.open(FSDirectory.open(new File(setting.sampledIndexPath)));
			idx++;
		}

		generalizedSampleDatabaseReader = new MultiReader(readers, true);
		return generalizedSampleDatabaseReader;
	}

	private void closeGeneralizedSampleDatabase() throws IOException {
		if (generalizedSampleDatabaseReader != null) {
			generalizedSampleDatabaseReader.close();
			generalizedSampleDatabaseReader = null;
		}
	}

	/**
	 * @return arithmetic mean of all absolute {@link #sourceDetails} errors
	 *         <p>
	 *         estimateSizes() has to be called before
	 * @throws IllegalStateException
	 *             if no absolute errors are calculated so far
	 */
	public double meanAbsoluteError() throws IllegalStateException {

		double divisor = Double.NaN;
		try {

			divisor = sourceDetails.size();
		} catch (NullPointerException e) {
			throw new IllegalStateException("necessary precalculated values not available");
		}

		if (divisor == 0) {
			throw new IllegalStateException("necessary precalculated values not available");
		}

		double sum = 0;
		for (Map.Entry<String, DatabaseDetails> resultEntry : sourceDetails.entrySet()) {
			sum += resultEntry.getValue().absoluteErrorRate;
		}

		return sum / divisor;
	}

	public static class QueryRelevance implements Comparable<QueryRelevance> {
		public static class Documents {
			public int numberCentralized;
			public int numberScoredCentralized;
			public int numberScoredFiltered;
		}

		public String query;
		public double relevance;
		public double relevanceDistribution;
		public double probabilityOfRelevanceRatio;
		public String indexName;
		public Documents documents = new Documents();

		@Override
		public int compareTo(QueryRelevance o) {
			if (this.relevanceDistribution < o.relevanceDistribution) {
				return -1;
			} else if (this.relevanceDistribution > o.relevanceDistribution) {
				return 1;
			}

			if (o.indexName != null) {
				return indexName.compareTo(o.indexName);
			}

			return -1;
		}
	}

	/**
	 * calls {@link #queryRelevance(String, DatabaseDetails, TRUE)}
	 * 
	 * @param queryString
	 * @param sampleDatabaseDetails
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public QueryRelevance queryRelevance(String queryString, DatabaseDetails sampleDatabaseDetails)
					throws ParseException, IOException, IllegalStateException {
		return queryRelevance(queryString, sampleDatabaseDetails, true);
	}

	/**
	 * calculates the number of relevant documents to query in sampleDatabase:
	 * <p>
	 * relevance = 0; for all documents di in sampleDatabase do: relevance +=
	 * (probabilityOfReflevance() * probabilitydocumentInSampleDatabase() *
	 * estimatedDatabaseSize() );
	 * 
	 * @param query
	 *            to calculate relevance for
	 * @param sampleDatabaseDetails
	 *            pre-calculated database (source) details after call to
	 *            {@link #estimateSourcesSize()} returned by
	 *            {@link #getSourceDatabaseDetails()}
	 * @param useRatio1
	 *            it true use probabilityOfRelevanceRatio_1 else
	 *            probabilityOfRelevanceRatio_2
	 * @return number of documents relevant to query
	 * @throws IllegalStateException
	 *             if estimateSizes() has not been called before
	 */
	public QueryRelevance queryRelevance(String queryString, DatabaseDetails sampleDatabaseDetails, boolean useRatio1)
					throws ParseException, IOException, IllegalStateException {
		openGeneralizedSampleDatabase();
		ScoreDoc[] rankedCentralizedSampleDocuments = scoreCentralizedSampledDatabase(queryString,
						generalizedSampleDatabaseReader);

		LinkedList<ScoreDoc> rankedSampleDocuments = filterDocumentsFromSource(sampleDatabaseDetails,
						rankedCentralizedSampleDocuments, generalizedSampleDatabaseReader);

		double probabilityOfRelevanceRatio = (useRatio1) ? probabilityOfRelevanceRatio_1
						: probabilityOfRelevanceRatio_2;
		double sum = 0;
		for (ScoreDoc document : rankedSampleDocuments) {

			DatabaseDetails documentDbDetails = dbDetails(document);
			double pRelevance = probabilityOfRelevance(rankedCentralizedSampleDocuments, document,
							probabilityOfRelevanceRatio);
			double pDocsInSmapleDb = probabilitydocumentInSampleDatabase(documentDbDetails);
			double estimatedDb = estimatedDatabaseSize(documentDbDetails);

			sum += (pRelevance * pDocsInSmapleDb * estimatedDb);
		}

		QueryRelevance relevanceDetails = new QueryRelevance();
		relevanceDetails.indexName = sampleDatabaseDetails.indexName;
		relevanceDetails.query = queryString;
		relevanceDetails.documents.numberCentralized = generalizedSampleDatabaseReader.numDocs();
		relevanceDetails.documents.numberScoredCentralized = rankedCentralizedSampleDocuments.length;
		relevanceDetails.documents.numberScoredFiltered = rankedSampleDocuments.size();
		relevanceDetails.relevance = sum;

		closeGeneralizedSampleDatabase();
		rankedCentralizedSampleDocuments = null;

		return relevanceDetails;
	}

	/**
	 * calculates the relevance distribution of relevant documents in different
	 * databases
	 * 
	 * @param relevances
	 *            relevances of a query in all databases
	 * @param sourceDbDetail
	 *            database (source) of witch to calculate the relevance
	 *            distribution
	 * @return relevance distribution
	 * @throws IllegalArgumentException
	 *             if relevances contains double indexName (see
	 *             {@link QueryRelevance}) values
	 */
	public double queryRelevanceDistribution(Set<QueryRelevance> relevances, DatabaseDetails sourceDbDetail)
					throws IllegalArgumentException {

		double dividend = Double.NaN;
		double divisor = 0;
		HashSet<String> seenSources = new HashSet<String>();

		for (QueryRelevance queryRelevance : relevances) {

			// sum up all relevances
			if (queryRelevance.indexName.compareTo(sourceDbDetail.indexName) == 0) {
				if (Double.isNaN(dividend) == false) {
					throw new IllegalArgumentException("illegal double entry of source [" + sourceDbDetail.indexName
									+ "]");
				}
				dividend = queryRelevance.relevance;
			}

			// particular relevance
			if (seenSources.contains(queryRelevance.indexName)) {
				throw new IllegalArgumentException("illegal double entry of source [" + queryRelevance.indexName + "]");
			}
			seenSources.add(queryRelevance.indexName);
			divisor += queryRelevance.relevance;
		}

		if (divisor <= 0) {
			throw new IllegalArgumentException("division by 0");
		}

		if (Double.isNaN(dividend)) {
			throw new IllegalArgumentException("dividend NaN");
		}

		return dividend / divisor;
	}

	/**
	 * Ranks sources regarding to a query's relevance-distribution using the
	 * modified ReDDE algorithm.
	 * 
	 * <p>
	 * rank all sources with
	 * {@link #queryRelevanceDistribution(Set, DatabaseDetails)} >=
	 * {@link #backoffThreshold} rank all other sources with re-calculated
	 * {@link #queryRelevance(String, DatabaseDetails, FALSE)} and
	 * {@link #queryRelevanceDistribution(Set, DatabaseDetails)} using
	 * {@link #probabilityOfRelevanceRatio_2}
	 * 
	 * @param query
	 *            (usually a single) word to be used for relevance calculation
	 * @return ascend ordered relevances sorted by relevance distribution
	 *         considering {@link #backoffThreshold} and
	 *         {@link #probabilityOfRelevanceRatio_1},
	 *         {@link #probabilityOfRelevanceRatio_2}
	 * @throws IOException
	 * @throws ParseException
	 */
	public LinkedHashSet<QueryRelevance> rankSources(String query) throws IOException, ParseException {

		// estimate database sizes and collect some more details
		estimateSourcesSize();

		TreeSet<QueryRelevance> primaryRelevances = new TreeSet<QueryRelevance>();
		// calculate primary query relevance for each source
		for (Map.Entry<String, DatabaseDetails> entry : sourceDetails.entrySet()) {
			primaryRelevances.add(queryRelevance(query, entry.getValue(), true));
		}

		// calculate relevance-distribution of every source
		for (QueryRelevance relevance : primaryRelevances) {
			double distrel = queryRelevanceDistribution(primaryRelevances, dbDetails(relevance));
			relevance.relevanceDistribution = distrel;
			relevance.probabilityOfRelevanceRatio = probabilityOfRelevanceRatio_1;
		}

		TreeSet<QueryRelevance> secondaryRelevances = new TreeSet<QueryRelevance>();
		// calculate secondary query relevance for each source where:
		// relevance-distribution < backoffThreshold
		for (QueryRelevance relevance : primaryRelevances) {
			if (relevance.relevanceDistribution < backoffThreshold) {
				QueryRelevance secondaryRelevance = queryRelevance(query, dbDetails(relevance), false);
				secondaryRelevance.probabilityOfRelevanceRatio = probabilityOfRelevanceRatio_2;
				secondaryRelevances.add(secondaryRelevance);
			}
		}

		// calculate secondary relevance-distribution where:
		// primary relevance-distribution < backoffThreshold
		for (QueryRelevance relevance : secondaryRelevances) {
			relevance.relevanceDistribution = queryRelevanceDistribution(secondaryRelevances, dbDetails(relevance));
		}

		// merge and order descending results

		LinkedHashSet<QueryRelevance> sourceRank = new LinkedHashSet<QueryRelevance>();
		for (Iterator<QueryRelevance> iterator = primaryRelevances.descendingIterator(); iterator.hasNext();) {
			QueryRelevance relevance = iterator.next();
			if (relevance.relevanceDistribution >= backoffThreshold) {
				sourceRank.add(relevance);
			}
		}

		for (Iterator<QueryRelevance> iterator = secondaryRelevances.descendingIterator(); iterator.hasNext();) {
			sourceRank.add(iterator.next());
		}

		return sourceRank;
	}

	private LinkedList<ScoreDoc> filterDocumentsFromSource(DatabaseDetails databaseDetails,
					ScoreDoc[] rankedCentralizedSampleDocuments, IndexReader reader) throws IOException {
		LinkedList<ScoreDoc> filtered = new LinkedList<ScoreDoc>();

		Set<String> fields = new HashSet<String>();
		fields.add(Settings.IndexFields.IndexNameField);

		for (ScoreDoc scoreDocument : rankedCentralizedSampleDocuments) {
			Document document = reader.document(scoreDocument.doc, fields);
			if (document.getField(Settings.IndexFields.IndexNameField).stringValue()
							.compareTo(databaseDetails.indexName) == 0) {
				filtered.add(scoreDocument);
			}
		}
		return filtered;
	}

	/**
	 * calculates the probability of a relevance given a document; the
	 * probability of documents ranked at the top is a (query dependent)
	 * constant and for all other it is 0
	 * 
	 * @param rankedCentralizedSampleDocuments
	 *            query dependent ranked sample database
	 * @param document
	 *            a arbitrary document contained in sampled database
	 * @return probability of relevance
	 * @throws IOException
	 */
	private double probabilityOfRelevance(ScoreDoc[] rankedCentralizedSampleDocuments, ScoreDoc document,
					double probabilityRatio) throws IOException {

		double centralRank = rankCentral(rankedCentralizedSampleDocuments, document);

		// TODO: enhanced method considers this both ratios: ratio1, ratio2

		double threshold = probabilityRatio * estimatedTotalDocumentsOfCentralizedCompleteDB();

		if (centralRank < threshold) {
			return queryDependentConstant(centralRank, threshold);
		} else {
			return 0;
		}
	}

	private double queryDependentConstant(double rank, double threshold) {
		if (rank > threshold) {
			throw new IllegalArgumentException();
		}
		// TODO: threshold dependent implementation is just assumed since the
		// redde paper discloses no details
		return threshold - rank;
	}

	/**
	 * construct the rank of document di in the centralized complete database
	 * using a centralized sample database:
	 * <p>
	 * {Di, Dj} ∈ {ranked centralized sample database}; rank = 0; for ∀ Dj ∈
	 * {ranked central sample database} (regarding query) do: if Dj.rank <
	 * Di.rank then rank += (estimatedDocs_database_j /
	 * numberSampledDocs_database_j);
	 * 
	 * @pram rankedSampleDocuments ranked (regarding to a query) centralized
	 *       database
	 * @throws IOException
	 * @return central rank
	 */
	private double rankCentral(ScoreDoc[] rankedCentralizedSampleDocuments, ScoreDoc di) throws IOException {

		double rank = 0;

		for (ScoreDoc dj : rankedCentralizedSampleDocuments) {
			if (dj.score > di.score) {
				DatabaseDetails dbDetails = dbDetails(dj);
				rank += dbDetails.estimatedToSampledRatio;
			}
		}
		return rank;
	}

	private DatabaseDetails dbDetails(ScoreDoc document) throws IOException {

		Set<String> fields = new HashSet<String>();
		fields.add(Settings.IndexFields.IndexNameField);
		Document doc = generalizedSampleDatabaseReader.document(document.doc, fields);
		return sourceDetails.get(doc.get(Settings.IndexFields.IndexNameField));
	}

	private DatabaseDetails dbDetails(QueryRelevance relevance) {

		for (Map.Entry<String, DatabaseDetails> entry : sourceDetails.entrySet()) {
			if (entry.getValue().indexName.compareTo(relevance.indexName) == 0) {
				return entry.getValue();
			}
		}
		return null;
	}

	private ScoreDoc[] scoreCentralizedSampledDatabase(String queryString, IndexReader dbReader) throws ParseException,
					IOException {

		return scoreDatabase(queryString, dbReader);
	}

	private ScoreDoc[] scoreDatabase(String queryString, IndexReader database) throws ParseException, IOException {

		Query query = new QueryParser(Settings.IndexFields.IndexTextField, new EnglishAnalyzer()).parse(queryString);
		IndexSearcher searcher = new IndexSearcher(database);
		TopDocs topDocs = searcher.search(query, database.numDocs());

		return topDocs.scoreDocs;
	}

	/**
	 * 
	 * @return the estimated size of a (not available) complete centralized
	 *         (complete centralized = union of all databases) database
	 * @throws IllegalStateException
	 *             if estimateSizes() has not been called before
	 */
	double estimatedTotalDocumentsOfCentralizedCompleteDB() throws IllegalStateException {
		double sum = 0;

		if (sourceDetails.size() <= 0) {
			throw new IllegalStateException();
		}

		for (Map.Entry<String, DatabaseDetails> entry : sourceDetails.entrySet()) {
			sum += entry.getValue().estimatedDBSize;
		}
		return sum;
	}

	/**
	 * @param databaseResults
	 *            the pre-calculated result of a test set
	 * @return the probability of a document in the sample database
	 * @throws IllegalArgumentException
	 *             if sampled database size <= 0
	 */
	private double probabilitydocumentInSampleDatabase(DatabaseDetails databaseResults) throws IllegalArgumentException {

		if (databaseResults.sampledDBSize <= 0) {
			throw new IllegalArgumentException();
		}

		return 1.0 / (double) databaseResults.sampledDBSize;
	}

	/**
	 * @param databaseResults
	 *            the pre-calculated result of a test set
	 * @return estimated database size
	 */
	private double estimatedDatabaseSize(DatabaseDetails databaseResults) {
		return databaseResults.estimatedDBSize;
	}
}
