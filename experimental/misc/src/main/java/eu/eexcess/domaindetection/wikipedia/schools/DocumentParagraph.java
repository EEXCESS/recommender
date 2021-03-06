/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.domaindetection.wikipedia.schools;

/**
 * This class keeps details of a Wikipedia document paragraph. A paragraph is
 * usually a text fragment of a whole site sliced at headings (h1, h2).
 * 
 * @author Raoul Rubien
 *
 */
public class DocumentParagraph {
    private String title;
    private String level;
    private String paragraphText;
    private boolean isSosParagraph;

    public DocumentParagraph(String title, String level, String paragraph) {
        this.title = title;
        this.level = level;
        this.paragraphText = paragraph;
        this.isSosParagraph = false;
    }

    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }

    public String getParagraph() {
        return paragraphText;
    }

    public boolean isSosParagraph() {
        return isSosParagraph;
    }

    public void isSosParagraph(boolean isSosParagraph) {
        this.isSosParagraph = isSosParagraph;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isSosParagraph ? 1231 : 1237);
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((paragraphText == null) ? 0 : paragraphText.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        DocumentParagraph other = (DocumentParagraph) obj;
        if (isSosParagraph != other.isSosParagraph)
            return false;
        if (level == null) {
            if (other.level != null)
                return false;
        } else if (!level.equals(other.level))
            return false;
        if (paragraphText == null) {
            if (other.paragraphText != null)
                return false;
        } else if (!paragraphText.equals(other.paragraphText))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }
    
}
