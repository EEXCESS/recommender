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

package eu.eexcess.federatedrecommender.sourceselection.domains;

import java.io.File;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.federatedrecommender.utils.domains.DomainTreeInflator;
import eu.eexcess.federatedrecommender.utils.domains.wordnet.WordnetDomainTreeInflator;

/**
 * Implementation of {@link DomainTreeInflator} construction.
 * 
 * @author Raoul Rubien
 *
 */
public class DomainTreeConstructorBuilder {

    /**
     * configures the file where the domain tree can be found as csv
     */
    private static final String wordnetDomainStructureCsvFile = "wn-domains-3.2-tree.csv";

    /**
     * Constructs a WordNet domain tree constructor.
     * 
     * @param wordnetDomainPath
     *            path to WordNet domain
     * @return a WordNet domain tree constructor reproducing weighted domain nodes trees
     */
    public static TreeConstructor newWordnetDomainTreeConstructor(File wordnetDomainPath) {

        File wordnetDomainStructure = new File(wordnetDomainPath.getParent() + "/" + wordnetDomainStructureCsvFile);
        WordnetDomainTreeInflator baseTreeInflator = eu.eexcess.federatedrecommender.utils.domains.DomainTreeInflatorBuilder
                .newDoubleValueTreeNodeInflator(wordnetDomainStructure);

        return new WordnetDomainTreeConstructor(baseTreeInflator);
    }
}
