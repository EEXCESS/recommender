/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.sourceselection.redde.indexer.topterm;

/**
 * Represents a word relation inbetween two words: source and target
 * @author Raoul Rubien
 *
 */
public class WordRelation implements Comparable<WordRelation> {

	/**
	 * binary relation parameters
	 */
	public static double MaxNounDistance = Double.MIN_VALUE;
	public static double MinNounDistance = Double.MAX_VALUE;
	public static double MaxVerbDistance = Double.MIN_VALUE;
	public static double MinVerbDistance = Double.MAX_VALUE;
	public static double MaxAdjetiveDistance = Double.MIN_VALUE;
	public static double MinAdjectiveDistance = Double.MAX_VALUE;
	public static double MaxDistance = Double.MIN_VALUE;
	public static double MinDistance = Double.MAX_VALUE;

	public double relativeNounRelation = Double.NaN;
	public double relativeVerbRelation = Double.NaN;
	public double relativeAdjectiveRelation = Double.NaN;

	public double weightedRelation = Double.NaN;
	public double normalizedRelation = Double.NaN;

	public String source;
	public String target;

	/**
	 * 1:n relation parameters
	 */
	public static double totalRelations = 0;
	public double averageRelation = 0;

	public static double MaxNumSynSets = Double.MIN_VALUE;
	public static double MinNumSynSet = Double.MAX_VALUE;

	public double numSynSets = Double.NaN;
	public double normalizedNumSynsets = Double.NaN;

	public static String fieldsToString() {
		return "min/max-weighted-distance [" + MinDistance + "/" + MaxDistance + "] min/max-noun distance ["
						+ MinNounDistance + "/" + MaxNounDistance + "] min-max-verb distance [" + MinVerbDistance + "/"
						+ MaxVerbDistance + "] min/max-adjective distance [" + MinAdjectiveDistance + "/"
						+ MaxAdjetiveDistance + "]";
	}

	@Override
	public int compareTo(WordRelation o) {
		if (this == o || this.averageRelation == o.averageRelation || this.averageRelation < o.averageRelation) {
			return -1;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "relation [" + source + "] -> [" + target + "] normalized[" + normalizedRelation + "] total-normalized["
						+ averageRelation + "] weighted[" + weightedRelation + "] noun/verb/adjective-rel.["
						+ relativeNounRelation + "/" + relativeVerbRelation + "/" + relativeAdjectiveRelation
						+ "] normalized/max-synset[" + normalizedNumSynsets + "/" + MaxNumSynSets + "]";
	}
}
