package eu.eexcess.systemtesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;

import org.xml.sax.SAXNotRecognizedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

public class MassivFedRecQueryTest {
    // private static final String searchRequest =
    // "http://eexcess.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
    private final static Logger LOGGER = Logger.getLogger(MassivFedRecQueryTest.class.getName());
    private static String searchRequest;
    // =
    // "http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
    // private static final String searchRequest =
    // "http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
    // private static final String searchRequest =
    // "http://localhost/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
    // private static final String searchRequest =
    // "http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-evaluation-1.0-SNAPSHOT/evaluation/recommend";
    private ExecutorService threadPool;

    public MassivFedRecQueryTest(String serverUri) {
        searchRequest = serverUri;
    }

    // @Test
    // public void singleQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    // List<String> queryList = test.getQueryList();
    // HashMap<String,Future<String>> futures = new
    // HashMap<String,Future<String>>();
    // for (int i = 0; i < 30; i++) {
    // for (final String string : queryList) {
    //
    // Future<String> future = threadPool.submit(new Callable<String>() {
    // @Override
    // public String call() throws Exception {
    // long start = System.currentTimeMillis();
    // ResultList resList= test.submitQueryToFedRec(string);
    // long end = System.currentTimeMillis();
    // return "" +(end - start) +"ms numResults:"+resList.totalResults;
    // }
    //
    // });
    //
    // futures.put(string + i, future);
    // try {
    // Thread.sleep(300);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // for (String string : futures.keySet()) {
    // try {
    // System.out.println("Query "+string+" took "+
    // futures.get(string).get(5000, TimeUnit.MILLISECONDS));
    // futures.get(string).cancel(true);
    // } catch (InterruptedException | ExecutionException
    // | TimeoutException e) {
    // futures.get(string).cancel(true);
    // // TODO Auto-generated catch block
    // // e.printStackTrace();
    // System.out.println("Timeout "+ string);
    //
    // }
    // }
    // futures.clear();
    // threadPool.shutdown();
    // assertTrue(true);
    // }
    //

    // public void singleOnceQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    // List<String> queryList = test.getQueryList();
    // HashMap<String, Future<String>> futures = new HashMap<String,
    // Future<String>>();
    // for (final String string : queryList) {
    //
    // Future<String> future = threadPool.submit(new Callable<String>() {
    // @Override
    // public String call() throws Exception {
    // long start = System.currentTimeMillis();
    // ResultList resList = test.submitQueryToFedRec(string);
    // long end = System.currentTimeMillis();
    // return "" + (end - start) + "ms numResults:" + resList.totalResults;
    // }
    //
    // });
    //
    // futures.put(string, future);
    // try {
    // Thread.sleep(30);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    // for (String string : futures.keySet()) {
    // try {
    // System.out.println("Query " + string + " took " +
    // futures.get(string).get(5000, TimeUnit.MILLISECONDS));
    // futures.get(string).cancel(true);
    // } catch (Exception e) {
    // futures.get(string).cancel(true);
    // // TODO Auto-generated catch block
    // // e.printStackTrace();
    // System.out.println("Timeout " + string);
    //
    // }
    // }
    // futures.clear();
    // threadPool.shutdown();
    // assertTrue(true);
    // }

