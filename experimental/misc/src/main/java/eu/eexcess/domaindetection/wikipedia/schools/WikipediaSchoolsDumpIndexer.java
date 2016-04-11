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

package eu.eexcess.domaindetection.wikipedia.schools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.conditional.ITagNodeCondition;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import eu.eexcess.logger.PianoLogger;

/**
 * This class creates an Lucene index out of an Wikipedia for Schools
 * (http://www
 * .sos-schools.org/wikipedia-for-schools/downloads/wikipedia-schools-
 * 3.0.2.tar.gz) dump. <br>
 * <br>
 * Indexed documents have following fields where if
 * (document-is-sos-advertisement==true || document-subject==
 * {@value #NO_SUBJECT_FOUND_STRING}) indicate and advertisement document:<br>
 * document-is-sos-advertisement:false<br>
 * document-relative-path:wp/a/
 * Aftershock_Hits_Haiti_As_Agencies_Fear_For_Orphans.htm<br>
 * document-subject:{@value #NO_SUBJECT_FOUND_STRING}<br>
 * document-title: Aftershock hits Haiti as agencies fear for orphans<br>
 * paragraph-level:h1<br>
 * paragraph-position-in-document:1/1<br>
 * paragraph-text: text of the paragraph<br>
 * paragraph-title: heading of the paragraph<br>
 */
public class WikipediaSchoolsDumpIndexer extends IndexWriterRessource {

    private static final String LUCENE_FIELD_DOCUMENT_RELATIVE_PATH = "document-relative-path";

    private static final String LUCENE_FIELD_PARAGRAPH_POSITION_IN_DOCUMENT = "paragraph-position-in-document";

    private static final String LUCENE_FIELD_PARAGRAPH_TEXT = "paragraph-text";
    
    private static final String LUCENE_FIELD_PARAGRAPH_TEXT_BIGRAM = "paragraph-text-bigram";

    private static final String LUCENE_FIELD_PARAGRAPH_LEVEL = "paragraph-level";

    private static final String LUCENE_FIELD_PARAGRAPH_TITLE = "paragraph-title";

    private static final String LUCENE_FIELD_DOCUMENT_TITLE = "document-title";

    public static final String LUCENE_FIELD_DOCUMENT_SUBJECT = "document-subject";

    private static final String LUCENE_FIELD_DOCUMENT_IS_SOS_ADVERTISEMENT = "document-is-sos-advertisement";

    private static final long serialVersionUID = 4856619691781902215L;

    private static final Logger LOGGER = PianoLogger.getLogger(WikipediaSchoolsDumpIndexer.class.getName());

    /**
     * XPath to 1st h1-heading text
     */
    private static final String XPATH_FIRST_HEADING = "/xhtml:html/xhtml:body/xhtml:h1/text()";

    /**
     * directory of content sites relative to wikipedia for Schools dump root
     * directory
     */
    private static final String RELATIVE_WIKI_SITES_CONTENT_PATH = "wp";

    /**
     * Separator of domains used in filenames: each domain has it's own well
     * named site out of that the domain name can be extracted nicely.
     */
    private static final String DOMAIN_TREE_SITE_SEPARATOR = "\\.";

    public static final String NO_SUBJECT_FOUND_STRING = "no-subject-found";

    /**
     * html tags that match one of these attributes will be ignored
     */
    private Map<String, String> tagAttributeValueToNameFilter = new HashMap<String, String>();
    /**
     * html documents containing phrases in their titles as enumerated (lower
     * case) in this filed will be ignored.
     */
    private Set<String> documentTitleFilter = new HashSet<String>();
    /**
     * html tags enumerated in {@link #anchorTagTypes} are taken as separators
     * for paragraphs
     */
    private final Set<String> anchorTagTypes = new HashSet<String>();
    private CleanerProperties cleanerProperties = new CleanerProperties();

    /**
     * staticstics
     * 
     * @{
     */
    private int numberDocumentsCount = 0;
    private int numberParagraphsCount = 0;
    private int numberDocumentsWithoutDomainCount = 0;
    private int numberParagraphsWithoutDomainCount = 0;
    private Map<String, AtomicInteger> seenDomains = new HashMap<String, AtomicInteger>();

    /**
     * @}
     */

