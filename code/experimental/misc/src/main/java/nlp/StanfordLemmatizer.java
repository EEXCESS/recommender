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
package nlp;

//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Properties;
//
//import javax.ws.rs.core.MediaType;
//
//import sqlite.Database;
//
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
////
////import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
////import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
////import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
////import edu.stanford.nlp.ling.CoreLabel;
////import edu.stanford.nlp.pipeline.Annotation;
////import edu.stanford.nlp.pipeline.StanfordCoreNLP;
////import edu.stanford.nlp.util.CoreMap;
//import eu.eexcess.dataformats.PartnerBadge;
//import eu.eexcess.dataformats.result.Result;
//import eu.eexcess.dataformats.result.ResultList;
//import eu.eexcess.dataformats.userprofile.ContextKeyword;
//import eu.eexcess.dataformats.userprofile.SecureUserProfile;
//import eu.eexcess.federatedrecommender.registration.PartnerRegister;
//import eu.eexcess.partnerrecommender.reference.PartnerRecommender;

/*
 * See http://stackoverflow.com/questions/1578062/lemmatization-java
 * 
 * Do not forget to include models in maven dependencies:
 * 
 * 
 * @author plopez
 */
@Deprecated
public class StanfordLemmatizer {

//    protected StanfordCoreNLP pipeline;
//
//    public StanfordLemmatizer() {
//        // Create StanfordCoreNLP object properties, with POS tagging
//        // (required for lemmatization), and lemmatization
//        Properties props;
//        props = new Properties();
//        props.put("annotators", "tokenize, ssplit, pos, lemma");
//
//        /*
//         * This is a pipeline that takes in a string and returns various analyzed linguistic forms. 
//         * The String is tokenized via a tokenizer (such as PTBTokenizerAnnotator), 
//         * and then other sequence model style annotation can be used to add things like lemmas, 
//         * POS tags, and named entities. These are returned as a list of CoreLabels. 
//         * Other analysis components build and store parse trees, dependency graphs, etc. 
//         * 
//         * This class is designed to apply multiple Annotators to an Annotation. 
//         * The idea is that you first build up the pipeline by adding Annotators, 
//         * and then you take the objects you wish to annotate and pass them in and 
//         * get in return a fully annotated object.
//         * 
//         *  StanfordCoreNLP loads a lot of models, so you probably
//         *  only want to do this once per execution
//         */
//        this.pipeline = new StanfordCoreNLP(props);
//    }
//
//    public List<String> lemmatize(String documentText)
//    {
//        List<String> lemmas = new LinkedList<String>();
//        // Create an empty Annotation just with the given text
//        Annotation document = new Annotation(documentText);
//        // run all Annotators on this text
//        this.pipeline.annotate(document);
//        // Iterate over all of the sentences found
//        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//        for(CoreMap sentence: sentences) {
//            // Iterate over all tokens in a sentence
//            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
//                // Retrieve and add the lemma for each word into the
//                // list of lemmas
//                lemmas.add(token.get(LemmaAnnotation.class));
//            }
//        }
//        return lemmas;
//    }
//
//    
//	private static final String DATABASE = "frequency.sqlite";
//	private static final String ENDPOINT = "http://localhost:8101/eexcess/partner/recommend";
//
//    public static void main(String[] args) throws SQLException {
//    	
//		/* Set up database connection */
//		
//		Connection dbConnection = null;;
//		
//		Statement stat = null;
//		
//        
//		try {
//			
//			dbConnection = Database.connect(DATABASE);
//			
//			//ArrayList elements = new ArrayList();
//			dbConnection.setAutoCommit(false);
//		    stat = dbConnection.createStatement();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		
//        System.out.println("Starting Stanford Lemmatizer");
//
//        
//        /*
//        String text = "How could you be seeing into my eyes like open doors? \n"+
//                "You led me down into my core where I've became so numb \n"+
//                "Without a soul my spirit's sleeping somewhere cold \n"+
//                "Until you find it there and led it back home \n"+
//                "You woke me up inside \n"+
//                "Called my name and saved me from the dark \n"+
//                "You have bidden my blood and it ran \n"+
//                "Before I would become undone \n"+
//                "You saved me from the nothing I've almost become \n"+
//                "You were bringing me to life \n"+
//                "Now that I knew what I'm without \n"+
//                "You can've just left me \n"+
//                "You breathed into me and made me real \n"+
//                "Frozen inside without your touch \n"+
//                "Without your love, darling \n"+
//                "Only you are the life among the dead \n"+
//                "I've been living a lie, there's nothing inside \n"+
//                "You were bringing me to life.";
//        */
//
//        SecureUserProfile p = new SecureUserProfile();
//		//p.contextList.add("women");
//		//p.contextList.add("labor");
//		p.contextKeywords.add( new ContextKeyword("europe"));
//		
//		//p.interestList.add("society");
//		//p.interestList.add("tourism");
//		//p.interestList.add("gastronomy");
//		
//		System.out.println(p.toString());
//        
//        StanfordLemmatizer slem = new StanfordLemmatizer();
//        
//        /*
//        for (String lemma : slem.lemmatize(p.toFreeText()))
//        {
//        	System.out.println(lemma);
//        }
//        */
//        
//        /* Query partner */
//        
//		
//        WebResource resource = new Client().resource(ENDPOINT);
//	    resource.accept(MediaType.APPLICATION_XML);
//		ResultList resultList = resource.post(ResultList.class, p);
//		
//		
//
//		
//		
//		// http://stackoverflow.com/questions/3647454/increment-counter-or-insert-row-in-one-statement-in-sqlite
//		
//	    for (Result r : resultList.results)
//	    {
//	    	String title = r.title;
//	    	
//	    	for (String lemma : slem.lemmatize(r.title))
//	    	{
//	    		//System.out.println(r.title + " -----> " + lemma);
//    	
//	    		String updateQuery = updateQuery(lemma.toLowerCase(), "Mendeley");
//	    		
//	    		//System.out.println(updateQuery);
//	    		
//			    int count = stat.executeUpdate(updateQuery);
//			  
//
//
//				
//				//con.setAutoCommit(false); // Disable autocommit (very slow, one commit per update)
//		        //Statement stat = con.createStatement();
//		        
//		        //stat.executeQuery(sql)
//		        
//	            //con.commit();
//	/*
//				ArrayList<ResultSet> results = Database.getAllResultsets("Frequency");
//				
//				for(ResultSet result : results )
//				{
//					System.out.println("*");
//					System.out.println(result.toString());
//					System.out.println(result.getString(1));
//				}
//				
//				
//	            con.close();
//	            Database.disconnect();
//	            
//	            */
//		          	          
//	    	}
//	    }
//	    
//	    dbConnection.commit();
//	    
//	    Database.disconnect();
//        
//    }
//
//    private static void updateModel(String term, String partner)
//    {
//    	
//    }
//    
//	private static String updateQuery(String term, String partner)
//	{
//		return "INSERT OR REPLACE INTO Mendeley VALUES ('" + term + "', COALESCE((SELECT count FROM " + partner + " WHERE term='" + term + "'), 0) + 1);";
//	}
//    
//    // Warning: Duplicated code
//	private static ResultList getPartnerResult(SecureUserProfile secureUserProfile) {
//		
//		String ENDPOINT = "http://localhost:8101/eexcess/partner/recommend";
//		
//        WebResource resource = new Client().resource(ENDPOINT);
//	    resource.accept(MediaType.APPLICATION_XML);
//		ResultList resultList = resource.post(ResultList.class, secureUserProfile);
//	    
//        StanfordLemmatizer slem = new StanfordLemmatizer();
//		
//	    for (Result r : resultList.results)
//	    {
//	    	String title = r.title;
//	    	
//	    	for (String lemma : slem.lemmatize(r.title))
//	    	{
//	    	System.out.println(r.title + " -----> " + slem.lemmatize(r.title));
//	    	}
//	    	
//	    };
//	    
//		return resultList;
//	}
}
