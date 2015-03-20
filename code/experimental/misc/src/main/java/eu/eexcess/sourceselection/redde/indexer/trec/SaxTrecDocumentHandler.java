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
 */

package eu.eexcess.sourceselection.redde.indexer.trec;

import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class reads documents of a TREC file. Dissection is case sensitive.
 * 
 * <DOC> <DOCNO> string </DOCNO> <HT> string </HT> <HEADER> further nodes
 * </HEADER> <TEXT> the text </TEXT> </DOC>
 * 
 * @author Raoul Rubien
 */

class SaxTrecDocumentHandler extends DefaultHandler {

	private RelevantNodeType currentNodeType = RelevantNodeType.UNDEFINED;
	private StringBuilder currentNodeValue = null;
	private List<Document> documents = null;
	private Document currentDocument = null;

	private static class TrecTags {
		public static final String Document = "DOC";
		public static final String DocumentNumber = "DOCNO";
		public static final String Text = "TEXT";
	}

	private static class LuceneDocumentFields {
		public static final String Text = "text";
		public static final String DocumentNumber = "documentNumber";
	}

	private static enum RelevantNodeType {
		UNDEFINED, HEADER, TEXT, DOCUMENT_NUMBER
	}

	SaxTrecDocumentHandler() {
		reset();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		switch (qName) {

		case TrecTags.DocumentNumber:
			currentNodeType = RelevantNodeType.DOCUMENT_NUMBER;
			break;

		case TrecTags.Text:
			currentNodeType = RelevantNodeType.TEXT;
			break;

		default:
			currentNodeType = RelevantNodeType.UNDEFINED;
			break;
		}
	}

	/**
	 * Store the temporary node value or document to the current node or document list.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		currentNodeType = RelevantNodeType.UNDEFINED;

		switch (qName) {
		case TrecTags.Document:
			documents.add(currentDocument);
			currentDocument = new Document();
			break;

		case TrecTags.DocumentNumber:
			currentDocument.add(new StringField(LuceneDocumentFields.DocumentNumber, currentNodeValue.toString(),
							Field.Store.YES));
			currentNodeValue = null;
			break;

		case TrecTags.Text:
			currentDocument.add(new TextField(LuceneDocumentFields.Text, currentNodeValue.toString(), Field.Store.YES));
			currentNodeValue = null;
			break;

		default:
			break;
		}
	}

	/**
	 * Store the current node value temporarily.
	 */
	@Override
	public void characters(char[] ch, int start, int length) {
		if (currentNodeType != RelevantNodeType.UNDEFINED) {
			currentNodeValue = new StringBuilder();
		}

		if (null != currentNodeValue) {
			currentNodeValue.append(ch, start, length);
		}
	}

	/**
	 * get documents parsed so far
	 * 
	 * @return
	 */
	public List<Document> documents() {
		return documents;
	}

	/**
	 * clear state after last parsing
	 */
	public void clear() {
		reset();
	}

	private void reset() {
		currentNodeValue = null;
		documents = new LinkedList<Document>();
		currentDocument = new Document();
	}
}
