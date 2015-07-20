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
package eu.eexcess.federatedrecommender.domaindetection.wordnet;

import java.util.Map;

import net.sf.extjwnl.data.Synset;
import at.knowcenter.ie.Annotation;
import eu.eexcess.federatedrecommender.domaindetection.probing.Domain;

/**
 * 
 * 
 * @author rkern@know-center.at
 */
public class WordnetDomain extends Domain {
	private String domainName;
	private Map<Annotation, Synset> termToSynset;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param domainName
	 * @param termToSynset
	 */
	WordnetDomain(String domainName, Map<Annotation, Synset> termToSynset) {
		this.domainName = domainName;
		this.termToSynset = termToSynset;
	}

	/**
	 * Returns the termToSynset.
	 * 
	 * @return the termToSynset
	 */
	Map<Annotation, Synset> getTermToSynset() {
		return termToSynset;
	}

	/**
	 * Returns the domainName.
	 * 
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	@Override
	public String toString() {
		return domainName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainName == null) ? 0 : domainName.hashCode());
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
		WordnetDomain other = (WordnetDomain) obj;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return domainName;
	}

}
