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

package eu.eexcess.diversityasurement.wikipedia.config;

import java.io.File;

/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH"
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Raoul Rubien
 */

public class Settings {

	public static class RDFCategories {
		public static final String PATH = "/opt/data/wikipedia/dbpedia/skos_categories_en.nt";

		public static boolean isCategoryFileAvailable() {
			return printWarning(PATH);
		}
	}

	public static class SQLiteDb {
		public static final String PATH = "/opt/iaselect/categories.sqlite";

		public static boolean isDBFileAvailable() {
			return printWarning(PATH);
		}
	}

	public static class Grph {
		public static final String PATH = "/opt/iaselect/gategories.grphbin";

		public static boolean isGrphFileAvailable() {
			return printWarning(PATH);
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
