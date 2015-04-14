package eu.eexcess.evaluation.queryexpansion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;

public class QueryExpansionEvaluationResultsAnalysis {

	private static final String directoryPath = "/home/hziak/Datasets/EExcess/EvalUserBackup/";
	private static final String[] singlePageQueriesArray= {"michelle obama","Autism","glass ceiling","february revolution",
		"war on terrorism"};
	
	public static void main(String[] args) {
		 List<String> singlePageQueriesList= Arrays.asList(singlePageQueriesArray);
		File dir = new File(directoryPath); 
		//for(File file : )
		List<LinkedHashMap> singleQueryMap =	new ArrayList<LinkedHashMap>();
		
		List<LinkedHashMap> vagueQueryMap =new ArrayList<LinkedHashMap>();
		for(File file : dir.listFiles()){
			try {
				//JsonReader reader = null;
				//reader = new JsonReader(new FileReader(file));
				ObjectMapper mapper = new ObjectMapper();
				EvaluationResultLists list = null;
				HashMap<String,LinkedHashMap> map=  mapper.readValue(file,new HashMap<String,LinkedHashMap>().getClass());
				
				//Gson gson = new GsonBuilder().create();
			//	 HashMap<String, EvaluationResultLists> map= gson.fromJson(reader,new HashMap<String,EvaluationResultLists>().getClass() );
				System.out.println(file.getAbsolutePath());
			
				for(String key : map.keySet()){
					if(singlePageQueriesList.contains(key)){
			//			System.out.println(map.get(key));
						singleQueryMap.add(map.get(key));
						
					}else
						vagueQueryMap.add(map.get(key));	
				}
			
				
				
			} catch (Exception e) {
				//System.out.println("Leaving out "+file.getAbsolutePath());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("single:");
		sumQueryMap(singleQueryMap);
		System.out.println("vague:");
		sumQueryMap(vagueQueryMap);
		

	}

	private static void sumQueryMap(List<LinkedHashMap> queryMap) {
		Integer sumWiki=0;
		Integer sumSource=0;
		Integer sumSourceP=0;
		Integer sumNone=0;
		for(LinkedHashMap value : queryMap){
			for(Object key : value.keySet()){
				//System.out.println(key.toString());
				//System.out.println(value.get(key));
				List<Object> asList = Arrays.asList(value.get(key));
				try{
				 Object entry = value.get(key);
				 //System.out.println(entry.getClass());
				 if(entry instanceof  java.util.ArrayList){
					 ArrayList<LinkedHashMap> objects = (ArrayList<LinkedHashMap>) entry;
					 for(LinkedHashMap obj : objects){
					//	 System.out.println(obj.get("provider").toString());
						 if(obj.get("provider").toString().startsWith("wikipedia")){
							 String num =((String) obj.get("numSelect")).trim();
							 sumWiki +=  Integer.parseInt(num);
						 }else if(obj.get("provider").toString().startsWith("source partners:FiFoPicker expansion source partners:[PartnerBadge")){
							 String num =((String) obj.get("numSelect")).trim();
							 sumSource +=  Integer.parseInt(num);
						 }
						 else if(obj.get("provider").toString().startsWith("source")){
							 String num =((String) obj.get("numSelect")).trim();
							 sumSourceP +=  Integer.parseInt(num);
						 }
						 else {
							 String num =((String) obj.get("numSelect")).trim();
							 sumNone +=  Integer.parseInt(num);
						 }
						 
					 }
				 }
				}catch(ClassCastException e){
					e.printStackTrace();
				}
			}
		}
		System.out.println("None: "+sumNone);
		
		System.out.println("Sour: "+sumSource);
		System.out.println("SouP: "+sumSourceP);
		System.out.println("Wiki: "+sumWiki);
	}

}
