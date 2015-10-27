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

package eu.eexcess.federatedrecommender.domaindetection.probing;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;

import edu.stanford.nlp.util.StringUtils;
import eu.eexcess.federatedrecommender.config.Settings;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

public class RandomWordsPerformanceTest {

    static class Stats {
        public int shouldNumWords = 0;
        public int numWords = 0;
        public Set<String> words = null;
        public List<Long> durationDrawWordsMs = new LinkedList<Long>();
        public List<Integer> duplicateConflictCount = new LinkedList<Integer>();
        public Long durationTotal = null;
        public int totalTries = 0;
    }

    private static class Arguments {
        public int numWords = 0;
        boolean noDuplicates = true;
        public int numRuns = 0;

        public Arguments(int numWords, int numRuns, boolean noDuplicates) {
            this.numWords = numWords;
            this.noDuplicates = noDuplicates;
            this.numRuns = numRuns;
        }
    }

    /**
     * to expose calls to contains ...
     */
    private static class TestableHashSet<E> extends HashSet<E> {
        private static final long serialVersionUID = -8498822740840730885L;
        public int containsTrueCount = 0;

        @Override
        public boolean contains(Object o) {

            if (super.contains(o)) {
                containsTrueCount++;
                return true;
            }
            return false;
        }
    }

    private static final String TEST_OUTPUT_CSV = System.getProperty("java.io.tmpdir") + "/wn-domain-performance-test-results.csv";

    private static TestableHashSet<String> wordsToIgnore = null;
    private static Set<String> dummyWordsToIgnore = new HashSet<String>(0);

    private Map<Integer, List<Stats>> runTests() {

        long startTimestamp = System.currentTimeMillis();

        List<Arguments> testSettings = new LinkedList<Arguments>();

        // testSettings.add(new Arguments(0, 20, true));
        // testSettings.add(new Arguments(3, 20, true));
        // testSettings.add(new Arguments(5, 20, true));
        // testSettings.add(new Arguments(10, 20, true));
        // testSettings.add(new Arguments(30, 20, true));
        // testSettings.add(new Arguments(50, 20, true));
        // testSettings.add(new Arguments(100, 10, true));
        // testSettings.add(new Arguments(200, 10, true));
        // testSettings.add(new Arguments(250, 10, true));
        // testSettings.add(new Arguments(300, 5, true));
        // testSettings.add(new Arguments(350, 3, true));

        testSettings.add(new Arguments(1000, 10, true));
        testSettings.add(new Arguments(3000, 5, true));
        testSettings.add(new Arguments(10000, 5, true));

        Map<Integer, List<Stats>> results = new HashMap<Integer, List<RandomWordsPerformanceTest.Stats>>();
        for (Arguments args : testSettings) {
            results.put(args.numWords, generateRandomWords(args.numWords, args.noDuplicates, args.numRuns));
        }

        System.out.println("total duration " + (System.currentTimeMillis() - startTimestamp) + "[ms]");
        return results;
    }

