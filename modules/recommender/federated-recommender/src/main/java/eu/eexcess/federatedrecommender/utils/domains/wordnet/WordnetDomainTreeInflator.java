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

package eu.eexcess.federatedrecommender.utils.domains.wordnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.LineIterator;

import eu.eexcess.federatedrecommender.utils.domains.DomainTreeInflator;
import eu.eexcess.federatedrecommender.utils.tree.BaseTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.factory.TreeNodeFactory;

/**
 * This class takes a WordNet domain csv and builds a domain tree structure
 * 
 * @author Raoul Rubien
 *
 */
public class WordnetDomainTreeInflator implements DomainTreeInflator {

    private static final String ROOT_NODE_NAME = "factotum";
    private static final String TOKEN_DELIMITER = "[,]";
    private TreeNodeFactory nodeFactory = null;
    private File wordnetCSVTreeFile = null;

    public WordnetDomainTreeInflator(TreeNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public WordnetDomainTreeInflator setStructureFile(File wordnetCSVTreeFile) {
        this.wordnetCSVTreeFile = wordnetCSVTreeFile;
        return this;
    }

    /**
     * call to {@link #inflateDomainTree(File, boolean)} 2nd argument = true
     * 
     */
    @Override
    public TreeNode inflateDomainTree() throws FileNotFoundException {
        return inflateDomainTree(true);
    }

    /**
     * 
     * @param wordnetCSVTreeFile
     * @param domainsToLowerCase
     *            if true domain names re converted to lower case
     * @return
     * @throws FileNotFoundException
     */
    public TreeNode inflateDomainTree(boolean domainsToLowerCase) throws FileNotFoundException {
        LineIterator iterator = new LineIterator(new FileReader(wordnetCSVTreeFile));
        String[] currentBranch = new String[5];
        currentBranch[0] = ROOT_NODE_NAME;

        // ValueTreeNode<String> treeRootNode = new ValueTreeNode<String>();
        TreeNode treeRootNode = nodeFactory.createTreeNode(ROOT_NODE_NAME);

        while (iterator.hasNext()) {

            // read current node and store its parents
            String line = iterator.nextLine();
            String[] tokensInLine = line.split(TOKEN_DELIMITER);

            int depth = -1;
            for (int i = 0; i < tokensInLine.length; i++) {
                tokensInLine[i] = tokensInLine[i].trim();
                if (!tokensInLine[i].isEmpty()) {
                    depth = i;
                    currentBranch[1 + depth] = tokensInLine[i];
                }
            }
            // clear tail
            for (int tail = depth + 2; tail < currentBranch.length; tail++) {
                currentBranch[tail] = null;
            }

            // reconstruct and append the missing branch according to the
            // current tree
            TreeNode branch = null;
            for (int branchDepth = currentBranch.length; branchDepth > 0; branchDepth--) {

                String nodeName = currentBranch[branchDepth - 1];

                if (nodeName == null) {
                    continue;
                }

                if (domainsToLowerCase) {
                    nodeName = nodeName.toLowerCase();
                }

                Set<TreeNode> result = new HashSet<TreeNode>();

                TreeNode node = nodeFactory.createTreeNode(nodeName);
                BaseTreeNode.findFirstNode(node, treeRootNode, result);
                TreeNode nodeInTree = null;
                if (result.iterator().hasNext()) {
                    nodeInTree = result.iterator().next();
                }

                // if node ∈ tree -> add branch to tree
                if (nodeInTree != null) {
                    if (branch != null) {
                        nodeInTree.addChild(branch);
                        branch = null;
                    }
                    break;
                    // if node !∈ tree -> reconstruct the branch until the mount
                    // point is clear
                } else {
                    TreeNode newParent = nodeFactory.createTreeNode(nodeName);

                    if (branch != null) {
                        newParent.addChild(branch);
                    }
                    branch = newParent;
                }
            }
        }
        iterator.close();
        return treeRootNode;
    }
}
