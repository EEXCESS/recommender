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

package eu.eexcess.diversityasurement.wikipedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import eu.eexcess.diversityasurement.wikipedia.config.Settings;
import eu.eexcess.tree.BaseTreeNode;
import eu.eexcess.tree.TreeNode;
import eu.eexcess.tree.WeightedTreeNode;

public class CategoryTreeInflatorTest {

	@Test
	public void fetchChildren_expectExcatlySpecifiedChildren() {
		if (!Settings.SQLiteDb.isDBFileAvailable()) {
			return;
		}

		CategoryTreeInflator tb = new CategoryTreeInflator(new File(Settings.SQLiteDb.PATH));
		Set<String> children = null;
		try {
			children = tb.fetchChildren("1915_in_Europe");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			tb.close();
		} catch (Throwable e) {
		}

		Set<String> expected = expectedChildrenOf_1915_in_Europe();
		assertTrue(children.containsAll(expected));
		assertEquals(expected.size(), children.size());
	}

	private Set<String> expectedChildrenOf_1915_in_Europe() {
		Set<String> categories = new HashSet<String>();

		categories.add("1915_in_Croatia");
		categories.add("1915_in_Ireland");
		categories.add("1915_in_Iceland");
		categories.add("1915_in_Serbia");
		categories.add("1915_in_Estonia");
		categories.add("1915_in_France");
		categories.add("1915_in_Denmark");
		categories.add("1915_in_Luxembourg");
		categories.add("1915_in_European_sport");
		categories.add("1915_in_Spain");
		categories.add("1915_in_Malta");
		categories.add("1915_in_Germany");
		categories.add("1915_in_Russia");
		categories.add("1915_in_Belgium");
		categories.add("1915_in_the_United_Kingdom");
		categories.add("1915_in_the_Netherlands");
		categories.add("1915_in_Finland");
		categories.add("1915_in_the_Ottoman_Empire");
		categories.add("1915_in_Italy");
		categories.add("1915_in_Portugal");
		categories.add("1915_in_Switzerland");
		categories.add("1915_in_Romania");
		categories.add("1915_in_Armenia");
		categories.add("1915_in_Bulgaria");
		categories.add("1915_elections_in_Europe");
		categories.add("1915_in_Greece");
		categories.add("1915_in_Sweden");
		categories.add("1915_in_England");
		categories.add("1915_in_Scotland");
		categories.add("1915_in_Austria");
		categories.add("1915_in_Ukraine");
		categories.add("1915_in_Poland");

		return categories;
	}

	@Test
	public void inflateFromBottomToRoots_givenDBPediaDB_expectCorrectTree() throws SQLException, IOException {
		if (!Settings.SQLiteDb.isDBFileAvailable()) {
			return;
		}

		CategoryTreeInflator builder = new CategoryTreeInflator(new File(Settings.SQLiteDb.PATH));
		WeightedTreeNode child = new WeightedTreeNode("Agriculture");
		Set<WeightedTreeNode> treeNodes = builder.inflateBF(child);
		builder.close();

		final AtomicInteger numNodes = new AtomicInteger(0);
		BaseTreeNode.NodeInspector<Double> traverser = (n) -> {
			if (0 == numNodes.get() % 10) {
				System.out.println("traversed [" + numNodes.get() + "] nodes");
			}
			numNodes.incrementAndGet();
		};
		WeightedTreeNode.depthFirstTraverser(child, traverser);

		assertEquals(0, numNodes.get());
		assertEquals(140, treeNodes.size());
	}
}
