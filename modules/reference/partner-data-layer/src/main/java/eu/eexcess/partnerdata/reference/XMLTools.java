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
package eu.eexcess.partnerdata.reference;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class XMLTools {


	public static String getStringFromDocument(Document doc)
    {
        try
        {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           javax.xml.transform.Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           return writer.toString();
        }
        catch(TransformerException ex)
        {
           ex.printStackTrace();
           return null;
        }
    }
	
	
	public static String writeModel(Model model) {
		model.setNsPrefix("wgs84", "http://www.w3.org/2003/01/geo/wgs84-pos/");
		model.setNsPrefix("dbpedia", "http://dbpedia.org/ontology/");
		model.setNsPrefix("freebase", "http://www.freebase.com/");
		model.setNsPrefix("Schema", "http://schema.org/");
		String syntax = "RDF/XML-ABBREV"; 
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		String ret = out.toString();
		return ret;
	}
	
	public static String writeModelNQuads(Model model) {
		String syntax = "NQUADS"; 
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		String ret = out.toString();
		return ret;
	}

	public static String writeModelJsonLD(Model model) {
		String syntax = "JSONLD"; 
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		String ret = out.toString();
		return ret;
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

	
	public static OntModel createModel(Document input) {
		OntModel model = ModelFactory.createOntologyModel();
   		String inputString = XMLTools.getStringFromDocument(input);
   		//System.out.println(inputString);
   		StringReader stream = new StringReader(inputString);
   		model.read(stream,null);
		return model;
	}


}
