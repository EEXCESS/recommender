/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

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
package eu.eexcess.partnerrecommender.api;

import java.io.IOException;

import org.w3c.dom.Document;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;

/**
 * 
 * 
 * @author plopez@know-center.at
 */
public interface PartnerConnectorApi {
	    
	    /** 
	     * Queries a partner
	     * @param partnerConfiguration
	     * @param userProfile
	     * @return the search request as string
	     * @throws IOException 
	     */
	    public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException;

	    /** 
	     * Queries a partner
	     * @param partnerConfiguration
	     * @param userProfile
	     * @return the search request as string
	     * @throws IOException 
	     */
	    public Document queryPartnerDetails(PartnerConfiguration partnerConfiguration, DocumentBadge document,PartnerdataLogger logger) throws IOException;

	    
	    /**
	     * Queries a partner and directly returns a ResultList instance.
	     * @param partnerConfiguration
	     * @param userProfile
	     * @return null, if not supported
	     * @throws IOException
	     */
		public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException;
}
