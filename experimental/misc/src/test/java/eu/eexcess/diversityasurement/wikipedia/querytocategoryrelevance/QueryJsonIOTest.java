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

package eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import eu.eexcess.diversityasurement.wikipedia.config.Settings;

public class QueryJsonIOTest {

	@Test
	public void readQueries_givenRegularFile_expectCorrectAmountOfQueries() throws FileNotFoundException, IOException {
		if (!Settings.Queries.isQueriesFileAvailable()) {
			return;
		}

		Queries queries = QueryJsonIO.readQueries(new File(Settings.Queries.PATH));
		assertNotNull(queries);
		assertTrue(queries.queries.length > 0);
		assertEquals(76, queries.queries.length);

		assertEquals(queries.queries[0].query, "occupation zone");
		assertEquals(queries.queries[0].description, "TODO: decription");

		assertEquals(queries.queries[75].query, "Burnt Corn Creek");
		assertEquals(queries.queries[75].description, "TODO: decription");
	}

	@Test
	public void writeQueries_expectNotExceptional() throws IOException {
		if (!Settings.Queries.isQueriesFileAvailable()) {
			return;
		}

		Queries queries = QueryJsonIO.readQueries(new File(Settings.Queries.PATH));
		File temp = File.createTempFile("temp", ".txt");
		QueryJsonIO.writeQueries(temp, queries);

		Queries otherQueries = QueryJsonIO.readQueries(temp);

		for (int idx = 0; idx < queries.queries.length; idx++) {
			assertEquals(queries.queries[idx].query, otherQueries.queries[idx].query);
			assertEquals(queries.queries[idx].description, otherQueries.queries[idx].description);
		}
		temp.deleteOnExit();
	}
}
