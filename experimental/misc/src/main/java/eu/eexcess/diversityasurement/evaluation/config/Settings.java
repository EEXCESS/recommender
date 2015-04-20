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

package eu.eexcess.diversityasurement.evaluation.config;

import java.io.File;

public class Settings {

	public static class RelevanceEvaluation {

		public static class Lucene {
			public static final String SEARCH_FIELD_SECTIONTEXT = "sectionText";
			public static final String SEARCH_FIELD_CATEGORY = "category";
			public static final String SEARCH_FIELD_TITLE = "title";
		}

		public static class IOFiles {
			public static final File inLuceneIndexParentDirectory = new File("/opt/data/wikipedia/eexcess/");
			public static final File inLuceneIndexDirectory = new File("/opt/data/wikipedia/eexcess/enwiki-big/");
			
			public static final File outLuceneIndexDirectory = new File(
							"/opt/iaselect/results/category-relevance-index-big/");

			public static final File outCachedNodes = new File("/opt/iaselect/results/cache/cached-nodes.bin");
			public static final File outCachedPaths = new File("/opt/iaselect/results/cache/cached-paths.bin");

			public static final File outCategoryIdToName = new File(
							"/opt/iaselect/results/cache/category-id-to-name.bin");
			public static final File outCategoryNameToId = new File(
							"/opt/iaselect/results/cache/category-name-to-id.bin");
			public static final File outRelevances = new File("/opt/iaselect/results/cache/category-relevances.bin");

			public static final File inManuallySelectedWeightedNotNormalizedQueries = new File(
							"/opt/iaselect/results/queries/queriesEn-with-categories-filtered-weighted-not-normalized.json");
			
			public static final File additionalDefinedCategoryIDs = new File ("/opt/iaselect/results/queries/in-additional-category-ids.json");
		}

		public static class EstimationArguments {
			public static int numTopDocumentsToConsider = 60;
			public static int numKClosestCategoryNeighborsToConsider = 1000;
			public static int kShortestPaths = 1;
			public static int nodesPerChunk = 2000;
			public static boolean isDistributionStartedAtSiblings = false;
		}
	}

	public static class ContrastEvaluation {
		public static class IOFIles {
			public static final File inCategoryIdToName = new File(
							"/opt/iaselect/results/cache/category-id-to-name.bin");
			public static final File inCategoryNameToId = new File(
							"/opt/iaselect/results/cache/category-name-to-id.bin");
			public static final File inRelevances = new File("/opt/iaselect/results/cache/category-relevances.bin");
		}
	}

	public static class Queries {
		public static final String PATH = "/opt/iaselect/queriesEn-selected-diversity.json";

		public static boolean isQueriesFileAvailable() {
			return printWarning(PATH);
		}
	}

	public static class QueryExpansionEvaluation {
		public static final int NUM_TOP_DOCS_TO_CONSIDER = 10;
		public static final int MAX_TERMS_TO_EXPAND_QUERY = 20;	
		
		public static class IOFiles {
			public static final File outExpandedCategoryIDs = new File("/opt/iaselect/results/queries/out-additional-category-ids.json");
		}
	}
	
	private static boolean printWarning(String path) {
		if (!(new File(path).canRead())) {
			System.err.println("resource does not exist [" + path + "]");
			return false;
		}
		return true;
	}
}
