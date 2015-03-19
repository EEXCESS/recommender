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
package eu.eexcess.federatedrecommender.evaluation.evaluation;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.dataformats.userprofile.Interest;

public class EvaluationQuery {
	  	
	    public List<Interest> interests = new ArrayList<Interest>();
		public EvaluationQuery(){}
		public EvaluationQuery(String query, String description, List<Interest> interests) {
			this.query = query;
			this.description = description;
			this.interests = interests;
	}
		public String query;
		public String description;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((query == null) ? 0 : query.hashCode());
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
			EvaluationQuery other = (EvaluationQuery) obj;
			if (query == null) {
				if (other.query != null)
					return false;
			} else if (!query.equals(other.query))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "\nEvaluationQuery [interests=" + interests + ",\n query="
					+ query + ",\n description=" + description + "\n]";
		}
		
		
}
