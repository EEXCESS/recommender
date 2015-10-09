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

package eu.eexcess.federatedrecommender.utils.tree.factory;

import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueTreeNode;

/**
 * a {@link ValueTreeNode}&lt;String, Double&gt; factory implementation
 * 
 * @author Raoul Rubien
 *
 */
public class DoubleValueTreeNodeFactory implements TreeNodeFactory {

    @Override
    public TreeNode createTreeNode() {
        ValueTreeNode<Double> vtn = new ValueTreeNode<Double>();
        vtn.setValue(0.0);
        return vtn;
    }

    @Override
    public TreeNode createTreeNode(String nodeName) {
        ValueTreeNode<Double> vtn = new ValueTreeNode<Double>();
        vtn.setName(nodeName);
        vtn.setValue(0.0);
        return vtn;
    }
}