    public WikipediaSchoolsDumpIndexer(File outIndexPath, Version luceneVersion) throws IOException {
        super(outIndexPath);
        setLuceneVersion(luceneVersion);
        try {
            open();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to open index: trying to close", e);
            close();
            throw e;
        }

        tagAttributeValueToNameFilter.put("thumb tleft", "class");
        tagAttributeValueToNameFilter.put("thumb tright", "class");
        tagAttributeValueToNameFilter.put("infobox vcard", "class");
        tagAttributeValueToNameFilter.put("soslink", "class");
        tagAttributeValueToNameFilter.put("menu", "class");
        tagAttributeValueToNameFilter.put("sosheading", "class");
        tagAttributeValueToNameFilter.put("printfooter", "class");
        tagAttributeValueToNameFilter.put("sosebar", "id");
        tagAttributeValueToNameFilter.put("logo", "id");
        tagAttributeValueToNameFilter.put("siteSub", "id");

        documentTitleFilter.add("sos children: charity facts: our partners");
        documentTitleFilter.add("sos children");
        documentTitleFilter.add("the stiry of sos child sponsorship");
        documentTitleFilter.add("sos child who became");
        documentTitleFilter.add("sos pakistan prepare to send food");
        documentTitleFilter.add("sos school");
        documentTitleFilter.add("sos feeding centre");
        documentTitleFilter.add("sos social centre");
        documentTitleFilter.add("sos framework for action");
        documentTitleFilter.add("sos family strengthening");
        documentTitleFilter.add("sos nursery in");
        documentTitleFilter.add("sos nursery and school");
        documentTitleFilter.add("sos emergency appeal");
        documentTitleFilter.add("sos haiti emergency appeal");
        documentTitleFilter.add("sos emergency relief");
        documentTitleFilter.add("sos mothers");
        documentTitleFilter.add("news from the sos village");
        documentTitleFilter.add("east africa famine:");
        documentTitleFilter.add("haiti news: sos");
        documentTitleFilter.add("haiti news: life has changed for sos mother francoise");
        documentTitleFilter.add("haiti downloads: sos magazine special");
        documentTitleFilter.add("haiti orphan appeal");
        documentTitleFilter.add("haiti video special: orphaned children find a new home with sos");
        documentTitleFilter.add("former sos child returns to haiti");
        documentTitleFilter.add("sponsor an sos children");
        documentTitleFilter.add("sponsor a child in africa");
        documentTitleFilter.add("sponsor a child in asia");
        documentTitleFilter.add("sponsor a child in eastern europe");
        documentTitleFilter.add("sponsor a child in the americas");
        documentTitleFilter.add("sponsor a child in afrika");
        documentTitleFilter.add("sponsor africa with sos children");
        documentTitleFilter.add("partnership with sos children");
        documentTitleFilter.add("sos medical centre");
        documentTitleFilter.add("sos medical and social centre");
        documentTitleFilter.add("sos social and medical centre");
        documentTitleFilter.add("christmas with sos children");
        documentTitleFilter.add("contact sos children");
        documentTitleFilter.add("join team sos children");
        documentTitleFilter.add("partnership with sos chilren");

        anchorTagTypes.add("h1");
        anchorTagTypes.add("h2");

        cleanerProperties.setTranslateSpecialEntities(true);
        cleanerProperties.setTransResCharsToNCR(true);
        cleanerProperties.setOmitComments(true);
        cleanerProperties.setOmitCdataOutsideScriptAndStyle(true);
    }

    /**
     * The first command line argument specifies the root folder of an extracted
     * Wikipedia for Schools dump. The newly created Lucene index is stored to
     * the system's default temporary folder. The exact location is printed on
     * the command line after the process has finished.
     * 
     * @param args
     *            args[0]='/home/hugo/.../wikipedia-schools-3.0.2/'
     */
    public static void main(String[] args) {
        try {
            File tempDir = getNewEmptyTempDir("wikipedia-schools-paragraph-index-");
            buildIndex(tempDir, args);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to perform indexing: unable to crate folder", e);
        }
    }

    /**
     * creates a new empty temporary directory
     * 
     * @param dirPrefix
     * @return the path to the newly created directory
     */
    public static File getNewEmptyTempDir(String dirPrefix) throws IOException {
        try {
            File tempDir = File.createTempFile(dirPrefix, "");
            tempDir.delete();
            tempDir.mkdir();
            return tempDir;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to create directory [" + dirPrefix + "] ", e);
            throw e;
        }
    }