    public Results testPartnersWithSimulatnousThreads(Integer threadAmount, Integer sleepTime) {
        Results result = new Results();
        threadPool = Executors.newFixedThreadPool(threadAmount);
        Long start = System.currentTimeMillis();
        List<String> queryList = getQueryList();
        HashMap<String, Future<String>> futures = new HashMap<String, Future<String>>();
        for (final String string : queryList) {

            Future<String> future = threadPool.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    long start = System.currentTimeMillis();
                    ResultList resList = submitQueryToFedRec(string);
                    long end = System.currentTimeMillis();
                    return resList.totalResults + "";
                }
            });

            futures.put(string, future);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Could  not send thread to sleep", e);
            }
        }

        for (String string : futures.keySet()) {
            try {
                if (!futures.get(string).get(7000, TimeUnit.MILLISECONDS).equals("10"))
                    result.setFailure(result.getFailure() + 1);
            } catch (Exception e) {
                futures.get(string).cancel(true);
                result.setFailure(result.getFailure() + 1);
            }
        }
        futures.clear();
        threadPool.shutdown();
        Long stop = System.currentTimeMillis();
        result.setTime(stop - start);
        return result;
    }

    //
    //
    // @Test
    // public void singleOnceSingleThreadedQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    // List<String> queryList = test.getQueryList();
    // HashMap<String, Future<String>> futures = new HashMap<String,
    // Future<String>>();
    // for (final String string : queryList) {
    //
    // long start = System.currentTimeMillis();
    // ResultList resList = null;
    // try {
    // resList = test.submitQueryToFedRec(string);
    // } catch (SAXNotRecognizedException e) {
    // // TODO Auto-generated catch block
    //
    // }
    // long end = System.currentTimeMillis();
    // if (resList != null)
    // System.out.println("" + (end - start) + "ms numResults:" +
    // resList.totalResults);
    // else
    // System.out.println("" + (end - start) + "ms numResults:" + 0);
    // }
    // }

    //
    //
    //
    public ResultList submitQueryToFedRec(String string) throws SAXNotRecognizedException {
        WebResource service = null;
        Client client;
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JAXBContext.class);
        client = Client.create(config);
        service = client.resource(searchRequest);
        return service.post(ResultList.class, createSecureUserProfileFromString(string));

    }

    private SecureUserProfile createSecureUserProfileFromString(String string) {
        SecureUserProfile sUserProfile = new SecureUserProfile();
        sUserProfile.numResults = 10;
        sUserProfile.contextKeywords.add(new ContextKeyword(string));
        return sUserProfile;
    }

    // @Test
    // public void multiQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    // List<String> queryList = test.getQueryList();
    // HashMap<String,Future<String>> futures = new
    // HashMap<String,Future<String>>();
    // for (int i = 0; i < 1; i++) {
    // try {
    // Thread.sleep(5000);
    // } catch(InterruptedException ex) {
    // Thread.currentThread().interrupt();
    // }
    // String prevString=" ";
    // for (final String string : queryList) {
    // if(prevString.length()==0)
    // prevString += " "+string +" ";
    // else{
    // final String query = prevString +" "+ string +" ";
    // prevString=" ";
    // Future<String> future = threadPool.submit(new Callable<String>() {
    // @Override
    // public String call() throws Exception {
    // long start = System.currentTimeMillis();
    //
    // ResultList resList= test.submitQueryToFedRec(query);
    //
    // long end = System.currentTimeMillis();
    // return "" +(end - start) +"ms numResults:"+resList.totalResults;
    // }
    //
    // });
    //
    // futures.put(string + i, future);
    // }
    // }
    // }
    //
    // for (String string : futures.keySet()) {
    // try {
    // System.out.println("Query "+string+" took "+
    // futures.get(string).get(5000, TimeUnit.MILLISECONDS));
    // // futures.get(string).cancel(true);
    // } catch (InterruptedException | ExecutionException
    // | TimeoutException e) {
    // futures.get(string).cancel(true);
    // // TODO Auto-generated catch block
    // // e.printStackTrace();
    // System.out.println("Timeout "+ string);
    //
    // }
    // }
    // futures.clear();
    // threadPool.shutdown();
    // assertTrue(true);
    // }
    // @Test
    // public void combinedQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    //
    // for (String query : getQueryList()) {
    // if(true){
    // long start = System.currentTimeMillis();
    //
    // ResultList resList = null;
    // try {
    // resList = test.submitQueryToFedRec(query);
    // } catch (SAXNotRecognizedException e) {
    // // TODO Auto-generated catch block
    // }
    //
    // long end = System.currentTimeMillis();
    // if(resList!=null)
    // System.out.println( "" +(end - start)
    // +"ms numResults:"+resList.totalResults);
    // else
    // System.out.println( "" +(end - start) +"ms numResults:"+0);
    // }
    // for (String query2 : getQueryList()) {
    // long start = System.currentTimeMillis();
    //
    // ResultList resList = null;
    // try {
    // resList = test.submitQueryToFedRec(query+ " "+ query2);
    // } catch (SAXNotRecognizedException e) {
    // // TODO Auto-generated catch block
    // }
    //
    // long end = System.currentTimeMillis();
    // if(resList!=null)
    // System.out.println( "" +(end - start)
    // +"ms numResults:"+resList.totalResults);
    // else
    // System.out.println( "" +(end - start) +"ms numResults:"+0);
    //
    // }
    //
    // }
    //
    // }
    private List<String> getQueryList() {
        List<String> queries = new ArrayList<String>();
        queries.add("Mozart");
        queries.add("Michelle Obama");
        queries.add("Obama");
        queries.add("Schuhmacher");
        queries.add("financial crises");
        queries.add("sales crises");
        queries.add("crises");
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
        queries.add("market");
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
        queries.add("Royal March");
        queries.add("Royal March france");
        queries.add("The Royal March Spain");
        queries.add("Marilyn Monroe");
        queries.add("Gustav Klimt");
        queries.add("Maximilian Carl Emil Weber ");
        queries.add("Carl Emil Weber ");
        queries.add("Emil Weber ");
        queries.add("Freiburg University");
        queries.add("capitalism");
        queries.add("bureaucracy");
        queries.add("income inequality");
        queries.add("inequality");
        queries.add("labor movement");
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
        queries.add("Roman Missal");
        queries.add("Revolution of 1789");
        queries.add("trees of liberty");
        queries.add("peaceful revolution");
        queries.add("storming of the Bastille");
        queries.add("march to Versailles");
        queries.add("French Foreign Legionnaires");
        queries.add("Red Army");
        queries.add("Bolsheviks");
        queries.add("German reunification");
        queries.add("Battle of the Nile");
        queries.add("Alexandria");
        queries.add("fall of Rome");
        queries.add("Rome");
        queries.add("Byzantine");
        queries.add("Anglo-American War");
        queries.add("Indian Wars");
        queries.add("Andrew Jackson");
        queries.add("Hanoi 1883");
        queries.add("Semyonov");
        queries.add("munich ruins");
        queries.add("munich");
        queries.add("ruins");
        queries.add("Revolution royal family paris versailles");
        queries.add("royal family paris");
        queries.add("tibet holy warrior");
        queries.add("holy warrior");
        queries.add("French Revolution 1789 journalists");
        queries.add("Revolution of 1830");
        queries.add("American Revolution");
        queries.add("Jacques Necker");
        queries.add("Ottoman Empire");
        queries.add("Mississippi river");
        queries.add("joseph stalin");
        queries.add("civil rights movement");
        queries.add("civil rights women");
        queries.add("rights women");
        queries.add("pregnancy discrimination");
        queries.add("discrimination");
        queries.add("migrant domestic workers");
        queries.add("domestic workers");
        queries.add("chemical warfare");
        queries.add("warfare");
        queries.add("hundred days offensive");
        queries.add("council of the five hundred");
        queries.add("chlorine");
        queries.add("mustard gas");
        queries.add("civil war finland");
        queries.add("mace");
        queries.add("battle axe");
        queries.add("axe");
        queries.add("medieval sword");
        queries.add("sword");
        queries.add("franco-thai war");
        queries.add("battle of koh chang");
        queries.add("koh chang");
        queries.add("knight horse");
        queries.add("knight");
        queries.add("horse");
        queries.add("manned torpedo");
        queries.add("torpedo");
        queries.add("mussolini");
        queries.add("civil rights");
        queries.add("resurgence of imperialism");
        queries.add("imperialism");
        queries.add("Industrial Revolution");
        queries.add("River Isar");
        queries.add("isar");
        queries.add("Red Stick War");
        queries.add("Red Stick");
        queries.add("Mexican-American War");
        queries.add("American War");
        queries.add("Mexican War");
        queries.add("National Security Agency");
        queries.add("NSA");
        queries.add("National Security");

        return queries;
    }

    public static void main(String[] args) {

        MassivFedRecQueryTest m = new MassivFedRecQueryTest(
                "http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend");
        try {
            m.submitQueryToFedRec("test");
        } catch (SAXNotRecognizedException e) {
            LOGGER.log(Level.INFO, "Could not send warming query to federated recommender", e);
        }
        Integer simThreads = 10;
        Integer threadTimeout = 10;

        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 30;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 35;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 40;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 50;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 80;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 100;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
        simThreads = 150;
        threadTimeout = 50;
        m.logResult(simThreads, threadTimeout, m.testPartnersWithSimulatnousThreads(simThreads, threadTimeout));
    }

    private void logResult(Integer simThreads, Integer threadTimeout, Results results) {
        LOGGER.log(Level.INFO, "Simultainous Threads: " + simThreads + " Time between calls: " + threadTimeout + " Time to get all results: " + results.getTime() + " Failure: "
                + results.getFailure());
    }

    class Results {
        private long time = 0;
        private Integer failure = 0;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public Integer getFailure() {
            return failure;
        }

        public void setFailure(Integer failure) {
            this.failure = failure;
        }
    }
}
