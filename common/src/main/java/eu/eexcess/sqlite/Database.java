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

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic Database Connection
 * 
 * @author hziak
 * 
 */
public class Database<T extends DatabasePreparedQuery> implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private String dBName = null;

    private Map<String, PreparedStatement> map = new HashMap<String, PreparedStatement>();

    private Connection con;

    public Database() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not load JDBC Driver", e);
        }
    }

    public Database(String dBName, T[] preparedStatementsDefinitions) {
        this.dBName = dBName;
        LOGGER.log(Level.INFO, "Trying to open DB:" + this.dBName);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not load JDBC Driver", e);
        }
        try {
            this.con = DriverManager.getConnection("jdbc:sqlite:" + this.dBName);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not connect to Database: " + this.dBName, e);
        }
        if (con != null) {
            try {
                initPreparedStatements(con, preparedStatementsDefinitions);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Prepared Statements could not be created", e);
            }
            try {

                con.setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Autocommit could not be set to false", e);
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
            LOGGER.log(Level.SEVERE, "Could not Connec to Database:" + database, e1);
            return null;
        }
        try {
            this.con = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not Connec to Database:" + database, e);
            return null;
        }
        return con;
    }

    /*
     * Initializes the Prepared Statements
     */
    private void initPreparedStatements(Connection con, T[] preparedStatements) throws SQLException {

        if (con != null) {
            for (T t : preparedStatements) {
                // try to create table
                try {
                    con.createStatement().executeUpdate(t.getCreateQuery());
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "failed to create table  [" + t.getInternName() + "]", e);
                }

                // map update statement
                try {
                    PreparedStatement updateStatement = con.prepareStatement(t.getUpdateQuery());
                    if (updateStatement != null) {
                        map.put(t.getInternName() + t.getUpdateQuery(), updateStatement);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "failed to prepare statement [" + t.getUpdateQuery() + "]", e);
                }

                // map select statement
                try {
                    PreparedStatement getStatement = con.prepareStatement(t.getSelectQuery());
                    if (getStatement != null) {
                        map.put(t.getInternName() + t.getSelectQuery(), getStatement);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "failed to prepare statement [" + t.getSelectQuery() + "]", e);
                }

                // map insert statement
                try {
                    PreparedStatement updateStatement = con.prepareStatement(t.getInsertQuery());
                    if (updateStatement != null) {
                        map.put(t.getInternName() + t.getInsertQuery(), updateStatement);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "failed to prepare statement [" + t.getInsertQuery() + "]", e);
                }

                // map delete statement
                try {
                    PreparedStatement updateStatement = con.prepareStatement(t.getDeleteQuery());
                    if (updateStatement != null) {
                        map.put(t.getInternName() + t.getDeleteQuery(), updateStatement);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "failed to prepare statement [" + t.getDeleteQuery() + "]", e);
                }

                // map drop statement
                try {
                    PreparedStatement updateStatement = con.prepareStatement(t.getDropQuery());
                    if (updateStatement != null) {
                        map.put(t.getInternName() + t.getDropQuery(), updateStatement);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "failed to prepare statement [" + t.getDropQuery() + "]", e);
                }
            }
        }
    }

    /**
     * Has to be called to close the Database
     * 
     * @throws SQLException
     */
    @Override
    public void close() throws IOException {
        try {
            this.con.close();
        } catch (SQLException sqe) {
            throw new IOException(sqe);
        }
    }

    /**
     * @return @see {@link DatabasePreparedQuery#getSelectQuery()}
     */
    public PreparedStatement getPreparedSelectStatement(T preparedQueryType) {
        return map.get(preparedQueryType.getInternName() + preparedQueryType.getSelectQuery());
    }

    /**
     * @return @see {@link DatabasePreparedQuery#getUpdateQuery()}
     */
    public PreparedStatement getPreparedUpdateStatement(T preparedQueryType) {
        return map.get(preparedQueryType.getInternName() + preparedQueryType.getUpdateQuery());
    }

    /**
     * @return @see {@link DatabasePreparedQuery#getCreateQuery()}
     */
    public PreparedStatement getPreparedCreateStatement(T preparedQueryType) {
        return map.get(preparedQueryType.getInternName() + preparedQueryType.getCreateQuery());
    }

    /**
     * @return @see {@link DatabasePreparedQuery#getDropQuery()}
     */
    public PreparedStatement getPreparedDropStatement(T preparedQueryType) {
        return map.get(preparedQueryType.getInternName() + preparedQueryType.getDropQuery());
    }

    /**
     * 
     * @return @see {@link DatabasePreparedQuery#getInsertQuery()}
     */
    public PreparedStatement getPreparedInsertStatement(T preparedQueryType) {
        return map.get(preparedQueryType.getInternName() + preparedQueryType.getInsertQuery());
    }

    /**
     * 
     * @return @see {@link DatabasePreparedQuery#getDeleteQuery()}
     */
    public PreparedStatement getPreparedDeleStatement(T preparedQueryType) {
        return map.get(preparedQueryType.getInternName() + preparedQueryType.getDeleteQuery());
    }

    public void commit() {
        if (con != null) {
            try {
                con.commit();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not commit Query", e);
            }
        }

    }
}