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
package eu.eexcess.domaindetection.wordnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.LineIterator;

class WordnetDomainsReader {
    private Map<String, Set<DomainAssignment>> synsetToDomains;
    private Map<String, Map<String, Double>> domainToParentDomainToWeight;
    
    /**
     * Creates a new instance of this class.
     */
    public WordnetDomainsReader(Map<String, Set<DomainAssignment>> synsetToDomains, Map<String, Map<String, Double>> domainToParentDomainToWeight) {
        this.synsetToDomains = synsetToDomains;
        this.domainToParentDomainToWeight = domainToParentDomainToWeight;
    }
    
    /**
     * @param wordNetFile
     * @throws FileNotFoundException 
     */
    public void readDefinition(File wordNetFile) throws FileNotFoundException {
        File file = new File(wordNetFile.getParentFile(), "wn-domains-3.2-tree.csv");
        LineIterator iterator = new LineIterator(new FileReader(file));
        String[] currentParents = new String[4];
        while (iterator.hasNext()) {
            String line = iterator.nextLine();
            String[] tokens = line.split("[,]");
            int depth = -1;
            for (int i = 0; i < tokens.length; i++) {
                if (!tokens[i].trim().isEmpty()) {
                    depth = i;
                    break;
                }
            }
            String domain = tokens[depth].trim().toLowerCase(Locale.US);
            if (depth >= 0) {
                Map<String, Double> parentToWeight = domainToParentDomainToWeight.get(domain);
                if (parentToWeight == null) {
                    parentToWeight = new LinkedHashMap<String, Double>();
                    domainToParentDomainToWeight.put(domain, parentToWeight);
                }
                for (int i = 0; i < depth; i++) {
                    double weight = 1.0 / ((depth-i+1)*(depth-i+1));
                    String parent = currentParents[i];
                    parentToWeight.put(parent, weight);
                }
                currentParents[depth] = domain;
                for (int i = depth+1; i < 4; i++) {
                    currentParents[i] = null;
                }
            } else {
                domainToParentDomainToWeight.put(domain, null);
            }
        }
        iterator.close();
    }

    public void read(File file) throws IOException {
        System.out.println("Read in the original WordNet Domains file: "+file);
        LineIterator iterator = new LineIterator(new FileReader(file));
        while (iterator.hasNext()) {
            String line = iterator.nextLine();
            String[] tokens = line.split("[\t\\ ]");
            String synset = tokens[0];
            for (int i = 1; i < tokens.length; i++) {
                DomainAssignment assignment = new DomainAssignment(tokens[i], 1);
                Set<DomainAssignment> domains = synsetToDomains.get(synset);
                if (domains == null) {
                    domains = new TreeSet<DomainAssignment>();
                    synsetToDomains.put(synset, domains);
                }
                domains.add(assignment);
            }
        }
        iterator.close();
    }
}