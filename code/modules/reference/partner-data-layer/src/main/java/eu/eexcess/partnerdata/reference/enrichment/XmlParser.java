/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.partnerdata.reference.enrichment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.PartnerdataTracer.FILETYPE;
import eu.eexcess.partnerdata.reference.XMLTools;


public class XmlParser {

	public static Set<String> getWordsFromAnchorTranscriptLIMSIXml(String xmlFilePath, String startTimeString, String endTimeString)
	{
		Document dom=parseXmlFile(xmlFilePath);
		HashSet<String> resultWordSet=new HashSet<String>();

		//time
		int startSec=TimeString.MinSecToSec(startTimeString);
		int endSec=TimeString.MinSecToSec(endTimeString);

		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of word elements
		NodeList nl = docEle.getElementsByTagName("Word");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the word element
				Element elWord = (Element)nl.item(i);
				String word=elWord.getFirstChild().getNodeValue();

				//check its time
				String wordTimeString=elWord.getAttribute("stime");
				int wordTimeSec=TimeString.SecMiliToSec(wordTimeString);

				if (wordTimeSec>=startSec && wordTimeSec<=endSec)
				{
					//remove first and last empty characters ' '
					word=word.substring(1, word.length()-1).toLowerCase();
					
					word=StringNormalizer.removeStopMarks(word);
					
					if (word.length()>0)
					{
						resultWordSet.add(word);
					}
				}
			}
		}
		return resultWordSet;
	}

	public static Map<String,WordTiming> getNounsAndTimingsFromTranscriptLIMSIXml(String xmlFilePath, WordFilter wordFilter, PartnerdataLogger logger)
	{
		Document dom=parseXmlFile(xmlFilePath);
		Map<String,WordTiming> resulNounsTimings=new Hashtable<String,WordTiming>();

		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of word elements
		NodeList nl = docEle.getElementsByTagName("Word");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the word element
				Element elWord = (Element)nl.item(i);
				String word=elWord.getFirstChild().getNodeValue();

				//remove first and last empty characters ' ', and normalize to lower case
				word=word.substring(1, word.length()-1).toLowerCase();
				
				word=StringNormalizer.removeStopMarks(word);

				//check if it's noun
				if (wordFilter.isKeyWord(word, logger))
				{
					//check if it's already in collection
					WordTiming wordTiming=resulNounsTimings.get(word);
					if (wordTiming==null)
					{
						wordTiming=new WordTiming(word);
						String timing=elWord.getAttribute("stime");
						Integer normalizedTiming=TimeString.SecMiliToSec(timing);
						wordTiming.addTiming(normalizedTiming.toString());
						resulNounsTimings.put(word, wordTiming);						
					}
					else
					{
						String timing=elWord.getAttribute("stime");
						Integer normalizedTiming=TimeString.SecMiliToSec(timing);
						wordTiming.addTiming(normalizedTiming.toString());
					}	
				}
			}
		}
		return resulNounsTimings;
	}

	public static Map<String,WordTiming> getNounsAndTimingsFromSubtitlesXml(String xmlFilePath, WordFilter wordFilter, PartnerdataLogger logger)
	{
		Document dom=parseXmlFile(xmlFilePath);
		Map<String,WordTiming> resulNounsTimings=new Hashtable<String,WordTiming>();

		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of word elements
		NodeList nl = docEle.getElementsByTagName("p");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				Element elWord = (Element)nl.item(i);
				String timing=elWord.getAttribute("begin");
				Integer normalizedTiming=TimeString.HoursMinSecToSec(timing);

				//get the words elements from paragraph
				List<String> wordsParList=new ArrayList<String>();
				NodeList wordsParagraphs=elWord.getElementsByTagName("span");
				for (int j=0;j<wordsParagraphs.getLength();j++)
				{
					Node wordsSpanNode=wordsParagraphs.item(j).getFirstChild();
					if (wordsSpanNode==null)
					{
						continue;
					}
					String wordsSpan=	wordsSpanNode.getNodeValue();
					if (wordsSpan==null)
					{
						continue;
					}

					String [] wordsSpanArray=wordsSpan.toLowerCase().split(" ");

					for(String s: wordsSpanArray)
					{
						s=StringNormalizer.removeStopMarks(s);
						
						if (s.length()>0)
						{
							wordsParList.add(s);
						}
					}
				}

				for (String w: wordsParList)
				{
					//check if it's noun
					if (wordFilter.isKeyWord(w, logger))
					{
						//check if it's already in collection
						WordTiming wordTiming=resulNounsTimings.get(w);
						if (wordTiming==null)
						{
							wordTiming=new WordTiming(w);
							wordTiming.addTiming(normalizedTiming.toString());
							resulNounsTimings.put(w, wordTiming);						
						}
						else
						{
							wordTiming.addTiming(normalizedTiming.toString());
						}	
					}
				}
			}
		}
		return resulNounsTimings;
	}
	
	public static Set<String> getWordsFromAnchorSubtitlesXml(String xmlFilePath, String startTimeString, String endTimeString)
	{
		
		Document dom=parseXmlFile(xmlFilePath);
		HashSet<String> resultWordSet=new HashSet<String>();

		//time
		int startSec=TimeString.MinSecToSec(startTimeString);
		int endSec=TimeString.MinSecToSec(endTimeString);

		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of word elements
		NodeList nl = docEle.getElementsByTagName("p");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				Element elWord = (Element)nl.item(i);
				String timing=elWord.getAttribute("begin");
				Integer normalizedTiming=TimeString.HoursMinSecToSec(timing);
				
				//check its time
				// if NOT in the right interval, then continue 
				if (!(normalizedTiming>=startSec && normalizedTiming<=endSec))
				{
					continue;
				}
				/////////////

				//get the words elements from paragraph
				NodeList wordsParagraphs=elWord.getElementsByTagName("span");
				for (int j=0;j<wordsParagraphs.getLength();j++)
				{
					Node wordsSpanNode=wordsParagraphs.item(j).getFirstChild();
					if (wordsSpanNode==null)
					{
						continue;
					}
					String wordsSpan=	wordsSpanNode.getNodeValue();
					if (wordsSpan==null)
					{
						continue;
					}

					String [] wordsSpanArray=wordsSpan.toLowerCase().split(" ");

					for(String s: wordsSpanArray)
					{
						s=StringNormalizer.removeStopMarks(s);
						
						if (s.length()>0)
						{
							resultWordSet.add(s);
						}
					}
				}
			}
		}
		return resultWordSet;
	}

	public static List<Anchor> getAnchors(String xmlFilePath)
	{
		Document dom=parseXmlFile(xmlFilePath);
		ArrayList<Anchor> resultAnchorList=new ArrayList<Anchor>();

		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of <anchor> elements
		NodeList nl = docEle.getElementsByTagName("anchor");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the anchor element and its item
				Element elAnchor = (Element)nl.item(i);
				Element elAnchorItem=(Element) elAnchor.getElementsByTagName("item").item(0);

				//Anchor properties
				String id=elAnchor.getElementsByTagName("anchorId").item(0).getFirstChild().getNodeValue();
				String anchorStartTime=elAnchor.getElementsByTagName("startTime").item(0).getFirstChild().getNodeValue();
				String anchorEndTime=elAnchor.getElementsByTagName("endTime").item(0).getFirstChild().getNodeValue();
				String fileName=elAnchorItem.getElementsByTagName("fileName").item(0).getFirstChild().getNodeValue();
				String itemStartTime=elAnchorItem.getElementsByTagName("startTime").item(0).getFirstChild().getNodeValue();
				String itemEndTime=elAnchorItem.getElementsByTagName("endTime").item(0).getFirstChild().getNodeValue();


				//Remove 'v' from beginning of the name
				fileName=fileName.substring(1, fileName.length());

				//create anchor element and add it to list
				Anchor anchor=new Anchor(id, fileName, anchorStartTime, anchorEndTime, itemStartTime, itemEndTime);
				resultAnchorList.add(anchor);
			}
		}
		return resultAnchorList;
	}

	public static Set<DbpediaSpotlightResult> getEntitiesDbpediaSpotlightCandidatesXML(PartnerConfiguration config, InputStream xmlContentStream, PartnerdataLogger logger)
	{
		Document dom=parseXmlFromStream(xmlContentStream);
		Set<DbpediaSpotlightResult> resultEntitySet=new HashSet<DbpediaSpotlightResult>();

		//get the root elememt
		if (dom == null) return resultEntitySet;
        PartnerdataTracer.dumpFile(DbpediaSpotlight.class,config, XMLTools.getStringFromDocument(dom), "dbpedia-response", FILETYPE.XML, logger);
		Element docEle = dom.getDocumentElement();

		//get a nodelist of word elements
		NodeList nl = docEle.getElementsByTagName("surfaceForm");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				//get the element
				Element surfaceFormElement = (Element)nl.item(i);
				String entity=surfaceFormElement.getAttribute("name");
				entity=StringNormalizer.removeStopMarks(entity);
				DbpediaSpotlightResult result = new DbpediaSpotlightResult();
				result.setName(entity);
				if (surfaceFormElement.hasChildNodes()) 
				{
					NodeList surfaceFormChilds = surfaceFormElement.getChildNodes();
					if(surfaceFormChilds != null && surfaceFormChilds.getLength() > 0) {
						for(int j = 0 ; j < surfaceFormChilds.getLength();j++) {
							Node resourceElement = surfaceFormChilds.item(j);
							if (resourceElement.getNodeType() == Node.ELEMENT_NODE) {
								if (resourceElement.getAttributes() != null && resourceElement.getAttributes().getNamedItem("uri") != null)
								{
									String uri = resourceElement.getAttributes().getNamedItem("uri").getNodeValue();
									result.setUri(uri);
								}
								if (resourceElement.getAttributes() != null && resourceElement.getAttributes().getNamedItem("types") != null)
								{
									String types = resourceElement.getAttributes().getNamedItem("types").getNodeValue();
									StringTokenizer typesTokenizer = new StringTokenizer(types,",");
									while (typesTokenizer.hasMoreElements()) {
										result.addType(((String) typesTokenizer.nextElement()).trim());
									}
								}
							}
							
						}
					}
				}
				resultEntitySet.add(result);
			}
		}
		return resultEntitySet;
	}

	public static Set<DbpediaSpotlightResult> getEntitiesDbpediaSpotlightAnnotateXML(PartnerConfiguration config, InputStream xmlContentStream, PartnerdataLogger logger)
	{
		Document dom=parseXmlFromStream(xmlContentStream);
		Set<DbpediaSpotlightResult> resultEntitySet=new HashSet<DbpediaSpotlightResult>();
		//String debug = XMLTools.getStringFromDocument(dom);
		//get the root elememt
		if (dom == null) return resultEntitySet;
        PartnerdataTracer.dumpFile(DbpediaSpotlight.class,config, XMLTools.getStringFromDocument(dom), "dbpedia-response", FILETYPE.XML, logger);
		Element annotationElement = dom.getDocumentElement();
		String text="";
		if (annotationElement.getAttributes() != null && annotationElement.getAttributes().getNamedItem("text") != null)
		{
			text = annotationElement.getAttributes().getNamedItem("text").getNodeValue();
		}
		NodeList annotationChilds = annotationElement.getChildNodes();
		if(annotationChilds != null && annotationChilds.getLength() > 0) {
			for(int j = 0 ; j < annotationChilds.getLength();j++) {
				Node resourcesElement = annotationChilds.item(j);
				if (resourcesElement.getNodeType() == Node.ELEMENT_NODE) {
					NodeList resourcesChilds = resourcesElement.getChildNodes();
					if(resourcesChilds != null && resourcesChilds.getLength() > 0) {
						for(int k = 0 ; k < resourcesChilds.getLength();k++) {
							Node resourceElement = resourcesChilds.item(j);
							if (resourceElement.getNodeType() == Node.ELEMENT_NODE) {
								
								DbpediaSpotlightResult result = new DbpediaSpotlightResult();
								result.setName(text);
								if (resourceElement.getAttributes() != null && resourceElement.getAttributes().getNamedItem("URI") != null)
								{
									String uri = resourceElement.getAttributes().getNamedItem("URI").getNodeValue();
									result.setUri(uri);
								}								
								if (resourceElement.getAttributes() != null && resourceElement.getAttributes().getNamedItem("types") != null)
								{
									String types = resourceElement.getAttributes().getNamedItem("types").getNodeValue();
									StringTokenizer typesTokenizer = new StringTokenizer(types,",");
									while (typesTokenizer.hasMoreElements()) {
										result.addType(((String) typesTokenizer.nextElement()).trim());
									}
								}
								resultEntitySet.add(result);
								
							}
						}
					}
				}
			}
		}
		return resultEntitySet;
	}

	public static Document parseXmlFile(String xmlFilePath) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom=null;

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlFilePath);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return dom;
	}

	public static Document parseXmlFromStream(InputStream xmlContentStream) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom=null;

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlContentStream);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return dom;
	}
}
