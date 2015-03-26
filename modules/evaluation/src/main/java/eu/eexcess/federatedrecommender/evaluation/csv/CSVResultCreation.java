/* Copyright (C) 2014 
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
package eu.eexcess.federatedrecommender.evaluation.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.LongToDoubleFunction;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeStats;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;

public class CSVResultCreation {
	final static String directoryPath = "/home/hziak/Datasets/EExcess/schloett-datacollection-785deb288e36/";

	private final  WebResource wRBlock;
//	private final  WebResource wRDefault; 
	public CSVResultCreation() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);
		Client client = Client.create(clientConfig);
		wRBlock   = client.resource("http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-evaluation-1.0-SNAPSHOT/evaluation/blockEvaluation");
//		wRDefault = client.resource("http://localhost:8099/excess-federated-recommender-web-service-evaluation-1.0-SNAPSHOT/evaluation/evaluation");

	}
	public static void main(String args[]) {
		CSVResultCreation creation = new CSVResultCreation();
		EvaluationQueryList queries = creation.getEvaluationQueriesFromJson();


		//creation.writeQueriesToQueryCSVFile(queries);

		creation.createCSVResultsFile(queries);

	}
	private void createCSVResultsFile(
			EvaluationQueryList queries) {
		FileWriter ofrw = null;
		try {
			ofrw = new FileWriter(new File(directoryPath + "queryresult.csv"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		
	
			for (EvaluationQuery query : queries.getQueries()) {
				 SecureUserProfileEvaluation secureUserProfileEvaluation=	convertEvalQueryToSecUserProfile(query);
				String blockResultString = getQueryResultCSV(getwRBlock(), 
						secureUserProfileEvaluation);
				String defaultResultString = null; //Todo: Get default string from get queyr resultCSV
				// defaultResultString = getQueryResultCSV(getwRDefault(),
					//	secureUserProfileEvaluation);
				String queryCSV =getQueryCSV(query);
				String finalCSVString=null;
				if(queryCSV!=null&&blockResultString!=null&&defaultResultString!=null)
					finalCSVString= queryCSV+","+blockResultString+","+defaultResultString;
				if(finalCSVString!=null)
					try {
						ofrw.write(finalCSVString);
						System.out.println(finalCSVString);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
	
		try {
			ofrw.flush();
			ofrw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private  EvaluationQueryList getEvaluationQueriesFromJson() {
		JsonReader reader = null;
		try {

			reader = new JsonReader(new FileReader(directoryPath
					+ "queriesEn-selected.json"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EvaluationQueryList queries = null;
		Gson gson = new GsonBuilder().create();

		queries = gson.fromJson(reader, EvaluationQueryList.class);
		return queries;
	}
	
	private void writeQueriesToQueryCSVFile(EvaluationQueryList queries) {
		FileWriter ofqw = null;
		try {
			ofqw = new FileWriter(new File(directoryPath + "query.csv"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (EvaluationQuery evalQueries : queries.getQueries()) {

			String result = getQueryCSV(evalQueries);
			try {
				ofqw.write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		try {
			ofqw.flush();
			ofqw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private  String getQueryCSV(EvaluationQuery evalQueries) {
		StringBuilder builder = new StringBuilder();
		builder.append("\"");
		builder.append(evalQueries.query.replaceAll(",|\"|\n|\r", ""));
		builder.append("\"");
		builder.append(",");
		builder.append("\"");
		builder.append(evalQueries.description.replaceAll(",|\"|\n|\r", ""));
		builder.append("\"");
//		builder.append(System.lineSeparator());
		return builder.toString();
	}

	private  SecureUserProfileEvaluation convertEvalQueryToSecUserProfile(
			EvaluationQuery query) {
		SecureUserProfileEvaluation profile = new SecureUserProfileEvaluation();
		for (String queryPart : query.query.split(" ")) {
			if (!queryPart.trim().isEmpty())
				profile.contextKeywords.add(new ContextKeyword(queryPart, 0.5));
		}
		profile.interestList.addAll(query.interests);
		PartnerBadge partner = new PartnerBadge();
		partner.longTimeStats= new PartnerBadgeStats();
		
		partner.shortTimeStats = new PartnerBadgeStats();
		partner.systemId="Mendeley";
	//	profile.partnerList.add(partner );	
		profile.picker="FiFoPicker";
		profile.numResults=10;
		return profile;
	}

	private static String getQueryResultCSV(WebResource wresource,
			SecureUserProfileEvaluation secureUserProfileEvaluation) {
		StringBuilder builder = new StringBuilder();
		EvaluationResultLists resp = null;
		secureUserProfileEvaluation.numResults=10;
		resp = wresource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(EvaluationResultLists.class, secureUserProfileEvaluation);
		
		if(resp.results.get(0).results.size()<10){
			System.out.println("query did not return enough results! 10!="+resp.results.get(0).results.size()+ " "+ secureUserProfileEvaluation.contextKeywords.toString());
		return null;
		}
		
		for (EvaluationResultList resultList : resp.results) {
			System.out.println("RL "+resultList);
			int counter=0;
			for (Result result : resultList.results) {
				
				if(result.title!=null){
				builder.append("\"");
				
				builder.append(result.title.replaceAll(",|\"|\n|\r", ""));
				builder.append("\",");
//				if (result.description != null)
//					if (!result.description.isEmpty()) {
//						builder.append(",");
//						builder.append("\"");
//						builder.append(result.description.replaceAll(
//								",|\"|\n|\r", " "));
//						builder.append("\"");
//					}
		//		builder.append(System.lineSeparator());
				}else if( resp.results.get(0).results.size()-(++counter)<10)
					return null;
			}
			builder.append(",");
		}
		String string = builder.toString();
		System.out.println("returned "+string);
		return string;
	}
	public WebResource getwRBlock() {
		return wRBlock;
	}
//	public WebResource getwRDefault() {
//		return wRDefault;
//	}
}
