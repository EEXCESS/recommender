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
package sqlite;

import java.sql.*;

import eu.eexcess.sqlite.Database;

public class Main {

    private static final String DATABASE = "frequency.sqlite";
    
	  public static void main( String args[] )
	  {
	  
          Connection dbConnection;
          Database db=new Database();
		try {
			
			dbConnection = db.connect(DATABASE);
			
			//ArrayList elements = new ArrayList();
			
		    Statement stat = dbConnection.createStatement();
		    ResultSet rs = stat.executeQuery("select * from " + "Mendeley" + ";");

		      while (rs.next()) {
		        System.out.println(rs.getString("term") + ": " + rs.getString("count"));
		      }
		    rs.close();
		    db.close();
			
			//con.setAutoCommit(false); // Disable autocommit (very slow, one commit per update)
	        //Statement stat = con.createStatement();
	        
	        //stat.executeQuery(sql)
	        
            //con.commit();
/*
			ArrayList<ResultSet> results = Database.getAllResultsets("Frequency");
			
			for(ResultSet result : results )
			{
				System.out.println("*");
				System.out.println(result.toString());
				System.out.println(result.getString(1));
			}
			
			
            con.close();
            Database.disconnect();
            
            */
	          	          
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    //System.out.println("Opened database successfully");
	  }	
	
}
