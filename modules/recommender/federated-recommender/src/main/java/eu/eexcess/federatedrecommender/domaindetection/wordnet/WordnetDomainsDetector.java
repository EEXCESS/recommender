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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;

import org.apache.commons.collections.CollectionUtils;

import at.knowcenter.ie.AnnotatedDocument;
import at.knowcenter.ie.Annotation;
import at.knowcenter.ie.Annotator;
import at.knowcenter.ie.BaseTypeSystem;
import at.knowcenter.ie.Language;
import at.knowcenter.ie.impl.DefaultDocument;
import at.knowcenter.ie.opennlp.PartOfSpeechAnnotator;
import at.knowcenter.ie.opennlp.TokenAnnotator;
import at.knowcenter.ie.postags.UnifiedTag;
import at.knowcenter.util.term.TermSet;
import at.knowcenter.util.term.TypedTerm;
import eu.eexcess.federatedrecommender.domaindetection.Domain;
import eu.eexcess.federatedrecommender.domaindetection.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.DomainDetectorException;

/**
 * Domain detector based on the wordnet domains dataset.
 * 
 * @author rkern@know-center.at
 */
public class WordnetDomainsDetector extends DomainDetector {
	/** String DOMAIN_FACTOTUM */
	private static final String DOMAIN_FACTOTUM = "factotum";
	private final Dictionary dictionary;
	private final List<Annotator> annotators;
	private final MorphologicalProcessor morphologicalProcessor;
	private final Map<String, Set<DomainAssignment>> synsetToDomains;
	private final Map<String, Map<String, Double>> domainToParentDomainToWeight;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param wordnetDir
	 * @param wordNetFile
	 * @param useOriginal
	 *            true, if wordNetFile points to an original WordNet Domains
	 *            file or false, if it points to the Extended WordNet Domains
	 *            directory
	 * @throws DomainDetectorException
	 *             if Wordnet cannot be loaded
	 */
	public WordnetDomainsDetector(File wordnetDir, File wordNetFile, boolean useOriginal)
					throws DomainDetectorException {
		try {
			this.dictionary = Dictionary.getFileBackedInstance(wordnetDir.getPath());
			this.morphologicalProcessor = dictionary.getMorphologicalProcessor();
			System.out.println("Read in Wordnet from: " + wordnetDir);
		} catch (JWNLException e) {
			throw new DomainDetectorException("Cannot read in the Wordnet resource from: " + wordnetDir, e);
		}

		try {
			annotators = new ArrayList<Annotator>();
			annotators.add(new TokenAnnotator(Language.English));
			annotators.add(new PartOfSpeechAnnotator(Language.English));
			System.out.println("Initialised the annotators");
		} catch (IOException e) {
			throw new DomainDetectorException("Cannot initialise the annotators", e);
		}

		try {
			// File modelFile = new File(xwndDir, "synsetToDomains.ser");
			// if (modelFile.exists()) {
			// byte[] bytes = FileUtils.readFileToByteArray(modelFile);
			// synsetToDomains = CompressionUtils.fromCompressedBytes(bytes);
			// } else {
			synsetToDomains = new HashMap<String, Set<DomainAssignment>>();
			domainToParentDomainToWeight = new LinkedHashMap<String, Map<String, Double>>();

			if (useOriginal) {
				WordnetDomainsReader wordnetDomainsReader = new WordnetDomainsReader(synsetToDomains,
								domainToParentDomainToWeight);
				wordnetDomainsReader.readDefinition(wordNetFile);
				wordnetDomainsReader.read(wordNetFile);
			} else {
				for (File file : wordNetFile.listFiles()) {
					if (file.getPath().toLowerCase(Locale.US).endsWith(".ppv")) {
						new XwndReader(synsetToDomains).read(file);
					}
				}
			}
			// byte[] bytes =
			// CompressionUtils.toCompressedBytes(synsetToDomains);
			// FileUtils.writeByteArrayToFile(modelFile, bytes);
			// }
			System.out.println("Finished reading in the WordNet Domains from: " + wordNetFile);
		} catch (IOException e) {
			throw new DomainDetectorException("Cannot read in the XWND resource from: " + wordNetFile, e);
		}
	}

