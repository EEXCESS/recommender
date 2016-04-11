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
package eu.eexcess.domaindetection.wikipedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import at.knowcenter.util.io.CompressionUtils;
import at.knowcenter.util.term.TermSet;
import at.knowcenter.util.term.TypedTerm;
import eu.eexcess.federatedrecommender.domaindetection.probing.Domain;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetectorException;

/**
 * Domain detector based on the Wikipedia categories.
 * 
 * @author rkern@know-center.at
 */
public class WikipediaDomainDetector extends DomainDetector {
	private TreeMap<String, Map<String, Double>> termsToSubjectsToProb = new TreeMap<>();
	private Set<String> subjects = new TreeSet<>();
	
	public WikipediaDomainDetector(File indexDir) throws DomainDetectorException {
		try {
			prepare(indexDir);
		} catch (IOException e) {
			throw new DomainDetectorException("Cannot open index in dir '" + indexDir + "'", e);
		}
	}
	
	private void prepare(File indexDir) throws IOException {
		Schema<TreeMap> schema = RuntimeSchema.getSchema(TreeMap.class);
		
		File cacheFile = new File("/tmp/eexcess-wikipedia-domains.cache");	
		if (cacheFile.exists()) {
			this.termsToSubjectsToProb = CompressionUtils.fromCompressedBytes(FileUtils.readFileToByteArray(cacheFile));
			Set<String> collector = new HashSet<>();
			termsToSubjectsToProb.values().forEach(m -> { collector.addAll(m.keySet()); });
			this.subjects = collector;
			return;
		}
		
		FSDirectory dir = FSDirectory.open(indexDir);
		IndexReader reader = DirectoryReader.open(dir);
		
		Map<String, int[]> subjectToDocsEnum = new HashMap<>();
		{
			long start = System.currentTimeMillis();
			String fieldName = "document-subject";
			Terms subjectsTerms = MultiFields.getTerms(reader, fieldName);
			TermsEnum termsEnum = subjectsTerms.iterator(null);
			BytesRef bytesRef = termsEnum.next();
			while ((bytesRef = termsEnum.next()) != null) {
				String term = bytesRef.utf8ToString();
				DocsEnum docsEnum = MultiFields.getTermDocsEnum(reader, null, fieldName, bytesRef, 0);
				int doc;
				List<Integer> docs = new ArrayList<>();
				while ((doc = docsEnum.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
					docs.add(doc);
				}
				subjectToDocsEnum.put(term, ArrayUtils.toPrimitive(docs.toArray(new Integer[docs.size()])));
			}
			System.out.println(String.format("Read in all %d subject doc enums in %.3f seconds",
					subjectToDocsEnum.size(), (System.currentTimeMillis()-start)/1000.0));
		}
		this.subjects = new HashSet<>(subjectToDocsEnum.keySet());
		TreeMap<String, Map<String, Double>> termsToSubjectsToProb = new TreeMap<>();
		{
			long start = System.currentTimeMillis();
			String fieldName = "paragraph-text-bigram";
			Terms subjectsTerms = MultiFields.getTerms(reader, fieldName);
			TermsEnum termsEnum = subjectsTerms.iterator(null);
			BytesRef bytesRef = termsEnum.next();
			while ((bytesRef = termsEnum.next()) != null) {
				String term = bytesRef.utf8ToString();
				int df = reader.docFreq(new Term(fieldName, term));
				if (df < 20) { continue; }
				
				DocsEnum docsEnum = MultiFields.getTermDocsEnum(reader, null, fieldName, bytesRef, 0);
				int doc;
				List<Integer> docs = new ArrayList<>();
				while ((doc = docsEnum.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
					docs.add(doc);
				}
				int[] array = ArrayUtils.toPrimitive(docs.toArray(new Integer[docs.size()]));
				Map<String, Double> subjectToProb = new TreeMap<>();
				for (Map.Entry<String, int[]> e : subjectToDocsEnum.entrySet()) {
					int[] array2 = subjectToDocsEnum.get(e.getKey());
					int ci = countIntersection(array, array2, array.length, array2.length);
					double p = (double)ci / Math.min(array.length, Math.max(20, array2.length));
					if (p > 0.4) {
						subjectToProb.put(e.getKey(), p);
					}
				}
//				if (!subjectToProb.isEmpty()) {
//						System.out.println(term +" -> "+ subjectToProb);
//				}
				termsToSubjectsToProb.put(term, subjectToProb);
			}
			this.termsToSubjectsToProb = termsToSubjectsToProb;
			
			System.out.println(String.format("Read in all %d terms in %.3f seconds",
					termsToSubjectsToProb.size(), (System.currentTimeMillis()-start)/1000.0));
		}
		
		FileUtils.writeByteArrayToFile(cacheFile, CompressionUtils.toCompressedBytes(termsToSubjectsToProb));
	}
	
	int countIntersection(int arr1[], int arr2[], int m, int n) {
		int i = 0, j = 0, counter = 0;
		while (i < m && j < n) {
			if (arr1[i] < arr2[j])
				i++;
			else if (arr2[j] < arr1[i])
				j++;
			else /* if arr1[i] == arr2[j] */
			{
				counter++;
				i++;
			}
		}
		return counter;
	}


    @Override
    public Set<Domain> detect(String text) throws DomainDetectorException {
    	try {
			ShingleAnalyzerWrapper analyzer = new ShingleAnalyzerWrapper(new EnglishAnalyzer(), 2);
			QueryParser queryParser = new QueryParser("paragraph-text-bigram", analyzer);
//			QueryParser queryParser = new QueryParser("paragraph-text", analyzer);
			Query query = queryParser.parse(text);
			Set<Term> terms = new LinkedHashSet<>();
			query.extractTerms(terms);
			
			TermSet<TypedTerm> collector = new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger());
			for (Term t : terms) {
				Map<String, Double> map = termsToSubjectsToProb.get(t.text());
				if (map != null) {
					map.forEach((s, p) -> {
						collector.add(new TypedTerm(s, null, (float) p.doubleValue()));
					});
				}
			}
			
			Set<Domain> result = new LinkedHashSet<>();
	        List<TypedTerm> topTerms = collector.getTopTerms(2);
	        float topWeight = -1;
	        for (TypedTerm term : topTerms) {
	            String domain = term.getText();
	            if (topWeight < 0) {
	                topWeight = term.getWeight();
	            } else if (term.getWeight() < (topWeight * .5f)) {
	                break;
	            }
	            result.add(new WikipediaDomain(term.getText()));
	        }
	        return result;
		} catch (ParseException e) {
			throw new DomainDetectorException("Cannot parse text '" + text + "'", e);
		}
    }

    @Override
    public String drawRandomAmbiguousWord(Set<String> wordsToIgnore) throws DomainDetectorException {
        // return null;
        throw new UnsupportedOperationException("Sorry, not implemented.");
    }
    
    @Override
    public Collection<Domain> getAllDomains() {
    	return subjects.stream().map(s -> new WikipediaDomain(s)).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException, DomainDetectorException {
		File indexDir = new File(args[0]);
		WikipediaDomainDetector instance = new WikipediaDomainDetector(indexDir);
		
		System.out.println(instance.getAllDomains());
		
		for (String query : new String[] { "women in the workforce", "battle of trafalgar", "scientific method", "women higher education", "gender roles",
                "gender inequality", "women wage gap", "first world war", "industrial revolution loom", "metallica enter sandman", "ufo movie",
                "world cup football", "java 8 features", "dinosaur t-rex", "higgs boson particle field", "railway junction", "sentimental tears emotions",
                "black matter", "bread and butter", "mandela prison", "aids hiv", "Russian Civil War", "satellite republic", "French Revolution",

                "computer graphics", "linux", "windows", "vacation", "apple notebook", "lipstick color", "messenger bag", "horse trailer for sale",
                "kittens for sale", "climate change", "department of justice",

        }) {
            Set<Domain> detect = instance.detect(query);
            System.out.println(query + " -> " + detect);
        }
	}
}
