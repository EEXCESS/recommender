/* Copyright (C) 2010 
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
package eu.eexcess.europeana.clickstream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.LineIterator;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;

/**
 * 
 * @author rkern
 */
public class EuropeanaQueryCollector {
	
	private static final String DEFAULT_FIELD = "default-field";
	private static final String QUERY_PREFIX = "query=";

	public static void main(String[] args) throws Exception {
		new EuropeanaQueryCollector().run(new File(args[0]));
	}

	private void run(File baseDir) throws IOException {
		File outFile = new File(baseDir, "multi-term-queries.tsv");
		PrintWriter writer = new PrintWriter(outFile);
		System.err.println("Writing results to: "+outFile);
		
		QueryParser queryParser = new QueryParser(DEFAULT_FIELD, new StandardAnalyzer(Version.LUCENE_CURRENT));
		Set<String> uniqueQueries = new TreeSet<String>();
		
		processDirectory(baseDir, writer, queryParser, uniqueQueries);
		for (File file : baseDir.listFiles()) {
			if (file.isDirectory()) {
				processDirectory(file, writer, queryParser, uniqueQueries);
			}
		}
		writer.close();
	}

	private void processDirectory(File dir, PrintWriter writer, QueryParser queryParser, Set<String> uniqueQueryCollector) throws IOException, FileNotFoundException, UnsupportedEncodingException {
		for (File file : dir.listFiles()) {
			if (!file.getName().endsWith(".gz")) { continue; }
			System.err.println("Parsing file: "+file);
			
			GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file));
			LineIterator iterator = new LineIterator(new InputStreamReader(inputStream, "UTF-8"));
			while (iterator.hasNext()) {
				String line = iterator.nextLine();
				if (line.contains(QUERY_PREFIX) && !line.contains("query=DATA_PROVIDER") && !line.contains("qf=DATA_PROVIDER") && !line.contains("bot") && !line.contains("slurp") && !line.contains("spider")) {
					int start = line.indexOf(QUERY_PREFIX),
					    end = line.indexOf("\"", start),
					    end2 = line.indexOf("&", start);
					if (end2 < end && end2 > 0) { end = end2; }
					if (end < 0) { end = line.length(); }
					try {
						String query = URLDecoder.decode(line.substring(start+QUERY_PREFIX.length(), end), "UTF-8");
						if (!query.contains(":")) {
							Query parsedQuery = queryParser.parse(query);
							if (parsedQuery instanceof BooleanQuery) {
								List<BooleanClause> clauses = ((BooleanQuery)parsedQuery).clauses();
								if (clauses != null) {
									List<String> queryTerms = new ArrayList<String>();
									boolean onlyTermQueries = true;
									for (BooleanClause clause : clauses) {
										if (!(clause.getQuery() instanceof TermQuery)) {
											// there is at lease a single non term query
											onlyTermQueries = false;
											break;
										} else {
											TermQuery termQuery = (TermQuery)clause.getQuery();
											if (termQuery.getTerm().field().equals(DEFAULT_FIELD)) {
												queryTerms.add(termQuery.getTerm().text());
											}
										}
									}
									
									if (onlyTermQueries && queryTerms.size() == 2) {
										StringBuilder builder = new StringBuilder();
										for (String e : new TreeSet<String>(queryTerms)) {
											if (builder.length() > 0) { builder.append('\t'); }
											builder.append(e);
										}
										// queryTerms.stream().map( (a, b) -> {b.append(a)});
										String normalisedQuery = builder.toString();
										if (uniqueQueryCollector.add(normalisedQuery)) {
											StringBuilder b = new StringBuilder();
											for (String e : queryTerms) {
												if (b.length() > 0) { b.append('\t'); }
												b.append(e);
											}
											String queryInNaturalSequence = b.toString();
											writer.println(queryInNaturalSequence);
											System.out.println(queryInNaturalSequence);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			iterator.close();
			inputStream.close();
		}
	}
}
