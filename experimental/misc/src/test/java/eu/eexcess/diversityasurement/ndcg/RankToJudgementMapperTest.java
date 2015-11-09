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

package eu.eexcess.diversityasurement.ndcg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.eexcess.diversityasurement.ndcg.NDCG.RankToJudgementMapper;

public class RankToJudgementMapperTest {

	@Test
	public void testMappings_expectCorrectMappings() {

		double maxRank = 42.0;
		double delta = 1E-10;
		RankToJudgementMapper m = new RankToJudgementMapper(maxRank);

		assertEquals(0, m.r(0 - delta));
		assertEquals(0, m.r(0));

		assertEquals(0, m.r(maxRank * 1.0/5.0));
		assertEquals(1, m.r(maxRank * 1.0/5.0 + delta));

		assertEquals(1, m.r(maxRank * 2.0/5.0));
		assertEquals(2, m.r(maxRank * 2.0/5.0 + delta));

		assertEquals(2, m.r(maxRank * 3.0/5.0));
		assertEquals(3, m.r(maxRank * 3.0/5.0 + delta));
		
		assertEquals(3, m.r(maxRank * 4.0/5.0));
		assertEquals(4, m.r(maxRank * 4.0/5.0 + delta));

		assertEquals(4, m.r(maxRank));
		assertEquals(4, m.r(maxRank + delta));
	}
}
