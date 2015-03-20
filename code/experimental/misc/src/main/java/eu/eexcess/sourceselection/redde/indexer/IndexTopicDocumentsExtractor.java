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
import java.io.IOException;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.indexer.trec.topic.Topic;

public class IndexTopicDocumentsExtractor extends BinaryIndexResource implements Closeable {

	private static final String FIELD_TEXT = Settings.IndexFields.IndexTextField;

	public IndexTopicDocumentsExtractor(String inIndexSourcePath, String outIndexDestPath, Version luceneVersion)
					throws IOException {
		super(inIndexSourcePath, outIndexDestPath, luceneVersion);
		super.open();
	}

	@Override
	public void close() {
		super.close();
	}

	public void storeTopicDocs(Topic topics[]) throws ParseException, IOException {

		boolean isFirst = true;
		StringBuilder queryString = new StringBuilder();
		for (Topic topic : topics) {
			String topicString = (topic.title == null) ? topic.description : topic.title;
			if (!isFirst) {
				queryString.append(" OR ");
			} else {
				isFirst = false;
			}
			queryString.append(" (");

			boolean isFirstWord = true;
			for (String word : topicString.replaceAll("\\p{Punct}", "").split("\\s+")) {
				if (!isFirstWord) {
					queryString.append(" AND ");
				} else {
					isFirstWord = false;
				}
				queryString.append(word);
			}

			queryString.append(") ");
		}

		QueryParser queryParser = new QueryParser(FIELD_TEXT, new EnglishAnalyzer());
		Query query = queryParser.parse(queryString.toString());
		IndexSearcher searcher = new IndexSearcher(inIndexReader);
		TopDocs topDocs = searcher.search(query, inIndexReader.numDocs());

		for (ScoreDoc hit : topDocs.scoreDocs) {
			Document document = searcher.doc(hit.doc);
			outIndexWriter.addDocument(document);
		}
	}
}
