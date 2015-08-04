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

package eu.eexcess.domaindetection.wikipedia.schools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import eu.eexcess.sourceselection.redde.tree.BaseTreeNode;
import eu.eexcess.sourceselection.redde.tree.TreeNode;

public class WikiediaSchoolsDomainTreeInflator extends IndexReaderRessource {

    private static final Logger LOGGER = Logger.getLogger(WikipediaSchoolsDumpIndexer.class.getName());
    private Map<String, TreeNode<String>> nodeMap = null;

    public WikiediaSchoolsDomainTreeInflator(File indexDirectory) {
        super(indexDirectory);
        open();
    }

    /**
     * Reads all domains from Lucene index and creates the tree structure. The
     * tree is an non-cyclic graph.
     * 
     * @return
     */
    public TreeNode<String> inflateDomainTree() {
        TreeNode<String> domainTree = null;
        nodeMap = new HashMap<String, TreeNode<String>>();

        try {
            for (int i = 0; i < indexReader.maxDoc(); i++) {
                Document doc = indexReader.document(i);
                String parentName = doc.get(WikipediaSchoolsDomainTreeIndexer.LUCENE_DOCUMENT_DOCUMENT_FIELD_NAME);

                TreeNode<String> parentNode = newOrCachedNode(parentName);

                if (domainTree == null) {
                    domainTree = parentNode;
                }

                for (IndexableField field : doc.getFields(WikipediaSchoolsDomainTreeIndexer.LUCENE_DOCUMENT_CHILD_FIELD_NAME)) {
                    String childName = field.stringValue();
                    TreeNode<String> childNode = newOrCachedNode(childName);
                    parentNode.addChild(childNode);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to read from index", e);
        }
        return domainTree;
    }

    /**
     * Creates and adds a new node to map or returns an already existent node.
     * 
     * @param nodeName
     *            name of the new node
     * @return the new or an already cached node
     */
    private TreeNode<String> newOrCachedNode(String nodeName) {
        TreeNode<String> theNode = nodeMap.get(nodeName);
        if (theNode == null) {
            theNode = new BaseTreeNode<String>(nodeName);
            nodeMap.put(nodeName, theNode);
        }
        return theNode;
    }

    /**
     * 
     * @return a map of same nodes created in {@link #inflateDomainTree()}
     * @throws IllegalStateException if {@link #inflateDomainTree()} was not called before
     */
    public Map<String, TreeNode<String>> getNodeMap() throws IllegalStateException {
        if (nodeMap == null) {
            throw new IllegalStateException();
        }
        return nodeMap;
    }
}