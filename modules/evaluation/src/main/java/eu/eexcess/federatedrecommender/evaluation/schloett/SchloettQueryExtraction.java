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
package eu.eexcess.federatedrecommender.evaluation.schloett;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.HighFreqTerms.DocFreqComparator;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

import com.google.gson.JsonSyntaxException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.federatedrecommender.evaluation.csv.EvaluationQueryList;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;
import eu.eexcess.federatedrecommender.evaluation.schloett.dataformats.SchloettHistory;
import eu.eexcess.federatedrecommender.evaluation.schloett.dataformats.SchloettQuery;
import eu.eexcess.federatedrecommender.evaluation.schloett.dataformats.SchloettQueryFormat;

/**
 * 
 * @author hziak
 *
 */
public class SchloettQueryExtraction {
    private static final Logger LOGGER = Logger.getLogger(SchloettQuerySelection.class.getName());
    private static final MaxentTagger TAGGER = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    static String[] blackListTerms = { "site", "item", "view", "use", "text", "talk", "use", "polish", "part", "dpa", "page", "type", "mode", "list", "help", "view", "talk",
            "shop", "page", "menu", "log", "link", "jump", "interaction", "file", "year", "ways", "way", "unit", "t", "der", "website", "von", "pdf", "zu", "yd", "w", "ul", "u2",
            "t", "sum", "son", "pm", "pg", "nl", "ne", "n", "mi", "m", "km", "im", "ich", "ft", "fdj", "economy", "dpa", "dm", "which", "using", "about", "where", "under",
            "succeeded", "sharealike", "retrieved", "firefox", "title", "search", "print", "policy", "links", "information", "contact", "русский", "wikipedia", "wikimedia",
            "wikidata", "views", "version", "variants", "upload", "trademark", "tools", "terms", "references", "privacy", "portal", "people", "pages", "organization", "order",
            "oldid", "norsk", "nederlands", "navigation", "namespaces", "names", "image", "subject", "online", "media", "source", "internet", "years", "support", "project",
            "years", "support", "description", "images", "events", "email", "context", "comments", "comment", "collection", "belief", "português", "polski", "cm", "bar", "web",
            "srpskohrvatski", "srpski", "nynorsk", "magyar", "indonesia", "hrvatski", "galego", "euskara", "esperanto", "encyclopedia", "eesti", "dansk", "commons", "bosanski",
            "articles", "zazaki", "time", "themes", "deutsch", "spanisch", "sie", "leo", "englisch", "ein", "chinesisch", "zusatzinformationen", "zur", "zune", "zuerst", "wort",
            "um", "trainer", "suchanfragen", "sehen", "neueste", "letzten", "klicken", "ipod", "iphone", "ipad", "installieren", "impressumwebseite", "italiano", "wikibooks",
            "srpskohrvatski", "srpski", "nynorsk", "melayu", "x6", "x4", "x2", "x18", "svenska", "suomi", "subjects", "share", "ru", "s", "polnisch", "letzten", "italienisch",
            "klicken", "impressumwebsei", "russisch", "portugiesisch", "suchanfragen", "magyar", "languages", "italiano", "indonesia", "hrvatski", "galego", "euskara",
            "esperanto", "encyclopedia", "eesti", "dansk", "commons", "bosanski", "twitter", "term", "publisher", "websites", };
    static List<String> blackList;

