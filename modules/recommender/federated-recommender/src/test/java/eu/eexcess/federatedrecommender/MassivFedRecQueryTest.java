package eu.eexcess.federatedrecommender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MassivFedRecQueryTest {
    // private static final String searchRequest =
    // "http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
    // private static final String searchRequest =
    // "http://localhost/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
    private static final String searchRequest = "http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";

    private final ExecutorService threadPool;

    public MassivFedRecQueryTest() {
        threadPool = Executors.newFixedThreadPool(30);

    }

    // @Test
    // public void singleQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    // List<String> queryList = test.getQueryList();
    // HashMap<String, Future<String>> futures = new HashMap<String,
    // Future<String>>();
    // for (int i = 0; i < 2; i++) {
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
    // futures.put(string + i, future);
    // try {
    // Thread.sleep(1500);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // for (String string : futures.keySet()) {
    // try {
    // System.out.println("Query " + string + " took " +
    // futures.get(string).get(5000, TimeUnit.MILLISECONDS));
    // futures.get(string).cancel(true);
    // } catch (InterruptedException | ExecutionException | TimeoutException e)
    // {
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
    //
    // protected ResultList submitQueryToFedRec(String string) throws
    // SAXNotRecognizedException {
    // WebResource service = null;
    //
    // Client client;
    // ClientConfig config = new DefaultClientConfig();
    // config.getClasses().add(JAXBContext.class);
    // client = Client.create(config);
    // service = client.resource(searchRequest);
    // return service.post(ResultList.class,
    // createSecureUserProfileFromString(string));
    //
    // }
    //
    // private SecureUserProfile createSecureUserProfileFromString(String
    // string) {
    // SecureUserProfile sUserProfile = new SecureUserProfile();
    // sUserProfile.numResults = 20;
    // sUserProfile.contextKeywords.add(new ContextKeyword(string));
    // return sUserProfile;
    // }
    //
    // @Test
    // public void multiQuery() {
    // final MassivFedRecQueryTest test = new MassivFedRecQueryTest();
    // List<String> queryList = test.getQueryList();
    // HashMap<String, Future<String>> futures = new HashMap<String,
    // Future<String>>();
    // for (int i = 0; i < 30; i++) {
    // try {
    // Thread.sleep(5000);
    // } catch (InterruptedException ex) {
    // Thread.currentThread().interrupt();
    // }
    // String prevString = "";
    // for (final String string : queryList) {
    // if (prevString.length() == 0)
    // prevString += string;
    // else {
    // final String query = prevString + string;
    // prevString = "";
    // Future<String> future = threadPool.submit(new Callable<String>() {
    // @Override
    // public String call() throws Exception {
    // long start = System.currentTimeMillis();
    //
    // ResultList resList = test.submitQueryToFedRec(query);
    //
    // long end = System.currentTimeMillis();
    // return "" + (end - start) + "ms numResults:" + resList.totalResults;
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
    // System.out.println("Query " + string + " took " +
    // futures.get(string).get(5000, TimeUnit.MILLISECONDS));
    // // futures.get(string).cancel(true);
    // } catch (InterruptedException | ExecutionException | TimeoutException e)
    // {
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
    //
    // private List<String> getQueryList() {
    // List<String> queries = new ArrayList<String>();
    // queries.add("Mozart");
    // queries.add("Michelle Obama");
    // queries.add("Schuhmacher");
    // queries.add("financial crises");
    // queries.add("sales crises");
    // queries.add("housing");
    // queries.add("science");
    // queries.add("labour");
    // queries.add("workforce");
    // queries.add("research");
    // queries.add("studies");
    // queries.add("medical research");
    // queries.add("Mercedes");
    // queries.add("Napoleon");
    // queries.add("geosciences");
    // queries.add("pluto");
    // queries.add("mars");
    // queries.add("snickers");
    // queries.add("Anaesthetic management");
    //
    // return queries;
    // }

    public static void main(String[] args) {
        MassivFedRecQueryTest m = new MassivFedRecQueryTest();
        // m.singleQuery();
    }

}
