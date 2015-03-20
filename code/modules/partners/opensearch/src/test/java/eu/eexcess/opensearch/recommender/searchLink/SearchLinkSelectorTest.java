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

package eu.eexcess.opensearch.recommender.searchLink;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Url;
import eu.eexcess.opensearch.opensearchDescriptionDocument.documentFields.Url.UrlRel;
import eu.eexcess.opensearch.recommender.searchLink.SearchLinkFilter;
import eu.eexcess.opensearch.recommender.searchLink.SearchLinkSelector;

public class SearchLinkSelectorTest {

//	@Test
//	public void select_withLinksAndMethodFilter_expectCompleteSelection() {
//
//		SearchLinkFilter filter = new SearchLinkFilter();
//		filter.setMethod("POST");
//
//		List<Url> links = newDummyLinks(10, "application/json", "POST",
//				"www.searchendpoint.edu/search.php");
//
//		SearchLinkSelector selector = new SearchLinkSelector();
//		List<Url> selection = selector.select(links, filter);
//
//		assertEquals(links.size(), selection.size());
//	}

	@Test
	public void select_withLinksAndMimeTypeFilter_expectOneSelected() {

		SearchLinkFilter filter = new SearchLinkFilter();
		filter.setType("application/json");

		List<Url> links = newDummyLinks(10, "application/json",
				"www.searchendpoint.edu/search.php");

		SearchLinkSelector selector = new SearchLinkSelector();
		List<Url> selection = selector.select(links, filter);

		assertEquals(1, selection.size());
	}

	@Test
	public void select_withLinksAndFilter_expectOneSelected() {

		SearchLinkFilter filter = new SearchLinkFilter();
		filter.setType("application/json[9]");
//		filter.setMethod("POST");

		List<Url> links = newDummyLinks(10, "application/json",
				"www.searchendpoint.edu/search.php");

		SearchLinkSelector selector = new SearchLinkSelector();
		List<Url> selection = selector.select(links, filter);

		assertEquals(1, selection.size());
	}

	@Test
	public void select_withNoLinksButFilter_expectNoneSelected() {

		SearchLinkFilter filter = new SearchLinkFilter();
		filter.setType("application/json");
//		filter.setMethod("POST");

		List<Url> links = new ArrayList<Url>();

		SearchLinkSelector selector = new SearchLinkSelector();
		List<Url> selection = selector.select(links, filter);

		assertEquals(0, selection.size());
	}

	@Test
	public void select_withLinksButNoFilter_expectCompleteSelection() {

		SearchLinkFilter filter = new SearchLinkFilter();

		List<Url> links = newDummyLinks(10, "application/json",
				"www.searchendpoint.edu/search.php");

		SearchLinkSelector selector = new SearchLinkSelector();
		List<Url> selection = selector.select(links, filter);

		assertEquals(10, selection.size());
	}

	@Test
	public void select_withLinksButNullFilter_expectCompleteSelection() {

		List<Url> links = newDummyLinks(10, "application/json",
				"www.searchendpoint.edu/search.php");

		SearchLinkSelector selector = new SearchLinkSelector();
		List<Url> selection = selector.select(links, null);

		assertEquals(10, selection.size());
	}
	
//	@Test
//	public void select_withLinksAndFilter_expectNoneSelected() {
//
//		SearchLinkFilter filter = new SearchLinkFilter();
////		filter.setMethod("GET");
//		filter.setType("application/xyz");
//		
//		List<Url> links = newDummyLinks(10, "application/json",
//				"www.searchendpoint.edu/search.php");
//
////		links.get(2).method = "GET";
//		links.get(3).type = "application/xyz";
//		links.get(4).type = "application/xyz";
//		
//		SearchLinkSelector selector = new SearchLinkSelector();
//		List<Url> selection = selector.select(links, filter);
//
//		assertEquals(0, selection.size());
//	}
	
	@Test
	public void select_withLinksAndFilter_expectThreeSelected() {

		SearchLinkFilter filter = new SearchLinkFilter();
//		filter.setMethod("GET");
		filter.setType("application/xyz");
		
		List<Url> links = newDummyLinks(10, "application/json",
				"www.searchendpoint.edu/search.php");

//		links.get(2).method = "GET";
		links.get(2).type = "application/xyz";
//		links.get(3).method = "GET";
		links.get(3).type = "application/xyz";
//		links.get(4).method = "GET";
		links.get(4).type = "application/xyz";
		
		SearchLinkSelector selector = new SearchLinkSelector();
		List<Url> selection = selector.select(links, filter);

		assertEquals(3, selection.size());
	}

	/**
	 * every link returned list with an index > 0 has it appended (i.e. "[1]")to
	 * mime type and search end point.
	 * 
	 * @param amount
	 *            number of dummy links
	 * @param mimeType
	 *            a dummy mime type
	 * @param method
	 *            usually GET or POST
	 * @param searchEndpoint
	 *            an arbitrary URL
	 * @return
	 */
	private List<Url> newDummyLinks(int amount, String mimeType,
			String searchEndpoint) {
		List<Url> dummyLinks = new ArrayList<Url>();

		for (int idx = 0; idx < amount; idx++) {

			if (idx == 0) {
				dummyLinks.add(new Url(mimeType, searchEndpoint + idx,
						UrlRel.UNDEFINED, 0, 0));
			} else {
				dummyLinks.add(new Url(mimeType + "[" + idx + "]",
						searchEndpoint + "[" + idx + "]", UrlRel.UNDEFINED, 0,
						0));
			}
		}
		return dummyLinks;
	}
}
