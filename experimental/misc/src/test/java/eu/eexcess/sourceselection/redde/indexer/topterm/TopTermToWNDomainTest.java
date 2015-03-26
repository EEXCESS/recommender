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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNLException;

import org.junit.Test;

import eu.eexcess.domaindetection.Domain;
import eu.eexcess.domaindetection.DomainDetectorException;
import eu.eexcess.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.sourceselection.redde.config.Settings;
import eu.eexcess.tree.TreeNode;
import eu.eexcess.tree.ValueTreeNode;

public class TopTermToWNDomainTest {

	private int nodeCount;
	private ValueTreeNode.NodeInspector<String> nodeCounter = (n) -> TopTermToWNDomainTest.this.nodeCount++;

	private int termCount;
	private ValueTreeNode.NodeInspector<String> termCounter = (n) -> TopTermToWNDomainTest.this.termCount += ((ValueTreeNode<String>) n)
					.getValues().size();

	private Set<String> collectedTerms = new HashSet<String>();
	private ValueTreeNode.NodeInspector<String> termCollector = (n) -> {
		for (String term : ((ValueTreeNode<String>) n).getValues()) {
			TopTermToWNDomainTest.this.collectedTerms.add(term);
		}
	};

	private Map<String, String> termToDmain = new HashMap<String, String>();
	private ValueTreeNode.NodeInspector<String> termToDomainCollector = (n) -> {
		for (String term : ((ValueTreeNode<String>) n).getValues()) {
			TopTermToWNDomainTest.this.termToDmain.put(term, n.getName());
		}
	};

	// private NodeInspector printer = (n) -> System.out.println(n.toString());