    public SchloettQueryExtraction() {
        blackList = Arrays.asList(blackListTerms);
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws JsonGenerationException {
        SchloettQueryExtraction extraction = new SchloettQueryExtraction();
        File folder = new File("/home/hziak/Datasets/EEXCESS/schloett-datacollection-785deb288e36/");
        File[] listOfFiles = folder.listFiles();
        List<File> files = getQueryFile(listOfFiles);

        List<LogPair> querys = new ArrayList<LogPair>();
        try {
            querys = extraction.parseQueryiesFile(files);

        } catch (JsonSyntaxException | IOException e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        EvaluationQueryList evalQueries = new EvaluationQueryList();
        for (LogPair logPair : querys) {
            if (logPair.getQuery().getMap() == null)
                LOGGER.log(Level.WARNING, "is null");

            else
                for (Entry<String, LinkedHashMap<String, Object>> schloettQueryFormat1 : logPair.getQuery().getMap().entrySet()) {

                    if (schloettQueryFormat1.getValue().get("task_name").toString().endsWith(".en")) {

                        LOGGER.log(Level.WARNING, "query: " + schloettQueryFormat1.getValue().get("query").toString());
                        List<Interest> keyword = getKeyWordsFromHistoryLinks(logPair.history.getMap(), schloettQueryFormat1.getValue().get("task_id"));

                        evalQueries.getQueries().add(new EvaluationQuery(schloettQueryFormat1.getValue().get("query").toString(), "TODO: decription", keyword));

                    }
                }

        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(folder.getCanonicalFile() + "/queriesEn-final.json");
            mapper.defaultPrettyPrintingWriter().writeValue(file, evalQueries);
            LOGGER.log(Level.INFO, "Writing to file:" + file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "", e);
        }

    }

    private static List<Interest> getKeyWordsFromHistoryLinks(HashMap<String, LinkedHashMap<String, Object>> hashMap, Object taskId) {

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();

        IndexWriter writer = null;

        if (hashMap != null)
            for (String keyset : hashMap.keySet()) {
                LinkedHashMap<String, Object> linkedHashMap = hashMap.get(keyset);
                if (linkedHashMap.get("task_id").equals(taskId))
                    if (linkedHashMap != null) {
                        Object urlObject = linkedHashMap.get("url");
                        if (urlObject != null)
                            if (!urlObject.toString().contains("http://de.wikipedia.org/wiki")) {

                                URL url = null;
                                IndexReader reader = null;
                                try {
                                    reader = DirectoryReader.open(dir);
                                } catch (IOException e4) {
                                    LOGGER.log(Level.WARNING, "", e4);
                                }
                                IndexSearcher searcher = null;
                                if (reader != null)
                                    searcher = new IndexSearcher(reader);
                                TopDocs docs = null;
                                if (searcher != null) {

                                    try {
                                        docs = searcher.search(new TermQuery(new Term("url", urlObject.toString())), 1);
                                    } catch (IOException e4) {
                                        LOGGER.log(Level.WARNING, "", e4);
                                    }
                                }
                                if (docs != null && docs.totalHits > 0) {
                                    LOGGER.log(Level.INFO, "docs where null or docs.totalHits zero");
                                } else {

                                    try {
                                        url = new URL(urlObject.toString());
                                    } catch (MalformedURLException e3) {
                                        LOGGER.log(Level.WARNING, "", e3);
                                    }

                                    try {
                                        reader.close();
                                    } catch (Exception e3) {
                                        LOGGER.log(Level.WARNING, "", e3);
                                    }
                                    InputStream input = null;
                                    if (url != null) {
                                        try {

                                            input = url.openStream();
                                        } catch (IOException e2) {
                                            LOGGER.log(Level.WARNING, "", e2);
                                        }
                                        if (input != null) {
                                            LinkContentHandler linkHandler = new LinkContentHandler();
                                            BodyContentHandler textHandler = new BodyContentHandler(10 * 1024 * 1024);
                                            ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
                                            TeeContentHandler teeHandler = new TeeContentHandler(linkHandler, textHandler, toHTMLHandler);
                                            Metadata metadata = new Metadata();
                                            ParseContext parseContext = new ParseContext();
                                            HtmlParser parser = new HtmlParser();

                                            try {
                                                parser.parse(input, teeHandler, metadata, parseContext);
                                            } catch (IOException | SAXException | TikaException e1) {

                                                LOGGER.log(Level.WARNING, urlObject.toString(), e1);

                                            }
                                            String string = textHandler.toString();
                                            String docString = " ";

                                            String tagged = TAGGER.tagString(string.toLowerCase());
                                            Pattern pattern = Pattern.compile("\\s\\w+(_NN|_NNS)");
                                            Matcher matcher = pattern.matcher(tagged);
                                            while (matcher.find()) {
                                                if (!blackList.contains(matcher.group().replaceAll("_NN|_NNS", "")))
                                                    docString += matcher.group().replaceAll("_NN|_NNS", " ") + " ";
                                            }

                                            // System.out.println("#######");
                                            // System.out.println(docString);
                                            // for (String string2 :
                                            // docString.split("\\s")) {
                                            // if(string2.length()>1)
                                            // System.out
                                            // .print("\""+string2+"\",");
                                            // }
                                            // System.out.println("#######");
                                            Document doc = new Document();

                                            doc.add(new TextField("content", docString, Store.YES));
                                            doc.add(new StringField("url", urlObject.toString(), Store.YES));

                                            try {
                                                IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
                                                writer = new IndexWriter(dir, config);
                                                writer.addDocument(doc);
                                                writer.close();
                                                input.close();
                                            } catch (IOException e) {
                                                LOGGER.log(Level.WARNING, "", e);
                                            }

                                        }
                                    }
                                }
                            }

                    }
            }

        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(dir);
        } catch (Exception e1) {
            LOGGER.log(Level.WARNING, "", e1);
        }
        TermStats[] tStats = null;
        if (reader != null)
            try {
                tStats = HighFreqTerms.getHighFreqTerms(reader, 30, "content", new DocFreqComparator());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "", e);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "", e);
                }
            }
        List<Interest> keywordList = new ArrayList<Interest>();
        LOGGER.log(Level.INFO, "Extraction: ");

        if (tStats != null) {
            for (TermStats termStats : tStats) {
                String utf8String = termStats.termtext.utf8ToString();
                if (!blackList.contains(utf8String.toLowerCase())) {

                    LOGGER.log(Level.INFO, "\"" + utf8String.toLowerCase() + "\",");

                    keywordList.add(new Interest(utf8String.toLowerCase()));
                    // System.out.println(utf8String.toLowerCase() + " docFreq "
                    // + termStats.docFreq + " TermFreq "
                    // + termStats.totalTermFreq + " "+tagged);
                }

            }
        }

        return keywordList;
    }

    @SuppressWarnings("unchecked")
    private List<LogPair> parseQueryiesFile(List<File> files) throws IOException {
        FileReader freader;
        List<LogPair> queries = new ArrayList<LogPair>();

        for (File file : files) {
            try {
                freader = new FileReader(file);
                BufferedReader br = new BufferedReader(freader);

                ObjectMapper mapper = new ObjectMapper();

                HashMap<String, LinkedHashMap<String, Object>> format = mapper.readValue(file, new HashMap<String, SchloettQuery>().getClass());
                HashMap<String, LinkedHashMap<String, Object>> history = mapper.readValue(new File(file.getParent() + "/history.json"),
                        new HashMap<String, SchloettHistory>().getClass());

                queries.add(new LogPair(new SchloettQueryFormat(format), new SchloettQueryFormat(history)));
                br.close();
                freader.close();

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "", e);
            }

        }
        return queries;
    }

    private static List<File> getQueryFile(File[] listOfFiles) {
        List<File> fileList = new ArrayList<File>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().contains("queries.json"))
                    fileList.add(file);
            } else if (file.isDirectory()) {
                fileList.addAll(getQueryFile(file.listFiles()));
            }
        }
        return fileList;
    }

    private class LogPair {
        private SchloettQueryFormat query;
        private SchloettQueryFormat history;

        public LogPair(SchloettQueryFormat schloettQueryFormat, SchloettQueryFormat schloettQueryFormat2) {
            this.query = schloettQueryFormat;
            this.history = schloettQueryFormat2;
        }

        public SchloettQueryFormat getQuery() {
            return query;
        }

        @SuppressWarnings("unused")
        public SchloettQueryFormat getHistory() {
            return history;
        }

    }

}
