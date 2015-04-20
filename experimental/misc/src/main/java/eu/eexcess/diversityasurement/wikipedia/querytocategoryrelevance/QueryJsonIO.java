/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.diversityasurement.wikipedia.querytocategoryrelevance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class QueryJsonIO {

	public static Queries readQueries(File queryFile) throws FileNotFoundException, IOException {
		JsonReader reader = new JsonReader(new FileReader(queryFile));
		Gson gson = new GsonBuilder().create();
		Queries queries = gson.fromJson(reader, Queries.class);
		reader.close();
		return queries;
	}

	static void writeQueries(File queryFile, Queries queries) throws IOException {
		FileWriter writer = new FileWriter(queryFile);
		Gson gson = new GsonBuilder().create();
		gson.toJson(queries, writer);
		writer.close();
	}

//	public static void writeQueries(File queryFile, Relevances relevances) throws IOException {
//		FileWriter writer = new FileWriter(queryFile);
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		gson.toJson(relevances, writer);
//		writer.close();
//	}

}
