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

package eu.eexcess.wikipedialocal.recommender;

import java.io.IOException;

import org.junit.Test;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;

public class PartnerConnectorTest {

	@Test
	public void queryPartnerNative_queryThreeTimes_expectOneTimeBoostrapping() {

		PartnerConfiguration configuration = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();
		ContextKeyword schroedinger = new ContextKeyword("Erwin Schrödinger");
		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.add(schroedinger);
		PartnerConnector connector = new PartnerConnector();

			String baseSearchEndpoint = configuration.searchEndpoint;
			ResultList firstTryResults = null;
			try {
				firstTryResults = connector.queryPartnerNative(configuration, userProfile,null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(firstTryResults);
			assert(609==firstTryResults.totalResults);
}
			
		

	
}
