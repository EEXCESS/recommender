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
package eu.eexcess.federatedrecommender.interfaces;

import java.util.List;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;

public interface PartnersFederatedRecommendationsPicker {
	/**
	 * Picks results out of the chronic from multiple queries
	 * @param pFRChronicle
	 * @param numResults
	 * @return
	 */
	public abstract ResultList pickResults(PFRChronicle pFRChronicle,
			int numResults);

	/**
	 * Pick results out of a single result list and aggregates the results into one list
	 * 
	 * @param secureUserProfile
	 * 
	 * @param resultList
	 * @param partners
	 * @param numResults
	 */
	public abstract ResultList pickResults(SecureUserProfile secureUserProfile,
			PartnersFederatedRecommendations resultList,
			List<PartnerBadge> partners, int numResults);

}
