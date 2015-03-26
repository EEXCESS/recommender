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
import java.util.Collection;
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

	/**
	 * inflates a category tree from startNode traversing all children that can
	 * be looked up in the given database
	 * 
	 * @param startNode
	 *            where to start construction
	 */
	public Set<WeightedTreeNode> inflate(WeightedTreeNode startNode) throws SQLException {
		while (true) {
			ArrayDeque<WeightedTreeNode> nodesToProcess = new ArrayDeque<WeightedTreeNode>();
			Set<WeightedTreeNode> seenNodes = new HashSet<WeightedTreeNode>();
			// memorize the start node as processed
			WeightedTreeNode parent = startNode;
			seenNodes.add(parent);

			// get all children start node
			for (String childName : fetchChildren(parent.getName())) {
				WeightedTreeNode child = new WeightedTreeNode(childName);
				parent.addChild(child);
				nodesToProcess.add(child);
			}

			// if nothing to process any more
			if (nodesToProcess.isEmpty()) {
				return seenNodes;
			}

			// set next child to be processed
			parent = nodesToProcess.remove();
		}
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
