package eu.eexcess.partnerdata.evaluation.enrichment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class StopWords {
	

		List<String> stopWordsList;

		public StopWords() {
			stopWordsList=new ArrayList<String>();
			//read stop words from file
			try {
				File file = new File("d:\\interactions-log\\stop_words_en.txt");
				InputStream inStream = new FileInputStream(file);
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
				File file = new File("d:\\interactions-log\\stop_words_de.txt");
				InputStream inStream = new FileInputStream(file);
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

		}

		public List<String> getStopWordsList() {
			return stopWordsList;
		}
}
