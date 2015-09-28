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

package eu.eexcess.federatedrecommender.utils.wordnet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import eu.eexcess.federatedrecommender.config.Settings;
import eu.eexcess.federatedrecommender.utils.tree.NodeInspector;
import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueTreeNode;

public class WordnetDomainTreeInflatorTest {

    private int nodeCount;
    private NodeInspector<String> nodeCounter = (n) -> {
        WordnetDomainTreeInflatorTest.this.nodeCount++;
        return false;
    };

    @Test
    public void inflateDomainTree_readTree_exptectCorrectNodeCountInTree() {

        if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable() && Settings.isWordNetDomainsResourceAvailable()) {
            try {
                ValueTreeNode<String> domainTree = WordnetDomainTreeInflator.inflateDomainTree(new File(Settings.WordnetDomains.CSVDomainPath));

                nodeCount = 0;
                ValueTreeNode.depthFirstTraverser(domainTree, nodeCounter);
                assertEquals(168 + 1, nodeCount);
            } catch (IOException e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }

    @Test
    public void inflateDomainTree_readTree_exptectCorrectDomainsAtDepth2and4() {

        if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable() && Settings.isWordNetDomainsResourceAvailable()) {
            try {
                TreeNode<String> domainTree = WordnetDomainTreeInflator.inflateDomainTree(new File(Settings.WordnetDomains.CSVDomainPath));

                // count level 2
                int subdomainsCount = 0;
                for (TreeNode<String> node : domainTree.getChildren()) {
                    subdomainsCount += node.getChildren().size();
                }
                assertEquals(45, subdomainsCount);

                // count level 4
                subdomainsCount = 0;
                for (TreeNode<String> level1 : domainTree.getChildren()) {
                    for (TreeNode<String> level2 : level1.getChildren()) {
                        for (TreeNode<String> level3 : level2.getChildren()) {
                            subdomainsCount += level3.getChildren().size();
                        }
                    }
                }
                assertEquals(12, subdomainsCount);

            } catch (IOException e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }

}
