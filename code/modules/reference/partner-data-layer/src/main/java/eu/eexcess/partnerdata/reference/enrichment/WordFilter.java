/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.partnerdata.reference.enrichment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;

//import edu.smu.tspell.wordnet.Synset;
//import edu.smu.tspell.wordnet.SynsetType;
//import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordFilter {

	List<String> stopWordsList;
	protected PartnerConfiguration partnerConfig;

	public WordFilter(PartnerConfiguration config) {
		this.partnerConfig = config;
		//set system property
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		//load wordnet database 
//		wordnetDataBase = WordNetDatabase.getFileInstance();
		//load stop words
		stopWordsList=new StopWords(this.partnerConfig).getStopWordsList();
	}

/*
	private WordNetDatabase wordnetDataBase ;

	public WordNetDatabase getWordNetDatabase()
	{
		return wordnetDataBase;
	}
*/
	public boolean isKeyWord(String word)
	{
		int demandedMinLength=3;

		//check if it's shorter than demandedMinLength
		if (word.length()<demandedMinLength)
		{
			return false;
		}

		//check if it's a stopword
		if (stopWordsList.contains(word.toLowerCase()))
		{
			return false;
		}

		return (isEntityDbpediaSpotlight(word));

		//		//check if it's rather noun or verb
		//		int nounMeanings = wordnetDataBase.getSynsets(word, SynsetType.NOUN).length;
		//		int verbMeanings = wordnetDataBase.getSynsets(word, SynsetType.VERB).length;

		//return true;
	}
	
	public Set<String> selectKeyWords (Set<String> words)
	{
		Set<String> results=new HashSet<String>();
		
		for (String w: words)
		{
			if (isKeyWord(w))
			{
				results.add(w);
			}
		}
		
		return results;
	}
/*
	public Set<String> getSynonymsWordNet(String word)
	{
		Synset[] synsets = wordnetDataBase.getSynsets(word, SynsetType.NOUN);

		HashSet<String> resultSet=new HashSet<String>();

		for (Synset s: synsets)
		{
			String synonym=s.getWordForms()[0];

			String[] synonyms =synonym.toLowerCase().split(" ");

			for (String syn: synonyms)
			{
				if (syn.equals(word))
				{
					continue;
				}
				resultSet.add(syn);
			}
		}

		return resultSet;
	}

	public Set<String> getEntietiesFreeBase(String word)
	{
		return FreeBase.getEntietiesFreeBase(word);
	}

	*/
	public boolean isEntityDbpediaSpotlight(String word)
	{
		DbpediaSpotlight spotlight = new DbpediaSpotlight(this.partnerConfig);
		return spotlight.isEntityDbpediaSpotlight(word);

	}
/*
	public Set<String> selectEntitiesDbpediaSpotlight(Set<String> words)
	{
		return DbpediaSpotlight.selectEntitiesDbpediaSpotlight(words);

	}
	
	public String selectEntityDbpediaSpotlight(String word)
	{
		return DbpediaSpotlight.selectEntityDbpediaSpotlight(word);

	}
	
	public Set<String> getLocationHierarchy(String location)
	{
		return GeoNames.getLocationHierarchy(location);
	}
	*/
}

class StopWords
{
//	String stopWordsFilePath="D:\\test\\stop_words.txt";
	List<String> stopWordsList;
	protected PartnerConfiguration partnerConfig;

	public StopWords(PartnerConfiguration config) {
		this.partnerConfig = config;
		stopWordsList=new ArrayList<String>();
		//read stop words from file
		try {
			InputStream inStream = this.getClass().getResourceAsStream("/stop_words_en.txt");
			InputStreamReader inStreamReader = new InputStreamReader(inStream);
			
			BufferedReader reader = new BufferedReader(inStreamReader);
			String line = reader.readLine();
			while (line != null) {
				stopWordsList.add(line);
				line = reader.readLine();
			}
			
			reader.close();
			inStreamReader.close();
			inStream.close();
			inStream=null;
			reader=null;
			inStreamReader=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			InputStream inStream = this.getClass().getResourceAsStream("/stop_words_de.txt");
			InputStreamReader inStreamReader = new InputStreamReader(inStream);
			BufferedReader reader = new BufferedReader(inStreamReader);
			String line = reader.readLine();
			while (line != null) {
				stopWordsList.add(line);
				line = reader.readLine();
			}
			reader.close();
			inStream.close();
			inStreamReader.close();
			reader=null;
			inStream=null;
			inStreamReader=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PartnerdataTracer.debugTrace(this.partnerConfig, "\n\n loaded words for stop words:" + stopWordsList.size());
	}

	public List<String> getStopWordsList() {
		return stopWordsList;
	}


}
