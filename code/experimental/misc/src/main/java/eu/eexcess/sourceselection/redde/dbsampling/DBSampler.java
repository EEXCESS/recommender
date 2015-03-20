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

package eu.eexcess.sourceselection.redde.dbsampling;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.indexer.BinaryIndexResource;
import eu.eexcess.sourceselection.redde.logger.PianoLogger;

/**
 * Takes an lucene index as general database and estimates its size using
 * sample-resample. Increasing the JVM max heap size may be necessary (-Xmx512m
 * or -Xmx1g).
 * 
 * @author Raoul Rubien
 */
public class DBSampler extends BinaryIndexResource {

	private Logger logger = PianoLogger.getLogger(DBSampler.class.getCanonicalName());

	private Dictionary dictionary;

	private double absoluteErrorRate = Double.NaN;
	private int numResampleAttempts = 0;

	/**
	 * create a new sampler and open a wordnet dictionary located in @see
	 * WordnetPath.SampleIndex.WordnetPath
	 * 
	 * @param generalIndexSourcePath
	 *            path to existent index
	 * @param sampleIndexDestPath
	 *            path where to store the sample index
	 * @param luceneVersion
	 * @param wordnetSourcePath
	 *            path to existent wordnet dictionary
	 * @throws JWNLException
	 */
	public DBSampler(String generalIndexSourcePath, String sampleIndexDestPath, Version luceneVersion,
					String wordnetSourcePath) throws JWNLException {
		super(generalIndexSourcePath, sampleIndexDestPath, luceneVersion);
		dictionary = Dictionary.getFileBackedInstance(wordnetSourcePath);
	}

	/**
	 * draw random word from wordnet dictionary
	 * 
	 * @param wordsToIgnore
	 * @return a random word not contained in words to ignore set
	 */
	String drawRandomWord(Set<String> wordsToIgnore) {

		String lemma = null;

		try {
			do {
				IndexWord randomIndexWord;

				try {
					randomIndexWord = dictionary.getRandomIndexWord(POS.NOUN);
				} catch (NullPointerException e) {
					continue;
				}

				if (wordsToIgnore.contains(randomIndexWord.getLemma())) {
					continue;
				}

				lemma = randomIndexWord.getLemma();

			} while (lemma == null);
		} catch (JWNLException e) {
			logger.log(Level.SEVERE, "cannot draw random word", e);
		}

		return lemma;
	}

