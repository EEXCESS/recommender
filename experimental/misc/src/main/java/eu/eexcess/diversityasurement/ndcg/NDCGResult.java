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
package eu.eexcess.diversityasurement.ndcg;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.dataformats.result.Result;

/**
 * 
 * @author hziak
 *
 */
public class NDCGResult extends Result{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double nDCGRelevance;
	public List<NDCGIACategory> categories = new ArrayList<NDCGIACategory>();
	@Override
	public String toString() {
		return "NDCGResult ["+super.title+"]";
	}
	
}
