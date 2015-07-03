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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNLException;

import org.apache.commons.io.LineIterator;

import eu.eexcess.federatedrecommender.domaindetection.Domain;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.sourceselection.redde.tree.TreeNode;
import eu.eexcess.sourceselection.redde.tree.ValueTreeNode;

public class TopTermToWNDomain extends Resources {

	private File wordnetCSVTreeFile;
	private static final String tokenDelimiter = "[,]";

	private ValueTreeNode<String> wnDomainTree;
	private static final String rootNodeName = "factotum";
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
	public TopTermToWNDomain(String indexPath, String wordnet20Path, String wordnetDomainsPath,
					String wordnetDomainCsvTreePath) throws IOException, JWNLException {
		super(indexPath);
		this.wordnetDomainDetectorFile = new File(wordnet20Path);
		this.wordnetCSVTreeFile = new File(wordnetDomainCsvTreePath);
		this.wordnetDomainsPath = new File(wordnetDomainsPath);
		wnDomainTree = new ValueTreeNode<String>();
		wnDomainTree.setName(rootNodeName);
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
		inflateDomainTree();

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
				String domainName = entry.getKey();
				Set<TreeNode<String>> resultCollector = new HashSet<TreeNode<String>>();
				ValueTreeNode.findFirstNode(domainName, wnDomainTree, resultCollector);

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

	TreeNode<String> inflateDomainTree() throws FileNotFoundException {
		LineIterator iterator = new LineIterator(new FileReader(wordnetCSVTreeFile));
		String[] currentBranch = new String[5];
		currentBranch[0] = rootNodeName;

		while (iterator.hasNext()) {

			// read current node and store its parents
			String line = iterator.nextLine();
			String[] tokensInLine = line.split(tokenDelimiter);

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
			ValueTreeNode<String> branch = null;
			for (int branchDepth = currentBranch.length; branchDepth > 0; branchDepth--) {
				String nodeName = currentBranch[branchDepth - 1];
				if (nodeName == null) {
					continue;
				}

				Set<TreeNode<String>> result = new HashSet<TreeNode<String>>();
				ValueTreeNode.findFirstNode(nodeName, wnDomainTree, result);
				TreeNode<String> nodeInTree = null;
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
					ValueTreeNode<String> newParent = new ValueTreeNode<String>();
					newParent.setName(nodeName);

					if (branch != null) {
						newParent.addChild(branch);
					}
					branch = newParent;
				}
			}
		}
		iterator.close();
		return wnDomainTree;
	}

}
