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

package eu.eexcess.sourceselection.redde.indexer.topterm;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.JWNLException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.FSDirectory;

import eu.eexcess.logger.PianoLogger;

public class IndexHelper implements Closeable {

	private Logger logger = PianoLogger.getLogger(IndexHelper.class.getCanonicalName());
	protected IndexReader inIndexReader;
	protected String indexPath;
	protected final static String fieldOfInterest = "text";
	protected final static String termVariableName = "term=";

	/**
	 * Builds an instance of this class and opens the index.
	 * 
	 * @param indexPath
	 * @throws IOException
	 * @throws JWNLException
	 */
	IndexHelper(String indexPath) throws IOException {
		this.indexPath = indexPath;
		openIndex();
	}

	private void openIndex() throws IOException {
		try {
			inIndexReader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "unable to open index at [" + indexPath + "]", e);
			throw e;
		}
	}

	private void closeIndex() {
		try {
			inIndexReader.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "index reader closed erroneous", e);
		} catch (NullPointerException npe) {
			logger.log(Level.SEVERE, "index reader already closed");
		}
		inIndexReader = null;
	}

	@Override
	public void close() {
		closeIndex();
	}

	/**
	 * returns the top n (to-startFrom+1) terms beginning with startFrom
	 * 
	 * @param startFrom
	 *            first term
	 * @param to
	 *            lastTerm
	 * @return
	 * @throws Exception
	 */
	protected String[] getTopTerms(int startFrom, int to) throws Exception {
		int numTerms = to - startFrom + 1;
		String[] termNames = null;
		TermStats[] terms = HighFreqTerms.getHighFreqTerms(inIndexReader, to + 1, fieldOfInterest,
						new HighFreqTerms.DocFreqComparator());

		termNames = new String[numTerms];
		int idx = 0;
		for (TermStats term : terms) {
			String termDetails = term.toString();
			int startIndex = termDetails.lastIndexOf(termVariableName) + termVariableName.length();
			int endIndex = termDetails.indexOf(" ", startIndex);
			termNames[idx++] = termDetails.substring(startIndex, endIndex);
			if (idx >= numTerms) {
				break;
			}
		}
		return termNames;
	}
}
