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
package eu.eexcess.federatedrecommender.evaluation.evaluation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Organizes queries and result logging for the evaluation
 * 
 * @author hziak
 *
 */
public class EvaluationManager {
	private static final Logger logger = Logger
			.getLogger(EvaluationResultLists.class.getName());
	private final EvaluationQueries evalQueries;
	private boolean cacheWarmed = false;
	private HashMap<Integer, Iterator<EvaluationQuery>> userQueryIteratorMap = new HashMap<Integer, Iterator<EvaluationQuery>>();
	private HashMap<Integer, HashMap<String, EvaluationResultLists>> userResultMap = new HashMap<Integer, HashMap<String, EvaluationResultLists>>();
	private HashMap<EvaluationQuery, EvaluationResultLists> queryCache = new HashMap<EvaluationQuery, EvaluationResultLists>();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String evaluationQueriesFile;
	private List<EvaluationQuery> removeList=new ArrayList<>();

	public EvaluationManager(String evaluationQueriesFile)
			throws FileNotFoundException {
		this.evaluationQueriesFile = evaluationQueriesFile;
		BufferedReader br = new BufferedReader(new FileReader(
				evaluationQueriesFile));

		this.evalQueries = gson.fromJson(br, EvaluationQueries.class);
	}

	public EvaluationQuery getNextQuery(Integer id) {
		if (!userQueryIteratorMap.containsKey(id)) {
			userQueryIteratorMap.put(id, evalQueries.queries.iterator());
		}
		EvaluationQuery query = null;
		try {

			query = userQueryIteratorMap.get(id).next();
		} catch (Exception e) {
			logger.log(Level.WARNING, "No query left");
		}
		return query;
	}

	/**
	 * 
	 * @param uID
	 * @param result
	 *            false if there is a query left else true
	 * @return
	 */
	public boolean addQueryResult(Integer uID, EvaluationResultLists result)
			throws NoSuchElementException {
		if (!userResultMap.containsKey(uID)) {
			HashMap<String, EvaluationResultLists> value = new HashMap<>();
			userResultMap.put(uID, value);
		}
		userResultMap.get(uID).put(result.query, result);
		return !userQueryIteratorMap.get(uID).hasNext();
	}

	/**
	 * logging function to writer the user results from the result list, results
	 * are removed afterwards
	 * 
	 * @param uID
	 */
	public void writeUserResultToFile(Integer uID) {
		if (userResultMap.containsKey(uID)) {
			String json = gson.toJson(userResultMap.get(uID));
			FileWriter writer;
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
			Date date = new Date();
			try {
				writer = new FileWriter("userResults" + uID + " "
						+ dateFormat.format(date) + ".json");
				writer.write(json);
				writer.close();
				logger.log(Level.INFO, "Write query log for user " + uID);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Could not write query log for user "
						+ uID, e);
			} finally {
				userResultMap.remove(uID);
			}

		}
	}

	/**
	 * writes all user results into result files
	 */
	public void writeAllResultsToFile() {
		for (Integer uID : userResultMap.keySet()) {
			writeUserResultToFile(uID);
		}
	}

	public List<EvaluationQuery> getAllQueries() {
		return evalQueries.queries;
	}

	/**
	 * adds the results to the query cache, if one of the partners returns zero
	 * results the query is removed from the pool
	 * 
	 * @param query
	 * @param evaluationResultLists
	 */
	public void addResultToQueryCache(EvaluationQuery query,
			EvaluationResultLists evaluationResultLists) {
		boolean resultSaved = true;
		for (EvaluationResultList result : evaluationResultLists.results) {
			if (result.totalResults == 0) {
				resultSaved = false;

				break;
			}
		}
		if (resultSaved)
			queryCache.put(query, evaluationResultLists);
		else {
			logger.log(
					Level.INFO,
					"Query "
							+ query
							+ ". Result not saved, at least one partner returned zero results. Query removed from pool");
			removeList.add(query);
		}

		if (queryCache.size()+removeList.size() == evalQueries.queries.size()) {
			cacheWarmed = true;
			
		}
	}

	/**
	 * write query file when cache is warmed and all queries that returned zero
	 * results for one of the partners are removed
	 */
	private void writeNewQueryFile() {
		String jsonString = gson.toJson(evalQueries);

		FileWriter writer = null;
		try {
			writer = new FileWriter(evaluationQueriesFile);
			writer.write(jsonString);
			writer.close();
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Queries couldn't be written back to queries file:"
							+ evaluationQueriesFile, e);
		}
	}

	public boolean isCacheWarmed() {
		return cacheWarmed;
	}

	/**
	 * returns the results out of the querycache cache has to be filled before
	 * by getAllQueries and addResultToQueryCache
	 * 
	 * @param uID
	 * @return
	 */
	public EvaluationResultLists getNextResultsForQuery(Integer uID) {
		EvaluationQuery nextQuery = getNextQuery(uID);
		if (cacheWarmed){
			if(removeList.size()>0){
				evalQueries.queries.removeAll(removeList);
				removeList.clear();
				writeNewQueryFile();
			}
			EvaluationResultLists evaluationResultLists = queryCache.get(nextQuery);
			Collections.shuffle(evaluationResultLists.results);
			return evaluationResultLists;
		}
		else
			return null;
	}

};
