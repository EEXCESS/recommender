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

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Keywords out of the context
 * 
 * @author hziak
 *
 */

public class ContextKeyword implements Serializable {

    private static final long serialVersionUID = -4047662119669146571L;
    private SpecialFieldsEum type;
    private String uri;
    private Boolean isMainTopic = false;

    // Is used for query expansion

    private ExpansionType expansion;
    private String text;
    @Deprecated
    /**
     * @deprecated
     */
    private Double weight;
    @Deprecated
    /**
     * @deprecated
     */
    private String reason;

    public ContextKeyword() {

    }

    public ContextKeyword(String text) {
        this.setText(text);
    }

    public ContextKeyword(String text, Double weight) {
        this.setText(text);
        this.setWeight(weight);
    }

    public ContextKeyword(String text, ExpansionType expansion) {
        this.setText(text);
        this.setExpansion(expansion);
    }

    public ContextKeyword(String text, Double weight, ExpansionType expansion) {
        this.setText(text);
        this.setWeight(weight);
        this.setExpansion(expansion);

    }

    public Boolean getIsMainTopic() {
        return isMainTopic;
    }

    public void setIsMainTopic(Boolean isMainTopic) {
        this.isMainTopic = isMainTopic;
    }

    public SpecialFieldsEum getType() {
        return type;
    }

    public void setType(SpecialFieldsEum type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @deprecated
     * @return
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * @deprecated
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * @deprecated
     * @return
     */
    public String getReason() {
        return reason;
    }

    /**
     * @deprecated
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlElement(name = "expansion", required = false)
    public ExpansionType getExpansion() {
        return expansion;
    }

    public void setExpansion(ExpansionType expansion) {
        this.expansion = expansion;
    }

}
