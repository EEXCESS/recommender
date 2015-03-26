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

package eu.eexcess.sourceselection.redde.indexer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.sf.extjwnl.JWNLException;

import org.junit.Test;

import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.sourceselection.redde.dbsampling.DBSampler;

public class BinaryIndexResourceTest {

	@SuppressWarnings("resource")
	@Test
	public void openOutIndex_openClose_notExceptional() {

		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet30ResourceAvailable()) {
			try {
				BinaryIndexResource sampler = new DBSampler(Settings.BaseIndex.baseIndexPath,
								Settings.BaseIndex.sampledIndexPath, Settings.LuceneVersion, Settings.WordNet.Path_3_0);
				sampler.openOutIndex();
				sampler.closeOutIndex();
			} catch (JWNLException | IOException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}

	}
}
