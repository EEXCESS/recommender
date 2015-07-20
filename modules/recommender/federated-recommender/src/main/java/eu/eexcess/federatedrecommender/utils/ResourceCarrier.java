/* Copyright (C) 2014 
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
package eu.eexcess.federatedrecommender.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reader and writer for Files in the Resources of the Packages or for
 * serialized objects
 * 
 * @author hziak
 */
public class ResourceCarrier {
    private static final Logger logger = Logger.getLogger(ResourceCarrier.class.getName());

    static final public Object readObjectFromResource(String file) throws FileNotFoundException, FederatedRecommenderException {
        InputStream fin = null;

        fin = ResourceCarrier.class.getClassLoader().getResourceAsStream(file);
        if (fin == null)
            throw new FileNotFoundException(" Resource " + file + " not found");
        ObjectInputStream ois = null;
        try {
            if (fin != null)
                ois = new ObjectInputStream(fin);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "InputStream could not be read", e);
            throw new FederatedRecommenderException("InputStream could not be read", e);
        }
        Object result = null;
        try {
            if (ois != null)
                result = ois.readObject();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Object of ObjectInputStream could not be found", e);
            throw new FederatedRecommenderException("Object of ObjectInputStream could not be found", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ObjectInputStream could not be read", e);
            throw new FederatedRecommenderException("ObjectInputStream could not be read", e);
        } finally {
            try {
                fin.close();
                ois.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IOError ", e);
            }

        }
        return result;
    }

    static final public Object readObjectFromFile(String file) throws FederatedRecommenderException {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Object File \"" + file + "\" could not be found", e);
            throw new FederatedRecommenderException("Object File \"" + file + "\" could not be found", e);
        }
        ObjectInputStream ois = null;
        try {
            if (fin != null)
                ois = new ObjectInputStream(fin);
            fin.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Object of ObjectInputStream could not be found", e);
            throw new FederatedRecommenderException("Object of ObjectInputStream could not be found", e);
        }
        Object result = null;
        try {
            if (ois != null)
                result = ois.readObject();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Object of ObjectInputStream could not be found", e);
            throw new FederatedRecommenderException("Object of ObjectInputStream could not be found", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ObjectInputStream could not be read", e);
            throw new FederatedRecommenderException("ObjectInputStream could not be read", e);
        } finally {
            try {
                fin.close();
                ois.close();
            } catch (IOException e) {
                logger.log(Level.INFO, "could not close file", e);
            }
        }
        return result;
    }

    static final public void writeObjectToFile(Object input, String name) throws FederatedRecommenderException, IOException {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(name + ".ser");
        } catch (FileNotFoundException e2) {
            logger.log(Level.SEVERE, "Object File \"" + name + ".ser \" could not be found", e2);
            throw new FederatedRecommenderException("Write Object To file could not find Object File \"" + name + ".ser \"", e2);
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fout);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ObjectOutputStream could not be created", e);
            fout.close();
            throw new FederatedRecommenderException("ObjectOutputStream could not be created", e);
        }
        try {
            if (oos != null)
                oos.writeObject(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ObjectOutputStream could not be written", e);
            oos.close();
            throw new FederatedRecommenderException("ObjectOutputStream could not be written", e);

        }
        if (fout != null)
            try {
                fout.close();
            } catch (IOException e) {
                logger.log(Level.INFO, "Could not close file", e);
            }
    }
}
