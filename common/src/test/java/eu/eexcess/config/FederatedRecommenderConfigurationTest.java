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

package eu.eexcess.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class FederatedRecommenderConfigurationTest {

	@Test
	public void federatedRecommenderConfig_testArrayOrder_expectSameAsInInput() {
		String json = "{ \"sourceSelectors\" : [ \"value1\", \"value2\", \"value3\"]}";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		try {
			FederatedRecommenderConfiguration config = mapper.readValue(json, FederatedRecommenderConfiguration.class);
			assertEquals(3, config.sourceSelectors.length);
			assertEquals("value1", config.sourceSelectors[0]);
			assertEquals("value2", config.sourceSelectors[1]);
			assertEquals("value3", config.sourceSelectors[2]);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
}
