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
import java.io.IOException;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;

/**
 * This class keeps count of an lucene index and wordnet dictionary.
 * 
 * @author Raoul Rubien
 *
 */
public class Resources extends IndexHelper implements Closeable {

	Dictionary wordnetDict;
	protected String wordnetSourcePath;

	/**
	 * Builds an instance of this class and opens the index and dictionary.
	 * 
	 * @param indexPath
	 * @param wordnetPath
	 * @throws IOException
	 * @throws JWNLException
	 */
	Resources(String indexPath, String wordnetPath) throws IOException, JWNLException {
		super(indexPath);
		this.indexPath = indexPath;
		wordnetSourcePath = wordnetPath;
		openDictionary();
	}

	/**
	 * Builds an instance of this class and opens the index.
	 * 
	 * @param indexPath
	 * @throws IOException
	 * @throws JWNLException
	 */
	Resources(String indexPath) throws IOException, JWNLException {
		super(indexPath);
		this.indexPath = indexPath;
	}

	private void openDictionary() throws JWNLException {
		wordnetDict = Dictionary.getFileBackedInstance(wordnetSourcePath);
	}

	private void closeDictionary() {
		if (wordnetDict != null) {
			wordnetDict.close();
		}
	}

	@Override
	public void close() {
		super.close();
		closeDictionary();
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
	@Override
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
