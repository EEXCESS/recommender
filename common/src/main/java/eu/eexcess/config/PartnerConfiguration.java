/* Copyright (C) 2010 
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
package eu.eexcess.config;

import eu.eexcess.dataformats.PartnerBadge;


/**
 * 
 * 
 * @author rkern@know-center.at
 */
public class PartnerConfiguration extends PartnerBadge {
	private static final long serialVersionUID = 2257947888683158873L;

	//public String systemId; 
    public String searchEndpoint; // e.g."http://localhost:8080/search/${query}";
	public String detailEndpoint;
	
    public String partnerConnectorClass;
    public String queryGeneratorClass; // = LuceneQueryGenerator.class.getName();   
    
    
    /** 
     * Data mappings and transformations
     */
    public boolean isTransformedNative; // in case of partner transforms himself a local transformation is not used
    public String transformerClass; // e.g., DummyTransformr
    public String mappingListTransformationFile;
    public String mappingObjectTransformationFile;
    
    /** 
     * Access credentials for partner system
     */

    public String userName;
    public String password;
    public String apiKey;
    
    public Boolean partnerDataRequestsTrace;
    
    public Boolean enableEnriching;
    
    public String partnerDataLogDir;
    public String partnerDataDataDir;
    public Boolean makeCleanupBeforeTransformation;
    
    public String federatedRecommenderURI;

}
