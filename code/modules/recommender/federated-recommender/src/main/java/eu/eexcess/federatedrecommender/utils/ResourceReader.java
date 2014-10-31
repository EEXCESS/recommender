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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reader for Files in the Resources of the Package
 * @author hziak
 */
public class ResourceReader {
	private static final Logger logger = Logger.getLogger(ResourceReader.class
			.getName());
		static final public Object readObjectFromResource(String file) throws FileNotFoundException {
			InputStream fin = null;
			
			fin = ResourceReader.class.getClassLoader().getResourceAsStream(file);
			if(fin == null)
				throw new FileNotFoundException(" Resource "+file+" not found");
			ObjectInputStream ois = null;
			try {
				if (fin != null)
					ois = new ObjectInputStream(fin);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "ObjectInputStream could not be read" ,e);
			}
			Object result = null;
			try {
				if (ois != null)
					result = ois.readObject();
			} catch (ClassNotFoundException e) {
				logger.log(Level.SEVERE, "Class of ObjectInputStream could not be read" ,e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "ObjectInputStream could not be read" ,e);
			}
			finally{
				try {
					fin.close();
					if(ois!=null)
						ois.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			return result;
		}
}
