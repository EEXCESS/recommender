package eu.eexcess.sqlite;

/**
 * interface for getting simple queries regarding to a table
 * 
 * @author hziak
 * 
 */
public interface DatabasePreparedQuery {

    /**
     * @return the table's name
     */
    public String getInternName();

    /**
     * @return A SQL statement to create the respective table. An implementation
     *         of the that method should consider "CREATE IF NOT EXISTS ..."
     */
    public String getCreateQuery();

    /**
     * @return a SQL statement to drop the respective table
     */
    public String getDropQuery();

    /**
     * @return a SQL statement to update the respective table
     */
    public String getUpdateQuery();

    /**
     * @return a SQL statement to insert values to table
     */
    public String getInsertQuery();

    /**
     * @return a SQL statement to delete values from table
     */
    public String getDeleteQuery();

    /**
     * @return a SQL select statement
     */
    public String getSelectQuery();
}
