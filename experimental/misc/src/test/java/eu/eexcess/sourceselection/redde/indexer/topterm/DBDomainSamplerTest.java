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

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

import eu.eexcess.federatedrecommender.utils.tree.NodeInspector;
import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueSetTreeNode;
import eu.eexcess.sourceselection.redde.config.ReddeSettings;
import eu.eexcess.sourceselection.redde.indexer.topterm.DBDomainSampler.SampleArguments;
import eu.eexcess.sourceselection.redde.indexer.topterm.DBDomainSampler.WordNetArguments;

public class DBDomainSamplerTest {

    private static WordNetArguments getDefaultWordNetArguments() {
        WordNetArguments args = new WordNetArguments();

        args.wordnetDomainCsvTreePath = ReddeSettings.WordnetDomains.CSVDomainPath;
        args.wordnetDomainsPath = ReddeSettings.WordnetDomains.Path;
        args.wordnetPath = ReddeSettings.WordNet.Path_2_0;
        return args;
    }

    private static Set<SampleArguments> getDefaultSampleArguments() {

        return new HashSet<SampleArguments>();
    }

    @Test
    public void construct_expectNotExceptional() {

        if (ReddeSettings.isResourceAvailable(ReddeSettings.BaseIndex) && ReddeSettings.isWordNet20ResourceAvailable() && ReddeSettings.isWordNet30ResourceAvailable()
                && ReddeSettings.isWordNetDomainsResourceAvailable()) {

            try {

                WordNetArguments wnArgs = getDefaultWordNetArguments();
                DBDomainSampler sampler = new DBDomainSampler(ReddeSettings.BaseIndex.baseIndexPath, wnArgs);
                sampler.close();

            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }

    @Test
    public void alignTerms_expectNotExceptional() {
        if (ReddeSettings.isResourceAvailable(ReddeSettings.BaseIndex) && ReddeSettings.isWordNet20ResourceAvailable() && ReddeSettings.isWordNet30ResourceAvailable()
                && ReddeSettings.isWordNetDomainsResourceAvailable()) {

            WordNetArguments wnArgs = getDefaultWordNetArguments();
            try (DBDomainSampler sampler = new DBDomainSampler(ReddeSettings.BaseIndex.baseIndexPath, wnArgs)) {
                TreeNode domainToTerms = sampler.alignTerms(0, 99);

                final AtomicInteger numNodes = new AtomicInteger(0);
                final AtomicInteger numTerms = new AtomicInteger(0);
                @SuppressWarnings("unchecked")
                NodeInspector counter = (n) -> {
                    numNodes.incrementAndGet();
                    numTerms.set(numTerms.get() + ((ValueSetTreeNode<String>) n).getValues().size());
                    return false;
                };

                ValueSetTreeNode.depthFirstTraverser(domainToTerms, counter);
                System.out.println("num terms [" + numTerms.get() + "] num nodes [" + numNodes.get() + "] ");
                assertTrue(numTerms.get() >= (99.0 * 0.04));
                assertTrue(numNodes.get() > 1);
            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }

    @Ignore
    @Test
    public void sampleDatabase_expectNotExceptoinal() {
        if (ReddeSettings.isResourceAvailable(ReddeSettings.BaseIndex) && ReddeSettings.isWordNet20ResourceAvailable() && ReddeSettings.isWordNet30ResourceAvailable()
                && ReddeSettings.isWordNetDomainsResourceAvailable()) {
            WordNetArguments wnArgs = getDefaultWordNetArguments();
            try (DBDomainSampler sampler = new DBDomainSampler(ReddeSettings.BaseIndex.baseIndexPath, wnArgs)) {
                TreeNode domainToTermTree = sampler.alignTerms(0, 500);

                @SuppressWarnings("unchecked")
                NodeInspector printer = (n) -> {
                    if (((ValueSetTreeNode<String>) n).getValues().size() > 0) {
                        System.out.print("#terms: " + ((ValueSetTreeNode<String>) n).getValues().size() + " ");
                        System.out.println(n.toString());
                    }
                    return false;
                };
                ValueSetTreeNode.depthFirstTraverser(domainToTermTree, printer);
                // TODO: wn domain alignment does not deliver expected result :/
                sampler.sample(getDefaultSampleArguments());
                sampler.close();

            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }
}
