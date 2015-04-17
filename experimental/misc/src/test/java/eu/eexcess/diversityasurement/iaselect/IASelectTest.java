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

package eu.eexcess.diversityasurement.iaselect;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import eu.eexcess.diversityasurement.iaselect.Category;
import eu.eexcess.diversityasurement.iaselect.Document;
import eu.eexcess.diversityasurement.iaselect.DocumentQualityValueV;
import eu.eexcess.diversityasurement.iaselect.IAselect;
import eu.eexcess.diversityasurement.iaselect.Query;

public class IASelectTest {

	@Test
	public void IASelect_givenTestData_expectCorrectdocumentOrder() {

		Query q = new Query("foo");
		q.addCategory(new Category("c1", 0.7));
		q.addCategory(new Category("c2", 0.3));

		DocumentQualityValueV V = new DocumentQualityValueV();
		addNewDocumentValue("d1", "c1", 0.5, "c2", 0.0, V);
		addNewDocumentValue("d2", "c1", 0.2, "c2", 0.0, V);
		addNewDocumentValue("d3", "c1", 0.15, "c2", 0.0, V);
		addNewDocumentValue("d4", "c1", 0.05, "c2", 0.0, V);
		addNewDocumentValue("d5", "c1", 0.05, "c2", 0.0, V);
		addNewDocumentValue("d6", "c1", 0.05, "c2", 0.0, V);
		addNewDocumentValue("d7", "c1", 0.05, "c2", 0.0, V);
		addNewDocumentValue("d8", "c1", 0.0, "c2", 0.33, V);
		addNewDocumentValue("d9", "c1", 0.0, "c2", 0.33, V);
		addNewDocumentValue("d10", "c1", 0.0, "c2", 0.33, V);

		LinkedHashSet<Document> expectedOrder = new LinkedHashSet<Document>();

		expectedOrder.add(new Document("d1"));
		expectedOrder.add(new Document("d8"));
		expectedOrder.add(new Document("d2"));
		expectedOrder.add(new Document("d9"));
		expectedOrder.add(new Document("d10"));

		IAselect diversityer = new IAselect();
		Set<Document> Rq = R(q);

				Iterator<Document> expectedOrderIterator = expectedOrder.iterator();
		for (Document d : diversityer.IASelect(5, q, Rq, V)) {
			assertEquals(expectedOrderIterator.next(), d);
			System.out.print(d + " ");
		}
	}

	private Set<Document> R(Query q) {
		Set<Document> selectedDocuments = new LinkedHashSet<Document>();
		selectedDocuments.add(new Document("d1", new Category("c1")));
		selectedDocuments.add(new Document("d2", new Category("c1")));
		selectedDocuments.add(new Document("d3", new Category("c1")));
		selectedDocuments.add(new Document("d4", new Category("c1")));
		selectedDocuments.add(new Document("d5", new Category("c1")));
		selectedDocuments.add(new Document("d6", new Category("c1")));
		selectedDocuments.add(new Document("d7", new Category("c1")));
		selectedDocuments.add(new Document("d8", new Category("c2")));
		selectedDocuments.add(new Document("d9", new Category("c2")));
		selectedDocuments.add(new Document("d10", new Category("c2")));

		return selectedDocuments;
	}

	/**
	 * add new document quality
	 * 
	 * @param documentName
	 * @param category1Name
	 * @param category1Probability
	 * @param category2Name
	 * @param category2Probability
	 */
	private void addNewDocumentValue(String documentName, String category1Name, double category1Probability,
					String category2Name, double category2Probability, DocumentQualityValueV V) {

		Document d = new Document(documentName);
		Category c1 = new Category(category1Name, category1Probability);
		Category c2 = new Category(category2Name, category2Probability);
		HashSet<Category> categories = new HashSet<Category>();
		categories.add(c1);
		categories.add(c2);
		V.documentQualities.put(d, categories);
	}
}
