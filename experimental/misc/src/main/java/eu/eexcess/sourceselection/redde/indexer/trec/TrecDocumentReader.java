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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Parses documents from XML TREC file.
 * 
 * @author Raoul Rubien
 */
public class TrecDocumentReader {

	Logger logger = Logger.getLogger(TrecDocumentReader.class.getName());

	private XMLReader xmlReader = null;
	SaxTrecDocumentHandler handler = null;

	/**
	 * 
	 * @param xmlDocuments
	 *            file containing xml documents
	 * @return parsed documents
	 */
	public TrecDocumentReader() {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		handler = new SaxTrecDocumentHandler();
		try {
			SAXParser parser = parserFactory.newSAXParser();
			xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(handler);
		} catch (ParserConfigurationException | SAXException e) {
			logger.log(Level.WARNING, "failed preparing for reading TREC documents", e);
		}
	}

	/**
	 * read all documents from file
	 * 
	 * @param xmlDocuments
	 * @return
	 */
	public List<Document> readAll(File xmlDocuments) {
		try {

			InputStream header = new ByteArrayInputStream(
							"<?xml version=\"1.0\"?><wrapper>".getBytes(StandardCharsets.UTF_8));
			InputStream file = new FileInputStream(xmlDocuments);
			InputStream inStream = new SequenceInputStream(header, file);
			InputStream footer = new ByteArrayInputStream("</wrapper>".getBytes(StandardCharsets.UTF_8));
			InputStream sequencialStream = new SequenceInputStream(inStream, footer);

			xmlReader.parse(new InputSource(sequencialStream));

			sequencialStream.close();
			inStream.close();
			footer.close();
			file.close();
			header.close();

			List<Document> parsedDocuments = handler.documents();
			handler.clear();
			return parsedDocuments;
		} catch (IOException | SAXException e) {
			try {
				logger.log(Level.WARNING, "failed reading TREC documents from file [" + xmlDocuments.getCanonicalFile()
								+ "]");
			} catch (IOException e1) {
				logger.log(Level.WARNING, "failed reading TREC documents from file [" + xmlDocuments.toString() + "]");
			}
		}
		return null;
	}

}
