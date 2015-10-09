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

package eu.eexcess.federatedrecommender.sourceselection.wordnetdomainsourceselection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.utils.tree.BaseTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.NodeInspector;
import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueTreeNode;
import eu.eexcess.federatedrecommender.utils.wordnet.WordnetDomainTreeInflator;

/**
 * Not thread save weighted WordNet domain tree construction utility class.
 * 
 * @author Raoul Rubien
 */
public class WordnetDomainTreeConstructor {

    private static final Logger LOGGER = Logger.getLogger(WordnetDomainTreeConstructor.class.getName());

    private ValueTreeNode<Double> templateTree = null;
    private ValueTreeNode<Double> treeInProcess = null;
    private Map<String, PartnerDomain> domainsInProcess = new HashMap<String, PartnerDomain>();
    private Map<String, ValueTreeNode<Double>> clonedNodesMap = new HashMap<String, ValueTreeNode<Double>>();

    /**
     * TODO: externalize string to {@link FederatedRecommenderConfiguration} if
     * possible<br>
     * configures the file where the domain tree can be found as csv
     */
    private String wordnetDomainStructureCsvFile = "wn-domains-3.2-tree.csv";

    /**
     * Inflates the a not weighted WordNet domain template tree from csv file to
     * an {@link ValueTreeNode<String>} tree.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    WordnetDomainTreeConstructor(File wordnetDomainPath) {
        WordnetDomainTreeInflator baseTreeInflator = WordnetDomainTreeInflator.newDoubleValueTreeNodeInflator();

        try {
            File wordnetDomainStructure = new File(wordnetDomainPath.getParent() + "/" + wordnetDomainStructureCsvFile);
            templateTree = (ValueTreeNode<Double>) baseTreeInflator.inflateDomainTree(wordnetDomainStructure, true);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "failed to infalte domain tree", ioe);
        }

    }

    /**
     * clones each invoked node to
     * {@link WordnetDomainTreeConstructor#clonedNodesMap}
     * 
     * @author Raoul Rubien
     *
     */
    private class TreeNodeDuplicator implements NodeInspector {
        @Override
        public boolean invoke(TreeNode n) {
            @SuppressWarnings("unchecked")
            Double nodeValue = ((ValueTreeNode<Double>) n).getValue();
            clonedNodesMap.put(n.getName(), new ValueTreeNode<Double>(n.getName(), new Double(nodeValue)));
            return false;
        }
    }

    /**
     * links each cloned node regarding to its equivalent node's children
     * 
     * @author Raoul Rubien
     *
     */
    private class TreeNodeLinker implements NodeInspector {
        @Override
        public boolean invoke(TreeNode n) {

            if (treeInProcess == null) {
                treeInProcess = clonedNodesMap.get(n.getName());
            }

            ValueTreeNode<Double> clonedNode = clonedNodesMap.get(n.getName());

            PartnerDomain domain = domainsInProcess.remove(n.getName());
            if (null != domain) {
                clonedNode.setValue(domain.getWeight());
            }

            for (TreeNode child : n.getChildren()) {
                clonedNode.addChild(clonedNodesMap.get(child.getName()));
            }

            return false;
        }
    }

    /**
     * Returns an neutrally weighted tree except of the predefined node weights
     * will be taken as new weight.
     * 
     * @param domains
     *            predefined domain weights
     * @return a complete WordnetDomain tree
     */
    public ValueTreeNode<Double> newTree(List<PartnerDomain> domains) {

        for (PartnerDomain domain : domains) {
            domainsInProcess.put(domain.getName(), domain);
        }

        BaseTreeNode.depthFirstTraverser(templateTree, new TreeNodeDuplicator());
        BaseTreeNode.depthFirstTraverser(templateTree, new TreeNodeLinker());

        clonedNodesMap.clear();
        ValueTreeNode<Double> tree = treeInProcess;
        treeInProcess = null;
        domainsInProcess.clear();

        return tree;
    }
}
