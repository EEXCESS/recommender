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

package eu.eexcess.sourceselection.redde.indexer.topterm;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import eu.eexcess.federatedrecommender.domaindetection.probing.Domain;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueTreeNode;
import eu.eexcess.federatedrecommender.utils.wordnet.WordnetDomainTreeInflator;

public class TopTermToWNDomain extends Resources {

    private File wordnetCSVTreeFile;

    private ValueTreeNode<String> wnDomainTree = null;
    private String[] topTerms;
    private File wordnetDomainDetectorFile;
    private File wordnetDomainsPath;

    /**
     * Constructs an instance of this class.
     * 
     * @param indexPath
     *            path to lucene index where to take top terms from.
     * @param wordnet20Path
     *            used for word net domain detection
     * @param wordnetDomainsPath
     *            used for word net domain detection
     * @param wordnetDomainCsvTreePath
     *            used to inflate a domain tree where top terms will be mounted
     *            onto
     * @throws IOException
     * @throws JWNLException
     */
    public TopTermToWNDomain(String indexPath, String wordnet20Path, String wordnetDomainsPath, String wordnetDomainCsvTreePath) throws IOException,
            JWNLException {
        super(indexPath);
        this.wordnetDomainDetectorFile = new File(wordnet20Path);
        this.wordnetCSVTreeFile = new File(wordnetDomainCsvTreePath);
        this.wordnetDomainsPath = new File(wordnetDomainsPath);
    }

    /**
     * Aligns all top terms within given boundary to word net domains.
     * 
     * @param fromTopTermIndex
     *            index of first top term to be considered (inclusive)
     * @param toTopTermIndex
     *            last index of top term to be considered (inclusive)
     * @throws Exception
     */
    public ValueTreeNode<String> assignToDomains(int fromTopTermIndex, int toTopTermIndex) throws Exception {
        return assignToDomains(getTopTerms(fromTopTermIndex, toTopTermIndex));
    }

    /**
     * Aligns terms word net domains.
     * 
     * @param terms
     *            array of terms to align (must be != null)
     * @throws Exception
     */

    ValueTreeNode<String> assignToDomains(String[] terms) throws Exception {
        this.topTerms = terms;
        WordnetDomainsDetector wdt = new WordnetDomainsDetector(wordnetDomainDetectorFile, wordnetDomainsPath, true);
        wnDomainTree = WordnetDomainTreeInflator.inflateDomainTree(wordnetCSVTreeFile);

        // construct a domain map containing terms
        IdentityHashMap<String, HashSet<String>> domainToTerms = new IdentityHashMap<String, HashSet<String>>();
        for (String term : terms) {
            Set<Domain> domains = wdt.detect(term);
            // if domains were detected
            if (domains.size() > 0) {
                String domainName = domains.iterator().next().getName();
                HashSet<String> domainTerms = domainToTerms.get(domainName);
                // if domain is not seen so far
                if (domainTerms == null) {
                    domainTerms = new HashSet<String>();
                    domainTerms.add(term);
                    domainToTerms.put(domainName, domainTerms);
                } else {
                    domainTerms.add(term);
                }
            }

            // mount the terms on the domain tree
            for (Map.Entry<String, HashSet<String>> entry : domainToTerms.entrySet()) {
                Set<TreeNode<String>> resultCollector = new HashSet<TreeNode<String>>();
                ValueTreeNode<String> domainNode = new ValueTreeNode<String>();
                domainNode.setName(entry.getKey());
                ValueTreeNode.findFirstNode(domainNode, wnDomainTree, resultCollector);

                // find domain in tree
                if (resultCollector.iterator().hasNext()) {
                    TreeNode<String> nodeInTree = resultCollector.iterator().next();
                    Set<String> domainTerms = entry.getValue();

                    ((ValueTreeNode<String>) nodeInTree).addValues(domainTerms);
                }
            }
        }
        return wnDomainTree;
    }

    /**
     * @return the last top terms fetched from
     *         {@link #assignToDomains(int, int)} or null
     */
    public String[] getTopTerms() {
        return topTerms;
    }

    public File getTreeFile() {
        return wordnetCSVTreeFile;
    }
}
