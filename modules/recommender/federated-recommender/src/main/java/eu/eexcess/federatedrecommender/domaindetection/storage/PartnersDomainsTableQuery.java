package eu.eexcess.federatedrecommender.domaindetection.storage;

import java.util.logging.Logger;

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

    public static class Domain {
        public final String name;
        public final String sqlType;
        /**
         * provides information about the column position i.e. for "SELECT *"
         * queries
         */
        public final int columnIndex;

        /**
         * 
         * @param domainName
         *            the column's name
         * @param domainSqlType
         *            the type of the column
         * @param columnIndex
         *            the position of the column; first column begins at 1; used for
         *            "SELECT" queries
         */
        public Domain(String domainName, String domainSqlType, int columnIndex) {
            this.name = domainName;
            this.sqlType = domainSqlType;
            this.columnIndex = columnIndex;
        }
    }

    public static class Tables {
        public static class PartnerProbes {
            public static class Domains {
                public static final Domain ID_PRIMARY_KEY = new Domain("ID", "INTEGER", 1);
                public static final Domain PROBE_TIMESTAMP = new Domain("ProbeTimestamp", "INTEGER", 2);
                public static final Domain PARTNER_NAME = new Domain("PartnerName", "TEXT", 3);
                public static final Domain DOMAIN_NAME = new Domain("PartnerDomain", "TEXT", 4);
                public static final Domain DOMAIN_WEIGHT = new Domain("DomainWeight", "REAL", 5);
            }

            /**
             * the table name
             */
            private static final String NAME = "PartnerDomainProbes";

            private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" + PartnerProbes.Domains.ID_PRIMARY_KEY.name + " "
                    + PartnerProbes.Domains.ID_PRIMARY_KEY.sqlType + " PRIMARY KEY NOT NULL, " + PartnerProbes.Domains.PROBE_TIMESTAMP.name + " "
                    + PartnerProbes.Domains.PROBE_TIMESTAMP.sqlType + " NOT NULL, " + PartnerProbes.Domains.PARTNER_NAME.name + " "
                    + PartnerProbes.Domains.PARTNER_NAME.sqlType + " NOT NULL, " + PartnerProbes.Domains.DOMAIN_NAME.name + " "
                    + PartnerProbes.Domains.DOMAIN_NAME.sqlType + " NOT NULL, " + PartnerProbes.Domains.DOMAIN_WEIGHT.name + " "
                    + PartnerProbes.Domains.DOMAIN_WEIGHT.sqlType + " NOT NULL)";
            /**
             * 1st ?: table name @see {@link PartnerProbes#NAME}
             */
            private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + PartnerProbes.NAME;
            /**
             * 1st ?: partner name
             */
            private static final String DELETE_PARTNER_PROBES = "DELETE FROM " + PartnerProbes.NAME + " WHERE " + PartnerProbes.Domains.PARTNER_NAME.name
                    + " LIKE ?";
            /**
             * 1st ?: partner name, 2nd ?: time stamp, 3rd ?: domain name, 4th
             * ?: domain weight
             */
            private static final String INSERT_PROBE_INTO_TABLE = "INSERT OR REPLACE INTO " + PartnerProbes.NAME + " ("
                    + PartnerProbes.Domains.PARTNER_NAME.name + ", " + PartnerProbes.Domains.PROBE_TIMESTAMP.name + ", "
                    + PartnerProbes.Domains.DOMAIN_NAME.name + ", " + PartnerProbes.Domains.DOMAIN_WEIGHT.name + ") VALUES (?, ?, ?, ?)";

            /**
             * 1st ?: partner name
             */
            private static final String SELECT_FROM_TABLE = "SELECT * FROM " + PartnerProbes.NAME + " WHERE " + PartnerProbes.Domains.PARTNER_NAME.name
                    + " LIKE ?";
        }
    }

    private static final Logger LOGGER = Logger.getLogger(PartnersDomainsTableQuery.class.getName());

    /**
     * @see {@link Tables.PartnerProbes#NAME}
     */
    @Override
    public String getInternName() {
        return Tables.PartnerProbes.NAME;
    }

    /**
     * @see {@link Tables.PartnerProbes.Domains#CREATE_TABLE}
     */
    @Override
    public String getCreateQuery() {
        return Tables.PartnerProbes.CREATE_TABLE;
    }

    /**
     * @see {@link Tables.PartnerProbes.Domains#DROP_TABLE}
     */
    @Override
    public String getDropQuery() {
        return Tables.PartnerProbes.DROP_TABLE;
    }

    /**
     * @see {@link Tables.PartnerProbes.Domains#INSERT_PROPE_INTO_TABLE}
     */
    @Override
    public String getInsertQuery() {
        return Tables.PartnerProbes.INSERT_PROBE_INTO_TABLE;
    }

    @Override
    public String getUpdateQuery() {
        LOGGER.warning("requested not implemented SQL query");
        return "select 1 union select 42";
    }

    /**
     * @see {@link Tables.PartnerProbes.Domains#SDELETE_PARTNER_PROBES}
     */
    @Override
    public String getDeleteQuery() {
        return Tables.PartnerProbes.DELETE_PARTNER_PROBES;
    }

    /**
     * @see {@link Tables.PartnerProbes.Domains#SELECT_FROM_TABLE}
     */
    @Override
    public String getSelectQuery() {
        return Tables.PartnerProbes.SELECT_FROM_TABLE;
    }

    private PartnersDomainsTableQuery() {
    }

}
