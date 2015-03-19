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
package eu.eexcess.languagemodel;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import sqlite.Database;

public class LanguageModel {
	
	public String resource;
	
	Hashtable <String, Integer> model = new Hashtable <String, Integer>();

	
	public LanguageModel(String resource) {
		super();
		this.resource = resource;
	}
	
	
	public void addTerm(String term) {
		
		if (this.model.containsKey(term))
		{
			this.model.put(term, this.model.get(term)+ 1);
		}
		else
		{
			this.model.put(term,  1);
		}
	}

	
	public void storeInDatabase(String dbName) throws SQLException
	{
	
		Connection dbConnection = null;
		
		Statement stat = null;
		
	    Database db = new Database();
		try {
			
			dbConnection = db.connect(dbName);
			
			dbConnection.setAutoCommit(false);
		    stat = dbConnection.createStatement();
		    
			stat.executeUpdate(clearQuery(this.resource));
		    
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		for (String term : model.keySet())
	    {  	  		
			     stat.executeUpdate(updateQuery(this.resource, term, model.get(term)));  	          
	    }
	    if(dbConnection!=null)
	    dbConnection.commit();
	    
	    db.disconnect();
		
	}
	
	private static String clearQuery(String tableName)
	{
		return "DELETE FROM " + tableName;
	}
	
	private static String updateQuery(String tableName, String term, int count)
	{
		return "INSERT INTO " + tableName
				+ " VALUES ('" + term + "', " + count + ")";
	}
    
	
	
	
	@Override
	public String toString() {
		return "LanguageModel [resource=" + resource + ", model=" + model + "]";
	}
	
	
	
	

}
