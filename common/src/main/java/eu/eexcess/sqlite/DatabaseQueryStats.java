package eu.eexcess.sqlite;

import java.util.logging.Logger;

/**
 * Prepared Statements Enum for QueryLogs
 * 
 * @author hziak
 *
 */
public enum DatabaseQueryStats implements DatabasePreparedQuery {
    REQUESTLOG(
            "CREATE TABLE IF NOT EXISTS PARTNERREQUESTCOUNT(SYSTEM_ID CHAR(255) PRIMARY KEY     NOT NULL, REQUESTCOUNT INT NOT NULL, FAILEDREQUESTCOUNT  INT     NOT NULL,  FAILEDREQUESTTIMEOUTCOUNT  INT  NOT NULL )",
            "INSERT OR REPLACE INTO PARTNERREQUESTCOUNT('SYSTEM_ID','REQUESTCOUNT','FAILEDREQUESTCOUNT','FAILEDREQUESTTIMEOUTCOUNT') VALUES (?,?,?,?)",
            "SELECT * FROM PARTNERREQUESTCOUNT WHERE SYSTEM_ID = ?"), QUERYLOG(
            "CREATE TABLE IF NOT EXISTS PARTNERQUERYLOG (SYSTEM_ID CHAR(255) NOT NULL,QUERY TEXT  NOT NULL, CALLTIME   INT     NULL,  FIRSTTRANSFORMATIONTIME            INT      NULL, SECONDTRANSFORMATIONTIME      INT      NULL , ENRICHMENTTIME      INT      NULL , RESULTCOUNT      INT      NULL )",
            "INSERT OR REPLACE INTO PARTNERQUERYLOG('SYSTEM_ID','QUERY','CALLTIME','FIRSTTRANSFORMATIONTIME','SECONDTRANSFORMATIONTIME','ENRICHMENTTIME','RESULTCOUNT') VALUES (?,?,?,?,?,?,?)",
            "SELECT * FROM PARTNERQUERYLOG WHERE SYSTEM_ID = ?");

    private static final String SELECT_1_UNION_SELECT_42 = "select 1 union select 42";
    private static final String REQUESTED_NOT_IMPLEMENTED = "requested not implemented SQL query";
    private static final Logger LOGGER = Logger.getLogger(DatabaseQueryStats.class.getName());
    private String createQuery;
    private String updateQuery;
    private String getQuery;
    private String internName;

    private DatabaseQueryStats(String create, String update, String get) {
        createQuery = create;
        updateQuery = update;
        getQuery = get;
    }

    private DatabaseQueryStats() {

    }

    @Override
    public String getInternName() {
        return internName;
    }

    @Override
    public String getCreateQuery() {
        return createQuery;
    }

    @Override
    public String getDropQuery() {
        LOGGER.warning(REQUESTED_NOT_IMPLEMENTED);
        return SELECT_1_UNION_SELECT_42;
    }

    @Override
    public String getUpdateQuery() {
        return updateQuery;
    }

    @Override
    public String getInsertQuery() {
        LOGGER.warning(REQUESTED_NOT_IMPLEMENTED);
        return SELECT_1_UNION_SELECT_42;
    }

    @Override
    public String getDeleteQuery() {
        LOGGER.warning(REQUESTED_NOT_IMPLEMENTED);
        return SELECT_1_UNION_SELECT_42;
    }

    @Override
    public String getSelectQuery() {
        return getQuery;
    }
}
