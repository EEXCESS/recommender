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
package eu.eexcess.dataformats.userprofile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * Keywords out of the context
 * 
 * @author hziak
 *
 */

public class ContextKeyword implements Serializable {

    private static final long serialVersionUID = -4047662119669146571L;
    @XmlElement(name = "entityType")
	private String entityType;
    @XmlElement(name = "entityUri")
    private String entityUri;
    @XmlElement(name = "isMainTopic")
    private Boolean isMainTopic=false;
    
 // Is used for query expansion
    @XmlElement(name = "expansion", required = false)
    public ExpansionType expansion;
//    @XmlElement(name = "reason")
//    public String reason;
    @XmlElement(name = "text")
    public String text;
    
    @Deprecated
    public Double weight;
    @Deprecated
	public String reason;
    
    
 
    public ContextKeyword() {

    }

     public ContextKeyword(String reason, String text, Double weight) {
     super();
     this.text = text;
     this.weight = weight;
     this.expansion=ExpansionType.NONE;
     }

    public ContextKeyword(String text) {
        this.text = text;
    }

    public ContextKeyword(String text, Double weight) {
        this.text = text;
        this.weight = weight;
    }

    public ContextKeyword(String text, ExpansionType expansion) {
        this.text = text;
        this.expansion = expansion;
    }

    public ContextKeyword(String text, Double weight, ExpansionType expansion) {
        this.text = text;
        this.weight = weight;
        this.expansion = expansion;

    }


	public Boolean getIsMainTopic() {
		return isMainTopic;
	}

	public void setIsMainTopic(Boolean isMainTopic) {
		this.isMainTopic = isMainTopic;
	}

	public String getEntityUri() {
		return entityUri;
	}

	public void setEntityUri(String entityUri) {
		this.entityUri = entityUri;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}


	

}
