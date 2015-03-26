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
package eu.eexcess.federatedrecommender.evaluation.schloett.dataformats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchloettQuery implements Serializable {
	private static final long serialVersionUID = 4433607137755412112L;
	public SchloettQuery(){
	}
	
	@JsonProperty("timestamp")
	public String timeStamp;
	public List<SchloettContext> context = new ArrayList<SchloettContext>();
	@JsonProperty("task_name")
	public String taskName;
	public Integer id;
	public String query;
	@JsonProperty("task_id")
	public String taskId;
	@Override
	public String toString() {
		return "SchloettQuery [timeStamp=" + timeStamp + ",  query=" + query+ "]";
	}
	
}