    private List<Stats> generateRandomWords(int numWords, boolean noDuplicates, int numRuns) {
        System.out.println("numWords [" + numWords + "] numRuns [" + numRuns + "] noDuplicates [" + noDuplicates + "]");
        List<Stats> results = new LinkedList<Stats>();
        int run = 0;

        while (run < numRuns) {

            run++;
            wordsToIgnore = new TestableHashSet<String>();
            Stats s = new Stats();
            s.shouldNumWords = numWords;

            int progressPercent = 100 * (run * 100) / (numRuns * 100);
            if (progressPercent % 10 == 0) {
                System.out.println("numWords [" + numWords + "] numRuns [" + numRuns + "] noDuplicates [" + noDuplicates + "]");
                System.out.println("run: " + progressPercent + "%");
            }

            try {
                s.durationTotal = System.currentTimeMillis();
                DomainDetector domainDetector = new WordnetDomainsDetector(new File(Settings.WordNet.Path_2_0), new File(Settings.WordnetDomains.Path), true);
                System.out.println("words: ");
                while (wordsToIgnore.size() < numWords) {
                    s.totalTries++;
                    long startTimestamp = System.currentTimeMillis();
                    if (noDuplicates) {
                        wordsToIgnore.add(domainDetector.drawRandomAmbiguousWord(wordsToIgnore));
                    } else {
                        wordsToIgnore.add(domainDetector.drawRandomAmbiguousWord(dummyWordsToIgnore));
                    }
                    s.durationDrawWordsMs.add(System.currentTimeMillis() - startTimestamp);

                    s.duplicateConflictCount.add(wordsToIgnore.containsTrueCount);
                    wordsToIgnore.containsTrueCount = 0;

                    int progressPercent2 = 100 * (wordsToIgnore.size() * 100) / (numWords * 100);
                    if (progressPercent2 % 10 == 0) {
                        System.out.println(progressPercent2 + "% (" + wordsToIgnore.size() + ") of " + numWords + " in "
                                + (System.currentTimeMillis() - s.durationTotal) + " [ms] ");
                    }
                }
                s.durationTotal = System.currentTimeMillis() - s.durationTotal;
            } catch (DomainDetectorException e) {
                e.printStackTrace();
                assertTrue(false);
            }
            s.numWords = wordsToIgnore.size();
            s.words = wordsToIgnore;
            wordsToIgnore = null;
            results.add(s);
            System.out.println();
        }
        return results;
    }

    /**
     * converts restults to set size vs duration it took to draw that set
     * 
     * @param results
     * @return
     */

    private XYDataset createRandomWordSetVsDurationDataset(Map<Integer, List<Stats>> results) {
        XYSeriesCollection result = new XYSeriesCollection();

        for (Map.Entry<Integer, List<Stats>> resultSet : results.entrySet()) {

            XYSeries series = new XYSeries(resultSet.getKey() + "-words");

            for (Stats s : resultSet.getValue()) {
                series.add(s.numWords, s.durationTotal);
            }
            result.addSeries(series);
        }
        return result;
    }

    /**
     * convert results word positions vs duration it took to draw that random
     * word to draw a random ambiguous word at that position
     * 
     * @param testResults
     * @return
     */
    private XYDataset createWordPositionVsDurationDataset(Map<Integer, List<Stats>> testResults) {

        XYSeriesCollection result = new XYSeriesCollection();
        for (Map.Entry<Integer, List<Stats>> resultSet : testResults.entrySet()) {

            XYSeries series = new XYSeries(resultSet.getKey() + "-words");

            for (Stats s : resultSet.getValue()) {
                int wordPosition = 1;
                for (Long duration : s.durationDrawWordsMs) {
                    series.add(wordPosition, duration);
                    wordPosition++;
                }
            }
            result.addSeries(series);
        }
        return result;
    }

    /**
     * converts results to word position vs retry attempts
     * {@link WordnetDomainsDetector#drawRandomAmbiguousWord(Set)} took to draw
     * a new word
     * 
     * @param results
     * @return
     */
    private XYDataset createWordPositionVsRetryDataset(Map<Integer, List<Stats>> results) {

        XYSeriesCollection result = new XYSeriesCollection();

        for (Map.Entry<Integer, List<Stats>> resultSet : results.entrySet()) {

            XYSeries series = new XYSeries(resultSet.getKey() + "-words");

            for (Stats s : resultSet.getValue()) {
                int wordPosition = 1;
                for (Integer retry : s.duplicateConflictCount) {
                    series.add(wordPosition, retry);
                    wordPosition++;
                }
            }
            result.addSeries(series);
        }
        return result;
    }

    public void plotFromTest() {
        Map<Integer, List<Stats>> testResults = runTests();
        resultsToCsv(testResults, new File(TEST_OUTPUT_CSV));
        plotAllPlots(testResults);
    }

    public void plotFromCsv(String filename) {
        plotAllPlots(resultsFromCsv(new File(filename)));
    }

