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

/**
 * Interests of the user
 * 
 * @author hziak
 *
 */

public class Interest implements Serializable {
    private static final long serialVersionUID = -2510863722705747232L;

    private String text;
    private Double weight;
    private Double confidence;
    private Double competenceLevel;
    private String source;
    private String uri;

    public Interest(String text, Double weight, Double confidence, Double competenceLevel, String source, String uri) {
        super();
        this.setText(text);
        this.setWeight(weight);
        this.setConfidence(confidence);
        this.setCompetenceLevel(competenceLevel);
        this.setSource(source);
        this.setUri(uri);
    }

    public Interest(String text) {
        this.setText(text);
    }

    public Interest() {
    }

    @Override
    public String toString() {
        return "Interest [text=" + getText() + ", weight=" + getWeight() + ", confidence=" + getConfidence() + ", competenceLevel=" + getCompetenceLevel() + ", source="
                + getSource() + ", uri=" + getUri() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCompetenceLevel() == null) ? 0 : getCompetenceLevel().hashCode());
        result = prime * result + ((getConfidence() == null) ? 0 : getConfidence().hashCode());
        result = prime * result + ((getSource() == null) ? 0 : getSource().hashCode());
        result = prime * result + ((getText() == null) ? 0 : getText().hashCode());
        result = prime * result + ((getUri() == null) ? 0 : getUri().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Interest other = (Interest) obj;
        if (getCompetenceLevel() == null) {
            if (other.getCompetenceLevel() != null)
                return false;
        } else if (!getCompetenceLevel().equals(other.getCompetenceLevel()))
            return false;
        if (getConfidence() == null) {
            if (other.getConfidence() != null)
                return false;
        } else if (!getConfidence().equals(other.getConfidence()))
            return false;
        if (getSource() == null) {
            if (other.getSource() != null)
                return false;
        } else if (!getSource().equals(other.getSource()))
            return false;
        if (getText() == null) {
            if (other.getText() != null)
                return false;
        } else if (!getText().equals(other.getText()))
            return false;
        if (getUri() == null) {
            if (other.getUri() != null)
                return false;
        } else if (!getUri().equals(other.getUri()))
            return false;
        if (getWeight() == null) {
            if (other.getWeight() != null)
                return false;
        } else if (!getWeight().equals(other.getWeight()))
            return false;
        return true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Double getCompetenceLevel() {
        return competenceLevel;
    }

    public void setCompetenceLevel(Double competenceLevel) {
        this.competenceLevel = competenceLevel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}