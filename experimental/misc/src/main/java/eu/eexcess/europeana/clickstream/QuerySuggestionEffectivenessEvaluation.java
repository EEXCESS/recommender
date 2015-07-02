package eu.eexcess.europeana.clickstream;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.LocaleUtils;

import cern.colt.Arrays;
import at.knowcenter.commons.wikipedia.entity.index.WikipediaPreprocessedIndex;
import at.knowcenter.commons.wikipedia.entity.index.WikipediaPreprocessedIndex.PreprocessedArticleCallback;
import at.knowcenter.formatconverter.parser.AnnotatedPlainText;
import at.knowcenter.ie.AnnotatedDocument;
import at.knowcenter.ie.Annotation;
import at.knowcenter.ie.BaseTypeSystem;
import at.knowcenter.ie.tools.AnnotatedDocumentUtils;
import at.knowcenter.patternmining.dataset.Item;
import at.knowcenter.patternmining.fpgrowth.FPGrowthAlgorithm;
import at.knowcenter.patternmining.util.Itemset;
import at.knowcenter.patternmining.fpgrowth.FPGrowthAlgorithm.Builder;

/**
 * 
 * 



50% Keywords
Found: [locomotive, model]^4.0 for [locomotive, model] at rank 3
Update of MRR: 0.5484549782329142
Update of MRR: 0.0029597168636668076 and ratio: 0.005396462756528932, not found: [lepsius, kanon]

All keywords
Found: [locomotive, model]^4.0 for [locomotive, model] at rank 18
Update of MRR: 0.3756090925897088
Update of MRR: 0.004106718715111479 and ratio: 0.010933491217683047, not found: [water, around]

 * @author rkern
 *
 */
public class QuerySuggestionEffectivenessEvaluation {
	public static void main(String[] args) throws IOException {
		new QuerySuggestionEffectivenessEvaluation().run(new File(args[0]), new File(args[1]), LocaleUtils.toLocale(args[2]));
	}

