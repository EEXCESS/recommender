package eu.eexcess.sqlite;

/**
 * interface for getting simple queries regarding to a table
 * 
 * @author hziak
 * 
 */
public interface DatabasePreparedQuery {
    public String getUpdateQuery();

    /**
     * @return a SQL select statement
     */
    public String getSelectQuery();

    /**
     * @return a SQL create statement
     */
    public String getCreateQuery();

    /**
     * @return the table's name
     */
    public String getInternName();
}
