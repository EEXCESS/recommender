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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.sqlite.SQLiteConfig;

import eu.eexcess.logger.PianoLogger;
import eu.eexcess.tree.WeightedTreeNode;

public class CategoryTreeInflator implements Closeable {

	Logger logger = PianoLogger.getLogger(CategoryTreeInflator.class);

	Connection db;

	/**
	 * creates an instance of this class and opens a SQLite database where to
	 * put collected tuples into
	 * 
	 * @param sqliteDbFile
	 *            path to SQLite database file containing parent-child tuples
	 *            {@link SQliteTupleCollector.Tables}
	 */
	CategoryTreeInflator(File sqliteDbFile) {
		db = newReadonlyConnection(sqliteDbFile);
	}

	private Connection newReadonlyConnection(File dbPath) {
		try {
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true);
			return DriverManager.getConnection("jdbc:sqlite:" + dbPath.getAbsolutePath(), config.toProperties());
		} catch (Exception e) {
			logger.warning(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	@Deprecated
	Set<String> fetchChildren(String parent) throws SQLException {
		Set<String> children = new HashSet<String>();

		PreparedStatement statement = db.prepareStatement(SQliteTupleCollector.Tables.Category.SELECT_CHILDREN);
		statement.setString(1, parent);
		ResultSet result = statement.executeQuery();

		while (result.next()) {
			children.add(result.getString(SQliteTupleCollector.Tables.Category.Domains.CHILD));
		}
		return children;
	}

	Set<String> fetchParents(String child) throws SQLException {
		Set<String> parents = new HashSet<String>();

		PreparedStatement statement = db.prepareStatement(SQliteTupleCollector.Tables.Category.SELECT_PARENTS);
		statement.setString(1, child);
		ResultSet result = statement.executeQuery();

		while (result.next()) {
			parents.add(result.getString(SQliteTupleCollector.Tables.Category.Domains.PARENT));
		}
		return parents;
	}

	/**
	 * Inflates (breadth-first) a category tree from startNode traversing all predecessors (in
	 * other words bottom up, or in reverse direction) that can be looked up in
	 * the given database. Loops are skipped.
	 * 
	 * @param child
	 *            where to start construction
	 */
	public Set<WeightedTreeNode> inflateBF(WeightedTreeNode child) throws SQLException {
		ArrayDeque<WeightedTreeNode> nodesToProcess = new ArrayDeque<WeightedTreeNode>();
		nodesToProcess.add(child);
		child = null;
		Set<WeightedTreeNode> seenNodes = new HashSet<WeightedTreeNode>();

		while (true) {
			// if (0 == seenNodes.size() % 50) {
			// System.out.println("inflating nodes: seen[" + seenNodes.size() +
			// "] to be processed["
			// + nodesToProcess.size() + "]");
			// }
			// if nothing to process any more
			if (nodesToProcess.isEmpty()) {
				return seenNodes;
			}

			// next node to be processed
			child = nodesToProcess.remove();

			// memorize the start node as processed
			seenNodes.add(child);

			// get all parents of current child
			for (String parentName : fetchParents(child.getName())) {
				WeightedTreeNode parent = findOrCreateNewNode(seenNodes, nodesToProcess, parentName);
				parent.addChild(child);
				System.out.println(parentName + "->" + child.getName() + "; ");
			}
		}
	}

	public ArrayList<WeightedTreeNode> inflate() throws SQLException {
		ArrayList<WeightedTreeNode> nodes = new ArrayList<WeightedTreeNode>();
		
		
		return nodes;
	}
	/**
	 * Looks for a node named name in seenNodes and nodesToProcess. Returns it
	 * if found else a new node is created, added to nodesToProcess and
	 * returned.
	 * 
	 * @param seenNodes
	 *            set of already seen nodes so far
	 * @param name
	 *            name of the wanted node
	 * @return a node found in seenNodes or a newly but to nodesToProcess added
	 *         node
	 */
	private WeightedTreeNode findOrCreateNewNode(Set<WeightedTreeNode> seenNodes,
					ArrayDeque<WeightedTreeNode> nodesToProcess, String name) {
		WeightedTreeNode wanted = null;

		for (WeightedTreeNode seen : seenNodes) {
			if (seen.getName().compareTo(name) == 0) {
				wanted = seen;
				break;
			}
		}

		for (WeightedTreeNode queued : nodesToProcess) {
			if (queued.getName().compareTo(name) == 0) {
				wanted = queued;
				break;
			}
		}

		if (null == wanted) {
			wanted = new WeightedTreeNode(name);
			nodesToProcess.add(wanted);
		}
		return wanted;
	}

	@Override
	public void close() throws IOException {
		if (null != db) {
			try {
				db.close();
			} catch (SQLException e) {
				logger.warning("failed closing database: " + e.getMessage());
			}
		}
	}
}
