package eu.eexcess.federatedrecommender.evaluation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.federatedrecommender.evaluation.csv.EvaluationQueryList;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;

import org.apache.tika.language.LanguageIdentifier;
public class SchloettQuerySelection {
	final static String directoryPath = "/home/hziak/Datasets/EExcess/schloett-datacollection-785deb288e36/";
	
	private  EvaluationQueryList getEvaluationQueriesFromJson(String queryfile) {
		JsonReader reader = null;
		try {

			reader = new JsonReader(new FileReader(directoryPath
					+ queryfile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EvaluationQueryList queries = null;
		Gson gson = new GsonBuilder().create();

		queries = gson.fromJson(reader, EvaluationQueryList.class);
		return queries;
	}
	public static void main(String[] args){
		SchloettQuerySelection sQS = new SchloettQuerySelection();
		EvaluationQueryList preselectedQueries = sQS.getEvaluationQueriesFromJson("queriesEn-selected.json");
		EvaluationQueryList unprocessedqueries = sQS.getEvaluationQueriesFromJson("queriesEn.json");
		
		ArrayList<EvaluationQuery> upqueries = unprocessedqueries.getQueries();
		
		HashMap<Interest, Integer> countedWorksMap = new HashMap<Interest, Integer >();
		for (EvaluationQuery evaluationQuery :upqueries) {
			for (Interest interest : evaluationQuery.interests) {

				 LanguageIdentifier identifier = new LanguageIdentifier(interest.text);
			      String language = identifier.getLanguage();
			      
			      if(language.equals("en")){
			    	  if(countedWorksMap.containsKey(interest)){
						countedWorksMap.put(interest, countedWorksMap.get(interest)+1);
					}else countedWorksMap.put(interest, 1);
			      }
			}
		}
		List<Interest> interestList= new ArrayList<Interest>();
		for (Interest key : countedWorksMap.keySet()) {
			if(countedWorksMap.get(key)<300){
			System.out.println(key.text +" " + countedWorksMap.get(key));
			interestList.add(key);
			}
		}
		
		if(true){
			for (EvaluationQuery preselectedQuery : preselectedQueries.getQueries()) {
		
			if(upqueries.contains(preselectedQuery)){
				for (Interest interest : upqueries.get(upqueries.indexOf(preselectedQuery)).interests) {
					if(interestList.contains(interest))
						preselectedQuery.interests.add(interest);	
				}
				
			}
			else{
				System.out.println("error");
			}
			
		}
		System.out.println(preselectedQueries);
		System.out.println(preselectedQueries.getQueries().size());

		}
	}
	
}
