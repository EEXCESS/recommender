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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.logger.PianoLogger;
import eu.eexcess.sourceselection.redde.tree.BaseTreeNode;

/**
 * Check every indexed domain of {@link WikipediaSchoolsDomainTreeParser} and
 * link {@link WikipediaSchoolsDumpIndexer} if it is contained in the
 * respectively other index.
 * 
 * @author Raoul Rubien
 *
 */
public class WikipediaSchoolsReasonableDomainsTest {

    private static final Logger LOGGER = PianoLogger.getLogger(WikipediaSchoolsReasonableDomainsTest.class.getName());
    private static final Map<String, BaseTreeNode<String>> nodeMap = new HashMap<String, BaseTreeNode<String>>();
    private static final File domainTreeIndex = new File("/opt/data/wikipedia-schools/domain-index/wikipedia-schools-domain-index/");
    private static final File paragraphIndex = new File("/opt/data/wikipedia-schools/paragraph-index/wikipedia-schools-paragraph-index/");
    private static volatile IndexReader domainTreeIndexReader = null;
    private static volatile IndexReader paragraphIndexReader = null;
    private static BaseTreeNode<String> domainTree = null;

    @BeforeClass
    public static void inflateTree() {

        try {
            domainTreeIndexReader = DirectoryReader.open(FSDirectory.open(domainTreeIndex));
            for (int i = 0; i < domainTreeIndexReader.maxDoc(); i++) {
                Document doc = domainTreeIndexReader.document(i);
                String parentName = doc.get(WikipediaSchoolsDomainTreeParser.LUCENE_DOCUMENT_DOCUMENT_FIELD_NAME);

                BaseTreeNode<String> parentNode = addNodeToMap(parentName);

                if (domainTree == null) {
                    domainTree = parentNode;
                }

                for (IndexableField field : doc.getFields(WikipediaSchoolsDomainTreeParser.LUCENE_DOCUMENT_CHILD_FIELD_NAME)) {
                    String childName = field.stringValue();
                    BaseTreeNode<String> childNode = addNodeToMap(childName);
                    parentNode.addChild(childNode);
                }
            }

            // BaseTreeNode.depthFirstTraverser(domainTree, (n) ->
            // System.out.println(n.toString()));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to open index at [" + domainTreeIndex.getAbsolutePath() + "]", e);
        }
    }

    @BeforeClass
    public static void openParagraphsReader() {

        try {
            paragraphIndexReader = DirectoryReader.open(FSDirectory.open(paragraphIndex));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to open index at [" + paragraphIndex.getAbsolutePath() + "]", e);
        }
    }

    @AfterClass
    public static void close() {

        try {
            if (domainTreeIndexReader != null) {
                domainTreeIndexReader.close();
                domainTreeIndexReader = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to close domain tree index", e);
        }
        try {
            if (paragraphIndexReader != null) {
                paragraphIndexReader.close();
                paragraphIndexReader = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to close paragraph index", e);
        }
    }

    @Test
    public void verify_allDomainsOfDumpIndex_also_in_treeIndex() throws IOException {
        Map<String, AtomicInteger> seenDomainsOfParagraphs = getParagraphsDomains();
        WikipediaSchoolsDumpIndexer.printHistogram(seenDomainsOfParagraphs);

        // all domains seen in paragraphs must be also in the domain tree
        int numUnseenDomains = 0;
        for (Map.Entry<String, AtomicInteger> entry : seenDomainsOfParagraphs.entrySet()) {
            if (!WikipediaSchoolsDumpIndexer.NO_SUBJECT_FOUND_STRING.equals(entry.getKey()) && !nodeMap.containsKey(entry.getKey())) {
                LOGGER.severe("paragraph domain [" + entry.getKey() + "] not found in tree index");
                numUnseenDomains++;
            }
        }
        assertEquals(0, numUnseenDomains);
    }

    @Test
    public void verify_allDomainsOfTreeIndex_also_in_dumpIndex() throws IOException {
        Map<String, AtomicInteger> seenDomainsOfParagraphs = getParagraphsDomains();

        int numUnseenDomains = 0;
        Map<String, AtomicInteger> paragraphsDomainsInTreeIndexCount = new HashMap<String, AtomicInteger>();

        for (String domainName : nodeMap.keySet()) {
            paragraphsDomainsInTreeIndexCount.put(domainName, new AtomicInteger(0));
            if (!seenDomainsOfParagraphs.containsKey(domainName)) {
                numUnseenDomains++;
                LOGGER.info("domain from tree [" + domainName + "] never seen in paragraph index");
            }
        }
        // domains music, it, people, factotum and business_studies are never
        // seen in the paragraphs index
        assertEquals(5, numUnseenDomains);

        // just for statistics: print per-domain-tree histogram
        for (Map.Entry<String, AtomicInteger> entry : seenDomainsOfParagraphs.entrySet()) {
            if (!WikipediaSchoolsDumpIndexer.NO_SUBJECT_FOUND_STRING.equals(entry.getKey())) {
                paragraphsDomainsInTreeIndexCount.get(entry.getKey()).addAndGet(entry.getValue().intValue());
            }
        }

        assertEquals(nodeMap.size(), paragraphsDomainsInTreeIndexCount.size());
        WikipediaSchoolsDumpIndexer.printHistogram(paragraphsDomainsInTreeIndexCount);

    }

    /**
     * accumulates all domains seen domains of paragraphs
     * 
     * @return
     * @throws IOException
     */
    private Map<String, AtomicInteger> getParagraphsDomains() throws IOException {
        Map<String, AtomicInteger> seenDomainsOfParagraphs = new HashMap<String, AtomicInteger>();

        int documentCount = 0, sysoutEveryCount = 7000;

        for (; documentCount < paragraphIndexReader.maxDoc(); documentCount++) {
            Document doc = paragraphIndexReader.document(documentCount);

            Set<String> domainNames = new HashSet<String>();
            for (IndexableField domain : doc.getFields(WikipediaSchoolsDumpIndexer.LUCENE_FIELD_DOCUMENT_SUBJECT)) {
                domainNames.add(domain.stringValue());
            }

            WikipediaSchoolsDumpIndexer.logNewlySeenDomains(domainNames, seenDomainsOfParagraphs);

            if (0 == documentCount % sysoutEveryCount) {
                LOGGER.info("processed [" + documentCount + "/" + paragraphIndexReader.maxDoc() + "] documetns");
            }
        }
        return seenDomainsOfParagraphs;
    }

    /**
     * add new node to map if not already existent
     * 
     * @param nodeName
     * @return the new or an already mapped node
     */
    private static BaseTreeNode<String> addNodeToMap(String nodeName) {
        BaseTreeNode<String> theNode = nodeMap.get(nodeName);
        if (theNode == null) {
            theNode = new BaseTreeNode<String>(nodeName);
            nodeMap.put(nodeName, theNode);
        }

        return theNode;
    }
}
