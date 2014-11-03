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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import eu.eexcess.config.PartnerConfiguration;

public class PartnerdataTracer {

	public enum FILETYPE {
	    XML, JSON, TXT 
	}
	
	
	static public void debugTrace(PartnerConfiguration partnerConfig, String debug) {
		if (partnerConfig.partnerDataRequestsTrace) {
			Logger.getLogger(PartnerdataTracer.class.getName()).info(debug);
		}
	}

	private static String getExtensionFromType(FILETYPE filetype) {
		if (filetype  == FILETYPE.JSON) return "json";
		if (filetype  == FILETYPE.XML) return "xml";
		if (filetype  == FILETYPE.TXT) return "txt";
		return "";
	}
	public static void dumpFile(@SuppressWarnings("rawtypes") Class myClass,PartnerConfiguration partnerConfig, String input, String postfix, FILETYPE filetype) {
		if (!partnerConfig.partnerDataRequestsTrace) return;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd--HH.mm.ss.SSS");
		Date today = Calendar.getInstance().getTime();        
		String reportDate = df.format(today);
		try {
			
			File myTempFile = new File(PartnerdataConfig.logDir + reportDate + "-" + partnerConfig.systemId + "-"
					+ myClass.getSimpleName() + "-" + postfix + "." + getExtensionFromType(filetype));

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(myTempFile), "UTF-8"));

			out.append(input);

			out.flush();
			out.close();

		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