    private void plotAllPlots(Map<Integer, List<Stats>> results) {
        scatterPlot(createRandomWordSetVsDurationDataset(results), "drawing random words set size vs. duration", "words [#]", "duration [ms]", "1st chart");
        scatterPlot(createWordPositionVsDurationDataset(results), "drawing random words: word position vs. duration", "position of word", "duration [ms]",
                "2nd chart");
        scatterPlot(createWordPositionVsRetryDataset(results), "drawing random words: word position vs. word duplicate", "word position", "number of retries",
                "3rd chart");
        scatterPlot(createWordPositionVsDurationVsDuplicateDataset(results), "drawing random words: word position vs. avg. duplicate/avg.duration",
                "word position", "avg. duplicate [#] / avg. duration [ms]", "4th chart");
    }

    /**
     * converts results to a word position vs. average duration and word
     * position vs. average duplicate recall
     * 
     * @param results
     * @return
     */
    private XYDataset createWordPositionVsDurationVsDuplicateDataset(Map<Integer, List<Stats>> results) {

        Map<Integer, List<Integer>> positionToDuplicate = new HashMap<Integer, List<Integer>>();
        Map<Integer, List<Long>> positionToDuration = new HashMap<Integer, List<Long>>();

        for (Map.Entry<Integer, List<Stats>> result : results.entrySet()) {
            for (Stats stat : result.getValue()) {

                // get all recall conflicts
                int positionOfWord = 1;
                for (Integer recalls : stat.duplicateConflictCount) {
                    List<Integer> dupRecalls = positionToDuplicate.get(positionOfWord);
                    if (null == dupRecalls) {
                        dupRecalls = new LinkedList<Integer>();
                        positionToDuplicate.put(positionOfWord, dupRecalls);
                    }

                    dupRecalls.add(recalls);
                    positionOfWord++;
                }

                // get all durations
                positionOfWord = 1;
                for (Long duration : stat.durationDrawWordsMs) {
                    List<Long> durations = positionToDuration.get(positionOfWord);
                    if (null == durations) {
                        durations = new LinkedList<Long>();
                        positionToDuration.put(positionOfWord, durations);
                    }

                    durations.add(duration);
                    positionOfWord++;
                }

            }
        }

        // calculate average
        Map<Integer, Double> positionToDurationAverage = new HashMap<Integer, Double>();
        for (Map.Entry<Integer, List<Long>> entry : positionToDuration.entrySet()) {

            double sum = 0.0;
            for (Long value : entry.getValue()) {
                sum += value;
            }
            positionToDurationAverage.put(entry.getKey(), sum / (double) entry.getValue().size());
        }

        Map<Integer, Double> positionToRecallAverage = new HashMap<Integer, Double>();
        for (Map.Entry<Integer, List<Integer>> entry : positionToDuplicate.entrySet()) {

            double sum = 0.0;
            for (Integer value : entry.getValue()) {
                sum += value;
            }
            positionToRecallAverage.put(entry.getKey(), sum / (double) entry.getValue().size());
        }

        // create data set
        XYSeriesCollection result = new XYSeriesCollection();

        XYSeries series = new XYSeries("duplicate recalls");
        for (Map.Entry<Integer, Double> entry : positionToRecallAverage.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }
        result.addSeries(series);

        // XYSeries
        series = new XYSeries("duration");
        for (Map.Entry<Integer, Double> entry : positionToDurationAverage.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }
        result.addSeries(series);

        return result;
    }