	private void run(File wikipediaIndexDir, File queryFile, Locale locale) throws IOException {
		final Builder builder = new FPGrowthAlgorithm.Builder(2);
		
		final int[] counter = new int[1]; 
		WikipediaPreprocessedIndex wikipediaPreprocessedIndex = new WikipediaPreprocessedIndex(wikipediaIndexDir, locale);
		
		wikipediaPreprocessedIndex.forEachParsedArticle(new PreprocessedArticleCallback() {
			@SuppressWarnings({ "unchecked", "unused" })
			@Override
			public void onArticle(String title, AnnotatedPlainText annotatedText, AnnotatedDocument annotatedDocument) {
				Set<Itemset> itemsets = new HashSet<>();
				
				if (false) {
					Iterable<Annotation> keyPhrases = annotatedDocument.getAnnotations(BaseTypeSystem.KeyPhrase);
					for (Annotation keyPhrase : keyPhrases) {
						Iterable<Annotation> keyPhraseTokens = annotatedDocument.getAnnotations(keyPhrase.getStart(), keyPhrase.getEnd(), BaseTypeSystem.Token);
						int annotationCount = AnnotatedDocumentUtils.getAnnotationCount(keyPhraseTokens);
						if (annotationCount >= 2) {
							Itemset items = new Itemset();
							for (Annotation kpt : keyPhraseTokens) {
								items.add(new Item(kpt.getText()));
							}
							itemsets.add(items);
						}
					}
				} else if (false) {
					Iterable<Annotation> sentences = annotatedDocument.getAnnotations(BaseTypeSystem.Sentence);
					for (Annotation sentence : sentences) {
						Iterable<Annotation> tokens = annotatedDocument.getAnnotations(sentence.getStart(), sentence.getEnd(), BaseTypeSystem.Token);
						int annotationCount = AnnotatedDocumentUtils.getAnnotationCount(tokens);
						if (annotationCount >= 2) {
							Itemset items = new Itemset();
							for (Annotation kpt : tokens) {
								items.add(new Item(kpt.getText()));
							}
							itemsets.add(items);
						}
					}
				} else {
					Iterable<Annotation> tokens = annotatedDocument.getAnnotations(BaseTypeSystem.Token);
					Itemset tokenItems = new Itemset();
					for (Annotation token : tokens) {
						tokenItems.add(new Item(token.getText()));
					}
					itemsets.add(tokenItems);
				}
				
				for (Itemset items : itemsets) {
					SortedSet<Item> set = (SortedSet<Item>)items.items();
					builder.addTransactionFirstPass(set);
				}
				
				if (++counter[0] % 100000 == 0) {
					System.out.println("First pass: "+counter[0]);
				}
			}
		});
		counter[0] = 0;
		long start = System.currentTimeMillis();
		builder.finishFirstPass(2);
		long firstPassTime = System.currentTimeMillis() - start;
		System.err.println("Finished first pass, took: "+firstPassTime);
		
		
		wikipediaPreprocessedIndex.forEachParsedArticle(new PreprocessedArticleCallback() {
			@SuppressWarnings("unchecked")
			@Override
			public void onArticle(String title, AnnotatedPlainText annotatedText, AnnotatedDocument annotatedDocument) {
				Iterable<Annotation> keyPhrases = annotatedDocument.getAnnotations(BaseTypeSystem.KeyPhrase);
				for (Annotation keyPhrase : keyPhrases) {
					Iterable<Annotation> keyPhraseTokens = annotatedDocument.getAnnotations(keyPhrase.getStart(), keyPhrase.getEnd(), BaseTypeSystem.Token);
					int annotationCount = AnnotatedDocumentUtils.getAnnotationCount(keyPhraseTokens);
					if (annotationCount >= 2) {
						Set<Item> items = new HashSet<>();
						for (Annotation kpt : keyPhraseTokens) {
							items.add(new Item(kpt.getText()));
						}
						builder.addTransactionSecondPass(items);
					}
				}
				if (++counter[0] % 100000 == 0) {
					System.out.println("Second pass: "+counter[0]);
				}
			}
		});
		start = System.currentTimeMillis();
		builder.finishSecondPass(10);
		long secondPassTime = System.currentTimeMillis() - start;
		System.err.println("Finished second pass, took: "+secondPassTime);
		wikipediaPreprocessedIndex.close();
		
		start = System.currentTimeMillis();
		FPGrowthAlgorithm fpgrowth = builder.build();
		long buildTime = System.currentTimeMillis() - start;
		
		int size = fpgrowth.getSize();
		System.err.println("Finished building tree, nodes: " + size + ", took: "+buildTime);
		
		double reciprocalRankSum = 0;
		int queryCount = 0;
		int foundQueries = 0;
		long lookupTime = 0;
		
		LineIterator iterator = new LineIterator(new FileReader(queryFile));
		while (iterator.hasNext()) {
			String line = iterator.nextLine();
			String[] queryWords = line.split("\\t");
			
			Item startItem = new Item(queryWords[0]);
			
			Itemset expectedItems = new Itemset();
			for (int i = 1; i < queryWords.length; i++) {
				Item expectedItem = new Item(queryWords[i]);
				expectedItems.add(expectedItem);
			}
			int rank = 1;
			boolean foundMatchingItem = false;
			
			start = System.nanoTime();
			List<Itemset> frequentItemsets = fpgrowth.getFrequentItemsets(startItem, null);
			lookupTime += System.nanoTime() - start;
			if (frequentItemsets != null && !frequentItemsets.isEmpty()) {
				for (Itemset itemset : frequentItemsets) {
					for (Item item : itemset) {
						if (item.equals(startItem)) { continue; }
						
						if (expectedItems.contains(item)) {
							foundMatchingItem = true;
							System.out.println("Found: "+itemset+" for "+Arrays.toString(queryWords)+" at rank "+rank);
							break;
						}
					}
					if (foundMatchingItem) { 
						break;
					}
					rank++;
				}
			}
			
			queryCount++;
			if (foundMatchingItem) {
				foundQueries++;
				reciprocalRankSum += 1.0 / rank;
			} 
			
			if (queryCount % 1000 == 0) {
				System.out.println("MRR/found: "+(reciprocalRankSum / foundQueries)+", found queries: "+foundQueries+ 
						", MRR/total: "+(reciprocalRankSum / queryCount)+", ratio: " + ((double)foundQueries/queryCount) + ", avg. time: "+(lookupTime / queryCount));
			}
		}
		System.out.println("MRR/found: "+(reciprocalRankSum / foundQueries)+", found queries: "+foundQueries+ 
				", MRR/total: "+(reciprocalRankSum / queryCount)+", ratio: " + ((double)foundQueries/queryCount) + ", avg. time: "+(lookupTime / queryCount));
	}
	
	
}
