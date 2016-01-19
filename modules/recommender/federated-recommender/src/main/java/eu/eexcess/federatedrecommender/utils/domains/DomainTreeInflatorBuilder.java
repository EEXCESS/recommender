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

package eu.eexcess.federatedrecommender.utils.domains;

import eu.eexcess.federatedrecommender.utils.domains.wordnet.WordnetDomainTreeInflator;
import eu.eexcess.federatedrecommender.utils.tree.BaseTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueSetTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.factory.BaseTreeNodeFactory;
import eu.eexcess.federatedrecommender.utils.tree.factory.DoubleValueTreeNodeFactory;
import eu.eexcess.federatedrecommender.utils.tree.factory.StringValueSetTreeNodeFactory;

import java.io.File;

public class DomainTreeInflatorBuilder {

    /**
     * @return instance of {@link WordnetDomainTreeInflator} creating a tree of
     *         {@link BaseTreeNode}&lt;String&gt; nodes
     */
    public static WordnetDomainTreeInflator newBaseTreeNodeInflator(File wordnetCSVTreeFile) {
        return new WordnetDomainTreeInflator(new BaseTreeNodeFactory()).setStructureFile(wordnetCSVTreeFile);
    }

    /**
     * @return instance of {@link WordnetDomainTreeInflator} creating a tree of
     *         {@link ValueSetTreeNode}&lt;String&gt; nodes
     */
    public static WordnetDomainTreeInflator newStringValueSetTreeNodeInflator(File wordnetCSVTreeFile) {
        return new WordnetDomainTreeInflator(new StringValueSetTreeNodeFactory()).setStructureFile(wordnetCSVTreeFile);
    }

    /**
     * @return instance of {@link WordnetDomainTreeInflator} creating a tree of
     *         {@link ValueTreeNode}&lt;String, Double&gt; nodes
     */
    public static WordnetDomainTreeInflator newDoubleValueTreeNodeInflator(File wordnetCSVTreeFile) {
        return new WordnetDomainTreeInflator(new DoubleValueTreeNodeFactory()).setStructureFile(wordnetCSVTreeFile);
    }

}
