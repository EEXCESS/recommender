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

import java.util.Iterator;

import org.junit.Test;

public class TreeNodeTest {

	@Test
	public void iterator_iterate_expectDepthEquals1_nonDescentInDepth() {
		TreeNode<String> n = new TreeNode<String>();
		n.setName("root");
		n.addValue("1");
		n.addValue("2");
		n.addValue("3");

		TreeNode<String> c = new TreeNode<String>();
		c.setName("child0");
		c.addValue("42");
		c.addValue("43");
		c.addValue("44");
		n.addChild(c);

		TreeNode<String> cc = new TreeNode<String>();
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

		c = new TreeNode<String>();
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
}