	@Override
	public Collection<Domain> getAllDomains() {
		Set<Domain> result = new HashSet<Domain>();
		for (Entry<String, Map<String, Double>> e : domainToParentDomainToWeight.entrySet()) {
			result.add(new WordnetDomain(e.getKey(), null));
			Map<String, Double> parentToWeight = e.getValue();
			if (parentToWeight != null) {
				for (String parentDomain : parentToWeight.keySet()) {
					result.add(new WordnetDomain(parentDomain, null));
				}
			}
		}
		return result;
	}

	@Override
	public String drawRandomAmbiguousWord(Set<String> wordsToIgnore) throws DomainDetectorException {
		try {
			String lemma = null;
			do {
				IndexWord randomIndexWord;
				try {
					randomIndexWord = dictionary.getRandomIndexWord(POS.NOUN);
				} catch (NullPointerException e) {
					continue;
				}

				if (wordsToIgnore.contains(randomIndexWord.getLemma())) {
					continue;
				}

				Set<String> allDomains = new HashSet<String>();
				List<Synset> senses = randomIndexWord.getSenses();
				for (Synset synset : senses) {
					String synsetId = String.format("%08d-%s", (long) synset.getKey(), synset.getPOS().getKey());
					Set<DomainAssignment> domains = synsetToDomains.get(synsetId);
					if (domains != null && !domains.isEmpty()) {
						for (DomainAssignment domain : domains) {
							allDomains.add(domain.domain);
						}
					}
				}
				if (allDomains.size() == 1 && senses.size() > 2) {
					String domain = allDomains.iterator().next();
					if (DOMAIN_FACTOTUM.equals(domain)) {
						lemma = randomIndexWord.getLemma();
					}
				}
			} while (lemma == null);

			return lemma;
		} catch (Exception e) {
			throw new DomainDetectorException("Cannot draw random ambiuous word from WordNet", e);
		}
	}

	@Override
	public Set<Domain> detect(String text) throws DomainDetectorException {
		AnnotatedDocument doc = new DefaultDocument();
		doc.setText(text);
		doc.newAnnotation(0, text.length(), BaseTypeSystem.Sentence);
		for (Annotator annotator : annotators) {
			annotator.annotate(doc);
		}

		Set<Domain> result = new LinkedHashSet<Domain>();
		try {
			// IndexWord nounphrase = dictionary.lookupIndexWord(POS.NOUN,
			// text);
			// List<Synset> npsenses = nounphrase.getSenses();
			// System.out.println(npsenses);

			Map<Annotation, Map<Synset, Set<DomainAssignment>>> assignments = new LinkedHashMap<Annotation, Map<Synset, Set<DomainAssignment>>>();
			for (Annotation annotation : doc.getAnnotations(BaseTypeSystem.Token)) {
				String token = annotation.getText();
				UnifiedTag unifiedTag = annotation.getFeature(BaseTypeSystem.TokenFeatures.PosTagUnified);
				POS pos = unifiedToWordnetPos(unifiedTag);
				if (pos != null) {
					IndexWord indexWord = morphologicalProcessor.lookupBaseForm(pos, token);
					// System.out.println(token+" -> "+indexWord);
					if (indexWord != null) {
						Map<Synset, Set<DomainAssignment>> synsetToAssignments = new HashMap<Synset, Set<DomainAssignment>>();
						List<Synset> senses = indexWord.getSenses();
						for (Synset synset : senses) {
							collectDomains(synset, synsetToAssignments);
						}
						assignments.put(annotation, synsetToAssignments);
					}
				}
			}

			disambiguate(assignments, result);
		} catch (JWNLException e) {
			throw new DomainDetectorException("Cannot detect the domain", e);
		}

		return result;
	}

	/**
	 * @param assignments
	 * @param result
	 */
	private void disambiguate(Map<Annotation, Map<Synset, Set<DomainAssignment>>> assignments,
					Set<Domain> resultCollector) {
		TermSet<TypedTerm> collector = new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger());

