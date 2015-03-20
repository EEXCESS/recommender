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

package eu.eexcess.sourceselection.redde.indexer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import eu.eexcess.sourceselection.redde.logger.PianoLogger;

/**
 * opens one index for reading and creates/overwrites an other for writing or
 * closes them
 * 
 * @author Raoul Rubien
 *
 */
public class BinaryIndexResource implements Closeable {

	private Logger logger = PianoLogger.getLogger(BinaryIndexResource.class.getCanonicalName());
	protected String inIndexPath = null;
	protected String outIndexPath = null;
	protected Version luceneVersion = null;

	protected IndexWriter outIndexWriter = null;
	protected IndexReader inIndexReader = null;

	/**
	 * opens one index for reading and creates an other for writing
	 * 
	 * @param inIndexSourcePath
	 *            path to existent index to be opened read only
	 * @param outIndexDestPath
	 *            path of new index that will be newly created and opened
	 *            read/write
	 * @param luceneVersion
	 */
	public BinaryIndexResource(String inIndexSourcePath, String outIndexDestPath, Version luceneVersion) {
		this.inIndexPath = inIndexSourcePath;
		this.outIndexPath = outIndexDestPath;
		this.luceneVersion = luceneVersion;
	}

	/**
	 * Opens the general and the sampled database (index).
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {
		openInIndex();
		openOutIndex();
	}

	void openOutIndex() throws IOException {
		openOutIndex(512.0);
	}

	/**
	 * opens the sample index for writing; overwrites existing one
	 * 
	 * @param ramBufferSizeMB
	 *            determines the amount of RAM that may be used for buffering
	 * @throws IOException
	 *             if unable to open/create index
	 */
	void openOutIndex(double ramBufferSizeMB) throws IOException {

		try {
			Directory indexDirectory = FSDirectory.open(new File(outIndexPath));
			Analyzer analyzer = new EnglishAnalyzer();
			IndexWriterConfig writerConfig = new IndexWriterConfig(luceneVersion, analyzer);
			writerConfig.setOpenMode(OpenMode.CREATE);
			writerConfig.setRAMBufferSizeMB(ramBufferSizeMB);
			outIndexWriter = new IndexWriter(indexDirectory, writerConfig);

		} catch (IOException e) {
			logger.log(Level.SEVERE, "unable to open/create index at [" + outIndexPath + "]", e);
			throw e;
		}
	}

	/**
	 * opens the general database that will be sampled
	 * 
	 * @throws IOException
	 *             if unable to open index
	 */
	private void openInIndex() throws IOException {

		try {
			inIndexReader = DirectoryReader.open(FSDirectory.open(new File(inIndexPath)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "unable to open index at [" + inIndexPath + "]", e);
			throw e;
		}
	}

	private void closeInIndex() {
		try {
			inIndexReader.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "index reader closed erroneous", e);
		} catch (NullPointerException npe) {
			logger.log(Level.SEVERE, "index reader already closed");
		}
		inIndexReader = null;
	}

	void closeOutIndex() {

		if (outIndexWriter != null) {
			try {
				outIndexWriter.commit();
				outIndexWriter.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "index closed erroneous", e);
			} catch (NullPointerException npe) {
				logger.log(Level.SEVERE, "index reader already closed");
			}
			outIndexWriter = null;
		}
	}

	/**
	 * Close the general and the sampled database (index).
	 */
	@Override
	public void close() {
		closeOutIndex();
		closeInIndex();
	}

}
