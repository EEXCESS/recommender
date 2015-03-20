package eu.eexcess.partnerdata.evaluation.enrichment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.JsonGenerationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SurveyGeneratorFromTraceFiles {

	private static final String PATH_TO_XML_FILES = "C:\\eexcess-temp\\log\\";
//	private static final String PATH_TO_XML_FILES = "C:\\dev\\eexcess\\trunk\\src\\wp4\\enrichment-analysis\\testbed-1\\2015-02-10\\log\\";
	
	private static final String PATH_TO_SURVEY_FILE = "d:\\";
	
	public SurveyGeneratorFromTraceFiles() {

	}

	public ArrayList<String> getEnrichedFiles() {
		ArrayList fileList = new ArrayList<String>();
		File folder = new File(PATH_TO_XML_FILES);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
			} else {
				if (fileEntry.getName().contains("Enrichment-done-enrichment")) {
					// System.out.println(fileEntry.getName());
					fileList.add(fileEntry.getName());
				}
			}
		}
		System.out.println("found " + fileList.size() + " files");
		return fileList;
	}
	
	public Document readXMLFile(String filename) {
		StringBuffer content = new StringBuffer();
		File logFile = new File(filename);
		try {
			FileReader freader = new FileReader(logFile);
			BufferedReader br = new BufferedReader(freader);
			String line;
		    while ((line = br.readLine()) != null) {
		    	content.append(line + System.lineSeparator());
		    }
			br.close();
			freader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertStringToDocument(content.toString());
	}
	
	public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try 
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }		

	// <eexcess:Proxy rdf:about="http://www.econbiz.de/Record/10010323353/proxy/">
	/*
 <dc:title>Universidad pública, abierta y gratuita: Análisis de factores cruciales para la evaluación de esta popítica pública</dc:title>
 	 
	 */
	
	// <eexcess:Proxy rdf:about="http://www.econbiz.de/Record/10010323353/enrichedProxy/">
	
	/*
<eexcess:Proxy rdf:about="http://www.econbiz.de/Record/10010323353/enrichedProxy/">
    <dc:subject rdf:resource="http://www.freebase.com/m/01bm6t"/>
    <dc:subject rdf:resource="http://www.freebase.com/m/0f_78"/>
    <dc:subject rdf:resource="http://dbpedia.org/resource/Lusitania"/>
    <dc:subject rdf:resource="http://www.freebase.com/m/07szfpb"/>
    <dc:subject rdf:resource="http://dbpedia.org/resource/Mary_%28mother_of_Jesus%29"/>
    <dc:subject rdf:resource="http://www.freebase.com/m/07jkft"/>
    <dc:subject>pará</dc:subject>
    <dc:subject>brazil</dc:subject>
    <dc:subject rdf:resource="http://www.freebase.com/m/024x1x"/>
    <dc:subject rdf:resource="http://www.freebase.com/m/01hdnp"/>
    <dc:subject rdf:resource="http://www.freebase.com/m/020pjy"/>
    <ore:proxyFor rdf:resource="http://www.econbiz.de/Record/10010323353"/>
    <ore:proxyIn>
      <ore:Aggregation rdf:about="http://www.econbiz.de/Record/10010323353/enrichedAggregation/">
        <edm:dataProvider rdf:resource="http://www.eexcess.eu/data/agents/metadataEnrichmentAgent/"/>
        <edm:aggregatedCHO rdf:resource="http://www.econbiz.de/Record/10010323353"/>
      </ore:Aggregation>
    </ore:proxyIn>
    <dc:subject>belém</dc:subject>
  </eexcess:Proxy>	
	 */
	
	
	public static void dumpXML(Document xmlObject) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(xmlObject), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
			//System.out.println(output);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static void main(String[] args) throws JsonGenerationException {
		SurveyGeneratorFromTraceFiles generator = new SurveyGeneratorFromTraceFiles();
		ArrayList<String> enrichedFiles = generator.getEnrichedFiles();
		generator.generateSurvey(generator, enrichedFiles);
	}

	private void generateSurvey(
			SurveyGeneratorFromTraceFiles generator,
			ArrayList<String> enrichedFiles) {
		ArrayList<PartnerRecommenderResultsFile> results = new ArrayList<PartnerRecommenderResultsFile>();
		for (int indexEnrichedFiles = 0; indexEnrichedFiles < enrichedFiles.size(); indexEnrichedFiles++) {
			
			
			
			
			// TODO remove
//			if (indexEnrichedFiles ==10 || indexEnrichedFiles ==15) {
				PartnerRecommenderResultsFile resultFile = new PartnerRecommenderResultsFile();
				resultFile.setFilename(enrichedFiles.get(indexEnrichedFiles) );
				resultFile.setFilenamePath(PATH_TO_XML_FILES + enrichedFiles.get(indexEnrichedFiles) );
				
				System.out.println("FILE:" + PATH_TO_XML_FILES + enrichedFiles.get(indexEnrichedFiles) );

				
				
				
				
				
				
				
				
				
				Document enriched = generator.readXMLFile(PATH_TO_XML_FILES + enrichedFiles.get(indexEnrichedFiles));
				dumpXML(enriched);
				//"/rdf:RDF/eexcess:Proxy"
				NodeList nodes = generator.getElementsViaXPath("//*[local-name()='Proxy']",enriched);
				for (int indexNodeProxy = 0; indexNodeProxy < nodes.getLength(); indexNodeProxy++) {
					Node proxyNode = nodes.item(indexNodeProxy);
					if (proxyNode != null)
					{
						//System.out.println("found node:" + proxyNode.getNodeName() );
						//System.out.println("found node:" + proxyNode.getNodeName() + " " + proxyNode.getAttributes().toString());
						if (proxyNode.getAttributes() != null && proxyNode.getAttributes().getNamedItem("rdf:about") != null)
						{
							String proxyRdfAbout = proxyNode.getAttributes().getNamedItem("rdf:about").getNodeValue();
							String proxyURL = "/proxy/";
							//System.out.println("found :" + text);
							String proxyKey = proxyRdfAbout.substring(0, proxyRdfAbout.length()-proxyURL.length());
							//System.out.println("found proxyKey:" + proxyKey);
							if (proxyRdfAbout.endsWith(proxyURL))
							{// search for the enriched Proxy
								for (int indexEnrichedProxy = 0; indexEnrichedProxy < nodes.getLength(); indexEnrichedProxy++) {
									Node enrichedProxyNode = nodes.item(indexEnrichedProxy);
									if (enrichedProxyNode != null)
									{
										if (enrichedProxyNode.getAttributes() != null && enrichedProxyNode.getAttributes().getNamedItem("rdf:about") != null)
										{
											String enrichedProxyRdfAbout = enrichedProxyNode.getAttributes().getNamedItem("rdf:about").getNodeValue();
											String enrichedProxyURL = "/enrichedProxy/";
											//System.out.println("found :" + text);
											String enrichedProxyKey = enrichedProxyRdfAbout.substring(0, enrichedProxyRdfAbout.length()-enrichedProxyURL.length());
											//System.out.println("found enrichedProxyKey:" + enrichedProxyKey);
											if (enrichedProxyRdfAbout.endsWith("/enrichedProxy/"))
											{
												if (enrichedProxyKey.equalsIgnoreCase(proxyKey))
												{
													PartnerRecommenderResultLogEntry resultEntry = new PartnerRecommenderResultLogEntry();
													System.out.println("found 2 matching proxies:\nenrichedProxyKey:" + enrichedProxyKey + "\nproxyKey:        " + proxyKey);
													if (enrichedProxyNode.hasChildNodes())
													{
														NodeList enrichedProxyNodeChilds = enrichedProxyNode.getChildNodes();
														for (int indexEnrichedProxyNodeChilds = 0; indexEnrichedProxyNodeChilds < enrichedProxyNodeChilds.getLength(); indexEnrichedProxyNodeChilds++) {
															Node enrichedProxyNodeChild = enrichedProxyNodeChilds.item(indexEnrichedProxyNodeChilds);
															if (enrichedProxyNodeChild != null)
															{
																if (enrichedProxyNodeChild.getNodeType() == Node.ELEMENT_NODE)
																{
																	String nodeName =enrichedProxyNodeChild.getNodeName();
																	if (nodeName != null && !nodeName.isEmpty() && 
																			!nodeName.equalsIgnoreCase("ore:proxyFor") &&
																			!nodeName.equalsIgnoreCase("ore:proxyIn")
																			)
																	{
																		resultEntry.addEnrichedNode(enrichedProxyNodeChild);
																	}
																}
																	
															}
														}
													}
													resultEntry.setEnrichedProxyKey(enrichedProxyKey);
													resultEntry.setProxyKey(proxyKey);
													resultEntry.setProxy(proxyNode);
													resultEntry.setEnrichedProxy(enrichedProxyNode);
													resultFile.addResult(resultEntry);
//													System.out.println("enrichedProxy:\n" + nodeToString(enrichedProxyNode) + "\n enrichedNotes:" + resultEntry.getEnrichedNodes().size());
													break;
												}
											}
										}
									}
								}
							}
							
						}

					}
				}
				results.add(resultFile);
				
				// REMOVE
//				if ( indexEnrichedFiles >15) break;
//			}
		}
		System.out.println("found results:"+results.size());
		StringBuilder surveyFile = new StringBuilder();
		surveyFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><document><LimeSurveyDocType>Survey</LimeSurveyDocType><DBVersion>178</DBVersion><languages><language>en</language></languages><groups><fields><fieldname>gid</fieldname><fieldname>sid</fieldname><fieldname>group_name</fieldname><fieldname>group_order</fieldname><fieldname>description</fieldname><fieldname>language</fieldname><fieldname>randomization_group</fieldname><fieldname>grelevance</fieldname></fields>");
		
		// die Gruppen schreiben - pro result eine Gruppe
		/*
 <rows>
   <row>
    <gid><![CDATA[13]]></gid>
    <sid><![CDATA[367634]]></sid>
    <group_name><![CDATA[result1]]></group_name>
    <group_order><![CDATA[0]]></group_order>
    <description><![CDATA[XML from First result]]></description>
    <language><![CDATA[en]]></language>
    <randomization_group/>
    <grelevance/>
   </row>
   <row>
    <gid><![CDATA[14]]></gid>
    <sid><![CDATA[367634]]></sid>
    <group_name><![CDATA[result2]]></group_name>
    <group_order><![CDATA[1]]></group_order>
    <description><![CDATA[xml second result]]></description>
    <language><![CDATA[en]]></language>
    <randomization_group/>
    <grelevance/>
   </row>
  </rows>
  	*/
		surveyFile.append("<rows>");
		String sid = "367634";
		StringBuilder surveyQuestions = new StringBuilder();
		surveyQuestions.append("<rows>");
		int qid = 22;
		for (int indexFile = 0; indexFile < results.size(); indexFile++) {
			PartnerRecommenderResultsFile resultFile = results.get(indexFile);
			//resultFile.getFilename()
			surveyFile.append("<row>");
			String gid = (indexFile+13)+"";
			surveyFile.append("<gid><![CDATA[" + gid +  "]]></gid>");
			surveyFile.append("<sid><![CDATA[" + sid +  "]]></sid>");
			surveyFile.append("<group_name><![CDATA[" + resultFile.getFilename() +  "]]></group_name>");
			surveyFile.append("<group_order><![CDATA[" + indexFile +  "]]></group_order>");
			surveyFile.append("<description><![CDATA[ enriched Results from File " + resultFile.getFilename() +  "]]></description>");
			surveyFile.append("<language><![CDATA[en]]></language> <randomization_group/> <grelevance/> </row>");
			
			for (int indexResults = 0; indexResults < resultFile.getResults().size(); indexResults++) {
				PartnerRecommenderResultLogEntry result = resultFile.getResults().get(indexResults);
				for (int indexEnrichments = 0; indexEnrichments < result.getEnrichedNodes().size(); indexEnrichments++) {
					surveyQuestions.append("<row>");
					surveyQuestions.append("<qid><![CDATA[" + qid +  "]]></qid>");
					surveyQuestions.append("<parent_qid><![CDATA[0]]></parent_qid>");
					surveyQuestions.append("<sid><![CDATA[367634]]></sid>");
					surveyQuestions.append("<gid><![CDATA[" + gid +  "]]></gid>");
					surveyQuestions.append("<type><![CDATA[5]]></type>");
					surveyQuestions.append("<title><![CDATA[file"+indexFile+"result"+indexResults+"enrichment"+indexEnrichments+"]]></title>");//xx
					
					String question = "";//"<p>Result:<br/>"+result.getProxyKey() +" </p><br/><br/>";
					question += "<p><table>";
					
					NodeList proxyChilds = result.getProxy().getChildNodes();
					for (int indexProxyChilds = 0; indexProxyChilds < proxyChilds.getLength(); indexProxyChilds++) {
						Node child = proxyChilds.item(indexProxyChilds);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
//							question += "<tr><td style=\"padding:4px;\">" + child.getNodeName() + " </td><td style=\"padding:4px;\">" + child.getTextContent() + "</td></tr>";
							question += "<tr><td class=\"EEXCESSQuestionResultTableTd\">" + child.getNodeName() + " </td><td class=\"EEXCESSQuestionResultTableTd\"><b>" + child.getTextContent() + "</b></td></tr>";
						}
					}
					Node enrichedNode = result.getEnrichedNodes().get(indexEnrichments);
					String enrichmentQuestion = "<br/><p><b>enrichment:</b><br/> <i>"+enrichedNode.getNodeName() + "</i>: ";

					
					NodeList enrichedNodeChilds = enrichedNode.getChildNodes();
					for (int indexEnrichedChilds = 0; indexEnrichedChilds < enrichedNodeChilds.getLength(); indexEnrichedChilds++) {
						Node enrichedChild = enrichedNodeChilds.item(indexEnrichedChilds);
						if (enrichedChild.getNodeType() == Node.ELEMENT_NODE) {
							if (enrichedChild.getTextContent() != null ) enrichmentQuestion += enrichedChild.getTextContent();
							if (enrichedChild.getAttributes() != null && enrichedChild.getAttributes().getNamedItem("rdf:resource") != null)
							{
								enrichmentQuestion += enrichedChild.getAttributes().getNamedItem("rdf:resource").getNodeValue();
							}
							if (enrichedChild.getAttributes() != null && enrichedChild.getAttributes().getNamedItem("rdf:about") != null)
							{
								String tempURL = enrichedChild.getAttributes().getNamedItem("rdf:about").getNodeValue();
								enrichmentQuestion += "<a href=\"" + tempURL + "\"  target=\"_blank\">" + tempURL + "</a>";
							}
							
						}
					}
							
							
//					if (enrichedNode.getTextContent() != null ) enrichmentQuestion += enrichedNode.getTextContent();
//					if (enrichedNode.getAttributes() != null && enrichedNode.getAttributes().getNamedItem("rdf:resource") != null)
//					{
//						enrichmentQuestion += enrichedNode.getAttributes().getNamedItem("rdf:resource").getNodeValue();
//					}
					/*
					statt:
						http://dbpedia.org/resource/Wolfgang_Amadeus_Mozart	
					besser:	
					<a href="http://dbpedia.org/resource/Wolfgang_Amadeus_Mozart" target="_blank">http://dbpedia.org/resource/Wolfgang_Amadeus_Mozart</a> Wolfgang Amadeus Mozart
						
					*/	
					
					
					question = question+"</table></p>" + enrichmentQuestion + "</p>";
					
					surveyQuestions.append("<question><![CDATA["+ question  + "]]></question>");
					surveyQuestions.append("<preg/>");
					surveyQuestions.append("<help/>");
					surveyQuestions.append("<other><![CDATA[N]]></other>");
					surveyQuestions.append("<mandatory><![CDATA[Y]]></mandatory>");
					surveyQuestions.append("<question_order><![CDATA["+(indexResults+1)+"]]></question_order>");
					surveyQuestions.append("<language><![CDATA[en]]></language>");
					surveyQuestions.append("<scale_id><![CDATA[0]]></scale_id>");
					surveyQuestions.append("<same_default><![CDATA[0]]></same_default>");
					surveyQuestions.append("<relevance><![CDATA[1]]></relevance>");
					surveyQuestions.append("</row>");
					
					
					qid++;
				}
				
			}
			
		}
		surveyFile.append("</rows>");
		surveyQuestions.append("</rows>");
		
		
		surveyFile.append("</groups><questions><fields><fieldname>qid</fieldname><fieldname>parent_qid</fieldname><fieldname>sid</fieldname><fieldname>gid</fieldname><fieldname>type</fieldname><fieldname>title</fieldname><fieldname>question</fieldname><fieldname>preg</fieldname><fieldname>help</fieldname><fieldname>other</fieldname><fieldname>mandatory</fieldname><fieldname>question_order</fieldname><fieldname>language</fieldname><fieldname>scale_id</fieldname><fieldname>same_default</fieldname><fieldname>relevance</fieldname></fields>");

		surveyFile.append(surveyQuestions);
	// die Fragen schreiben
	/*
  <rows>
   <row>
    <qid><![CDATA[22]]></qid>
    <parent_qid><![CDATA[0]]></parent_qid>
    <sid><![CDATA[367634]]></sid>
    <gid><![CDATA[13]]></gid>
    <type><![CDATA[5]]></type>
    <title><![CDATA[r1q1]]></title>
    <question><![CDATA[enrichment XML]]></question>
    <preg/>
    <help/>
    <other><![CDATA[N]]></other>
    <mandatory><![CDATA[Y]]></mandatory>
    <question_order><![CDATA[1]]></question_order>
    <language><![CDATA[en]]></language>
    <scale_id><![CDATA[0]]></scale_id>
    <same_default><![CDATA[0]]></same_default>
    <relevance><![CDATA[1]]></relevance>
   </row>
   <row>
    <qid><![CDATA[23]]></qid>
    <parent_qid><![CDATA[0]]></parent_qid>
    <sid><![CDATA[367634]]></sid>
    <gid><![CDATA[13]]></gid>
    <type><![CDATA[5]]></type>
    <title><![CDATA[r1q2]]></title>
    <question><![CDATA[sceond enrichment xml]]></question>
    <preg/>
    <help/>
    <other><![CDATA[N]]></other>
    <mandatory><![CDATA[Y]]></mandatory>
    <question_order><![CDATA[2]]></question_order>
    <language><![CDATA[en]]></language>
    <scale_id><![CDATA[0]]></scale_id>
    <same_default><![CDATA[0]]></same_default>
    <relevance><![CDATA[1]]></relevance>
   </row>
   <row>
    <qid><![CDATA[24]]></qid>
    <parent_qid><![CDATA[0]]></parent_qid>
    <sid><![CDATA[367634]]></sid>
    <gid><![CDATA[14]]></gid>
    <type><![CDATA[5]]></type>
    <title><![CDATA[r2q1]]></title>
    <question><![CDATA[<p>
	result 2 </p>
<p>
	enrichment 1</p>
]]></question>
    <preg/>
    <help/>
    <other><![CDATA[N]]></other>
    <mandatory><![CDATA[Y]]></mandatory>
    <question_order><![CDATA[1]]></question_order>
    <language><![CDATA[en]]></language>
    <scale_id><![CDATA[0]]></scale_id>
    <same_default><![CDATA[0]]></same_default>
    <relevance><![CDATA[1]]></relevance>
   </row>
  </rows>

	 
	 */

	/*
		for (int indexResults = 0; indexResults < results.size(); indexResults++) {
			PartnerRecommenderResultsFile result = results.get(indexResults);
			System.out.println("enrichedNodes:" + result.getEnrichedNodes().size());
			if (result.getEnrichedNodes().size() > 0) {
				for (int i = 0; i < result.getEnrichedNodes().size(); i++) {
					System.out.println("\nenrichedNode:\n" + nodeToString(result.getEnrichedNodes().get(i)));
				}
			}
		}
		*/
	

		String surveyTitle = "EEXCESS Data Enrichment Survey ";// + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		String surveyWelcomeMessage = "Welcome at the EEXCESS DataEnrichment Survey!<br/>We want your opinion to our enriched data. ";
		String surveyDescription = "We use the data from our data provider and use web service to add more information to the data.";
		String surveyEndText = "Thank you!";
		String surveyAdminEmail = "thomas.orgel@joanneum.at";
		String surveyBounceEmail = "thomas.orgel@joanneum.at";
		/*
		String surveyTitle = "";
		String surveyWelcomeMessage = "";
		String surveyDescription = "";
		String surveyEndText = "";
		String surveyAdminEmail = "";
		String surveyBounceEmail = "";
		*/
		//surveyFile.append("</questions><surveys><fields><fieldname>sid</fieldname><fieldname>admin</fieldname><fieldname>expires</fieldname><fieldname>startdate</fieldname><fieldname>adminemail</fieldname><fieldname>anonymized</fieldname><fieldname>faxto</fieldname><fieldname>format</fieldname><fieldname>savetimings</fieldname><fieldname>template</fieldname><fieldname>language</fieldname><fieldname>additional_languages</fieldname><fieldname>datestamp</fieldname><fieldname>usecookie</fieldname><fieldname>allowregister</fieldname><fieldname>allowsave</fieldname><fieldname>autonumber_start</fieldname><fieldname>autoredirect</fieldname><fieldname>allowprev</fieldname><fieldname>printanswers</fieldname><fieldname>ipaddr</fieldname><fieldname>refurl</fieldname><fieldname>publicstatistics</fieldname><fieldname>publicgraphs</fieldname><fieldname>listpublic</fieldname><fieldname>htmlemail</fieldname><fieldname>sendconfirmation</fieldname><fieldname>tokenanswerspersistence</fieldname><fieldname>assessments</fieldname><fieldname>usecaptcha</fieldname><fieldname>usetokens</fieldname><fieldname>bounce_email</fieldname><fieldname>attributedescriptions</fieldname><fieldname>emailresponseto</fieldname><fieldname>emailnotificationto</fieldname><fieldname>tokenlength</fieldname><fieldname>showxquestions</fieldname><fieldname>showgroupinfo</fieldname><fieldname>shownoanswer</fieldname><fieldname>showqnumcode</fieldname><fieldname>bouncetime</fieldname><fieldname>bounceprocessing</fieldname><fieldname>bounceaccounttype</fieldname><fieldname>bounceaccounthost</fieldname><fieldname>bounceaccountpass</fieldname><fieldname>bounceaccountencryption</fieldname><fieldname>bounceaccountuser</fieldname><fieldname>showwelcome</fieldname><fieldname>showprogress</fieldname><fieldname>questionindex</fieldname><fieldname>navigationdelay</fieldname><fieldname>nokeyboard</fieldname><fieldname>alloweditaftercompletion</fieldname><fieldname>googleanalyticsstyle</fieldname><fieldname>googleanalyticsapikey</fieldname></fields><rows><row><sid>367634</sid><admin>Administrator</admin><adminemail>iis-wikiadmin@joanneum.at</adminemail><anonymized>N</anonymized><faxto/><format>G</format><savetimings>N</savetimings><template>EEXCESS</template><language>en</language><datestamp>N</datestamp><usecookie>N</usecookie><allowregister>N</allowregister><allowsave>Y</allowsave><autonumber_start>0</autonumber_start><autoredirect>N</autoredirect><allowprev>N</allowprev><printanswers>N</printanswers><ipaddr>N</ipaddr><refurl>N</refurl><publicstatistics>N</publicstatistics><publicgraphs>N</publicgraphs><listpublic>N</listpublic><htmlemail>Y</htmlemail><sendconfirmation>Y</sendconfirmation><tokenanswerspersistence>N</tokenanswerspersistence><assessments>N</assessments><usecaptcha>D</usecaptcha><usetokens>N</usetokens><bounce_email>your-email@example.net</bounce_email><emailresponseto/><emailnotificationto/><tokenlength>15</tokenlength><showxquestions>Y</showxquestions><showgroupinfo>B</showgroupinfo><shownoanswer>Y</shownoanswer><showqnumcode>X</showqnumcode><bounceprocessing>N</bounceprocessing><showwelcome>Y</showwelcome><showprogress>Y</showprogress><questionindex>0</questionindex><navigationdelay>0</navigationdelay><nokeyboard>N</nokeyboard><alloweditaftercompletion>N</alloweditaftercompletion></row></rows></surveys><surveys_languagesettings><fields><fieldname>surveyls_survey_id</fieldname><fieldname>surveyls_language</fieldname><fieldname>surveyls_title</fieldname><fieldname>surveyls_description</fieldname><fieldname>surveyls_welcometext</fieldname><fieldname>surveyls_endtext</fieldname><fieldname>surveyls_url</fieldname><fieldname>surveyls_urldescription</fieldname><fieldname>surveyls_email_invite_subj</fieldname><fieldname>surveyls_email_invite</fieldname><fieldname>surveyls_email_remind_subj</fieldname><fieldname>surveyls_email_remind</fieldname><fieldname>surveyls_email_register_subj</fieldname><fieldname>surveyls_email_register</fieldname><fieldname>surveyls_email_confirm_subj</fieldname><fieldname>surveyls_email_confirm</fieldname><fieldname>surveyls_dateformat</fieldname><fieldname>surveyls_attributecaptions</fieldname><fieldname>email_admin_notification_subj</fieldname><fieldname>email_admin_notification</fieldname><fieldname>email_admin_responses_subj</fieldname><fieldname>email_admin_responses</fieldname><fieldname>surveyls_numberformat</fieldname><fieldname>attachments</fieldname></fields><rows><row><surveyls_survey_id>367634</surveyls_survey_id><surveyls_language>en</surveyls_language><surveyls_title>EEXCESS Data Enrichment</surveyls_title><surveyls_description>here comes my descrption</surveyls_description><surveyls_welcometext>welcome message</surveyls_welcometext><surveyls_endtext>thanks</surveyls_endtext><surveyls_url/><surveyls_urldescription/><surveyls_email_invite_subj>Invitation to participate in a survey</surveyls_email_invite_subj><surveyls_email_invite>Dear {FIRSTNAME},<br /> <br /> you have been invited to participate in a survey.<br /> <br /> The survey is titled:<br /> \"{SURVEYNAME}\"<br /> <br /> \"{SURVEYDESCRIPTION}\"<br /> <br /> To participate, please click on the link below.<br /> <br /> Sincerely,<br /> <br /> {ADMINNAME} ({ADMINEMAIL})<br /> <br /> ----------------------------------------------<br /> Click here to do the survey:<br /> {SURVEYURL}<br /> <br /> If you do not want to participate in this survey and don't want to receive any more invitations please click the following link:<br /> {OPTOUTURL}<br /> <br /> If you are blacklisted but want to participate in this survey and want to receive invitations please click the following link:<br /> {OPTINURL}</surveyls_email_invite><surveyls_email_remind_subj>Reminder to participate in a survey</surveyls_email_remind_subj><surveyls_email_remind>Dear {FIRSTNAME},<br /> <br /> Recently we invited you to participate in a survey.<br /> <br /> We note that you have not yet completed the survey, and wish to remind you that the survey is still available should you wish to take part.<br /> <br /> The survey is titled:<br /> \"{SURVEYNAME}\"<br /> <br /> \"{SURVEYDESCRIPTION}\"<br /> <br /> To participate, please click on the link below.<br /> <br /> Sincerely,<br /> <br /> {ADMINNAME} ({ADMINEMAIL})<br /> <br /> ----------------------------------------------<br /> Click here to do the survey:<br /> {SURVEYURL}<br /> <br /> If you do not want to participate in this survey and don't want to receive any more invitations please click the following link:<br /> {OPTOUTURL}</surveyls_email_remind><surveyls_email_register_subj>Survey registration confirmation</surveyls_email_register_subj><surveyls_email_register>Dear {FIRSTNAME},<br /> <br /> You, or someone using your email address, have registered to participate in an online survey titled {SURVEYNAME}.<br /> <br /> To complete this survey, click on the following URL:<br /> <br /> {SURVEYURL}<br /> <br /> If you have any questions about this survey, or if you did not register to participate and believe this email is in error, please contact {ADMINNAME} at {ADMINEMAIL}.</surveyls_email_register><surveyls_email_confirm_subj>Confirmation of your participation in our survey</surveyls_email_confirm_subj><surveyls_email_confirm>Dear {FIRSTNAME},<br /> <br /> this email is to confirm that you have completed the survey titled {SURVEYNAME} and your response has been saved. Thank you for participating.<br /> <br /> If you have any further questions about this email, please contact {ADMINNAME} on {ADMINEMAIL}.<br /> <br /> Sincerely,<br /> <br /> {ADMINNAME}</surveyls_email_confirm><surveyls_dateformat>9</surveyls_dateformat><email_admin_notification_subj>Response submission for survey {SURVEYNAME}</email_admin_notification_subj><email_admin_notification>Hello,<br /> <br /> A new response was submitted for your survey '{SURVEYNAME}'.<br /> <br /> Click the following link to reload the survey:<br /> {RELOADURL}<br /> <br /> Click the following link to see the individual response:<br /> {VIEWRESPONSEURL}<br /> <br /> Click the following link to edit the individual response:<br /> {EDITRESPONSEURL}<br /> <br /> View statistics by clicking here:<br /> {STATISTICSURL}</email_admin_notification><email_admin_responses_subj>Response submission for survey {SURVEYNAME} with results</email_admin_responses_subj><email_admin_responses>Hello,<br /> <br /> A new response was submitted for your survey '{SURVEYNAME}'.<br /> <br /> Click the following link to reload the survey:<br /> {RELOADURL}<br /> <br /> Click the following link to see the individual response:<br /> {VIEWRESPONSEURL}<br /> <br /> Click the following link to edit the individual response:<br /> {EDITRESPONSEURL}<br /> <br /> View statistics by clicking here:<br /> {STATISTICSURL}<br /> <br /> <br /> The following answers were given by the participant:<br /> {ANSWERTABLE}</email_admin_responses><surveyls_numberformat>0</surveyls_numberformat></row></rows></surveys_languagesettings></document>");
		surveyFile.append(" </questions> <surveys> <fields> <fieldname>sid</fieldname> <fieldname>admin</fieldname> <fieldname>expires</fieldname> <fieldname>startdate</fieldname> <fieldname>adminemail</fieldname> <fieldname>anonymized</fieldname> <fieldname>faxto</fieldname> <fieldname>format</fieldname> <fieldname>savetimings</fieldname> <fieldname>template</fieldname> <fieldname>language</fieldname> <fieldname>additional_languages</fieldname> <fieldname>datestamp</fieldname> <fieldname>usecookie</fieldname> <fieldname>allowregister</fieldname> <fieldname>allowsave</fieldname> <fieldname>autonumber_start</fieldname> <fieldname>autoredirect</fieldname> <fieldname>allowprev</fieldname> <fieldname>printanswers</fieldname> <fieldname>ipaddr</fieldname> <fieldname>refurl</fieldname> <fieldname>publicstatistics</fieldname> <fieldname>publicgraphs</fieldname> <fieldname>listpublic</fieldname> <fieldname>htmlemail</fieldname> <fieldname>sendconfirmation</fieldname> <fieldname>tokenanswerspersistence</fieldname> <fieldname>assessments</fieldname> <fieldname>usecaptcha</fieldname> <fieldname>usetokens</fieldname> <fieldname>bounce_email</fieldname> <fieldname>attributedescriptions</fieldname> <fieldname>emailresponseto</fieldname> <fieldname>emailnotificationto</fieldname> <fieldname>tokenlength</fieldname> <fieldname>showxquestions</fieldname> <fieldname>showgroupinfo</fieldname> <fieldname>shownoanswer</fieldname> <fieldname>showqnumcode</fieldname> <fieldname>bouncetime</fieldname> <fieldname>bounceprocessing</fieldname> <fieldname>bounceaccounttype</fieldname> <fieldname>bounceaccounthost</fieldname> <fieldname>bounceaccountpass</fieldname> <fieldname>bounceaccountencryption</fieldname> <fieldname>bounceaccountuser</fieldname> <fieldname>showwelcome</fieldname> <fieldname>showprogress</fieldname> <fieldname>questionindex</fieldname> <fieldname>navigationdelay</fieldname> <fieldname>nokeyboard</fieldname> <fieldname>alloweditaftercompletion</fieldname> <fieldname>googleanalyticsstyle</fieldname> <fieldname>googleanalyticsapikey</fieldname> </fields> <rows> <row> <sid><![CDATA[367634]]></sid> <admin><![CDATA[Administrator]]></admin> <adminemail><![CDATA["+surveyAdminEmail+"]]></adminemail> <anonymized><![CDATA[N]]></anonymized> <faxto/> <format><![CDATA[G]]></format> <savetimings><![CDATA[N]]></savetimings> <template><![CDATA[EEXCESS]]></template> <language><![CDATA[en]]></language> <datestamp><![CDATA[N]]></datestamp> <usecookie><![CDATA[N]]></usecookie> <allowregister><![CDATA[N]]></allowregister> <allowsave><![CDATA[Y]]></allowsave> <autonumber_start><![CDATA[0]]></autonumber_start> <autoredirect><![CDATA[N]]></autoredirect> <allowprev><![CDATA[N]]></allowprev> <printanswers><![CDATA[N]]></printanswers> <ipaddr><![CDATA[N]]></ipaddr> <refurl><![CDATA[N]]></refurl> <publicstatistics><![CDATA[N]]></publicstatistics> <publicgraphs><![CDATA[N]]></publicgraphs> <listpublic><![CDATA[N]]></listpublic> <htmlemail><![CDATA[Y]]></htmlemail> <sendconfirmation><![CDATA[Y]]></sendconfirmation> <tokenanswerspersistence><![CDATA[N]]></tokenanswerspersistence> <assessments><![CDATA[N]]></assessments> <usecaptcha><![CDATA[D]]></usecaptcha> <usetokens><![CDATA[N]]></usetokens> <bounce_email><![CDATA["+surveyBounceEmail+"]]></bounce_email> <emailresponseto/> <emailnotificationto/> <tokenlength><![CDATA[15]]></tokenlength> <showxquestions><![CDATA[Y]]></showxquestions> <showgroupinfo><![CDATA[B]]></showgroupinfo> <shownoanswer><![CDATA[Y]]></shownoanswer> <showqnumcode><![CDATA[X]]></showqnumcode> <bounceprocessing><![CDATA[N]]></bounceprocessing> <showwelcome><![CDATA[Y]]></showwelcome> <showprogress><![CDATA[Y]]></showprogress> <questionindex><![CDATA[0]]></questionindex> <navigationdelay><![CDATA[0]]></navigationdelay> <nokeyboard><![CDATA[N]]></nokeyboard> <alloweditaftercompletion><![CDATA[N]]></alloweditaftercompletion> </row> </rows> </surveys> <surveys_languagesettings> <fields> <fieldname>surveyls_survey_id</fieldname> <fieldname>surveyls_language</fieldname> <fieldname>surveyls_title</fieldname> <fieldname>surveyls_description</fieldname> <fieldname>surveyls_welcometext</fieldname> <fieldname>surveyls_endtext</fieldname> <fieldname>surveyls_url</fieldname> <fieldname>surveyls_urldescription</fieldname> <fieldname>surveyls_email_invite_subj</fieldname> <fieldname>surveyls_email_invite</fieldname> <fieldname>surveyls_email_remind_subj</fieldname> <fieldname>surveyls_email_remind</fieldname> <fieldname>surveyls_email_register_subj</fieldname> <fieldname>surveyls_email_register</fieldname> <fieldname>surveyls_email_confirm_subj</fieldname> <fieldname>surveyls_email_confirm</fieldname> <fieldname>surveyls_dateformat</fieldname> <fieldname>surveyls_attributecaptions</fieldname> <fieldname>email_admin_notification_subj</fieldname> <fieldname>email_admin_notification</fieldname> <fieldname>email_admin_responses_subj</fieldname> <fieldname>email_admin_responses</fieldname> <fieldname>surveyls_numberformat</fieldname> <fieldname>attachments</fieldname> </fields> <rows> <row> <surveyls_survey_id><![CDATA[367634]]></surveyls_survey_id> <surveyls_language><![CDATA[en]]></surveyls_language> <surveyls_title><![CDATA["+surveyTitle+"]]></surveyls_title> <surveyls_description><![CDATA["+surveyDescription+"]]></surveyls_description> <surveyls_welcometext><![CDATA["+surveyWelcomeMessage+"]]></surveyls_welcometext> <surveyls_endtext><![CDATA["+surveyEndText+"]]></surveyls_endtext> <surveyls_url/> <surveyls_urldescription/> <surveyls_email_invite_subj><![CDATA[Invitation to participate in a survey]]></surveyls_email_invite_subj> <surveyls_email_invite><![CDATA[Dear {FIRSTNAME},<br /> <br /> you have been invited to participate in a survey.<br /> <br /> The survey is titled:<br /> \"{SURVEYNAME}\"<br /> <br /> \"{SURVEYDESCRIPTION}\"<br /> <br /> To participate, please click on the link below.<br /> <br /> Sincerely,<br /> <br /> {ADMINNAME} ({ADMINEMAIL})<br /> <br /> ----------------------------------------------<br /> Click here to do the survey:<br /> {SURVEYURL}<br /> <br /> If you do not want to participate in this survey and don't want to receive any more invitations please click the following link:<br /> {OPTOUTURL}<br /> <br /> If you are blacklisted but want to participate in this survey and want to receive invitations please click the following link:<br /> {OPTINURL}]]></surveyls_email_invite> <surveyls_email_remind_subj><![CDATA[Reminder to participate in a survey]]></surveyls_email_remind_subj> <surveyls_email_remind><![CDATA[Dear {FIRSTNAME},<br /> <br /> Recently we invited you to participate in a survey.<br /> <br /> We note that you have not yet completed the survey, and wish to remind you that the survey is still available should you wish to take part.<br /> <br /> The survey is titled:<br />  \"{SURVEYNAME}\"<br /> <br /> \"{SURVEYDESCRIPTION}\"<br /> <br /> To participate, please click on the link below.<br /> <br /> Sincerely,<br /> <br /> {ADMINNAME} ({ADMINEMAIL})<br /> <br /> ----------------------------------------------<br /> Click here to do the survey:<br /> {SURVEYURL}<br /> <br /> If you do not want to participate in this survey and don't want to receive any more invitations please click the following link:<br /> {OPTOUTURL}]]></surveyls_email_remind> <surveyls_email_register_subj><![CDATA[Survey registration confirmation]]></surveyls_email_register_subj> <surveyls_email_register><![CDATA[Dear {FIRSTNAME},<br /> <br /> You, or someone using your email address, have registered to participate in an online survey titled {SURVEYNAME}.<br /> <br /> To complete this survey, click on the following URL:<br /> <br /> {SURVEYURL}<br /> <br /> If you have any questions about this survey, or if you did not register to participate and believe this email is in error, please contact {ADMINNAME} at {ADMINEMAIL}.]]></surveyls_email_register> <surveyls_email_confirm_subj><![CDATA[Confirmation of your participation in our survey]]></surveyls_email_confirm_subj> <surveyls_email_confirm><![CDATA[Dear {FIRSTNAME},<br /> <br /> this email is to confirm that you have completed the survey titled {SURVEYNAME} and your response has been saved. Thank you for participating.<br /> <br /> If you have any further questions about this email, please contact {ADMINNAME} on {ADMINEMAIL}.<br /> <br /> Sincerely,<br /> <br /> {ADMINNAME}]]></surveyls_email_confirm> <surveyls_dateformat><![CDATA[9]]></surveyls_dateformat> <email_admin_notification_subj><![CDATA[Response submission for survey {SURVEYNAME}]]></email_admin_notification_subj> <email_admin_notification><![CDATA[Hello,<br /> <br /> A new response was submitted for your survey '{SURVEYNAME}'.<br /> <br /> Click the following link to reload the survey:<br /> {RELOADURL}<br /> <br /> Click the following link to see the individual response:<br /> {VIEWRESPONSEURL}<br /> <br /> Click the following link to edit the individual response:<br /> {EDITRESPONSEURL}<br /> <br /> View statistics by clicking here:<br /> {STATISTICSURL}]]></email_admin_notification> <email_admin_responses_subj><![CDATA[Response submission for survey {SURVEYNAME} with results]]></email_admin_responses_subj> <email_admin_responses><![CDATA[Hello,<br /> <br /> A new response was submitted for your survey '{SURVEYNAME}'.<br /> <br /> Click the following link to reload the survey:<br /> {RELOADURL}<br /> <br /> Click the following link to see the individual response:<br /> {VIEWRESPONSEURL}<br /> <br /> Click the following link to edit the individual response:<br /> {EDITRESPONSEURL}<br /> <br /> View statistics by clicking here:<br /> {STATISTICSURL}<br /> <br /> <br /> The following answers were given by the participant:<br /> {ANSWERTABLE}]]></email_admin_responses> <surveyls_numberformat><![CDATA[0]]></surveyls_numberformat> </row> </rows> </surveys_languagesettings> </document>");
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.PATH_TO_SURVEY_FILE+ "test-survey.lss", "UTF-8");
					writer.println(surveyFile.toString());
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	
	 private static String nodeToString(Node node) {
		    StringWriter sw = new StringWriter();
		    try {
		      Transformer t = TransformerFactory.newInstance().newTransformer();
		      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		      t.setOutputProperty(OutputKeys.INDENT, "yes");
		      t.transform(new DOMSource(node), new StreamResult(sw));
		    } catch (TransformerException te) {
		      System.out.println("nodeToString Transformer Exception");
		    }
		    return sw.toString();
		  }
	public NodeList getElementsViaXPath(String xpath, Document doc) {
		//Evaluate XPath against Document itself
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		try {
			nodes = (NodeList)xPath.compile(xpath).evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return nodes;
	}
}
