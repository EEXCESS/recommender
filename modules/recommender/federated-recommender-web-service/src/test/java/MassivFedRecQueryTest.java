import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBContext;

import org.junit.Test;
import org.xml.sax.SAXNotRecognizedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;


public class MassivFedRecQueryTest {
	//private static final String searchRequest = "http://eexcess.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
	//private static final String searchRequest = "http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
private static final String searchRequest = "http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
//	private static final String searchRequest = "http://localhost/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
	//private static final String searchRequest = "http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-evaluation-1.0-SNAPSHOT/evaluation/recommend";
	private final ExecutorService threadPool;
	public  MassivFedRecQueryTest() {
		threadPool= Executors.newFixedThreadPool(30);
		
	}
	@Test
	public void singleQuery() {
		final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
		List<String> queryList = test.getQueryList();
		HashMap<String,Future<String>> futures = new HashMap<String,Future<String>>();
		for (int i = 0; i < 30; i++) {
			for (final String string : queryList) {

				Future<String> future = threadPool.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						long start = System.currentTimeMillis();
						ResultList resList= test.submitQueryToFedRec(string);
						long end = System.currentTimeMillis();
						return "" +(end - start) +"ms numResults:"+resList.totalResults;
					}

				});

				futures.put(string + i, future);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (String string : futures.keySet()) {
			try {
				System.out.println("Query "+string+" took "+ futures.get(string).get(5000, TimeUnit.MILLISECONDS));
			futures.get(string).cancel(true);	
			} catch (InterruptedException | ExecutionException
					| TimeoutException e) {
				futures.get(string).cancel(true);
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				System.out.println("Timeout "+ string);
				
			}
		}
		futures.clear();
		threadPool.shutdown();
		assertTrue(true);
	}
	
	@Test
	public void singleOnceQuery() {
		final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
		List<String> queryList = test.getQueryList();
		HashMap<String,Future<String>> futures = new HashMap<String,Future<String>>();
			for (final String string : queryList) {

				Future<String> future = threadPool.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						long start = System.currentTimeMillis();
						ResultList resList= test.submitQueryToFedRec(string);
						long end = System.currentTimeMillis();
						return "" +(end - start) +"ms numResults:"+resList.totalResults;
					}

				});

				futures.put(string , future);
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		for (String string : futures.keySet()) {
			try {
				System.out.println("Query "+string+" took "+ futures.get(string).get(5000, TimeUnit.MILLISECONDS));
			futures.get(string).cancel(true);	
			} catch (InterruptedException | ExecutionException
					| TimeoutException e) {
				futures.get(string).cancel(true);
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				System.out.println("Timeout "+ string);
				
			}
		}
		futures.clear();
		threadPool.shutdown();
		assertTrue(true);
	}


	@Test
	public void singleOnceSingleThreadedQuery() {
		final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
		List<String> queryList = test.getQueryList();
//		HashMap<String,Future<String>> futures = new HashMap<String,Future<String>>();
			for (final String string : queryList) {
				
						long start = System.currentTimeMillis();
						ResultList resList = null;
						try {
							resList = test.submitQueryToFedRec(string);
						} catch (SAXNotRecognizedException e) {
							// TODO Auto-generated catch block
					
						}
						long end = System.currentTimeMillis();
						if(resList!=null)
								System.out.println( "" +(end - start) +"ms numResults:"+resList.totalResults);
						else
							System.out.println( "" +(end - start) +"ms numResults:"+0);
					}
			}
	

	
	protected ResultList submitQueryToFedRec(String string) throws SAXNotRecognizedException {
		  WebResource service = null;
		
		Client client;
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JAXBContext.class);
        client = Client.create(config);
        service = client.resource(searchRequest);
        return service.post(ResultList.class,createSecureUserProfileFromString(string));
		
	}
	private SecureUserProfile createSecureUserProfileFromString(String string) {
		SecureUserProfile sUserProfile = new SecureUserProfile();
		sUserProfile.numResults=10;
		sUserProfile.contextKeywords.add(new ContextKeyword(string));
		return sUserProfile;
	}
	@Test
	public void multiQuery() {
		final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
		List<String> queryList = test.getQueryList();
		HashMap<String,Future<String>> futures = new HashMap<String,Future<String>>();
		for (int i = 0; i < 1; i++) {
			try {
			    Thread.sleep(5000);             
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			 String prevString=" ";
			for (final String string : queryList) {
				if(prevString.length()==0)
					prevString += " "+string +" ";
				else{
					final String query = prevString +" "+  string +" ";
					prevString=" ";
				Future<String> future = threadPool.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						long start = System.currentTimeMillis();
						
						ResultList resList= test.submitQueryToFedRec(query);
						
						long end = System.currentTimeMillis();
						return "" +(end - start) +"ms numResults:"+resList.totalResults;
					}

				});

				futures.put(string + i, future);
				}
			}
		}
		
		for (String string : futures.keySet()) {
			try {
				System.out.println("Query "+string+" took "+ futures.get(string).get(5000, TimeUnit.MILLISECONDS));
//				futures.get(string).cancel(true);		
			} catch (InterruptedException | ExecutionException
					| TimeoutException e) {
				futures.get(string).cancel(true);	
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				System.out.println("Timeout "+ string);
				
			}
		}
		futures.clear();
		threadPool.shutdown();
		assertTrue(true);
	}
	@Test
	public void combinedQuery() {
		final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
		
		for (String query : getQueryList()) {
			if(true){
				long start = System.currentTimeMillis();
				
				ResultList resList = null;
				try {
					resList = test.submitQueryToFedRec(query);
				} catch (SAXNotRecognizedException e) {
					// TODO Auto-generated catch block
				}
				
				long end = System.currentTimeMillis();
				if(resList!=null)
					System.out.println( "" +(end - start) +"ms numResults:"+resList.totalResults);
				else
					System.out.println( "" +(end - start) +"ms numResults:"+0);
			}
			for (String query2 : getQueryList()) {
				long start = System.currentTimeMillis();
				
				ResultList resList = null;
				try {
					resList = test.submitQueryToFedRec(query+ " "+ query2);
				} catch (SAXNotRecognizedException e) {
					// TODO Auto-generated catch block
				}
				
				long end = System.currentTimeMillis();
				if(resList!=null)
					System.out.println( "" +(end - start) +"ms numResults:"+resList.totalResults);
				else
					System.out.println( "" +(end - start) +"ms numResults:"+0);
					
			}
	
		}
		
	}
	private List<String> getQueryList() {
		List<String> queries = new ArrayList<String>();
		queries.add("Mozart");
		queries.add("Michelle Obama");
		queries.add("Schuhmacher");
		queries.add("financial crises");
		queries.add("sales crises");
		queries.add("housing");
		queries.add("science");
		queries.add("labour");
		queries.add("workforce");
		queries.add("research");
		queries.add("studies");
		queries.add("medical research");
		queries.add("Mercedes");
		queries.add("Napoleon");
		queries.add("geosciences");
		queries.add("pluto");
		queries.add("mars");
		queries.add("housing market");
		queries.add("financial market");
		queries.add("Schubert");
		queries.add("Kleine nachtmusik");
		queries.add("sternennacht");
		queries.add("van gogh sternennacht");
		queries.add("van gogh");
		queries.add("soul music");
		queries.add("renaissance");
		queries.add("antiquity");
		queries.add("The Four Stages of Cruelty");
		queries.add("William Hogarth ");
		queries.add("German Romantic");
		queries.add("Copenhagen");
		queries.add(" Surrealists ");
		queries.add("Expressionists");
		queries.add("The Royal March");
		queries.add("The Royal March Spain");
		queries.add("Marilyn Monroe");
		queries.add("Gustav Klimt");
		queries.add("Maximilian Carl Emil Weber ");
		queries.add("Freiburg University");
		queries.add("capitalism");
		queries.add("bureaucracy");
		queries.add("income inequality");
		queries.add("Cuban labor movement");
		queries.add("Cuban");
		queries.add("community development");
		queries.add("Global village");
		queries.add("history of Poland");
		queries.add("World War II");
		queries.add("red army");
		queries.add("Commonwealth of Kentucky");
		queries.add("Battle of Blue Licks");
		queries.add("Boston revolt ");
		queries.add("Boston revolt 1689");
		queries.add("Dominion of New England");
		queries.add("New England");
		queries.add("Massachusetts Bay Colony");
		queries.add("Khoikhoi");
		queries.add("Napoleon Bonaparte ");
		queries.add("Elba");
		queries.add("Napoleon Bonaparte  elba");
		queries.add(" Governor General of Canada");
		queries.add("French Indochina");
		queries.add("Indochina");
		queries.add(" Byzantine Empire ");
		queries.add(" Eastern Roman Empire ");
		
		return queries;
	}
	public static void main(String [ ] args){
		MassivFedRecQueryTest m=new MassivFedRecQueryTest();
		m.singleQuery();
	}

}
