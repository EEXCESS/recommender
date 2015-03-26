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

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

/**
 * 
 * 
 * @author rkern@know-center.at
 */
public interface PartnerRecommenderApi {
    /**
     * @throws IOException 
     * 
     */
    // TODO: Create EexcessException instead of IOException
    public abstract void initialize() throws IOException;

    /**
     * Recommend items from a partner system matching to a given user profile.
     * @param userProfile a secure user profile containing the current users information need
     * @return an XML document in the EEXCESS partner result format
     * @throws IOException
     */
    public abstract ResultList recommend(SecureUserProfile userProfile) throws IOException;
    //public abstract ResultList details(SecureUserProfile userProfile, ResultList items) throws IOException;

    /** 
     * Returns the EEXCESS user profile for a given user.
     * @return
     * @throws IOException
     */
    public abstract Document getUserProfile(String userId) throws IOException;


}