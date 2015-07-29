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

    // public String systemId;
    private String searchEndpoint; // e.g."http://localhost:8080/search/${query}";
    private String detailEndpoint;
    private String partnerConnectorClass;
    /**
     * Data mappings and transformations
     */
    private boolean isTransformedNative; // in case of partner transforms
                                         // himself a local transformation is
                                         // not used
    private String transformerClass; // e.g., DummyTransformr
    private String mappingListTransformationFile;
    private String mappingObjectTransformationFile;

    /**
     * Access credentials for partner system
     */

    private String userName;
    private String password;
    private String apiKey;
    private Boolean isQueryExpansionEnabled;
    private Boolean partnerDataRequestsTrace;
    private Boolean enableEnriching;
    private String partnerDataLogDir;
    private String partnerDataDataDir;
    private Boolean makeCleanupBeforeTransformation;
    private String federatedRecommenderURI;

    public String getSearchEndpoint() {
        return searchEndpoint;
    }

    public void setSearchEndpoint(String searchEndpoint) {
        this.searchEndpoint = searchEndpoint;
    }

    public String getDetailEndpoint() {
        return detailEndpoint;
    }

    public void setDetailEndpoint(String detailEndpoint) {
        this.detailEndpoint = detailEndpoint;
    }

    public String getPartnerConnectorClass() {
        return partnerConnectorClass;
    }

    public void setPartnerConnectorClass(String partnerConnectorClass) {
        this.partnerConnectorClass = partnerConnectorClass;
    }

    public boolean isTransformedNative() {
        return isTransformedNative;
    }

    public void setTransformedNative(boolean isTransformedNative) {
        this.isTransformedNative = isTransformedNative;
    }

    public String getTransformerClass() {
        return transformerClass;
    }

    public void setTransformerClass(String transformerClass) {
        this.transformerClass = transformerClass;
    }

    public String getMappingListTransformationFile() {
        return mappingListTransformationFile;
    }

    public void setMappingListTransformationFile(String mappingListTransformationFile) {
        this.mappingListTransformationFile = mappingListTransformationFile;
    }

    public String getMappingObjectTransformationFile() {
        return mappingObjectTransformationFile;
    }

    public void setMappingObjectTransformationFile(String mappingObjectTransformationFile) {
        this.mappingObjectTransformationFile = mappingObjectTransformationFile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getPartnerDataRequestsTrace() {
        return partnerDataRequestsTrace;
    }

    public void setPartnerDataRequestsTrace(Boolean partnerDataRequestsTrace) {
        this.partnerDataRequestsTrace = partnerDataRequestsTrace;
    }

    public Boolean getEnableEnriching() {
        return enableEnriching;
    }

    public void setEnableEnriching(Boolean enableEnriching) {
        this.enableEnriching = enableEnriching;
    }

    public String getPartnerDataLogDir() {
        return partnerDataLogDir;
    }

    public void setPartnerDataLogDir(String partnerDataLogDir) {
        this.partnerDataLogDir = partnerDataLogDir;
    }

    public String getPartnerDataDataDir() {
        return partnerDataDataDir;
    }

    public void setPartnerDataDataDir(String partnerDataDataDir) {
        this.partnerDataDataDir = partnerDataDataDir;
    }

    public Boolean getMakeCleanupBeforeTransformation() {
        return makeCleanupBeforeTransformation;
    }

    public void setMakeCleanupBeforeTransformation(Boolean makeCleanupBeforeTransformation) {
        this.makeCleanupBeforeTransformation = makeCleanupBeforeTransformation;
    }

    public String getFederatedRecommenderURI() {
        return federatedRecommenderURI;
    }

    public void setFederatedRecommenderURI(String federatedRecommenderURI) {
        this.federatedRecommenderURI = federatedRecommenderURI;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((apiKey == null) ? 0 : apiKey.hashCode());
        result = prime * result + ((detailEndpoint == null) ? 0 : detailEndpoint.hashCode());
        result = prime * result + ((enableEnriching == null) ? 0 : enableEnriching.hashCode());
        result = prime * result + ((federatedRecommenderURI == null) ? 0 : federatedRecommenderURI.hashCode());
        result = prime * result + (isTransformedNative ? 1231 : 1237);
        result = prime * result + ((makeCleanupBeforeTransformation == null) ? 0 : makeCleanupBeforeTransformation.hashCode());
        result = prime * result + ((mappingListTransformationFile == null) ? 0 : mappingListTransformationFile.hashCode());
        result = prime * result + ((mappingObjectTransformationFile == null) ? 0 : mappingObjectTransformationFile.hashCode());
        result = prime * result + ((partnerConnectorClass == null) ? 0 : partnerConnectorClass.hashCode());
        result = prime * result + ((partnerDataDataDir == null) ? 0 : partnerDataDataDir.hashCode());
        result = prime * result + ((partnerDataLogDir == null) ? 0 : partnerDataLogDir.hashCode());
        result = prime * result + ((partnerDataRequestsTrace == null) ? 0 : partnerDataRequestsTrace.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((searchEndpoint == null) ? 0 : searchEndpoint.hashCode());
        result = prime * result + ((transformerClass == null) ? 0 : transformerClass.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PartnerConfiguration other = (PartnerConfiguration) obj;
        if (apiKey == null) {
            if (other.apiKey != null)
                return false;
        } else if (!apiKey.equals(other.apiKey))
            return false;
        if (detailEndpoint == null) {
            if (other.detailEndpoint != null)
                return false;
        } else if (!detailEndpoint.equals(other.detailEndpoint))
            return false;
        if (enableEnriching == null) {
            if (other.enableEnriching != null)
                return false;
        } else if (!enableEnriching.equals(other.enableEnriching))
            return false;
        if (federatedRecommenderURI == null) {
            if (other.federatedRecommenderURI != null)
                return false;
        } else if (!federatedRecommenderURI.equals(other.federatedRecommenderURI))
            return false;
        if (isTransformedNative != other.isTransformedNative)
            return false;
        if (makeCleanupBeforeTransformation == null) {
            if (other.makeCleanupBeforeTransformation != null)
                return false;
        } else if (!makeCleanupBeforeTransformation.equals(other.makeCleanupBeforeTransformation))
            return false;
        if (mappingListTransformationFile == null) {
            if (other.mappingListTransformationFile != null)
                return false;
        } else if (!mappingListTransformationFile.equals(other.mappingListTransformationFile))
            return false;
        if (mappingObjectTransformationFile == null) {
            if (other.mappingObjectTransformationFile != null)
                return false;
        } else if (!mappingObjectTransformationFile.equals(other.mappingObjectTransformationFile))
            return false;
        if (partnerConnectorClass == null) {
            if (other.partnerConnectorClass != null)
                return false;
        } else if (!partnerConnectorClass.equals(other.partnerConnectorClass))
            return false;
        if (partnerDataDataDir == null) {
            if (other.partnerDataDataDir != null)
                return false;
        } else if (!partnerDataDataDir.equals(other.partnerDataDataDir))
            return false;
        if (partnerDataLogDir == null) {
            if (other.partnerDataLogDir != null)
                return false;
        } else if (!partnerDataLogDir.equals(other.partnerDataLogDir))
            return false;
        if (partnerDataRequestsTrace == null) {
            if (other.partnerDataRequestsTrace != null)
                return false;
        } else if (!partnerDataRequestsTrace.equals(other.partnerDataRequestsTrace))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (searchEndpoint == null) {
            if (other.searchEndpoint != null)
                return false;
        } else if (!searchEndpoint.equals(other.searchEndpoint))
            return false;
        if (transformerClass == null) {
            if (other.transformerClass != null)
                return false;
        } else if (!transformerClass.equals(other.transformerClass))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartnerConfiguration [searchEndpoint=" + searchEndpoint + ", detailEndpoint=" + detailEndpoint + ", partnerConnectorClass=" + partnerConnectorClass
                + ", isTransformedNative=" + isTransformedNative + ", transformerClass=" + transformerClass + ", mappingListTransformationFile=" + mappingListTransformationFile
                + ", mappingObjectTransformationFile=" + mappingObjectTransformationFile + ", userName=" + userName + ", password=" + password + ", apiKey=" + apiKey
                + ", partnerDataRequestsTrace=" + partnerDataRequestsTrace + ", enableEnriching=" + enableEnriching + ", partnerDataLogDir=" + partnerDataLogDir
                + ", partnerDataDataDir=" + partnerDataDataDir + ", makeCleanupBeforeTransformation=" + makeCleanupBeforeTransformation + ", federatedRecommenderURI="
                + federatedRecommenderURI + "]";
    }

    public Boolean isQueryExpansionEnabled() {
        return isQueryExpansionEnabled;
    }

    public void setIsQueryExpansionEnabled(Boolean isQueryExpansionEnabled) {
        this.isQueryExpansionEnabled = isQueryExpansionEnabled;
    }

}