		// iterate over all terms in the input string
		Map<Annotation, Map<String, Synset>> termToDomainToSynset = new HashMap<Annotation, Map<String, Synset>>();
		Map<Annotation, Collection<DomainAssignment>> termToDomains = new HashMap<Annotation, Collection<DomainAssignment>>();
		for (Entry<Annotation, Map<Synset, Set<DomainAssignment>>> e : assignments.entrySet()) {
			Map<String, DomainAssignment> domains = new HashMap<String, DomainAssignment>();
			Map<String, Synset> domainToSynset = new HashMap<String, Synset>();
			double synsetDiscount = 0.5 + 0.5 / Math.sqrt(e.getValue().size());

			// iterate over all synsets (synonyms)
			for (Entry<Synset, Set<DomainAssignment>> entry : e.getValue().entrySet()) {
				Set<DomainAssignment> domainAssignments = entry.getValue();

				// iterate over all domains for the current synset
				for (DomainAssignment assignment : domainAssignments) {
					double assignmentWeight = assignment.weight;

					DomainAssignment existingAssignment = domains.get(assignment.domain);
					if (existingAssignment != null) {
						if (assignmentWeight > existingAssignment.weight) {
							DomainAssignment da = new DomainAssignment(assignment.domain, synsetDiscount
											* assignmentWeight);
							domains.put(assignment.domain, da);
							domainToSynset.put(assignment.domain, entry.getKey());
						}
					} else {
						DomainAssignment da = new DomainAssignment(assignment.domain, synsetDiscount * assignmentWeight);
						domains.put(assignment.domain, da);
						domainToSynset.put(assignment.domain, entry.getKey());
					}
				}
			}

			// Set<String> commonDomains = getCommonDomains(domains);
			// System.out.println(e.getKey()+" -> "+commonDomains);

			// expand the found domains with their parents
			Set<DomainAssignment> currentDomains = new LinkedHashSet<DomainAssignment>(domains.values());
			for (DomainAssignment assignment : currentDomains) {
				Synset synset = domainToSynset.get(assignment.domain);

				Map<String, Double> parentToWeight = domainToParentDomainToWeight.get(assignment.domain);
				if (parentToWeight != null) {
					for (Entry<String, Double> entry : parentToWeight.entrySet()) {
						double weight = assignment.weight * entry.getValue();

						DomainAssignment existing = domains.get(entry.getKey());
						if (existing == null || existing.weight < weight) {
							domains.put(entry.getKey(), new DomainAssignment(entry.getKey(), weight));
							domainToSynset.put(entry.getKey(), synset);
						}
					}
				}
			}

			// update the current domains
			currentDomains = new LinkedHashSet<DomainAssignment>(domains.values());
			termToDomains.put(e.getKey(), currentDomains);
			termToDomainToSynset.put(e.getKey(), domainToSynset);

			for (DomainAssignment domain : currentDomains) {
				collector.add(new TypedTerm(domain.domain, null, (float) domain.weight));
			}
		}

