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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.SQLException;

import org.junit.Test;

import eu.eexcess.diversityasurement.wikipedia.config.Settings;

public class SQLiteTupleCollectorTest {

	@Test
	public void sqliteTupleCollector_collectTuple_notExceptional() throws Exception {
		SQliteTupleCollector collector = new SQliteTupleCollector(new File(Settings.SQLiteDb.PATH));
		try {
			collector.takeTuple("foo-parent", "bar-child");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			collector.close();
		}
	}

}
