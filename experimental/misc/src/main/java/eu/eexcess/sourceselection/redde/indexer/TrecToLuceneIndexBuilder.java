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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import eu.eexcess.logger.PianoLogger;
import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.indexer.trec.TrecDocumentReader;

/**
 * builds a new Lucene index using a directory containing uncompressed TREC
 * documents
 * 
 * @author Raoul Rubien
 */
public class TrecToLuceneIndexBuilder {

	private Logger logger = PianoLogger.getLogger(TestSetBuilder.class.getCanonicalName());

	private TrecDocumentReader documentReader = new TrecDocumentReader();

	private int dirsCount;
	private int filesCount;
	private int filesSkipped;
	private int documentsTotal;

	private String documentsPath;
	private String indexPath;
	private String indexName;
	private Double ramBufferSize;
	private Version luceneVersion;

	/**
	 * @param sourceDocumentsPath
	 *            path to TREC documents; iterates recursively all documents
	 * @param destIndexPath
	 *            path where index is built
	 * @param ramBufferSize
	 *            determines the amount of RAM that may be used for buffering
	 * @param luceneVersion
	 */
	public TrecToLuceneIndexBuilder(String sourceDocumentsPath, String destIndexPath, String indexName, double ramBufferSize,
					Version luceneVersion) {
		documentsPath = sourceDocumentsPath;
		indexPath = destIndexPath;
		this.ramBufferSize = ramBufferSize;
		this.luceneVersion = luceneVersion;
		this.indexName = indexName;

	}

	/**
	 * Builds/overwrites existing Lucene index using TREC documents as source
	 */
	public void index() {
		Date startTimestamp = new Date();
		final File documentsDirectory = new File(documentsPath);

		if (!documentsDirectory.exists() || !documentsDirectory.canRead()) {

			logger.severe("cannot access document directory [" + documentsDirectory.getAbsolutePath() + "]");

		} else {

			try {
				logger.info("processing directory [" + documentsPath + "] to index [" + indexPath + "]");

				Directory indexDirectory = FSDirectory.open(new File(indexPath));
				Analyzer analyzer = new EnglishAnalyzer();
				IndexWriterConfig writerConfig = new IndexWriterConfig(luceneVersion, analyzer);

				writerConfig.setOpenMode(OpenMode.CREATE);
				writerConfig.setRAMBufferSizeMB(ramBufferSize);

				IndexWriter indexWriter = new IndexWriter(indexDirectory, writerConfig);
				indexDocs(indexWriter, documentsDirectory);

				indexWriter.commit();
				indexWriter.close();

				Date stopTimestamp = new Date();
				logger.info("processed [" + dirsCount + "] dirs [" + filesCount + "] files [" + documentsTotal
								+ "] documents [" + filesSkipped + "] files skipped in ["
								+ (stopTimestamp.getTime() - startTimestamp.getTime()) + "] ms]");

			} catch (IOException e) {
				logger.log(Level.SEVERE, "failed indexing documents", e);
			}
		}
	}

	/**
	 * recursive iterate over files in given path and write to index from TREC
	 * formatted documents
	 * 
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	private void indexDocs(IndexWriter writer, File file) throws IOException {

		if (file.canRead()) {

			if (file.isDirectory()) {
				String[] files = file.list();

				if (files != null) {
					dirsCount++;
					logger.info("process folder [" + file + "] containing [" + files.length + "] files");
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}

			} else { // if isFile()
				filesCount++;

				StringBuilder message = new StringBuilder();
				message.append("processing file [" + file.getCanonicalFile() + "] ");

				List<Document> documents = documentReader.readAll(file);

				int documentsInFile = 0;
				if (null == documents) {
					message.append("- ignored file because of invalid XML");
					filesSkipped++;
				} else {
					for (Document document : documents) {
						document.add(new TextField(Settings.IndexFields.IndexNameField, indexName, Field.Store.YES));
						writer.addDocument(document);
						documentsInFile++;
					}
					documentsTotal += documentsInFile;
					message.append("found [" + documentsInFile + "] documents");
				}
				logger.info(message.toString());
			}
		} else {
			logger.severe("ignored file because not readable");
			filesSkipped++;
		}
	}

}
