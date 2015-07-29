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

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikipediaSchoolsDocumentSubjectSaxHandler extends DefaultHandler {
    private boolean isMatchingElement = false;
    private StringBuilder characters = new StringBuilder();
    private static final String TOKEN_TO_REMOVE = "related subjects:";
    private static final String SITE_SUBJECTS_TAG_ID = "siteSub";
    private static final String SUBJECTS_SEPARATOR = ";";
    private Set<String> siteSubjects = new HashSet<String>();

    @Override
    public void startElement(String uri, String localName, String name, Attributes a) {
        if (name != null && "div".equalsIgnoreCase(name) && a != null && SITE_SUBJECTS_TAG_ID.compareTo(a.getValue("id")) == 0) {
            isMatchingElement = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isMatchingElement) {
            super.characters(ch, start, length);
            characters.append(ch, start, start + length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (isMatchingElement && qName != null && qName.compareTo("div") == 0) {
            isMatchingElement = false;

            for (String subject : characters.substring(characters.toString().toLowerCase().lastIndexOf(TOKEN_TO_REMOVE) + TOKEN_TO_REMOVE.length(),
                    characters.length()).split(SUBJECTS_SEPARATOR)) {
                siteSubjects.add(subject.toLowerCase().trim());
            }
            characters = new StringBuilder();
        }
    }

    /**
     * @return the last parsed site subjects
     */
    public Set<String> getSubjects() {
        return siteSubjects;
    }

    /**
     * clears the current siteSubjects set
     */
    public void clearSubjects() {
        siteSubjects = new HashSet<String>();
    }
}
