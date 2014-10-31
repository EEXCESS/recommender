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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.extjwnl.data.POS;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import at.knowcenter.util.io.BinaryInputStream;
import at.knowcenter.util.io.BinaryOutputStream;

class XwndReader {
    private Map<String, Set<DomainAssignment>> synsetToDomains;

    /**
     * Creates a new instance of this class.
     */
    public XwndReader(Map<String, Set<DomainAssignment>> synsetToDomains) {
        this.synsetToDomains = synsetToDomains;
    }
    
    public void read(File file) throws IOException {
        String domain = FilenameUtils.getBaseName(file.getName());
        
        File cacheFile = new File(file.getPath()+".cache");
        if (!cacheFile.exists()) {
            BinaryOutputStream bos = new BinaryOutputStream(new FileOutputStream(cacheFile));
            System.out.println("Read in the Extended WordNet Domains file: "+file);
            LineIterator iterator = new LineIterator(new FileReader(file));
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                String[] tokens = line.split("\t");
                String synset = tokens[0];
                double weight = Double.parseDouble(tokens[1]);
                String[] ssid = synset.split("-");
                int nr = Integer.parseInt(ssid[0]);
                POS pos = POS.getPOSForKey(ssid[1]);
                bos.writeInt(nr);
                bos.writeSmallInt(pos.getId());
                bos.writeInt(Float.floatToIntBits((float)weight));
            }
            iterator.close();
            bos.close();
        }
        
        System.out.println("Read in the Extended WordNet Domains cache file: "+file);
        FileInputStream fStream=new FileInputStream(cacheFile);
        BinaryInputStream bis = new BinaryInputStream(fStream);
        while (bis.available() > 0) {
            int nr = bis.readInt();
            int key = bis.readSmallInt();
            POS pos = POS.getPOSForId(key);
            String synset = String.format("%08d-%s", nr, pos.getKey());
            double weight = Float.intBitsToFloat(bis.readInt());
            DomainAssignment assignment = new DomainAssignment(domain, weight);
            Set<DomainAssignment> domains = synsetToDomains.get(synset);
            if (domains == null) {
                domains = new TreeSet<DomainAssignment>();
                synsetToDomains.put(synset, domains);
            }
            domains.add(assignment);
        }
        fStream.close();
        bis.close();
    }
    
}