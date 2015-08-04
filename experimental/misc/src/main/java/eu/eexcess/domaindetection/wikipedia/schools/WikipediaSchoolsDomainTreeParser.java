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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import eu.eexcess.logger.PianoLogger;
import eu.eexcess.sourceselection.redde.tree.BaseTreeNode;
import eu.eexcess.sourceselection.redde.tree.BaseTreeNode.NodeInspector;
import eu.eexcess.sourceselection.redde.tree.TreeNode;
import eu.eexcess.sourceselection.redde.tree.ValueTreeNode;

/**
 * This class parses the Wikipedia for Schools subjects and the their hierarchy.
 * The tree is visualized and also stored as Lucene index.
 * 
 * @author Raoul Rubien
 *
 */
public class WikipediaSchoolsDomainTreeParser extends IndexWriterRessource {

    /**
     * The node traverser creates a Lucene document for each node. Each document
     * contains the node name its childrens' names.
     * 
     * @author Raoul Rubien
     *
     */
    private static class DocumentGeneratingNodeInspector implements BaseTreeNode.NodeInspector<String> {

        private List<Document> documents = new ArrayList<Document>();

        @Override
        public void invoke(TreeNode<String> n) {
            Document document = new Document();
            document.add(new TextField(LUCENE_DOCUMENT_DOCUMENT_FIELD_NAME, n.getName(), Field.Store.YES));

            for (TreeNode<String> child : n.getChildren()) {
                document.add(new TextField(LUCENE_DOCUMENT_CHILD_FIELD_NAME, child.getName(), Field.Store.YES));
            }
            documents.add(document);
        }

        public List<Document> getDocuments() {
            return documents;
        }
    }

    private static final Logger LOGGER = PianoLogger.getLogger(WikipediaSchoolsDomainTreeParser.class);
    private static final String DOMAIN_TREE_ENTRYPOINT_DIRECTORY = "wp/index/";
    private static final String DOMAIN_TREE_SITE_PREFIX = "subject.";
    private static final String DOMAIN_TREE_SITE_SUFFIX = ".htm";
    private static final String ROOT_DOMAIN_NAME = "factotum";

    public static final String LUCENE_DOCUMENT_CHILD_FIELD_NAME = "child-subject";
    public static final String LUCENE_DOCUMENT_DOCUMENT_FIELD_NAME = "subject";

    public WikipediaSchoolsDomainTreeParser(File outIndexPath) {
        super(outIndexPath);
    }

    /**
     * The first command line argument specifies the root folder of an extracted
     * Wikipedia for Schools dump.
     * 
     * @param args
     *            args[0]='/home/hugo/.../wikipedia-schools-3.0.2/'
     */
    public static void main(String[] args) {
        try {
            File tempDir = WikipediaSchoolsDumpIndexer.getNewEmptyTempDir("wikipedia-schools-domain-index-");
            buildTree(tempDir, args);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to perform indexing: unable to crate folder", e);
        }
    }

    static ValueTreeNode<String> parseDomainFilesToTree(List<File> domainFiles) {
        ValueTreeNode<String> rootDomain = newNode(ROOT_DOMAIN_NAME);

        // expected domainFile name "subject.domain1.domain2.domainN.html"
        for (File domainFile : domainFiles) {
            List<String> domainPath = WikipediaSchoolsDumpIndexer.stripDomains(domainFile.getName());

            ValueTreeNode<String> lastVisited = rootDomain;
            Set<TreeNode<String>> collector = newNodeCollector();

            for (Iterator<String> domainIterator = domainPath.listIterator(); domainIterator.hasNext();) {
                ValueTreeNode<String> toBeFound = newNode(domainIterator.next());
                domainIterator.remove();
                ValueTreeNode.findFirstNode(toBeFound, lastVisited, collector);

                // find path connection point: skip all already seen domains
                if (!collector.isEmpty()) {
                    lastVisited = (ValueTreeNode<String>) collector.iterator().next();

                    // if domain is unseen ad it and all subsequent one
                } else {
                    lastVisited.addChild(toBeFound);
                    lastVisited = toBeFound;
                    // add all remaining domains
                    for (Iterator<String> unseenDomainsIterator = domainPath.iterator(); unseenDomainsIterator.hasNext();) {
                        ValueTreeNode<String> newChild = newNode(unseenDomainsIterator.next());
                        lastVisited.addChild(newChild);
                        lastVisited = newChild;
                    }
                    break;
                }
                collector.clear();
            }
        }

        // hack to replace the "subject" root by "factotum"
        Set<? extends TreeNode<String>> children = rootDomain.getChildren().iterator().next().getChildren();
        rootDomain.removeChildren();
        rootDomain.addChildren(children);

        return rootDomain;
    }

