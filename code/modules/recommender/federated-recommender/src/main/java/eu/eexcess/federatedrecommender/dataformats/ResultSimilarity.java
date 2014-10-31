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
package eu.eexcess.federatedrecommender.dataformats;

import java.io.Serializable;

import eu.eexcess.dataformats.result.Result;

public class ResultSimilarity extends Result implements Serializable {

	private static final long serialVersionUID = 8720498791053242424L;
	private double similarity;

	/**
	 * is NOT producing a defensive copy!
	 * 
	 * @param result
	 */

	public ResultSimilarity(Result result) {
		this.collectionName = result.collectionName;
		this.creator = result.creator;
		this.description = result.description;
		this.eexcessURI = result.eexcessURI;
		this.facets = result.facets;
		this.id = result.id;
		this.previewImage = result.previewImage;
		this.rdf = result.rdf;
		this.title = result.title;
		this.uri = result.uri;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public double calcSimilarity(Result result) {
		Double returnValue = 0.0;
		Double valueCount = 0.0;
		if (this.collectionName != null && result.collectionName != null){
			returnValue += this.collectionName.compareTo(result.collectionName) == 0 ? 1.0 : 0.0;
			valueCount+=1.0;
		}	
		if (this.creator != null && result.creator != null){
			returnValue += this.creator.compareTo(result.creator) == 0 ? 1.0 : 0.0;
			valueCount+=1.0;
		}
		if (this.description != null && result.description != null){
			returnValue += this.description.compareTo(result.description) == 0 ? 1.0 : 0.0;
			valueCount+=1.0;
		}
		if (this.facets != null && result.facets != null){
			returnValue += calcFacetSimilarity(result);
			valueCount+=1.0;
		}
		if (this.id != null && result.id != null){ 
			returnValue += this.id.compareTo(result.id) == 0 ? 3.0 : 0.0; //higher weight for id -> should mean same result was returned twice
			valueCount+=3.0;
		}
		
		if(valueCount==0.0)
			return 0.0;
		return returnValue/valueCount;
	}

	private Double calcFacetSimilarity(Result result) {
		Double returnValue = 0.0;
		Double valueCount = 0.0;
		if (result.facets != null && this.facets != null) {
// 			Leaving out the language as it perhabs isn't the best similarity factor (in context of diversity)			
//			if (result.facets.language != null && this.facets.language != null) {
//				returnValue += result.facets.language.compareTo(this.facets.language) == 0 ? 1.0 : 0.0;
//				valueCount += 1.0;
//			}
			if (result.facets.provider != null && this.facets.provider != null) {
				returnValue += result.facets.provider.compareTo(this.facets.provider) == 0 ? 1.0 : 0.0;
				valueCount += 1.0;
			}
			if (result.facets.type != null && this.facets.type != null) {
				returnValue += result.facets.type.compareTo(this.facets.type) == 0 ? 1.0 : 0.0;
				valueCount += 1.0;
			}
			if (result.facets.year != null && this.facets.year != null) {
				returnValue += result.facets.year.compareTo(this.facets.year) == 0 ? 1.0 : 0.0;
				valueCount += 1.0;
			}
		}
		if(valueCount==0.0)
			return 0.0;
		return returnValue/valueCount;
	}

}
