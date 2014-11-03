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
package eu.eexcess.partnerrecommender.reference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;

import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;

/**
 * 
 * 
 * @author thomas.orgel@joanneum.at
 */
public class PartnerConnectorBase {
	
	protected Document transformJSON2XML(String jsonData) throws EEXCESSDataTransformationException{
		XMLSerializer serializer = new XMLSerializer(); 
	    JSON json = JSONSerializer.toJSON( jsonData ); 
	    serializer.setTypeHintsEnabled(false);
	    
	    String xmlString = serializer.write( json );  
	    try  
	    {  
			SAXReader reader = new SAXReader();
			org.dom4j.Document dom4jDoc = reader.read(new StringReader(xmlString));
			  
			DOMWriter writer = new DOMWriter();
			org.w3c.dom.Document w3cDoc = writer.write(dom4jDoc);

	        return w3cDoc;
	    } catch (Exception e) {  
	        e.printStackTrace();  
		    throw new EEXCESSDataTransformationException(e);
	    } 
	} 
	
	protected void dumpFile(String input, String postfix) {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

		// Get the date today using Calendar object.
		Date today = Calendar.getInstance().getTime();        
		// Using DateFormat format method we can create a string 
		// representation of a date with the defined format.
		String reportDate = df.format(today);
		  try {
				File myTempFile = new File("c:\\eexcess-temp\\" + reportDate +"-"+postfix+".xml");
		 
				Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(myTempFile), "UTF-8"));
		 
				out.append(input);
		 
				out.flush();
				out.close();
		 
			    } 
			   catch (UnsupportedEncodingException e) 
			   {
				System.out.println(e.getMessage());
			   } 
			   catch (IOException e) 
			   {
				System.out.println(e.getMessage());
			    }
			   catch (Exception e)
			   {
				System.out.println(e.getMessage());
			   }
	}

	
}