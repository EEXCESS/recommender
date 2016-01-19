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

package eu.eexcess.federatedrecommender.sourceselection.domains;

import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.utils.tree.BaseTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.NodeInspector;
import eu.eexcess.federatedrecommender.utils.tree.TreeNode;
import eu.eexcess.federatedrecommender.utils.tree.ValueTreeNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordnetDomainTreeConstructorTest {

    private static final File WORDNET_DOMAIN_PATH = new File("/opt/data/wordnet-domains/wn-domains-3.2");

    private static boolean domainArtSeen = false;

    @Before
    public void reset() {
        domainArtSeen = false;
    }

    @Test
    public void create_new_tree_expect_with_predefined_weights_correct_weights() {

        try {
            TreeConstructor tc = DomainTreeConstructorBuilder.newWordnetDomainTreeConstructor(WORDNET_DOMAIN_PATH);
            PartnerDomain domain1 = new PartnerDomain("art", 3.141592653589793);
            List<PartnerDomain> predefinedDomains = new LinkedList<PartnerDomain>();
            predefinedDomains.add(domain1);

            ValueTreeNode<Double> tree = tc.newTree(predefinedDomains);

            assertEquals(1, predefinedDomains.size());
            BaseTreeNode.depthFirstTraverser(tree, new TreeWeightAsserter());
        } catch (Error e) {
            e.printStackTrace();
            assertTrue(false);
        }

        assertEquals(true, domainArtSeen);
    }

    @Test public void create_new_tree_expect_defaut_weights_and_not_exceptional() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        try {
            TreeConstructor tc = DomainTreeConstructorBuilder.newWordnetDomainTreeConstructor(WORDNET_DOMAIN_PATH);
            ValueTreeNode<Double> tree = tc.newTree(new LinkedList<PartnerDomain>());
            BaseTreeNode.depthFirstTraverser(tree, new TreeDefaultWeightAsserter());
        } catch (Error e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     *
     * @author Raoul Rubien
     *
     */
    private class TreeWeightAsserter implements NodeInspector {

        @Override
        public boolean invoke(TreeNode n) {
            @SuppressWarnings("unchecked")
            ValueTreeNode<Double> node = (ValueTreeNode<Double>) n;

            if ("art".equals(node.getName())) {
                assertEquals("expected value for node [art] is [PI]", new Double(3.141592653589793), node.getValue());
                domainArtSeen = true;
            } else {
                assertEquals("unexpected value for node [" + node.getName() + "]", new Double(0.0), node.getValue());
            }
            return false;
        }
    }

    /**
     * @author Raoul Rubien
     */
    private class TreeDefaultWeightAsserter implements NodeInspector {

        @Override public boolean invoke(TreeNode n) {
            @SuppressWarnings("unchecked") ValueTreeNode<Double> node = (ValueTreeNode<Double>) n;
            assertEquals(new Double(0.0), node.getValue());
            return false;
        }
    }

}
