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
package eu.eexcess.federatedrecommender.picker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;

/**
 * First in First out picker implementation
 * 
 * @author hziak
 *
 */
public class FiFoPicker extends PartnersFederatedRecommendationsPicker {

	public FiFoPicker() {
		super();
	}

	private static final Logger logger = Logger.getLogger(FiFoPicker.class
			.getName());

	@Override
	public ResultList pickResults(PFRChronicle pFRChronicle, int numResults) {
		logger.log(Level.SEVERE, "Not Implemented!");
		return null;
	}

	@Override
	public ResultList pickResults(SecureUserProfile secureUserProfile,
			PartnersFederatedRecommendations resultList,
			List<PartnerBadge> partners, int numResults) {
		ResultList result = new ResultList();
		List<PartnerBadge> partnerTmp = new ArrayList<PartnerBadge>();
		while (result.results.size() < numResults){
			if (partnerTmp.size() == partners.size())
					break;
			for (PartnerBadge partnerBadge : partners) {
			if (resultList.getResults().get(partnerBadge) != null)
				if (resultList.getResults().get(partnerBadge).results.size() == 0) {
					partnerTmp.add(partnerBadge);
					break;
				} else if (resultList.getResults().get(partnerBadge).results
						.size() > 0 && result.results.size() < numResults) {
					Result resultToAdd = resultList.getResults().get(
							partnerBadge).results.get(0);
					if (resultToAdd == null)
						break;

					byte[] signNewResult = getFuzzyHashSignature(resultToAdd);
					boolean found = false;
					for (Result selectedResult : result.results) {
						if (Arrays.equals(signNewResult,
								getFuzzyHashSignature(selectedResult))) {
							found = true;
							break;
						}
					}
					if (!found) {
						result.results.add(resultToAdd);
					}
					resultList.getResults().get(partnerBadge).results.remove(0);
				}
		}
		}
		return result;
	}
	

}