    private void scatterPlot(XYDataset dataSet, String title, String xLabel, String yLabel, String chartName) {
        JFreeChart chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataSet, PlotOrientation.VERTICAL, true, // include
                // legend
                true, // tooltips
                true // urls
        );
        ChartFrame frame = new ChartFrame(chartName, chart);
        frame.pack();
        frame.setVisible(true);
    }

    private void resultsToCsv(Map<Integer, List<Stats>> testResults, File file) {

        if (file.exists()) {
            file.delete();
        }

        Appendable out;
        try {
            out = new FileWriter(file);
            try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("shouldNumWordsInt", "numWordsInt", "totalDrawRandomWordTriesInt", "duplicateCountListInt",
                    "durationTotalMsLong", "durationMsListLong", "wordListString").print(out)) {
                List<String> record = new LinkedList<String>();

                for (List<Stats> run : testResults.values()) {
                    for (Stats stats : run) {
                        record.add(Integer.toString(stats.shouldNumWords));
                        record.add(Integer.toString(stats.numWords));
                        record.add(Integer.toString(stats.totalTries));
                        record.add(StringUtils.join(stats.duplicateConflictCount, ","));
                        record.add(Long.toString(stats.durationTotal));
                        record.add(StringUtils.join(stats.durationDrawWordsMs, ","));
                        record.add(StringUtils.join(stats.words, ","));
                        printer.printRecord(record);
                        record.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {

            e1.printStackTrace();
        }

    }

    static Map<Integer, List<Stats>> resultsFromCsv(File file) {

        Map<Integer, List<Stats>> testResults = new HashMap<Integer, List<Stats>>();

        CSVParser parser;
        try {
            parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.RFC4180.withHeader());

            for (CSVRecord csvRecord : parser) {

                Integer testRunId = new Integer(csvRecord.get("shouldNumWordsInt"));
                List<Stats> testRuns = testResults.get(testRunId);
                if (null == testRuns) {
                    testRuns = new LinkedList<Stats>();
                    testResults.put(testRunId, testRuns);
                }

                Stats stats = new Stats();
                stats.shouldNumWords = new Integer(csvRecord.get("shouldNumWordsInt"));
                stats.numWords = new Integer(csvRecord.get("numWordsInt"));
                stats.totalTries = new Integer(csvRecord.get("totalDrawRandomWordTriesInt"));
                stats.duplicateConflictCount = new LinkedList<Integer>();
                for (String value : StringUtils.split(csvRecord.get("duplicateCountListInt"), ",")) {
                    if (value.length() > 0) {
                        stats.duplicateConflictCount.add(new Integer(value));
                    }
                }
                stats.durationTotal = new Long(csvRecord.get("durationTotalMsLong"));
                stats.durationDrawWordsMs = new LinkedList<Long>();
                for (String value : StringUtils.split(csvRecord.get("durationMsListLong"), ",")) {
                    if (value.length() > 0) {
                        stats.durationDrawWordsMs.add(new Long(value));
                    }
                }
                stats.words = new HashSet<String>();
                stats.words.addAll(StringUtils.split(csvRecord.get("wordListString"), ","));

                testRuns.add(stats);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testResults;
    }

    @Test
    public void WordnetDomainsDetector_drawRandmomwords_untilExceptonOccurs() {

        int numWords = 10 ^ 6;
        Settings.isWordNet20ResourceAvailable();
        Settings.isWordNet30ResourceAvailable();
        File wordnetDir = new File(Settings.WordNet.Path_3_0);

        try {
            Dictionary dictionary = Dictionary.getFileBackedInstance(wordnetDir.getPath());

            while (numWords-- > 0) {

                IndexWord randomIndexWord;
                try {
                    randomIndexWord = dictionary.getRandomIndexWord(POS.NOUN);
                    randomIndexWord.equals(""); // expect NPE
                } catch (NullPointerException e) {
                    System.err.println("failed to draw random word => q.e.d.!");
                    e.printStackTrace();
                    continue;
                }
            }

        } catch (JWNLException e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    public static void main(String[] args) {
        RandomWordsPerformanceTest performanceTestRunner = new RandomWordsPerformanceTest();
        performanceTestRunner.plotFromTest();

        // RandomWordsPerformanceTest performanceTestPlotter = new
        // RandomWordsPerformanceTest();
        // performanceTestPlotter.plotFromCsv("/tmp/wn-domain-performance-test-results.csv");
    }
}
