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

package eu.eexcess.diversityasurement.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.junit.Ignore;
import org.junit.Test;

import eu.eexcess.diversityasurement.evaluation.config.Settings;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance;
import eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance.CategoryToTopCategoryRelevance.CategoryRelevance;

public class QueryCategoryKShortestPathsEvaluationTest {

	// currently serialized objects are not stored to the index any more
	@Ignore 
	@Test
	public void writeToIndexTest_reconstructFromBinary_expectCorrectValues() throws IOException, ParseException,
					ClassNotFoundException {
		CategoryToTopCategoryRelevance crd = new CategoryToTopCategoryRelevance();
		crd.query = "query";
		crd.queryDescription = "description of query";

		// TODO
		// crd.startCategories = new CategoryRelevanceDetails.SubCategory[1];
		// crd.startCategories[0] = new CategoryRelevanceDetails.SubCategory();
		// crd.startCategories[0].categoryId = 1;
		// crd.startCategories[0].categoryName = "subcat name";

		crd.topCategoryRelevances = new CategoryToTopCategoryRelevance.CategoryRelevance[2];
		crd.topCategoryRelevances[0] = new CategoryToTopCategoryRelevance.CategoryRelevance();
		crd.topCategoryRelevances[0].categoryId = 101;
		crd.topCategoryRelevances[0].categoryName = "agriculture";
		crd.topCategoryRelevances[0].categoryRelevance = 0.5;

		crd.topCategoryRelevances[1] = new CategoryToTopCategoryRelevance.CategoryRelevance();
		crd.topCategoryRelevances[1].categoryId = 102;
		crd.topCategoryRelevances[1].categoryName = "architecture";
		crd.topCategoryRelevances[1].categoryRelevance = 0.125;

		QueryCategoryKShortestPathsEvaluation.openOutIndex();
		QueryCategoryKShortestPathsEvaluation.writeToIndex(crd);
		QueryCategoryKShortestPathsEvaluation.closeOutIndex();

		IndexReader ir;
		try {
			ir = DirectoryReader.open(FSDirectory.open(Settings.RelevanceEvaluation.IOFIles.outLuceneIndexDirectory));
		} catch (IOException e) {
			System.out.println("unable to open index");
			throw e;
		}
		IndexSearcher searcher = new IndexSearcher(ir);

		Query query = new QueryParser("documentID", new EnglishAnalyzer()).parse("99");
		TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
		searcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		assertEquals(1, hits.length);
		int docId = hits[0].doc;
		Document document = searcher.doc(docId);

		ByteArrayInputStream bis = new ByteArrayInputStream(
						document.getFields("TopCategoryRelevance")[0].binaryValue().bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		CategoryRelevance tcr = (CategoryRelevance) ois.readObject();

		assertEquals(101, tcr.categoryId);
		assertEquals("agriculture", tcr.categoryName);
		assertEquals(0.5, tcr.categoryRelevance);

		bis = new ByteArrayInputStream(document.getFields("TopCategoryRelevance")[1].binaryValue().bytes);
		ois = new ObjectInputStream(bis);
		tcr = (CategoryRelevance) ois.readObject();

		assertEquals(102, tcr.categoryId);
		assertEquals("architecture", tcr.categoryName);
		assertEquals(0.125, tcr.categoryRelevance);
	}
}