    private static void buildTree(File tempDir, String[] args) {
        try (WikipediaSchoolsDomainTreeParser parser = new WikipediaSchoolsDomainTreeParser(tempDir)) {
            long timestamp = System.currentTimeMillis();
            parser.open();
            parser.run(args);
            LOGGER.info("total duration [" + (System.currentTimeMillis() - timestamp) + "]ms");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error during indexing", e);
            return;
        }
    }

    private static void writeTreeGraphml(ValueTreeNode<String> rootNode, File file) {
        file.delete();
        try {
            FileWriter writer = new FileWriter(file);

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            writer.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:y=\"http://www.yworks.com/xml/graphml\" xmlns:yed=\"http://www.yworks.com/xml/yed/3\" "
                    + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">\n");
            writer.write("  <!--Created by yEd 3.14-->\n");
            writer.write("  <key attr.name=\"Description\" attr.type=\"string\" for=\"graph\" id=\"d0\"/>\n");
            writer.write("  <key for=\"port\" id=\"d1\" yfiles.type=\"portgraphics\"/>\n");
            writer.write("  <key for=\"port\" id=\"d2\" yfiles.type=\"portgeometry\"/>\n");
            writer.write("  <key for=\"port\" id=\"d3\" yfiles.type=\"portuserdata\"/>\n");
            writer.write("  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d4\"/>\n");
            writer.write("  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d5\"/>\n");
            writer.write("  <key for=\"node\" id=\"d6\" yfiles.type=\"nodegraphics\"/>\n");
            writer.write("  <key for=\"graphml\" id=\"d7\" yfiles.type=\"resources\"/>\n");
            writer.write("  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d8\"/>\n");
            writer.write("  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d9\"/>\n");
            writer.write("  <key for=\"edge\" id=\"d10\" yfiles.type=\"edgegraphics\"/>\n");
            writer.write("  <graph edgedefault=\"directed\" id=\"G\">\n");
            writer.write("    <data key=\"d0\"/>\n");

            NodeInspector<String> graphmlContentWriter = new NodeInspector<String>() {
                @Override
                public void invoke(TreeNode<String> n) {
                    try {
                        writer.write("    <node id=\"n" + n.getName() + "\">\n");
                        // writer.write("      <data key=\"d4\"><![CDATA["+ some
                        // url to local file goes here+"]]></data>");
                        writer.write("      <data key=\"d5\"/>\n");
                        writer.write("      <data key=\"d6\">\n");
                        writer.write("        <y:ShapeNode>\n");
                        writer.write("          <y:Geometry height=\"30.0\" width=\"30.0\" x=\"-15.0\" y=\"-15.0\"/>\n");
                        writer.write("          <y:Fill color=\"#CCCCFF\" transparent=\"false\"/>\n");
                        writer.write("          <y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n");
                        writer.write("          <y:NodeLabel rotationAngle=\"270.0\" alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" "
                                + "fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" "
                                + "hasText=\"true\" height=\"17.0\" modelName=\"custom\" textColor=\"#000000\" visible=\"true\" "
                                + "width=\"26.0\" x=\"1.0\" y=\"6.0\">" + n.getName() + "<y:LabelModel>\n");
                        writer.write("            <y:SmartNodeLabelModel distance=\"4.0\"/>\n");
                        writer.write("            </y:LabelModel>\n");
                        writer.write("            <y:ModelParameter>\n");
                        writer.write("              <y:SmartNodeLabelModelParameter labelRatioX=\"0.0\" labelRatioY=\"0.0\" "
                                + "nodeRatioX=\"0.0\" nodeRatioY=\"0.0\" offsetX=\"0.0\" offsetY=\"0.0\" upX=\"0.0\" upY=\"-1.0\"/>\n");
                        writer.write("            </y:ModelParameter>\n");
                        writer.write("          </y:NodeLabel>\n");
                        writer.write("            <y:Shape type=\"ellipse\"/>\n");
                        writer.write("        </y:ShapeNode>\n");
                        writer.write("    </data>\n");
                        writer.write("    </node>\n");

                        for (TreeNode<String> child : n.getChildren()) {
                            writer.write("    <edge id=\"e" + child.getName() + "\" source=\"n" + n.getName() + "\" target=\"n" + child.getName() + "\">\n");
                            writer.write("      <data key=\"d10\">\n");
                            writer.write("        <y:GenericEdge configuration=\"com.yworks.bpmn.Connection\">\n");
                            writer.write("           <y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>\n");
                            writer.write("           <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n");
                            writer.write("           <y:Arrows source=\"none\" target=\"delta\"/>\n");
                            writer.write("           <y:StyleProperties>\n");
                            writer.write("             <y:Property class=\"com.yworks.yfiles.bpmn.view.BPMNTypeEnum\" "
                                    + "name=\"com.yworks.bpmn.type\" value=\"CONNECTION_TYPE_SEQUENCE_FLOW\"/>\n");
                            writer.write("           </y:StyleProperties>\n");
                            writer.write("        </y:GenericEdge>\n");
                            writer.write("      </data>\n");
                            writer.write("    </edge>\n");
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "failed to write graph to file [" + file.getPath() + "/" + file.getName() + "]", e);
                    }
                }
            };
            BaseTreeNode.depthFirstTraverser(rootNode, graphmlContentWriter);

            writer.write("  </graph>\n");
            writer.write("  <data key=\"d7\">\n");
            writer.write("    <y:Resources/>\n");
            writer.write("  </data>\n");
            writer.write("</graphml>\n");

            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "failed to write graph to file [" + file.getPath() + "/" + file.getName() + "]", e);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to write graph to file [" + file.getPath() + "/" + file.getName() + "]", e);
        }

    }

    private void run(String[] args) throws IOException {
        if (args.length > 0) {
            Path domainTreeRoot = FileSystems.getDefault().getPath(args[0] + DOMAIN_TREE_ENTRYPOINT_DIRECTORY);
            List<File> domainFiles = collectDomainFiles(domainTreeRoot);
            ValueTreeNode<String> rootDomain = parseDomainFilesToTree(domainFiles);
            BaseTreeNode.depthFirstTraverser(rootDomain, (n) -> System.out.println(n.toString()));

            File visTempFile = File.createTempFile("wikipedia-schools-domains-graph-", ".graphml");
            LOGGER.info("writing domain-tree to graphml [" + visTempFile.getAbsolutePath() + "]");
            writeTreeGraphml(rootDomain, visTempFile);
            writeTreeToIndex(rootDomain);
        } else {
            throw new IOException("no source path given");
        }
    }

    /**
     * stores a whole tree to a Lucene index
     * 
     * @param rootDomain
     *            the root to indexed from
     * @throws IOException
     *             if there is a low-level IO error
     */
    private void writeTreeToIndex(ValueTreeNode<String> rootDomain) throws IOException {
        DocumentGeneratingNodeInspector nodeToDocsMapper = new DocumentGeneratingNodeInspector();
        BaseTreeNode.depthFirstTraverser(rootDomain, nodeToDocsMapper);
        outIndexWriter.addDocuments(nodeToDocsMapper.getDocuments());
    }

    private static ValueTreeNode<String> newNode(String nodeName) {
        ValueTreeNode<String> node = new ValueTreeNode<String>();
        node.setName(nodeName);
        return node;
    }

    private static Set<TreeNode<String>> newNodeCollector() {
        return new HashSet<TreeNode<String>>();
    }

    private List<File> collectDomainFiles(Path domainTreeRoot) throws IOException {
        Set<String> whiteList = new HashSet<String>();
        whiteList.add(DOMAIN_TREE_SITE_SUFFIX);
        Set<String> blackList = new HashSet<String>();
        List<Path> matchedFiles = new ArrayList<Path>();
        WikipediaSchoolsDumpIndexer.collectFilePaths(domainTreeRoot, whiteList, blackList, matchedFiles);

        List<File> domainFiles = new ArrayList<File>();
        for (Iterator<Path> iterator = matchedFiles.iterator(); iterator.hasNext();) {
            File file = new File(iterator.next().toString());
            if (file.isFile() && file.getName().startsWith(DOMAIN_TREE_SITE_PREFIX)) {
                domainFiles.add(file);
                LOGGER.info("memorized file [" + file.getCanonicalFile() + "] for later processing");
            }
        }
        return domainFiles;
    }

}
