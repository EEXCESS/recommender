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

package eu.eexcess.diversityasurement.wikipedia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;

import eu.eexcess.logger.PianoLogger;

public class RDFCategoryExtractor {

    public static class Statistics {

        Statistics() {
        }

        Statistics(Statistics src) {
            this.linesConsidered = src.linesConsidered;
            this.linesSkipped = src.linesSkipped;
            this.linesTotal = src.linesTotal;
            this.linesInFile = src.linesInFile;
            this.startTimeStamp = src.startTimeStamp;
            this.endTimeStamp = src.endTimeStamp;
        }

        public long linesInFile = 0;
        public long linesTotal = 0;
        public long linesSkipped = 0;
        public long linesConsidered = 0;
        public long startTimeStamp;
        public long endTimeStamp;

        @Override
        public String toString() {
            return "inflated graph from rdf int [" + (endTimeStamp - startTimeStamp) + "]ms lines: considered [" + linesConsidered + "] skipped [" + linesSkipped
                    + "] seen total [" + linesTotal + "] total in file[" + linesInFile + "]";
        }
    }

    private static class ParentChildCategoryGlue {
        public String parent;
        public String child;
    }

    private Logger logger = PianoLogger.getLogger(RDFCategoryExtractor.class);

    private File categoryListing;
    private Statistics statistics = new Statistics();
    Pattern categoryRDFPattern = Pattern
            .compile("<http://dbpedia.org/resource/Category:(\\w+)>\\s*<http://www.w3.org/2004/02/skos/core#broader>\\s*<http://dbpedia.org/resource/Category:(\\w+)>");
    private CategoryTupleCollector collector;

    // private int printStatsEvery = 200000;

    public RDFCategoryExtractor(File filePath, CategoryTupleCollector callback) {
        categoryListing = filePath;
        collector = callback;
    }

    public void extract() throws IOException {
        LineIterator categoryEntryIterator = new LineIterator(new FileReader(categoryListing));
        statistics.startTimeStamp = System.currentTimeMillis();
        statistics.linesInFile = getTotalNumberOfLines(categoryListing.getAbsoluteFile());

        while (categoryEntryIterator.hasNext()) {
            statistics.linesTotal++;
            ParentChildCategoryGlue tuple = parseRelatedCategoryTuple(categoryEntryIterator.nextLine());
            if (null != tuple) {
                statistics.linesConsidered++;
                try {
                    collector.takeTuple(tuple.parent, tuple.child);
                } catch (Exception e) {
                }
            } else {
                statistics.linesSkipped++;
            }
            // if (0 == (statistics.linesTotal % printStatsEvery)) {
            // logStatistics();
            // }
        }
        categoryEntryIterator.close();
        statistics.endTimeStamp = System.currentTimeMillis();
        logStatistics();
    }

    private void logStatistics() {
        logger.info("processed categories[" + statistics.linesConsidered + "] out of lines in file [" + statistics.linesInFile + "], skipped lines[" + statistics.linesSkipped
                + "], total lines so far[" + statistics.linesTotal + "]  duration so far[" + ((System.currentTimeMillis() - statistics.startTimeStamp) / (1)) + "]");

    }

    private long getTotalNumberOfLines(File file) throws FileNotFoundException, IOException {
        long lines = 0;
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
        lineNumberReader.skip(Long.MAX_VALUE);
        lines = lineNumberReader.getLineNumber();
        lineNumberReader.close();
        return lines;
    }

    public Statistics getStatistics() {
        return new Statistics(statistics);
    }

    /**
     * Reads a tuple of related categories (parent, child) from an RDF formatted
     * string.
     * 
     * @param rdfString
     *            rdf string to parse i.e.
     *            <http://dbpedia.org/resource/Category:World_War_II>
     *            <http://www.w3.org/2004/02/skos/core#broader>
     *            <http://dbpedia.org/resource/Category:Wars_involving_Egypt> .
     * @return a tuple or null if string does not match the criteria
     *         {@link categoryRDFPattern}
     */
    private ParentChildCategoryGlue parseRelatedCategoryTuple(String rdfString) {
        Matcher matcher = categoryRDFPattern.matcher(rdfString);
        if (matcher.find()) {
            ParentChildCategoryGlue tuple = new ParentChildCategoryGlue();
            tuple.parent = matcher.group(2);
            tuple.child = matcher.group(1);
            return tuple;
        }
        return null;
    }
}
