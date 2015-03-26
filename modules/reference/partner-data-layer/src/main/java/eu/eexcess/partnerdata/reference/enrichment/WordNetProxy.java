package eu.eexcess.partnerdata.reference.enrichment;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.data.Word;

public class WordNetProxy {
	protected Dictionary dictionary;
	protected Dictionary mapDictionary;

	protected InputStream getProperties() throws IOException {
		return Dictionary.class
				.getResourceAsStream("/net.sf.extjwnl.dictionary.file_properties.xml");
	}

	public WordNetProxy() {
			try {
				dictionary = Dictionary.getInstance(getProperties());
			} catch (JWNLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// mapDictionary = Dictionary.getInstance(getProperties());

	}

	public Set<String> getSynonymsWordNet(String word) {
		HashSet<String> resultSet = new HashSet<String>();
		try {
			IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, word);
			if (indexWord != null ) {
				List<Synset> senses  = indexWord.getSenses();
				for (Iterator<Synset> iterator = senses.iterator(); iterator.hasNext();) {
					Synset synset = (Synset) iterator.next();
					List<Word> synWords = synset.getWords();
					for (int i = 0; i < synWords.size(); i++) {
						resultSet.add(synWords.get(i).getLemma());
					}
				}
			}
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}

}
