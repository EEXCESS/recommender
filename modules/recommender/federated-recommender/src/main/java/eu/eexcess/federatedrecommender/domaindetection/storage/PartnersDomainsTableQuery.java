package eu.eexcess.federatedrecommender.domaindetection.storage;

import eu.eexcess.sqlite.DatabasePreparedQuery;

/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH"
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Raoul Rubien
 */

public enum PartnersDomainsTableQuery implements DatabasePreparedQuery {
    PARTNER_DOMAINS_TABLE_QUERY;

    protected static class Domain {
        public final String NAME;
        public final String SQL_TYPE;

        public Domain(String domainName, String domainSqlType) {
            this.NAME = domainName;
            this.SQL_TYPE = domainSqlType;
        }
    }

    private static class Tables {
        private static class PartnerProbes {
            private static class Domains {
                public static final Domain ID_PRIMARY_KEY = new Domain("ID", "INTEGER");
                public static final Domain PROBE_TIMESTAMP = new Domain("ProbeTimestamp", "INTEGER");
                public static final Domain PARTNER_NAME = new Domain("PartnerName", "TEXT");
                public static final Domain DOMAIN_NAME = new Domain("PartnerDomain", "TEXT");
                public static final Domain DOMAIN_WEIGHT = new Domain("DomainWeight", "REAL");
            }

            private static final String NAME = "PartnerDomainProbes";
            private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" + PartnerProbes.Domains.ID_PRIMARY_KEY.NAME + " "
                    + PartnerProbes.Domains.ID_PRIMARY_KEY.SQL_TYPE + " PRIMARY KEY NOT NULL, " + PartnerProbes.Domains.PROBE_TIMESTAMP.NAME + " "
                    + PartnerProbes.Domains.PROBE_TIMESTAMP.SQL_TYPE + " NOT NULL, " + PartnerProbes.Domains.PARTNER_NAME.NAME + " "
                    + PartnerProbes.Domains.PARTNER_NAME.SQL_TYPE + " NOT NULL, " + PartnerProbes.Domains.DOMAIN_NAME.NAME + " "
                    + PartnerProbes.Domains.DOMAIN_NAME.SQL_TYPE + " NOT NULL, " + PartnerProbes.Domains.DOMAIN_WEIGHT.NAME + " "
                    + PartnerProbes.Domains.DOMAIN_WEIGHT.SQL_TYPE + " NOT NULL)";
            private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + PartnerProbes.NAME;

            // TODO: write the insert statement statement -> behaves like
            // update!
            // TODO: write the select statement: return the last probes
            // TODO: create some tests to verify the queries
            // TODO: use the db-table as storage for new probes

            private static final String DELETE_PARTNER_PROBES = "DELETE FROM " + PartnerProbes.NAME + " WHERE " + PartnerProbes.Domains.PARTNER_NAME.NAME
                    + " LIKE ?";
            private static final String INSERT_INTO_TABLE = "INSERT INTO " + PartnerProbes.NAME + " (" + PartnerProbes.Domains.PARTNER_NAME.NAME + ", "
                    + PartnerProbes.Domains.PROBE_TIMESTAMP.NAME + ", " + PartnerProbes.Domains.DOMAIN_NAME.NAME + ", "
                    + PartnerProbes.Domains.DOMAIN_WEIGHT.NAME + ") VALUES (?, ?, ?, ?)";

            private static final String SELECT_FROM_TABLE = "SELECT * FROM " + PartnerProbes.NAME + " WHERE " + PartnerProbes.Domains.PARTNER_NAME.NAME
                    + " LIKE ?";
        }
    }

    private PartnersDomainsTableQuery() {
    }

    @Override
    public String getUpdateQuery() {
        return Tables.PartnerProbes.INSERT_INTO_TABLE;
    }

    @Override
    public String getSelectQuery() {
        return Tables.PartnerProbes.SELECT_FROM_TABLE;
    }

    @Override
    public String getCreateQuery() {
        return Tables.PartnerProbes.CREATE_TABLE;
    }

    @Override
    public String getInternName() {
        return Tables.PartnerProbes.NAME;
    }

    public String getDropQuery() {
        return Tables.PartnerProbes.DROP_TABLE;
    }

    public String getDeletePartnerProbes() {
        return Tables.PartnerProbes.DELETE_PARTNER_PROBES;
    }

}
