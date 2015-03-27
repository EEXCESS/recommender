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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import eu.eexcess.logger.PianoLogger;

public class SQliteTupleCollector implements CategoryTupleCollector, Closeable {

	public static class Tables {
		public static class Category {
			public static class Domains {
				public static final String PARENT = "parent";
				public static final String PARENT_HASH = "parent_hash";
				public static final String CHILD = "child";
				public static final String CHILD_HASH = "child_hash";
			}

			public static final String TABLE_NAME = "Categories";

			public static final String CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + Domains.PARENT
							+ " TEXT, " + Domains.PARENT_HASH + " INTEGER, " + Domains.CHILD + " TEXT, "
							+ Domains.CHILD_HASH + " INTEGER)";
			public static final String DROP = "DROP TABLE " + TABLE_NAME;
			public static final String INSERT = "INSERT INTO " + TABLE_NAME + " (" + Domains.PARENT + ", "
							+ Domains.PARENT_HASH + ", " + Domains.CHILD + ", " + Domains.CHILD_HASH
							+ ") VALUES (?, ?, ?, ?)";
			public static final String SELECT_CHILDREN = "SELECT " + TABLE_NAME + "." + Domains.CHILD + " FROM "
							+ TABLE_NAME + " WHERE " + Domains.PARENT + " IS ?";
			public static final String SELECT_PARENTS = "SELECT " + TABLE_NAME + "." + Domains.PARENT + " FROM "
							+ TABLE_NAME + " WHERE " + Domains.CHILD + " IS ?";
		}
	}

	private Logger logger = PianoLogger.getLogger(SQliteTupleCollector.class);

	private Connection db;
	private int cachedStatements = 0;
	private int maxCachedStatements = 2000;
	PreparedStatement preparedInsertStatement;

	SQliteTupleCollector(File dbFile) throws SQLException {
		db = newConnection(dbFile);
		createDb();
		preparedInsertStatement = db.prepareStatement(Tables.Category.INSERT);
		db.setAutoCommit(false);
	}

	private Connection newConnection(File dbPath) {
		try {
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:" + dbPath.getAbsolutePath());
		} catch (Exception e) {
			logger.warning(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	@Override
	public void takeTuple(String parent, String child) throws SQLException {
		preparedInsertStatement.setString(1, parent);
		preparedInsertStatement.setInt(2, parent.hashCode());
		preparedInsertStatement.setString(3, child);
		preparedInsertStatement.setInt(4, child.hashCode());
		preparedInsertStatement.addBatch();

		if (cachedStatements < maxCachedStatements) {
			cachedStatements++;
		} else {
			preparedInsertStatement.executeBatch();
			db.commit();
			cachedStatements = 0;
		}
	}

	private void createDb() throws SQLException {
		PreparedStatement prepared = null;
		try {
			prepared = db.prepareStatement(Tables.Category.DROP);
			prepared.execute();
			prepared.close();
		} catch (Exception e) {
		}
		prepared = db.prepareStatement(Tables.Category.CREATE);
		prepared.execute();
		prepared.close();
	}

	@Override
	public void close() {
		if (null != preparedInsertStatement) {
			if (cachedStatements > 0) {
				try {
					preparedInsertStatement.executeBatch();
				} catch (SQLException e) {
				}
				cachedStatements = 0;
			}

			try {
				preparedInsertStatement.close();
			} catch (SQLException e) {
				logger.warning("failed closing prepared statement");
			}
		}
		try {
			db.commit();
			db.close();
		} catch (SQLException e) {
		}
	}

}
