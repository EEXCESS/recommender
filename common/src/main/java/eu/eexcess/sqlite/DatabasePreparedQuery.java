package eu.eexcess.sqlite;

/**
 * interface for getting
 * 
 * @author hziak
 * 
 */
public interface DatabasePreparedQuery {
	public String getUpdateQuery();

	public String getSelectQuery();

	public String getCreateQuery();

	public String getInternName();
}
