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

package eu.eexcess.diversityasurement.wikipedia;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import eu.eexcess.diversityasurement.wikipedia.config.Settings;

public class RDFCategoryExtractorTest {

	private static class CategorySubcategoryGlue {
		CategorySubcategoryGlue(String child, String parent) {
			this.parent = parent;
			this.child = child;
		}

		String parent;
		String child;
	}

	@Test
	public void categoryRDFPattern_givenSampleInput_expectCorrectMatches() {
		if (!Settings.RDFCategories.isCategoryFileAvailable()) {
			return;
		}
		Set<CategorySubcategoryGlue> expectedResults = getExpectedSampleResults();
		RDFCategoryExtractor builder = new RDFCategoryExtractor(null, null);
		Pattern pattern = builder.categoryRDFPattern;
		for (String entry : getSampleEntries()) {
			Matcher matcher = pattern.matcher(entry);
			System.out.println("group count [" + matcher.groupCount() + "] against [" + entry + "]");
			if (matcher.find()) {
				assertEquals(2, matcher.groupCount());
				CategorySubcategoryGlue g = expectedResults.iterator().next();
				System.out.println("expeced parent[" + g.parent + "] child [" + g.child + "]");
				System.out.println("found   parent[" + matcher.group(2) + "] child [" + matcher.group(1) + "]");
				assertEquals(g.parent, matcher.group(2));
				assertEquals(g.child, matcher.group(1));
				expectedResults.remove(g);
			}
		}
		assertEquals(0, expectedResults.size());
	}

	private Set<String> getSampleEntries() {
		HashSet<String> entries = new LinkedHashSet<String>();
		entries.add("<http://dbpedia.org/resource/Category:Linear_algebra> <http://www.w3.org/2004/02/skos/core#prefLabel> \"Linear algebra\"@en .");
		entries.add("<http://dbpedia.org/resource/Category:Linear_algebra> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Algebra> .");
		entries.add("<http://dbpedia.org/resource/Category:Linear_algebra> <http://www.w3.org/2004/02/skos/core#related> <http://dbpedia.org/resource/Category:Affine_geometry> .");
		entries.add("<http://dbpedia.org/resource/Category:Calculus> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept> .");
		entries.add("<http://dbpedia.org/resource/Category:Calculus> <http://www.w3.org/2004/02/skos/core#prefLabel> \"Calculus\"@en .");
		entries.add("<http://dbpedia.org/resource/Category:Calculus> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Mathematical_analysis> .");
		entries.add("<http://dbpedia.org/resource/Category:Calculus> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Fields_of_mathematics> .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept> .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/2004/02/skos/core#prefLabel> \"Monarchs\"@en .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Heads_of_state> .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Royalty> .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Monarchy> .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Government_officials_with_life_tenure> .");
		entries.add("<http://dbpedia.org/resource/Category:Monarchs> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Oligarchs> .");
		entries.add("<http://dbpedia.org/resource/Category:British_monarchs> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept> .");
		entries.add("<http://dbpedia.org/resource/Category:British_monarchs> <http://www.w3.org/2004/02/skos/core#prefLabel> \"British monarchs\"@en .");
		return entries;
	}

	private Set<CategorySubcategoryGlue> getExpectedSampleResults() {
		Set<CategorySubcategoryGlue> categories = new LinkedHashSet<RDFCategoryExtractorTest.CategorySubcategoryGlue>();
		categories.add(new CategorySubcategoryGlue("Linear_algebra", "Algebra"));
		categories.add(new CategorySubcategoryGlue("Calculus", "Mathematical_analysis"));
		categories.add(new CategorySubcategoryGlue("Calculus", "Fields_of_mathematics"));
		categories.add(new CategorySubcategoryGlue("Monarchs", "Heads_of_state"));
		categories.add(new CategorySubcategoryGlue("Monarchs", "Royalty"));
		categories.add(new CategorySubcategoryGlue("Monarchs", "Monarchy"));
		categories.add(new CategorySubcategoryGlue("Monarchs", "Government_officials_with_life_tenure"));
		categories.add(new CategorySubcategoryGlue("Monarchs", "Oligarchs"));
		return categories;
	}

	@Test
	public void extract_notExceptional() throws Exception {
		if (!Settings.RDFCategories.isCategoryFileAvailable()) {
			return;
		}

		CategoryTupleCollector mockCollector = new CategoryTupleCollector() {
			@Override
			public void takeTuple(String parent, String child) throws Exception {
			}
		};

		RDFCategoryExtractor builder = new RDFCategoryExtractor(new File(Settings.RDFCategories.PATH), mockCollector);
		builder.extract();
	}

}
