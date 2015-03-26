/**
 * Copyright (C) 2014
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
 */
package eu.eexcess.opensearch.opensearchDescriptionDocument.parse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import eu.eexcess.opensearch.opensearchDescriptionDocument.OpensearchDescription;
import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Image;
import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Query;
import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Url;
import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Url.UrlRel;
import eu.eexcess.opensearch.opensearchDescriptionDocument.parse.OpenSearchDocumentParser;

public class OpenSearchDocumentParserTest {

	private Logger logger = Logger.getLogger(OpenSearchDocumentParserTest.class
			.getName());

	/**
	 * Verify that a detailed OpensearchDocument can be constructed from XML
	 * described at <a
	 * href="http://www.opensearch.org/Specifications/OpenSearch/1.1#Examples">
	 * www.opensearch.org</a>
	 */
	@Test
	public void OpenSearchDocumentParser_readFromFile() {
		OpensearchDescription document = readFromXMLFile("OpenSearchTestDescription.xml");
		assertEquals(new String("foo"), new String("foo"));

		assertEquals(document.adultContent, false);
		assertEquals(document.attribution,
				"Search data Copyright 2005, Example.com, Inc., All Rights Reserved");
		assertEquals(document.contact, "admin@example.com");
		assertEquals(document.description, "Use Example.com to search the Web.");
		assertEquals(document.developer, "Example.com Development Team");
		assertThat(document.inputEncodings,
				is(Arrays.asList(StringUtils.split("UTF-8 UTF-7", " "))));
		assertThat(document.outputEncodings,
				is(Arrays.asList(StringUtils.split("UTF-8 UTF-7", " "))));
		assertThat(document.languages,
				is(Arrays.asList(StringUtils.split("en-us de-at", " "))));
		assertEquals(document.longName, "Example.com Web Search");
		assertEquals(document.shortName, "Web Search");
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.OPEN);
		assertThat(document.tags,
				is(Arrays.asList(StringUtils.split("example web", " "))));
		assertEquals(document.xmlns, "http://a9.com/-/spec/opensearch/1.1/");

		List<Image> images = new ArrayList<Image>();

		images.add(new Image(64, 64, "image/png",
				"http://example.com/websearch.png"));
		images.add(new Image(16, 16, "image/vnd.microsoft.icon",
				"http://example.com/websearch.ico"));
		assertThat(document.images, is(images));

		List<Query> queries = new ArrayList<Query>();
		queries.add(new Query("example", "", "cat", "", "", "", -1, -1, -1, -1));
		assertThat(document.queries, is(queries));

		List<Url> searchLinks = new ArrayList<Url>();
		searchLinks
				.add(new Url(
						"application/atom+xml",
						"http://example.com/?q={searchTerms}&pw={startPage?}&format=atom",
						UrlRel.UNDEFINED, -1, -1));
		searchLinks
				.add(new Url(
						"application/rss+xml",
						"http://example.com/?q={searchTerms}&pw={startPage?}&format=rss",
						UrlRel.UNDEFINED, -1, -1));
		searchLinks.add(new Url("text/html",
				"http://example.com/?q={searchTerms}&pw={startPage?}",
				UrlRel.UNDEFINED, -1, -1));
		assertThat(document.searchLinks, is(searchLinks));

	}

	/**
	 * Parse XML from relativeFilepath and build an
	 * {@link OpensearchDescription}
	 * 
	 * @param relativeFilepath
	 * @return
	 */
	private OpensearchDescription readFromXMLFile(String relativeFilepath) {
		InputStream inputFile = OpenSearchDocumentParserTest.class.getResourceAsStream(
				relativeFilepath);

		OpensearchDescription document = null;
		
		InputStreamReader fileReader = new InputStreamReader(inputFile);

		try {
			StringBuilder xmlDocumentDescription = new StringBuilder();
			int character = fileReader.read();
			while (character != -1) {
				xmlDocumentDescription.append((char) character);
				character = fileReader.read();
			}
			fileReader.close();

			OpenSearchDocumentParser parser = new OpenSearchDocumentParser();
			document = parser.toDescriptionDocument(xmlDocumentDescription
					.toString());

		} catch (IOException e) {
			logger.log(Level.ERROR, e);
		}
		return document;
	}

	/**
	 * Verify all possible '<url rel="?">' attribute values.
	 */
	@Test
	public void OpenSearchDescriptionParser_readUrlRelAttributes() {
		String relTemplate = "<OpenSearchDescription> <Url rel=\"${rel}\" /> </OpenSearchDescription>";
		OpenSearchDocumentParser parser = new OpenSearchDocumentParser();

		OpensearchDescription document = parser
				.toDescriptionDocument(relTemplate.replace("${rel}", "results"));
		assertEquals(1, document.searchLinks.size());
		assertEquals(document.searchLinks.get(0).rel, Url.UrlRel.RESULTS);

		document = parser.toDescriptionDocument(relTemplate.replace("${rel}",
				"Results"));
		assertEquals(1, document.searchLinks.size());
		assertEquals(document.searchLinks.get(0).rel, Url.UrlRel.UNDEFINED);

		document = parser.toDescriptionDocument(relTemplate.replace("${rel}",
				"collection"));
		assertEquals(1, document.searchLinks.size());
		assertEquals(document.searchLinks.get(0).rel, Url.UrlRel.COLLECTION);

		document = parser.toDescriptionDocument(relTemplate.replace("${rel}",
				"self"));
		assertEquals(1, document.searchLinks.size());
		assertEquals(document.searchLinks.get(0).rel, Url.UrlRel.SELF);

		document = parser.toDescriptionDocument(relTemplate.replace("${rel}",
				"suggestions"));
		assertEquals(1, document.searchLinks.size());
		assertEquals(document.searchLinks.get(0).rel, Url.UrlRel.SUGGESTIONS);

		document = parser.toDescriptionDocument(relTemplate.replace("${rel}",
				"asdf"));
		assertEquals(1, document.searchLinks.size());
		assertEquals(document.searchLinks.get(0).rel, Url.UrlRel.UNDEFINED);

	}

	/**
	 * Verify all possible '<SyndicationRight>?</SyndicationRight>' values.
	 */
	@Test
	public void OpenSearchDescriptionParser_readSyndicationRightElement() {
		String relTemplate = "<OpenSearchDescription> <SyndicationRight>${right}</SyndicationRight></OpenSearchDescription>";
		OpenSearchDocumentParser parser = new OpenSearchDocumentParser();

		OpensearchDescription document = parser
				.toDescriptionDocument(relTemplate.replace("${right}", "open"));
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.OPEN);

		document = parser.toDescriptionDocument(relTemplate.replace("${right}",
				"Closed"));
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.UNDEFINED);

		document = parser.toDescriptionDocument(relTemplate.replace("${right}",
				"closed"));
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.CLOSED);

		document = parser.toDescriptionDocument(relTemplate.replace("${right}",
				"limited"));
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.LIMITED);

		document = parser.toDescriptionDocument(relTemplate.replace("${right}",
				"private"));
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.PRIVATE);

		document = parser.toDescriptionDocument(relTemplate.replace("${right}",
				"asdf"));
		assertEquals(document.syndicationRight,
				OpensearchDescription.SyndicationRight.UNDEFINED);

	}
}
