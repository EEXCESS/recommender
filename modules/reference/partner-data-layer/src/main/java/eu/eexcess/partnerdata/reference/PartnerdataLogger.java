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

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

public class PartnerdataLogger {

    protected PartnerConfiguration partnerConfiguration;

    public PartnerdataLogger(PartnerConfiguration partnerConfiguration) {
        this.partnerConfiguration = partnerConfiguration;
        this.actLogEntry = new PartnerdataLogEntry();
        this.actLogEntry.setSystemId(this.partnerConfiguration.getSystemId());
    }

    protected PartnerdataLogEntry actLogEntry;

    public PartnerdataLogEntry getActLogEntry() {
        return actLogEntry;
    }

    public void setActLogEntry(PartnerdataLogEntry actLogEntry) {
        this.actLogEntry = actLogEntry;
    }

    public void save() {
        // write the actLogEntry in a file
        // writeActLogEntryAsCSV();
        PartnerdataLoggerCSVSingleFilePartner.getInstance().save(this.actLogEntry);
    }

    /*
     * private void writeActLogEntryAsCSV() { StringBuffer csvResult=new
     * StringBuffer(); csvResult.append(this.actLogEntry.getCSVHeader());
     * csvResult.append('\n');
     * csvResult.append(this.actLogEntry.getCVSValues());
     * 
     * DateFormat df = new SimpleDateFormat("yyyy-MM-dd--HH.mm.ss"); Date today
     * = Calendar.getInstance().getTime(); String reportDate = df.format(today);
     * try {
     * 
     * File myTempFile = new File(PartnerdataConfig.dataDir + reportDate + "-" +
     * "csv-logs.csv");
     * 
     * Writer out = new BufferedWriter(new OutputStreamWriter( new
     * FileOutputStream(myTempFile), "UTF-8"));
     * 
     * out.append(csvResult.toString());
     * 
     * out.flush(); out.close();
     * 
     * } catch (UnsupportedEncodingException e) {
     * System.out.println(e.getMessage()); } catch (IOException e) {
     * System.out.println(e.getMessage()); } catch (Exception e) {
     * System.out.println(e.getMessage()); }
     * 
     * }
     */

    public void addQuery(SecureUserProfile userProfile) {
        this.actLogEntry.addQuery(userProfile);
    }

    public void addResults(ResultList recommendations) {
        this.actLogEntry.addResults(recommendations);
    }
}
