package eu.eexcess.federatedrecommender.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.eexcess.federatedrecommender.evaluation.csv.EvaluationQueryList;
import eu.eexcess.federatedrecommender.evaluation.dataformats.SchloettQuery;
import eu.eexcess.federatedrecommender.evaluation.dataformats.SchloettQueryFormat;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;

import com.google.gson.JsonSyntaxException;
/**
 * 
 * @author hziak
 *
 */
public class SchloettQueryExtraction {
	

	public SchloettQueryExtraction() {

		
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws JsonGenerationException {
		SchloettQueryExtraction extraction = new SchloettQueryExtraction();
		File folder = new File(
				"/home/hziak/Datasets/EExcess/schloett-datacollection-785deb288e36/");
		File[] listOfFiles = folder.listFiles();
		List<File> files= getQueryFile(listOfFiles);
		
		List<SchloettQueryFormat> querys= new ArrayList<SchloettQueryFormat>();
		try {
			querys.addAll(parseQueryiesFile(files,extraction));
		} catch (JsonSyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EvaluationQueryList evalQueries = new EvaluationQueryList();
		for (SchloettQueryFormat schloettQueryFormat : querys) {
			if(schloettQueryFormat.getQuerieMap()==null)
				System.out.println("is null");
			else
				for (Entry<String, LinkedHashMap<String, String>> schloettQueryFormat1 : schloettQueryFormat.getQuerieMap().entrySet()) {
					
					if(	schloettQueryFormat1.getValue().get("task_name").endsWith(".en")){
						System.out.println("query: "+schloettQueryFormat1.getValue().get("query"));
						evalQueries.getQueries().add(new EvaluationQuery(schloettQueryFormat1.getValue().get("query"),"TODO: decription", null));
						
					}
				}
			
		}
		
		

		ObjectMapper mapper = new ObjectMapper();
		
		try {
			File file = new File( folder.getCanonicalFile()+"/queriesEn.json");
			mapper.defaultPrettyPrintingWriter().writeValue(file, evalQueries);
			System.out.println("Writing to file:" +file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<SchloettQueryFormat> parseQueryiesFile(
			List<File> files, SchloettQueryExtraction extraction) throws JsonSyntaxException, IOException {
		FileReader freader;
		List<SchloettQueryFormat> queries = new ArrayList<SchloettQueryFormat>();
	
		for (File file : files) {
			try {
				freader = new FileReader(file);
				BufferedReader br = new BufferedReader(freader);
				
					
					ObjectMapper mapper = new ObjectMapper();
					HashMap format = mapper.readValue(file, new HashMap<String,SchloettQuery>().getClass());
					
						queries.add(new SchloettQueryFormat(format));
				br.close();
				freader.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return queries;
	}

	private static List<File> getQueryFile(File[] listOfFiles) {
		List<File> fileList= new ArrayList<File>();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	if(file.getName().contains("queries.json"))
		    		fileList.add(file);
		    }else if(file.isDirectory()){
		    	fileList.addAll(getQueryFile(file.listFiles()));
		    }
		}
		return fileList;
	}


}
