/*
Copyright (C) 2014 
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

package eu.eexcess.partnerdata.evaluation.enrichment;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class KeywordsList {

	public KeywordsList() {

	}

	public ArrayList<String> readFile(String filename) {
		ArrayList<String> keywords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();

			while (line != null) {
				keywords.add(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keywords;
	}

	public void sortKeywords() {
		ArrayList<String> keywords = readFile("D:\\interactions-log\\keywords.txt");
		java.util.Collections.sort(keywords);
		writeFile("D:\\interactions-log\\keywords_sorted.txt", keywords,-1);
	}

	public void resizeKeywords(int size) {
		ArrayList<String> keywords = readFile("D:\\interactions-log\\keywords_sorted.txt");
		writeFile("D:\\interactions-log\\keywords_sorted_100.txt", keywords,size);
	}

	public void randomKeywords(int size) {
		ArrayList<String> keywords = readFile("D:\\interactions-log\\keywords_sorted.txt");
		Random randomGenerator = new Random();
		ArrayList<String> ret = new ArrayList<String>(); 
	    for (int idx = 1; idx <= size; ++idx){
	      int randomInt = randomGenerator.nextInt(keywords.size());
	      ret.add(keywords.get(randomInt));
	    }		
		writeFile("D:\\interactions-log\\keywords_random_"+size+".txt", ret,-1);
	}

	public void writeFile(String filename, ArrayList<String> keywords, int size) {
		PrintWriter writer;
		int count = 0;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			for (int i = 0; i < keywords.size(); i++) {
				if (size != -1  &&  count > size)
				{
					break;
				} else {
					writer.println(keywords.get(i));
					count ++;
				}
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		KeywordsList tester = new KeywordsList();
		tester.randomKeywords(20);

	}

}