    /**
     * Collects recursively all files (absolute paths) starting from a root
     * folder considering file extensions as white-list and directories as
     * blacklist.
     * 
     * @param rootDir
     *            where to start recursive traversal from
     * @param fileExtensionWhiteList
     *            files ending with a string enumerated in the list will be
     *            considered
     * @param dirsBlackList
     *            directories (ending) with a string enumerated in that list
     *            will be ignored
     * @param matchedFiles
     *            all matching files
     * @throws IOException
     */
    public static void collectFilePaths(Path rootDir, Set<String> fileExtensionWhiteList, Set<String> dirsBlackList, List<Path> matchedFiles)
            throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir)) {
            for (Path entry : stream) {
                if (entry != null && !Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)) {
                    for (String whiteToken : fileExtensionWhiteList) {
                        Path entryFileName = entry.getFileName();
                        if (entryFileName != null && entryFileName.toString().endsWith(whiteToken)) {
                            matchedFiles.add(entry);
                        }
                    }
                } else {
                    for (String blackToken : dirsBlackList) {
                        if (!entry.endsWith(blackToken)) {
                            collectFilePaths(entry, fileExtensionWhiteList, dirsBlackList, matchedFiles);
                        }
                    }
                }
            }
        } catch (DirectoryIteratorException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Splits a given string of domains by {@value #DOMAIN_TREE_SITE_SEPARATOR}
     * separator to a list of strings but removes the last element (the file
     * extension - i.e. "htm").
     * 
     * @param domainPath
     *            string to be separated to domains
     * @return
     */
    public static List<String> stripDomains(String domainPath) {

        List<String> domainList = new LinkedList<String>(Arrays.asList(domainPath.toLowerCase().split(DOMAIN_TREE_SITE_SEPARATOR)));
        if (!domainList.isEmpty()) {
            Iterator<String> iterator = domainList.listIterator(domainList.size() - 1);
            iterator.next();
            iterator.remove();
        }
        return domainList;
    }

    private static void buildIndex(File tempFile, String[] args) {
        try (WikipediaSchoolsDumpIndexer indexer = new WikipediaSchoolsDumpIndexer(tempFile, Version.LATEST)) {
            long timestamp = System.currentTimeMillis();
            indexer.run(args);
            LOGGER.info("total duration [" + (System.currentTimeMillis() - timestamp) + "]ms");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error during indexing", e);
            return;
        }
    }

    private void run(String[] args) throws IOException {
        if (args.length > 0) {
            try {
                Set<String> dirsBlackListlackList = new HashSet<String>();
                dirsBlackListlackList.add("index");
                Set<String> fileExtensionWhiteList = new HashSet<String>();
                fileExtensionWhiteList.add(".html");
                fileExtensionWhiteList.add(".htm");

                List<Path> files = new ArrayList<Path>();

                collectFilePaths(FileSystems.getDefault().getPath(args[0] + RELATIVE_WIKI_SITES_CONTENT_PATH), fileExtensionWhiteList, dirsBlackListlackList,
                        files);
                String filePathAbsolutePrefix = args[0];

                for (Path file : files) {
                    numberDocumentsCount++;
                    String filePathString = file.toString();
                    Set<String> documentSubjects = parseDocumentSubjects(filePathString);
                    TagNode document = cleanDocument(filePathString);
                    List<? extends TagNode> documentAnchors = markAnchorsDistinct(document);
                    String documentTitle = StringEscapeUtils.unescapeHtml(removeWhitespaces(parseXpathPartFromHTML(filePathString, XPATH_FIRST_HEADING)));
                    List<DocumentParagraph> paragraphs = getParagraphsOfDocument(document, documentTitle, documentAnchors);
                    indexDocumentParagraphs(filePathString.replaceFirst(filePathAbsolutePrefix, ""), documentTitle, documentSubjects, paragraphs);

                    logNewlySeenDomains(documentSubjects, seenDomains);
                    if (documentSubjects.isEmpty()) {
                        numberDocumentsWithoutDomainCount++;
                    }
                }
                printStatistics();
                printHistogram(seenDomains);
            } catch (IOException e) {
                StringBuilder message = new StringBuilder("failed create index");
                if (args.length > 0) {
                    message.append("[" + args[0] + "]");
                }
                LOGGER.log(Level.SEVERE, message.toString(), e);
            }
        } else {
            throw new IOException("no source path given");
        }
    }

    private static Map<String, AtomicInteger> sortByValue(Map<String, AtomicInteger> unsortedMap) {

        List<Map.Entry<String, AtomicInteger>> entries = new ArrayList<Map.Entry<String, AtomicInteger>>(unsortedMap.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, AtomicInteger>>() {
            @Override
            public int compare(Map.Entry<String, AtomicInteger> entry1, Map.Entry<String, AtomicInteger> entry2) {
                return new Integer(entry2.getValue().intValue()).compareTo(new Integer(entry1.getValue().intValue()));
            }
        });

        Map<String, AtomicInteger> orderedMap = new LinkedHashMap<String, AtomicInteger>();
        for (Map.Entry<String, AtomicInteger> entry : entries) {
            orderedMap.put(entry.getKey(), entry.getValue());
        }
        return orderedMap;
    }

    private void printStatistics() {
        System.out.println("documents count .... [" + numberDocumentsCount + "]  [" + numberDocumentsWithoutDomainCount / (double) numberDocumentsCount
                + "]% without domains [" + numberDocumentsWithoutDomainCount + "]");
        System.out.println("paragraphs count ... [" + numberParagraphsCount + "] [" + numberParagraphsWithoutDomainCount / (double) numberParagraphsCount
                + "]% without domains [" + numberParagraphsWithoutDomainCount + "]");

    }

    public static void printHistogram(Map<String, AtomicInteger> seenDomains) {

        int maxCount = 0, minCount = Integer.MAX_VALUE;
        for (Map.Entry<String, AtomicInteger> entry : seenDomains.entrySet()) {
            int entryValue = entry.getValue().intValue();
            if (entryValue > maxCount) {
                maxCount = entryValue;
            }
            if (entryValue < minCount) {
                minCount = entryValue;
            }
        }

        Map<String, AtomicInteger> seenDomainsSortedByValue = sortByValue(seenDomains);

        System.out.println("seen domains ....... [" + seenDomains.size() + "] max count [" + maxCount + "] min count[" + minCount + "]");
        System.out.println("domain histogram:");

        maxCount++;
        for (Map.Entry<String, AtomicInteger> entry : seenDomainsSortedByValue.entrySet()) {
            for (int times = 0; times < (entry.getValue().intValue() / ((double) maxCount)) * 100; times++) {
                System.out.print(".");
            }
            System.out.println(" " + entry.getKey() + " [" + entry.getValue().intValue() + "]x");
        }
    }

    public static void logNewlySeenDomains(Set<String> documentSubjects, Map<String, AtomicInteger> seenDomains) {
        for (String domain : documentSubjects) {
            AtomicInteger seenDomainCount = seenDomains.get(domain);
            if (null == seenDomainCount) {
                seenDomains.put(domain, new AtomicInteger(1));
            } else {
                seenDomainCount.incrementAndGet();
            }
        }
    }

    private static String removeWhitespaces(String text) {
        return text.replaceAll("\\s+", " ").replaceAll("\\\\n", "");
    }

    /**
     * Slices the given document to pieces with anchors as cutting positions.
     * 
     * @param document
     *            input document
     * @param anchors
     *            usually headings i.e. h1, h2, ... hn
     * @return the resulting slices: a list of paragraphs
     */
    private List<DocumentParagraph> getParagraphsOfDocument(TagNode document, String documentTitle, List<? extends TagNode> anchors) {

        List<DocumentParagraph> result = new ArrayList<DocumentParagraph>();

        StringBuilder asPlaintext = new StringBuilder(removeWhitespaces(document.getText().toString()));

        ListIterator<? extends TagNode> iterator = anchors.listIterator(anchors.size());
        while (iterator.hasPrevious()) {
            TagNode anchor = iterator.previous();
            String paragraphTitle = removeWhitespaces(anchor.getText().toString());
            String paragraphLevel = anchor.getName();

            int paragraphStart = asPlaintext.lastIndexOf(paragraphTitle);
            String paragraphText = asPlaintext.substring(paragraphStart + paragraphTitle.length());
            asPlaintext.delete(paragraphStart, asPlaintext.length());
            DocumentParagraph paragraph = new DocumentParagraph(StringEscapeUtils.unescapeHtml(
                    paragraphTitle.replace(Long.toString(WikipediaSchoolsDumpIndexer.serialVersionUID), "")).trim(), paragraphLevel,
                    StringEscapeUtils.unescapeHtml(paragraphText.trim()));

            paragraph.isSosParagraph(isSosDocumentTitle(documentTitle, documentTitleFilter));

            result.add(paragraph);
        }
        return result;
    }

    /**
     * Inspect paragraph to decide whether it is an SOS Children's Village
     * advertisement or not.
     * 
     * @param paragraphText
     * @return true if the paragraph is detected to be an SOS Children's Village
     *         advertisement
     */
    private static boolean isSosDocumentTitle(String documentTitle, Set<String> documentTitleFilter) {

        for (String skipTitleToken : documentTitleFilter) {
            if (documentTitle.toLowerCase().contains(skipTitleToken)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes paragraphs to an index.
     * 
     * @param relativeFilePath
     *            relative file path of the
     * @param siteTitle
     * @param siteSubjects
     * @param paragraphs
     *            A paragraph is tuple of title and its following text below.
     *            Usually
     * @throws IOException
     */
    private void indexDocumentParagraphs(String relativeFilePath, String siteTitle, Set<String> siteSubjects, List<DocumentParagraph> paragraphs)
            throws IOException {
        LOGGER.info("indexing [" + relativeFilePath + "]");
        int paragraphPositionCounter = 0;
        for (DocumentParagraph paragraph : paragraphs) {
            numberParagraphsCount++;
            Document document = new Document();

            if (paragraph.isSosParagraph()) {
                document.add(new TextField(LUCENE_FIELD_DOCUMENT_IS_SOS_ADVERTISEMENT, "true", Field.Store.YES));
            } else {
                document.add(new TextField(LUCENE_FIELD_DOCUMENT_IS_SOS_ADVERTISEMENT, "false", Field.Store.YES));
                numberParagraphsWithoutDomainCount++;
            }

            document.add(new TextField(LUCENE_FIELD_DOCUMENT_RELATIVE_PATH, relativeFilePath, Field.Store.YES));

            if (siteSubjects.isEmpty() && !paragraph.isSosParagraph()) {
                document.add(new TextField(LUCENE_FIELD_DOCUMENT_SUBJECT, NO_SUBJECT_FOUND_STRING, Field.Store.YES));
            } else {
                for (String subject : siteSubjects) {
                    document.add(new TextField(LUCENE_FIELD_DOCUMENT_SUBJECT, subject, Field.Store.YES));
                }
            }

            document.add(new TextField(LUCENE_FIELD_DOCUMENT_TITLE, siteTitle, Field.Store.YES));

            document.add(new TextField(LUCENE_FIELD_PARAGRAPH_TITLE, paragraph.getTitle(), Field.Store.YES));
            document.add(new TextField(LUCENE_FIELD_PARAGRAPH_LEVEL, paragraph.getLevel(), Field.Store.YES));
            document.add(new TextField(LUCENE_FIELD_PARAGRAPH_TEXT, paragraph.getParagraph(), Field.Store.YES));
            document.add(new TextField(LUCENE_FIELD_PARAGRAPH_TEXT_BIGRAM, paragraph.getParagraph(), Field.Store.NO));

            int paragraphPosition = paragraphs.size() - paragraphPositionCounter;
            paragraphPositionCounter++;
            document.add(new TextField(LUCENE_FIELD_PARAGRAPH_POSITION_IN_DOCUMENT, paragraphPosition + "/" + paragraphs.size(), Field.Store.YES));

            outIndexWriter.addDocument(document);
        }
    }

    /**
     * Reads Wikipedia for Schools site subject(s) from a given htm-site path.
     * 
     * @param absoluteFilePath
     * @return a list of (lower case) subjects
     * @throws IOException
     */
    private static Set<String> parseDocumentSubjects(String absoluteFilePath) throws IOException {
        Set<String> documentSubjects = new HashSet<String>();

        String documentString = new String(Files.readAllBytes(new File(absoluteFilePath).toPath()), "utf-8");
        StringBuilder documentBuffer = new StringBuilder(documentString.toLowerCase());

        String startAnchor = "related subjects:";
//        String endAnchor = "</div>";
        String endAnchor = "</h3>";

        int startIndex = documentBuffer.indexOf(startAnchor);

        if (startIndex >= 0) {
        	documentBuffer.delete(0, startIndex + startAnchor.length());
        	documentBuffer.delete(documentBuffer.indexOf(endAnchor), documentBuffer.length());
        	Validate.isTrue(documentBuffer.length() > 10, "Too small document: "+documentBuffer.toString());
        	Validate.isTrue(documentBuffer.length() < 450, "Too long document: "+documentBuffer.toString());
        	
	        /**
	         * matches: '<a href="xxx">' where xxx can be found in group 1
	         */
	        String domainRegexp = "<a\\s+href=(\"[^\"]*\"|'[^']*'|[^'\">])*\\s*>";
	        Pattern pattern = Pattern.compile(domainRegexp);
	        java.util.regex.Matcher matcher = pattern.matcher(documentBuffer);
	
	        try {
	            while (matcher.find()) {
	                List<String> stripedDomains = stripDomains(matcher.group(1));
	                if (stripedDomains.size() > 0) {
						documentSubjects.add(stripedDomains.get(stripedDomains.size() - 1));
					}
	            }
	        } catch (NoSuchElementException e) {
	            throw new IOException("failed parsing document subject of document [" + absoluteFilePath + "]", e);
	        }
        } else {
        	System.err.println("No subjects found: "+absoluteFilePath);
        	
        }

        return documentSubjects;
    }

    /**
     * Fetches the part matching the xpath from an html document.
     *
     * @param absoluteFilPath
     *            path to html input file
     * @param xPathString
     *            xpath to the corresponding html-tag/-part
     * @return the matching part as html
     * @throws IOException
     *             on any tika or inpustream exception
     */
    private static String parseXpathPartFromHTML(String absoluteFilPath, String xPathString) throws IOException {
        XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);

        Matcher divContentMatcher = xhtmlParser.parse(xPathString);
        ContentHandler handler = new MatchingContentHandler(new ToXMLContentHandler(), divContentMatcher);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = new FileInputStream(absoluteFilPath)) {
            try {
                parser.parse(stream, handler, metadata);
            } catch (SAXException | TikaException e) {
                throw new IOException(e);
            }
            return handler.toString();
        }
    }

    /**
     * Cleans HTML using Tagsoup and removes tags from document as enumerated in
     * {@link #tagAttributeValueToNameFilter}.
     * 
     * @param absoluteFilePath
     *            to the html document
     * @return the cleaned document
     */
    private TagNode cleanDocument(String absoluteFilePath) {
        try (InputStream fileStream = new FileInputStream(absoluteFilePath)) {
            TagNode tagNode = new HtmlCleaner(cleanerProperties).clean(fileStream);

            for (Map.Entry<String, String> entry : tagAttributeValueToNameFilter.entrySet()) {

                for (TagNode tagToRemove : tagNode.getElementsByAttValue(entry.getValue(), entry.getKey(), true, false)) {
                    tagToRemove.removeFromTree();
                }
            }

            return tagNode;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to  clean document", e);
            return new TagNode("exceptional tag node");
        }
    }

    /**
     * Returns a {@link TagNode} list of headings as enumerated in
     * {@link #anchorTagTypes} in same order as they appear in the html
     * document. The content of these headings is permanently changed in the
     * TagNode argument: {@value #serialVersionUID} is prepended.
     * 
     * @param tagNode
     *            the root node
     * @return list of headings
     */
    private List<? extends TagNode> markAnchorsDistinct(TagNode tagNode) {
        ITagNodeCondition textAnchorCondition = new ITagNodeCondition() {
            @Override
            public boolean satisfy(TagNode tagNode) {
                for (String anchor : anchorTagTypes) {
                    if (tagNode.getName().equals(anchor)) {
                        return true;
                    }
                }
                return false;
            }
        };

        List<? extends TagNode> anchors = tagNode.getElementList(textAnchorCondition, true);

        for (TagNode anchor : anchors) {
            String text = anchor.getText().toString();
            ContentNode markedAnchor = new ContentNode(WikipediaSchoolsDumpIndexer.serialVersionUID + text);
            List<ContentNode> children = new ArrayList<ContentNode>();
            children.add(markedAnchor);
            anchor.removeAllChildren();
            anchor.setChildren(children);
        }

        return anchors;
    }
}
