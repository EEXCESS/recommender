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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import eu.eexcess.sourceselection.redde.tree.TreeNode;
import eu.eexcess.sourceselection.redde.tree.ValueTreeNode;

public class ValueTreeNodeTest {

    @Test
    public void iterator_iterate_expectDepthEquals1_nonDescentInDepth() {
        ValueTreeNode<String> n = new ValueTreeNode<String>();
        n.setName("root");
        n.addValue("1");
        n.addValue("2");
        n.addValue("3");

        ValueTreeNode<String> c = new ValueTreeNode<String>();
        c.setName("child0");
        c.addValue("42");
        c.addValue("43");
        c.addValue("44");
        n.addChild(c);

        ValueTreeNode<String> cc = new ValueTreeNode<String>();
        cc.setName("child01");
        c.addChild(cc);

        int count = 0;
        Iterator<TreeNode<String>> iterator = c.iterator();
        iterator = c.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
            count++;
        }
        assertEquals(1, count);

        count = 0;
        iterator = cc.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
            count++;
        }
        assertEquals(0, count);

        c = new ValueTreeNode<String>();
        c.setName("child1");
        c.addValue("12");
        c.addValue("13");
        c.addValue("14");
        n.addChild(c);

        count = 0;
        iterator = c.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
            count++;
        }
        assertEquals(0, count);

        count = 0;
        iterator = n.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void findFirstNode_expectCorrectNode() {
        ValueTreeNode<String> n = new ValueTreeNode<String>();
        n.setName("root");
        n.addValue("1");
        n.addValue("2");
        n.addValue("3");

        ValueTreeNode<String> c = new ValueTreeNode<String>();
        c.setName("child0");
        c.addValue("42");
        c.addValue("43");
        c.addValue("44");
        n.addChild(c);

        ValueTreeNode<String> cc = new ValueTreeNode<String>();
        cc.setName("child01");
        c.addChild(cc);

        ValueTreeNode<String> cc1 = new ValueTreeNode<String>();
        cc1.setName("child02");
        cc.addChild(cc1);

        ValueTreeNode<String> template = new ValueTreeNode<String>();
        template.setName("root");
        Set<TreeNode<String>> resultCollector = new HashSet<TreeNode<String>>();
        ValueTreeNode.findFirstNode(template, n, resultCollector);
        assertEquals(1, resultCollector.size());
        assertTrue((Object) resultCollector.iterator().next() == (Object) n);

        template.setName("child0");
        resultCollector.clear();
        ValueTreeNode.findFirstNode(template, n, resultCollector);
        assertEquals(1, resultCollector.size());
        assertTrue((Object) resultCollector.iterator().next() == (Object) c);

        template.setName("child01");
        resultCollector.clear();
        ValueTreeNode.findFirstNode(template, n, resultCollector);
        assertEquals(1, resultCollector.size());
        assertTrue((Object) resultCollector.iterator().next() == (Object) cc);

        template.setName("child02");
        resultCollector.clear();
        ValueTreeNode.findFirstNode(template, n, resultCollector);
        assertEquals(1, resultCollector.size());
        assertTrue((Object) resultCollector.iterator().next() == (Object) cc1);
    }
}