	@Test
	public void inflateDomainTree_readTree_exptectCorrectNodeCountInTree() {

		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable()
						&& Settings.isWordNetDomainsResourceAvailable()) {
			try {
				TopTermToWNDomain mapper = new TopTermToWNDomain(Settings.BaseIndex.baseIndexPath,
								Settings.WordNet.Path_2_0, Settings.WordnetDomains.Path,
								Settings.WordnetDomains.CSVDomainPath);

				TreeNode<String> domainTree = mapper.inflateDomainTree();
				mapper.close();

				nodeCount = 0;
				ValueTreeNode.depthFirstTraverser(domainTree, nodeCounter);
				assertEquals(168, nodeCount);
			} catch (JWNLException | IOException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void inflateDomainTree_readTree_exptectCorrectDomainsAtDepth2and4() {

		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable()
						&& Settings.isWordNetDomainsResourceAvailable()) {
			try {
				TopTermToWNDomain mapper = new TopTermToWNDomain(Settings.BaseIndex.baseIndexPath,
								Settings.WordNet.Path_2_0, Settings.WordnetDomains.Path,
								Settings.WordnetDomains.CSVDomainPath);
				TreeNode<String> domainTree = mapper.inflateDomainTree();
				mapper.close();

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

			} catch (JWNLException | IOException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void allignToDomains_allignTermsToDotmains_expectNotExceptional() {

		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable()
						&& Settings.isWordNetDomainsResourceAvailable()) {
			TopTermToWNDomain mapper;
			try {
				mapper = new TopTermToWNDomain(Settings.BaseIndex.baseIndexPath, Settings.WordNet.Path_2_0,
								Settings.WordnetDomains.Path, Settings.WordnetDomains.CSVDomainPath);
				nodeCount = 0;

				TreeNode<String> domainsToTermsTree = mapper.assignToDomains(0, 99);
				mapper.close();
				assertEquals(100, mapper.getTopTerms().length);

				collectedTerms.clear();
				ValueTreeNode.depthFirstTraverser(domainsToTermsTree, termCounter);

				assertTrue(termCount <= 100);
				assertTrue(termCount <= 100 * 0.5);
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void allignToDomains_allignTermsToDomains_expectCorrectAllignmentOfSomeTerms() {
		TopTermToWNDomain mapper;
		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable()
						&& Settings.isWordNetDomainsResourceAvailable()) {
			try {
				mapper = new TopTermToWNDomain(Settings.BaseIndex.baseIndexPath, Settings.WordNet.Path_2_0,
								Settings.WordnetDomains.Path, Settings.WordnetDomains.CSVDomainPath);
				nodeCount = 0;

				// TERM -> DOMAIN
				// parent -> person
				// stupid -> psychological_features
				// drunk -> person
				// bad -> quality
				// victor -> person
				// absorb -> psychological_features
				// youth -> person
				// uncertain -> psychological_features

				HashMap<String, String> testTerms = new HashMap<String, String>();
				testTerms.put("parent", "person");
				testTerms.put("stupid", "psychological_features");
				testTerms.put("drunk", "person");
				testTerms.put("bad", "quality");
				testTerms.put("victor", "person");
				testTerms.put("absorb", "psychological_features");
				testTerms.put("youth", "person");
				testTerms.put("uncertain", "psychological_features");

				String[] terms = testTerms.keySet().toArray(new String[0]);

				TreeNode<String> domainsToTermsTree = mapper.assignToDomains(terms);
				mapper.close();

				termToDmain.clear();
				ValueTreeNode.depthFirstTraverser(domainsToTermsTree, termToDomainCollector);
				for (Map.Entry<String, String> entry : testTerms.entrySet()) {
					String term = entry.getKey();
					assertEquals(entry.getValue(), termToDmain.get(term));
				}
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void allignToDomains_allignTermsToDomains_expectCorrectNumOfTermsInSubDomain() {

		if (Settings.isResourceAvailable(Settings.BaseIndex) && Settings.isWordNet20ResourceAvailable()
						&& Settings.isWordNetDomainsResourceAvailable()) {
			TopTermToWNDomain mapper;
			try {
				mapper = new TopTermToWNDomain(Settings.BaseIndex.baseIndexPath, Settings.WordNet.Path_2_0,
								Settings.WordnetDomains.Path, Settings.WordnetDomains.CSVDomainPath);

				ValueTreeNode<String> domainsToTermsTree = mapper.assignToDomains(0, 99);
				mapper.close();

				Set<TreeNode<String>> resultCollector = new HashSet<TreeNode<String>>();

				ValueTreeNode.findFirstNode("time_period", domainsToTermsTree, resultCollector);
				ValueTreeNode<String> startNode = (ValueTreeNode<String>) resultCollector.iterator().next();

				collectedTerms.clear();
				ValueTreeNode.depthFirstTraverser(startNode, termCollector);
				assertTrue(collectedTerms.size() == 0);
				assertTrue(startNode.getValues().size() == 2);

			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}

	@Test
	public void wordnetDomainDetector_detect_detectDomainsAsExpected() {
		if (Settings.isWordNetDomainsResourceAvailable()) {
			try {
				WordnetDomainsDetector wdt = new WordnetDomainsDetector(new File(Settings.WordNet.Path_2_0), new File(
								Settings.WordnetDomains.Path), true);

				// TERM -> DOMAIN
				// parent -> person
				// stupid -> psychological_features
				// drunk -> person
				// bad -> quality
				// victor -> person
				// absorb -> psychological_features
				// youth -> person
				// uncertain -> psychological_features

				HashMap<String, String> testTerms = new HashMap<String, String>();
				testTerms.put("parent", "person");
				testTerms.put("stupid", "psychological_features");
				testTerms.put("drunk", "person");
				testTerms.put("bad", "quality");
				testTerms.put("victor", "person");
				testTerms.put("absorb", "psychological_features");
				testTerms.put("youth", "person");
				testTerms.put("uncertain", "psychological_features");

				for (Map.Entry<String, String> entry : testTerms.entrySet()) {
					String term = entry.getKey();
					Set<Domain> domains = wdt.detect(term);
					assertEquals(1, domains.size());
					assertEquals(entry.getValue(), domains.iterator().next().getName());
				}
			} catch (DomainDetectorException e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}
}
