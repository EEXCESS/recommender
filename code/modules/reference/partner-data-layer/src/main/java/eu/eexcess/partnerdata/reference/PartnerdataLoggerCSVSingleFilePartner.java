/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.partnerdata.reference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PartnerdataLoggerCSVSingleFilePartner {

	 private static final PartnerdataLoggerCSVSingleFilePartner obj = new PartnerdataLoggerCSVSingleFilePartner(); 
     
     private PartnerdataLoggerCSVSingleFilePartner() { 

     } 
          
     public static PartnerdataLoggerCSVSingleFilePartner getInstance() { 
       return obj; 
     } 
     

	public void save(PartnerdataLogEntry entry)
     {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date today = Calendar.getInstance().getTime();        
		String reportDate = df.format(today);

		String filename = PartnerdataConfig.dataDir + reportDate + "-"+ entry.getSystemId()+"-enchrichment-stats.csv";
		boolean fileExists = false;
		{
			File f = new File(filename);
			if(f.exists() && !f.isDirectory()) 
			{  
				fileExists = true;
			}
		}
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		    if (!fileExists) 
		    	out.println(entry.getCSVHeader());
		    out.println(entry.getCVSValues());
		    out.close();
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	
		
	}

}
