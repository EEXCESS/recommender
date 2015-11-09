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
 * Information which language the user knows and their competence level
 * 
 * @author hziak
 *
 */
public class Language implements Serializable {
    private static final long serialVersionUID = 388649425945017482L;
    private String iso2;
    private Double languageCompetenceLevel;

    public Language(String iso2, Double competenceLevel) {
        this.setIso2(iso2);
        this.setLanguageCompetenceLevel(competenceLevel);
    }

    public Language() {
    }

    @Override
    public String toString() {
        return "Language [iso2=" + getIso2() + ", competenceLevel=" + getLanguageCompetenceLevel() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLanguageCompetenceLevel() == null) ? 0 : getLanguageCompetenceLevel().hashCode());
        result = prime * result + ((getIso2() == null) ? 0 : getIso2().hashCode());
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
        Language other = (Language) obj;
        if (getLanguageCompetenceLevel() == null) {
            if (other.getLanguageCompetenceLevel() != null)
                return false;
        } else if (!getLanguageCompetenceLevel().equals(other.getLanguageCompetenceLevel()))
            return false;
        if (getIso2() == null) {
            if (other.getIso2() != null)
                return false;
        } else if (!getIso2().equals(other.getIso2()))
            return false;
        return true;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public Double getLanguageCompetenceLevel() {
        return languageCompetenceLevel;
    }

    public void setLanguageCompetenceLevel(Double languageCompetenceLevel) {
        this.languageCompetenceLevel = languageCompetenceLevel;
    }
}
