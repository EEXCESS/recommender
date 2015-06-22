/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eexcess.sqlite;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic Database Connection
 * 
 * @author hziak
 * 
 */
public class Database {
	private static final Logger logger = Logger.getLogger(Database.class
			.getName());
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";
	private static String dBName = null;

	private HashMap<String, PreparedStatement> map = new HashMap<String, PreparedStatement>();

	private Connection con;

	public Database() {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Could not load JDBC Driver", e);
		}
	}

	public <T extends DatabasePreparedQuery> Database(String dBName,
			T[] preparedStatementsDefinitions) {
		logger.log(Level.INFO,"Trying to open DB:" +Database.dBName);
		Database.dBName = dBName;
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Could not load JDBC Driver", e);
		}
		try {
			this.con = DriverManager
					.getConnection("jdbc:sqlite:" + Database.dBName);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Could not connect to Database: "
					+ Database.dBName, e);
		}
		if (con != null) {
			try {
				initPreparedStatements(con, preparedStatementsDefinitions);
			} catch (SQLException e) {
				logger.log(Level.SEVERE,
						"Prepared Statements could not be created", e);
			}
			try {

				con.setAutoCommit(false);
			} catch (SQLException e) {
				logger.log(Level.SEVERE,
						"Autocommit could not be set to false", e);
			}
		}
	}

	/**
	 * Creates a Connection to a Database if the Database wasn't defined in the
	 * constructor
	 * 
	 * @param database
	 * @return
	 */
	public Connection connect(String database) {

		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e1) {
			logger.log(Level.SEVERE,
					"Could not Connec to Database:" + database, e1);
			return null;
		}
		try {
			this.con = DriverManager.getConnection("jdbc:sqlite:" + database);
		} catch (SQLException e) {
			logger.log(Level.SEVERE,
					"Could not Connec to Database:" + database, e);
			return null;
		}
		return con;
	}

	/*
	 * Initializes the Prepared Statements
	 */
	private <T extends DatabasePreparedQuery> void initPreparedStatements(
			Connection con, T[] preparedStatements) throws SQLException {
		
		if (con != null) {
			for (T t : preparedStatements) {
				PreparedStatement updateStatement = null;

				try {
					updateStatement = con.prepareStatement(t.getUpdateQuery());
				} catch (SQLException e) {
					logger.log(Level.SEVERE,
							"Could not prepare Statement \"QueryLog\"", e);
					try {
						con.createStatement().executeUpdate(t.getCreateQuery());
					} catch (SQLException e1) {
						logger.log(Level.SEVERE, "Could not create Table  \""
								+ t.getInternName() + "\"", e);
					}
				}
				if (updateStatement == null)
					updateStatement = con.prepareStatement(t.getUpdateQuery());

				if (updateStatement != null)
					map.put(t.getInternName() + t.getUpdateQuery(),
							updateStatement);

				PreparedStatement getStatement = null;
				try {
					getStatement = con.prepareStatement(t.getSelectQuery());
				} catch (SQLException e) {
					logger.log(Level.SEVERE,
							"Could not prepare Statement \"QueryLog\"", e);
				}
				map.put(t.getInternName() + t.getSelectQuery(), getStatement);
			}
		}
	}

	/**
	 * Has to be called to close the Database
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		this.con.close();
	}

	/**
	 * returns the get statement from the DatabasePreparedQuery
	 */
	public <T extends DatabasePreparedQuery> PreparedStatement getPreparedSelectStatement(
			T preparedQueryType) {
		return map.get(preparedQueryType.getInternName()
				+ preparedQueryType.getSelectQuery());
	}

	/**
	 * returns the update statement from the DatabasePreparedQuery
	 */
	public <T extends DatabasePreparedQuery> PreparedStatement getPreparedUpdateStatement(
			T preparedQueryType) {
		return map.get(preparedQueryType.getInternName()
				+ preparedQueryType.getUpdateQuery());
	}

	public void commit() {
		if (con != null) {
			try {
				con.commit();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Could not commit Query", e);
			}
		}

	}
}