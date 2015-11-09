/**
 * Copyright (C) 2014
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
 */

package eu.eexcess.opensearch.opensearchdescriptiondocument;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.eexcess.opensearch.opensearchdescriptiondocument.documentfields.Image;
import eu.eexcess.opensearch.opensearchdescriptiondocument.documentfields.Query;
import eu.eexcess.opensearch.opensearchdescriptiondocument.documentfields.Url;

/**
 * Holds fields and attributes of an OpenSearch Description Document.
 * 
 * @see <a
 *      href="http://www.opensearch.org/Specifications/OpenSearch/1.1#Examples">OpenDocument
 *      Description Document example</a>
 * @author Raoul Rubien
 */
public class OpensearchDescription {

    public static enum SyndicationRight {
        OPEN, LIMITED, PRIVATE, CLOSED, UNDEFINED
    }

    public String shortName = "";
    public String description = "";
    public String longName = "";
    public List<String> tags = new ArrayList<String>();
    public List<Url> searchLinks = new LinkedList<Url>();
    public List<Query> queries = new ArrayList<Query>();
    public String contact = "";
    public SyndicationRight syndicationRight = SyndicationRight.OPEN;
    public String attribution = "";
    public boolean adultContent = false;
    public List<String> languages = new ArrayList<String>();
    public List<String> outputEncodings = new ArrayList<String>();
    public List<String> inputEncodings = new ArrayList<String>();
    public List<Image> images = new ArrayList<Image>();
    public String developer = "";
    public String xmlns = "";

    @Override
    public String toString() {
        return new StringBuilder().append(
                "shortname[" + shortName + "] description[" + description + "] longName[" + longName + "] tags[" + tags + "] searchLinks[" + searchLinks
                        + "] queries [" + queries + "] contact[" + contact + "] syndication [" + syndicationRight + "] attribution [" + attribution
                        + "] adultContent[" + adultContent + "] languages[" + languages + "] outputEncodings[" + outputEncodings + "] inputEndocings ["
                        + inputEncodings + "] images[" + images + "] developer[" + developer + "] xmlns[" + xmlns + "]").toString();
    }
}
