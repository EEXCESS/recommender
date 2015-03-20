package eu.eexcess.partnerdata.evaluation.enrichment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.codehaus.jackson.JsonGenerationException;


public class SearchTermsFromInteractionsLogParser {
	

	private StopWords stopWords;


	public SearchTermsFromInteractionsLogParser() {
		stopWords = new StopWords();
		
		
	}

	public ArrayList<String> extractSearchTerms(String filename, ArrayList<String> keywordResults)
	{
		int countKeywords = 0;
		File logFile = new File(filename);
		try {
			FileReader freader = new FileReader(logFile);
			BufferedReader br = new BufferedReader(freader);
			
			String line;
		    while ((line = br.readLine()) != null) {
		    	try {
			    	if (line.substring(0,40).contains("[QUERY]")) {
			    		int index = line.indexOf("[ip:");
			    		if (index > 0 ){
			    			String jsonString = line.substring(index + 20);
			    			countKeywords = parseForKeywords(keywordResults, countKeywords, line, jsonString);	
//			    			JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonString);	
//			    			JSONArray keywordsArray = (JSONArray)json.get("contextKeywords");
//			    			
//			    			for (int i = 0; i < keywordsArray.size(); i++) {
//								JSONObject  keyword = (JSONObject)keywordsArray.get(i);
//								String keywordString = (String)keyword.get("text");
//								if ( ! keywordResults.contains(keywordString))
//									keywordResults.add(keywordString);
//								countKeywords++;
//							}
					        
			    			
			    		}
				        
			    	}
		    	} catch (net.sf.json.JSONException e) {
//					e.printStackTrace();
//					System.out.println("Error in line:\n" + line);
					if (line.contains("\"contextKeywords\""))
					{
						int startIndex = line.indexOf("\"contextKeywords\"");
						int endIndex = line.indexOf("],", startIndex);
						if (startIndex > 0 && endIndex > 0 && startIndex < endIndex) {
							String keywords = line.substring(startIndex, endIndex+1);
							keywords = "{"+keywords + "}";
//							System.out.println("keywords:\n" + keywords);
							try{
					    	countKeywords = parseForKeywords(keywordResults, countKeywords, line, keywords);
							} catch (net.sf.json.JSONException innerExc){
								innerExc.printStackTrace();
								System.out.println("Error in line:\n" + keywords);
							}
						}
					}

		    	} catch (RuntimeException e) {
					e.printStackTrace();
					System.out.println("Error in line:\n" + line);
		    	}
		    }
//				ObjectMapper mapper = new ObjectMapper();
//				HashMap format = mapper.readValue(file, new HashMap<String,SchloettQuery>().getClass());
//				
//					queries.add(new SchloettQueryFormat(format));
			br.close();
			freader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("found " + countKeywords +" keywords in file: "+ filename);
		System.out.println("Size of the Keyword-List " + keywordResults.size() + " after processing file: "+ filename);
		return keywordResults;
	}

	private int parseForKeywords(ArrayList<String> keywordResults,
			int countKeywords, String line, 
			String keywords) throws Exception{
		try {
			JSONObject json = (JSONObject) JSONSerializer.toJSON(keywords);	
			JSONArray keywordsArray = (JSONArray)json.get("contextKeywords");
			
			for (int i = 0; i < keywordsArray.size(); i++) {
				JSONObject  keyword = (JSONObject)keywordsArray.get(i);
				Object keywordObject = keyword.get("text");
				if (!(keywordObject instanceof JSONNull))
				{
					String keywordString = (String)keywordObject;
					keywordString = keywordString.toLowerCase();
					if (! this.stopWords.getStopWordsList().contains(keywordString) )
					{
						if ( ! keywordResults.contains(keywordString))
						{
							keywordResults.add(keywordString);
						}						
						countKeywords++;
					}
				}
			}
		} catch (net.sf.json.JSONException e) {
			throw e;
		}
		return countKeywords;
	}
	
	
	public static void main(String[] args) throws JsonGenerationException {
		SearchTermsFromInteractionsLogParser extraction = new SearchTermsFromInteractionsLogParser();
		String filename = "";

		ArrayList<String> keywordResults = new ArrayList<String>();

		filename = "D:\\interactions-log\\2014-10-16 10.00-2014-10-16 15.23-interactions.log";
		keywordResults = extraction.extractSearchTerms(filename, keywordResults);

		filename = "D:\\interactions-log\\2014-10-16 10.49-2014-10-20 13.28-interactions.log";
		keywordResults = extraction.extractSearchTerms(filename, keywordResults);
		
		filename = "D:\\interactions-log\\2014-10-16 10.49-2014-11-06 10.39-interactions.log";
		keywordResults = extraction.extractSearchTerms(filename, keywordResults);
		
		filename = "D:\\interactions-log\\2014-11-06 10.46-2014-11-24 11.51-interactions.log";
		keywordResults = extraction.extractSearchTerms(filename, keywordResults);
		
		System.out.println("\n\nSize of the Keyword-List " + keywordResults.size());
		
		PrintWriter writer;
		try {
			writer = new PrintWriter("D:\\interactions-log\\keywords.txt", "UTF-8");
			for (int i = 0; i < keywordResults.size(); i++) {
				writer.println(keywordResults.get(i));
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
