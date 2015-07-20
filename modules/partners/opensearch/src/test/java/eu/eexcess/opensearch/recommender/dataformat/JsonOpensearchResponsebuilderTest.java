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

package eu.eexcess.opensearch.recommender.dataformat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.opensearch.opensearch_description_document.OpensearchDescription;
import eu.eexcess.opensearch.recommender.dataformat.JsonOpensearchResultListBuilder;
import eu.eexcess.opensearch.recommender.dataformat.OpensearchResultListBuilder;

public class JsonOpensearchResponsebuilderTest {

	private final OpensearchDescription opensearchDescription = new OpensearchDescription();
	private final String osDescriptionShortName = "Wikipedia (en)";
	private final String osDescriptionLongName = "Wikipedia (german)";

	@Before
	public void beforeTest() {
		opensearchDescription.shortName = osDescriptionShortName;
		opensearchDescription.longName = osDescriptionLongName;
	}

	/**
	 * Verify that {@link ResultList} can be built from valid JSON.
	 */
	@Test
	public void responseBuilder_buildFromJson_nonExceptional() {

		String response = "[ \"search term\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		ResultList osResponse = builder.build();

		assertNotNull(osResponse);
		assertEquals(null, osResponse.queryID);

		List<Result> results = new LinkedList<Result>();

		Result r = new Result();
		r.title = "short desc. 1";
		r.description = "description 1";
		r.documentBadge= new DocumentBadge("","link 1",osDescriptionShortName);
		results.add(r);

		r = new Result();
		r.title = "short desc. 2";
		r.description = "description 2";
		r.documentBadge= new DocumentBadge("","link 2",osDescriptionShortName);
		
		results.add(r);

		assertThat(osResponse.results, equalTo(results));
	}

	/**
	 * Verify that no {@link ResultList} is build if JSON contains lists with
	 * unequal length.
	 */
	@Test
	public void responseBuilder_buildFromJson_unequalListSizes_additionalShortDescription() {

		String response = "[ \"search term\", [ \"short desc. 1\", \"short desc. 2\", \"short desc. 3\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);
		assertNull(builder.build());
	}

	/**
	 * Verify that no {@link ResultList} is build if JSON contains lists with
	 * unequal length.
	 */
	@Test
	public void responseBuilder_buildFromJson_unequalListSizes_additionalDescription() {

		String response = "[ \"search term\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\", \"description 3\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);
		assertNull(builder.build());
	}

	/**
	 * Verify that no {@link ResultList} is build if JSON contains lists with
	 * unequal length.
	 */
	@Test
	public void responseBuilder_buildFromJson_unequalListSizes_additionalLink() {

		String response = "[ \"search term\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\", \"link 3\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);
		assertNull(builder.build());
	}

	/**
	 * Verify that {@link ResultList} is build even if search term exists but is
	 * an empty string.
	 */
	@Test
	public void responseBuilder_buildFromJson_missinSearchTerm() {

		String response = "[ \"\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		ResultList osResponse = builder.build();
		List<Result> results = new ArrayList<Result>();

		Result r = new Result();
		r.title = "short desc. 1";
		r.description = "description 1";
		r.documentBadge= new DocumentBadge("","link 1",osDescriptionShortName);
		results.add(r);

		r = new Result();
		r.title = "short desc. 2";
		r.description = "description 2";
		r.documentBadge= new DocumentBadge("","link 2",osDescriptionShortName);
		results.add(r);

		assertThat(osResponse.results, equalTo(results));
	}

	/**
	 * Verify that an {@link ResultList} can be built from empty JSON response.
	 */
	@Test
	public void responseBuilder_buildFromJson_emptryResponse() {

		String response = "[\"\", [],[],[]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		ResultList osResponse = builder.build();
		assertNotNull(osResponse);
		List<Result> results = new ArrayList<Result>();
		assertThat(osResponse.results, equalTo(results));
	}

	@Test
	public void responsebuilder_buildfromJsonandDescriptionDocument_EmptyShortName_expectLongNameFallback() {

		String response = "[ \"\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		opensearchDescription.shortName = "";

		ResultList osResponse = builder.build();
		assertNotNull(osResponse);

		assertEquals(osDescriptionLongName, osResponse.results.get(0).documentBadge.provider);

	}

	@Test
	public void responsebuilder_buildfromJsonandDescriptionDocument_NullShortName_expectLongNameFallback() {

		String response = "[ \"\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		opensearchDescription.shortName = null;

		ResultList osResponse = builder.build();
		assertNotNull(osResponse);

		assertEquals(osDescriptionLongName, osResponse.results.get(0).documentBadge.provider);

	}

	@Test
	public void responsebuilder_buildfromJsonandDescriptionDocument_EmptyLongName_expectShortNameFallback() {

		String response = "[ \"\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		opensearchDescription.longName = "";

		ResultList osResponse = builder.build();
		assertNotNull(osResponse);

		assertEquals(osDescriptionShortName, osResponse.results.get(0).documentBadge.provider);
	}

	@Test
	public void responsebuilder_buildfromJsonandDescriptionDocument_NullLongName_expectShortNameFallback() {

		String response = "[ \"\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		opensearchDescription.longName = null;

		ResultList osResponse = builder.build();
		assertNotNull(osResponse);

		assertEquals(osDescriptionShortName, osResponse.results.get(0).documentBadge.provider);
	}

	@Test
	public void responsebuilder_buildfromJsonandDescriptionDocument_NullShortOrLongName_expectEmptyStringFallback() {

		String response = "[ \"\", [ \"short desc. 1\", \"short desc. 2\"],[\"description 1\", \"description 2\"],[\"link 1\", \"link 2\"]]";

		OpensearchResultListBuilder builder = new JsonOpensearchResultListBuilder(new JSONArray(response),
						opensearchDescription);

		opensearchDescription.longName = null;
		opensearchDescription.shortName= null;

		ResultList osResponse = builder.build();
		assertNotNull(osResponse);

		assertEquals("", osResponse.results.get(0).documentBadge.provider);
	}
}
