package eu.eexcess.sqlite;
/**
 * Prepared Statements Enum for QueryLogs
 * @author hziak
 *
 */
public enum DatabaseQueryStats implements DatabasePreparedQuery {
	REQUESTLOG(
			"CREATE TABLE PARTNERREQUESTCOUNT(SYSTEM_ID CHAR(255) PRIMARY KEY     NOT NULL, REQUESTCOUNT INT NOT NULL, FAILEDREQUESTCOUNT  INT     NOT NULL,  FAILEDREQUESTTIMEOUTCOUNT  INT  NOT NULL )",
			"INSERT OR REPLACE INTO PARTNERREQUESTCOUNT('SYSTEM_ID','REQUESTCOUNT','FAILEDREQUESTCOUNT','FAILEDREQUESTTIMEOUTCOUNT') VALUES (?,?,?,?)",
			"SELECT * FROM PARTNERREQUESTCOUNT WHERE SYSTEM_ID = ?"), 
	QUERYLOG(
			"CREATE TABLE PARTNERQUERYLOG (SYSTEM_ID CHAR(255) NOT NULL,QUERY TEXT  NOT NULL, CALLTIME   INT     NULL,  FIRSTTRANSFORMATIONTIME            INT      NULL, SECONDTRANSFORMATIONTIME      INT      NULL , ENRICHMENTTIME      INT      NULL , RESULTCOUNT      INT      NULL )",
			"INSERT OR REPLACE INTO PARTNERQUERYLOG('SYSTEM_ID','QUERY','CALLTIME','FIRSTTRANSFORMATIONTIME','SECONDTRANSFORMATIONTIME','ENRICHMENTTIME','RESULTCOUNT') VALUES (?,?,?,?,?,?,?)",
			"SELECT * FROM PARTNERQUERYLOG WHERE SYSTEM_ID = ?");

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

	public String getUpdateQuery() {
		return updateQuery;
	}

	public void setUpdateQuery(String updateQuery) {
		this.updateQuery = updateQuery;
	}

	public String getSelectQuery() {
		return getQuery;
	}

	public void setGetQuery(String getQuery) {
		this.getQuery = getQuery;
	}

	public String getCreateQuery() {
		return createQuery;
	}

	public void setCreateQuery(String createQuery) {
		this.createQuery = createQuery;
	}

	public String getInternName() {
		return internName;
	}

}