		// System.out.println(collector);
		List<TypedTerm> topTerms = collector.getTopTerms(2);
		float topWeight = -1;
		for (TypedTerm term : topTerms) {
			String domain = term.getText();
			if (DOMAIN_FACTOTUM.equals(domain)) {
				continue;
			}
			if (topWeight < 0) {
				topWeight = term.getWeight();
			} else if (term.getWeight() < (topWeight * .5f)) {
				break;
			}

			Map<Annotation, Synset> termToSynset = new HashMap<Annotation, Synset>();
			for (Entry<Annotation, Map<Synset, Set<DomainAssignment>>> e : assignments.entrySet()) {
				Map<String, Synset> domainToSynset = termToDomainToSynset.get(e.getKey());
				Synset synset = domainToSynset.get(domain);
				if (synset == null) {
					Set<Synset> synsets = e.getValue().keySet();
					if (!synsets.isEmpty()) {
						synset = synsets.iterator().next();
					}
				}
				termToSynset.put(e.getKey(), synset);
			}

			resultCollector.add(new WordnetDomain(domain, termToSynset));
		}
	}

	/**
	 * Returns the set of common domains give the domain tree
	 * 
	 * @param domains
	 * @return
	 */
	@SuppressWarnings("unused")
	private Set<String> getCommonDomains(Map<String, DomainAssignment> domains) {

		Set<String> commonDomains = null;
		for (String domain : domains.keySet()) {
			if (commonDomains != null && commonDomains.contains(domain)) {
				// ok, within the selected branch
			} else if (commonDomains == null) {
				// make this the new selected branch
				Map<String, Double> parentsToWeight = domainToParentDomainToWeight.get(domain);
				if (parentsToWeight != null) {
					commonDomains = new HashSet<String>(parentsToWeight.keySet());
				} else {
					commonDomains = new HashSet<String>();
				}
				commonDomains.add(domain);
			} else {
				// if there are any common domains
				Map<String, Double> parentsToWeight = domainToParentDomainToWeight.get(domain);
				Set<String> currentDomains;
				if (parentsToWeight != null) {
					currentDomains = new HashSet<String>(parentsToWeight.keySet());
				} else {
					currentDomains = new HashSet<String>();
				}
				currentDomains.add(domain);

				if (CollectionUtils.containsAny(commonDomains, currentDomains)) {
					for (Iterator<String> iterator = commonDomains.iterator(); iterator.hasNext();) {
						String commonDomain = iterator.next();
						if (!currentDomains.contains(commonDomain)) {
							iterator.remove();
						}
					}
				}

				// no commons domains, so we have to give up
				if (commonDomains.isEmpty()) {
					break;
				}
			}
		}
		return commonDomains;
	}

	/**
	 * @param synsetToAssignments
	 * @param synset
	 */
	protected void collectDomains(Synset synset, Map<Synset, Set<DomainAssignment>> synsetToAssignments) {
		String synsetId = String.format("%08d-%s", (long)synset.getKey(), synset.getPOS().getKey());
		Set<DomainAssignment> domains = synsetToDomains.get(synsetId);
		if (domains != null && !domains.isEmpty()) {
			// System.out.println("  "+synset.getKey()+", "+synset+", "+domains);
			Map<String, Double> assignments = new HashMap<String, Double>();
			for (DomainAssignment a : domains) {
				assignments.put(a.domain, a.weight);
			}

			/*
			 * for (DomainAssignment assignment : domains) { Map<String, Double>
			 * parentToWeight =
			 * domainToParentDomainToWeight.get(assignment.domain); if
			 * (parentToWeight != null) { for (Entry<String, Double> e :
			 * parentToWeight.entrySet()) { double weight = assignment.weight *
			 * e.getValue();
			 * 
			 * Double existing = assignments.get(e.getKey()); if (existing ==
			 * null || existing < weight) { assignments.put(e.getKey(), weight);
			 * } } } }
			 */
			Set<DomainAssignment> synsetDomains = new HashSet<DomainAssignment>();
			for (Entry<String, Double> e : assignments.entrySet()) {
				synsetDomains.add(new DomainAssignment(e.getKey(), e.getValue()));
			}
			synsetToAssignments.put(synset, synsetDomains);
		}
	}

	/**
	 * @param unifiedTag
	 * @return null, if there is no mapping available
	 */
	protected POS unifiedToWordnetPos(UnifiedTag unifiedTag) {
		POS pos;
		switch (unifiedTag) {
		case ADJECTIVE:
			pos = POS.ADJECTIVE;
			break;
		case NOUN:
			pos = POS.NOUN;
			break;
		case VERB:
			pos = POS.VERB;
			break;
		case ADVERB:
			pos = POS.ADVERB;
			break;
		default:
			pos = null;
		}
		return pos;
	}

	public static void main(String[] args) throws DomainDetectorException {
		// WordnetDomainsDetector wordnetDomainsDetector = new
		// WordnetDomainsDetector(
		// new File("/opt/data/wordnet/WordNet-3.0/dict"),
		// new File("/opt/data/wordnet-domains/xwnd/xwnd-30g"), false);
		WordnetDomainsDetector wordnetDomainsDetector = new WordnetDomainsDetector(new File(
						"/opt/data/wordnet/WordNet-2.0/dict"), new File(
						"/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223"), true);
		for (String query : new String[] { "women in the workforce", "battle of trafalgar", "scientific method",
						"women higher education", "gender roles", "gender inequality", "women wage gap",
						"first world war", "industrial revolution loom", "metallica enter sandman", "ufo movie",
						"world cup football", "java 8 features", "dinosaur t-rex", "higgs boson particle field",
						"railway junction", "sentimental tears emotions", "black matter", "bread and butter",
						"mandela prison", "aids hiv", "Russian Civil War", "satellite republic", "French Revolution",

						"computer graphics", "linux", "windows", "vacation", "apple notebook", "lipstick color",
						"messenger bag", "horse trailer for sale", "kittens for sale", "climate change",
						"department of justice",

		}) {
			Set<Domain> detect = wordnetDomainsDetector.detect(query);
			System.out.println(query + " -> " + detect);
		}
	}
}
