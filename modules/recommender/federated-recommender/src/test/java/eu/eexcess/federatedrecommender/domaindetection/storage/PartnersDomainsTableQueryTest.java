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

package eu.eexcess.federatedrecommender.domaindetection.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class PartnersDomainsTableQueryTest {

    private Connection dbConnection;

    @Before
    public void setupNewEmptyDatabase() throws IOException, ClassNotFoundException, SQLException {
        File dbfile = File.createTempFile("tmp-", ".db");
        dbfile.deleteOnExit();
        assertEquals(true, dbfile.exists());
        Class.forName("org.sqlite.JDBC");

        // connect to fresh / empty database
        dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbfile.getCanonicalPath());
    }

    @Test
    public void createEmptyTmpDb_readFromTable_insertIntoTable_readFromTable_clearTable_removeTale_expectNotExceptional() throws SQLException {

        PartnersDomainsTableQuery qs = PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY;

        PreparedStatement pst = dbConnection.prepareStatement(qs.getDropQuery());
        pst.execute();

        boolean caughtExpectedException = false;
        try {
            // will fail due to missing table
            pst = dbConnection.prepareStatement(qs.getSelectQuery());
        } catch (SQLException e) {
            caughtExpectedException = true;
        }
        assertTrue(caughtExpectedException);

        // create new table
        pst = dbConnection.prepareStatement(qs.getCreateQuery());
        assertEquals(false, pst.execute());

        // insert value into table
        pst = dbConnection.prepareStatement(qs.getUpdateQuery());
        pst.setString(1, "zwb");
        pst.setInt(2, 123);
        pst.setString(3, "domain-x");
        pst.setDouble(4, 1.23);
        assertEquals(1, pst.executeUpdate());

        // remove value from table
        pst = dbConnection.prepareStatement(qs.getDeletePartnerProbes());
        pst.setString(1, "zwb");
        assertEquals(1, pst.executeUpdate());

        // expect no rows selected
        pst = dbConnection.prepareStatement(qs.getSelectQuery());
        assertEquals(true, pst.execute());
        assertEquals(false, pst.getResultSet().next());

        // drop table
        pst = dbConnection.prepareStatement(qs.getDropQuery());
        assertEquals(false, pst.execute());

        caughtExpectedException = false;
        try {
            // will fail due to missing table
            pst = dbConnection.prepareStatement(qs.getSelectQuery());
        } catch (SQLException e) {
            caughtExpectedException = true;
        }
        assertTrue(caughtExpectedException);
    }

    @Test
    public void contatenateTwoQueries_expectError() throws SQLException {
        PartnersDomainsTableQuery qs = PartnersDomainsTableQuery.PARTNER_DOMAINS_TABLE_QUERY;
        String insertQuery = "INSERT INTO PartnerDomainProbes (PartnerName, ProbeTimestamp, PartnerDomain, DomainWeight) VALUES (\"zbwxx\", 123, \"foobardomain\", ?)";
        PreparedStatement pst = dbConnection.prepareStatement(qs.getCreateQuery() + "; " + insertQuery);

        try {
            pst.setDouble(1, 0.99999);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
        assertTrue(false);
    }
}