	/**
	 * sollects samples from general database (index)
	 * 
	 * @param queryString
	 *            the search string
	 * @param searcher
	 * @param maxHitsPerQuery
	 *            maximum number of documents stored from result for queryString
	 * @throws ParseException
	 * @throws IOException
	 */
	private void collectSamples(String queryString, IndexSearcher searcher, int maxHitsPerQuery) throws ParseException,
					IOException {

		Query query = new QueryParser(Settings.IndexFields.IndexTextField, new EnglishAnalyzer()).parse(queryString);

		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHitsPerQuery, true);
		searcher.search(query, collector);
		writeToIndex(searcher, collector, maxHitsPerQuery);
	}

	void writeToIndex(IndexSearcher searcher, TopScoreDocCollector collector, int maxHitsPerQuery) throws IOException {

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		maxHitsPerQuery = (maxHitsPerQuery < hits.length) ? maxHitsPerQuery : hits.length;

		for (int i = 0; i < maxHitsPerQuery; i++) {
			int docId = hits[i].doc;
			Document document = searcher.doc(docId);
			outIndexWriter.addDocument(document);
		}
	}

	/**
	 * @return the number of documents indexed in sample database
	 * @throws NullPointerException
	 */
	public int size() throws NullPointerException {
		return outIndexWriter.numDocs();
	}

	/**
	 * Samples the general database.
	 * 
	 * @param numOneTermQueries
	 *            number of one-term random queries submitted to the general
	 *            database
	 * @param maxHitsPerQuery
	 *            amount of top documents to sample
	 * @throws IOException
	 */
	public void sample(int numOneTermQueries, int maxHitsPerQuery) throws IOException {

		HashSet<String> terms = new HashSet<String>();

		IndexSearcher searcher = new IndexSearcher(inIndexReader);
		do {
			try {
				/**
				 * FIXME: TODO: draw random word from underlying index as
				 * described in ReDDE 3.1 "A term from the database’s resource
				 * description is picked randomly and submitted to the database
				 * as a single-term query (resampling);"
				 */
				String randomWord = drawRandomWord(terms);
				collectSamples(randomWord, searcher, maxHitsPerQuery);
			} catch (ParseException pe) {
				continue;
			}
			numOneTermQueries--;
		} while (numOneTermQueries > 0);
	}

	/**
	 * 
	 * @return the absolute error of last re-sampling
	 */
	public double absoluteErrorRate() {
		return absoluteErrorRate;
	}

	/**
	 * Arithmetical average of estimated database size.
	 * 
	 * @param numIterations
	 *            number of estimated results to take to average
	 * @return
	 * @throws IllegalArgumentException
	 *             if index is empty
	 */
	public double estimateSize(int numIterations) throws ParseException, IOException, IllegalArgumentException {

		numResampleAttempts = 0;
		double sum = 0;
		HashSet<String> seenWords = new HashSet<String>();

		for (int iterations = 0; iterations < numIterations;) {
			numResampleAttempts++;
			String term = drawRandomWord(seenWords);
			double estimatedSize = resample(term);

			if (Double.isInfinite(estimatedSize) || Double.isNaN(estimatedSize)) {
				continue;
			}
			iterations++;
			sum += estimatedSize;
		}

		double estimateMean = sum / (double) (numIterations);
		absoluteErrorRate = Math.abs(estimateMean - inIndexReader.numDocs()) / inIndexReader.numDocs();

		return estimateMean;
	}

	/**
	 * @return total number (including failed) attempts for last estimation
	 */
	public int resampleAttempts() {
		return numResampleAttempts;
	}

	/**
	 * Estimates the database size of general database using sample-resample and
	 * search term "term".
	 * 
	 * @param term
	 *            one-term search term for general and sampled index
	 * @return the estimated database size of the general index
	 * @throws ParseException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if an index (base or sampled) contains no documents
	 */
	private double resample(String term) throws ParseException, IOException, IllegalArgumentException {

		Query query = new QueryParser(Settings.IndexFields.IndexTextField, new EnglishAnalyzer()).parse(term);

		outIndexWriter.commit();

		if (inIndexReader.numDocs() <= 0) {
			throw new IllegalArgumentException("failed to resample using empty index [inIndexReader]");
		} else if (outIndexWriter.numDocs() <= 0) {
			throw new IllegalArgumentException("failed to resample using empty index [outIndexWriter]");
		}

		double estimation = 0;
		IndexReader sampleIndexReader = null;

		try {
			// get total hits for term in sample index
			sampleIndexReader = DirectoryReader.open(outIndexWriter, true);
			IndexSearcher sampleIndexSearcher = new IndexSearcher(sampleIndexReader);
			TopDocs sampleSearchDocs = sampleIndexSearcher.search(query, sampleIndexReader.numDocs());

			// get total hits for term in general index
			IndexSearcher generalIndexSearcher = new IndexSearcher(inIndexReader);
			TopDocs generalSearchDocs = generalIndexSearcher.search(query, inIndexReader.numDocs());

			estimation = estimationCalculator(generalSearchDocs.totalHits, sampleSearchDocs.totalHits,
							sampleIndexReader.numDocs(), true);
		} finally {
			if (sampleIndexReader != null) {
				sampleIndexReader.close();
			}
		}

		return estimation;
	}

	/**
	 * db size estimation calculation
	 * 
	 * @param docFrequencyGeneralDb
	 *            number of hits in general database
	 * @param docFrequencySampleDb
	 *            number of hits in sample database
	 * @param totalDocumentsSample
	 *            size of sample database
	 * @param ignoreZeroOperands
	 *            if zero operands that may result in NaN/Infinity index should
	 *            be skipped and return per default NaN
	 * @return NaN if totalDocumentSample == true and any argument == 0;
	 *         estimated size if totalDocumentSample == false and no argument ==
	 *         0; NaN, Infinity or estimated size if totalDocumentSample ==
	 *         false
	 */
	double estimationCalculator(int docFrequencyGeneralDb, int docFrequencySampleDb, int totalDocumentsSample,
					boolean ignoreZeroOperands) {

		if (ignoreZeroOperands) {
			if (docFrequencyGeneralDb == 0 || docFrequencySampleDb == 0 || totalDocumentsSample == 0) {
				return Double.NaN;
			}
		}

		return ((double) docFrequencyGeneralDb * (double) totalDocumentsSample) / (double) docFrequencySampleDb;
	}
}
