package eu.eexcess.test;

import static org.junit.Assert.*;


/**
 * Unit test for simple App.
 */
public class PrivacyProxyDevelopmentTest 
{

	public void log(String out)
	{
		System.out.println("Development: " + out);
	}
	
    @org.junit.Test
    public void testZBW_Women_workforce_china()
    {
        PrivacyProxy tester = new PrivacyProxy();
		tester.addContext("women workforce china");
		tester.recommendDevServer();
		
		assertTrue(tester.getResponseString().toLowerCase().contains("women"));
		
		String provider = "ZBW";
		int foundSoll = 4;
		checkProvider(tester, provider, foundSoll);		
    }

    @org.junit.Test
    public void testKIMCollect_bierglas()
    {
    	PrivacyProxy tester = new PrivacyProxy();
		tester.addContext("bierglas");
		tester.recommendDevServer();
		
		assertTrue(tester.getResponseString().toLowerCase().contains("bierglas"));

		String provider = "KIM.Collect";
		int foundSoll = 7;
		checkProvider(tester, provider, foundSoll);		
    }

    @org.junit.Test
    public void testWissenserver_Immanuel_Kant()
    {
    	PrivacyProxy tester = new PrivacyProxy();
		tester.addContext("immanuel kant");
		tester.recommendDevServer();
		
		assertTrue(tester.getResponseString().toLowerCase().contains("kant"));

		String provider = "Wissenmedia";
		int foundSoll = 3;
		checkProvider(tester, provider, foundSoll);		
    }

    @org.junit.Test
    public void testMendeley_work()
    {
    	PrivacyProxy tester = new PrivacyProxy();
		tester.addContext("work");
		tester.recommendDevServer();
		
		assertTrue(tester.getResponseString().toLowerCase().contains("work"));

		String provider = "mendeley";
		int foundSoll = 19;
		checkProvider(tester, provider, foundSoll);		
    }

	protected void checkProvider(PrivacyProxy tester, String provider,
			int foundSoll) {
		int count = tester.countProvider(provider);
		log(provider + " found:" + count);
		assertTrue(count >= foundSoll);
	}

    @org.junit.Test
    public void testEuropeana_work()
    {
    	PrivacyProxy tester = new PrivacyProxy();
		tester.addContext("hubert work");
		tester.recommendDevServer();
		
		assertTrue(tester.getResponseString().toLowerCase().contains("hubert work"));

		String provider = "Europeana";
		int foundSoll = 5;
		checkProvider(tester, provider, foundSoll);		
    }
    
    @org.junit.Test
    public void testEuropeana_mona_lisa()
    {
    	PrivacyProxy tester = new PrivacyProxy();
		tester.addContext("mona");
		tester.recommendDevServer();
		
		assertTrue(tester.getResponseString().toLowerCase().contains("mona lisa"));

		String provider = "Europeana";
		int foundSoll = 5;
		checkProvider(tester, provider, foundSoll);		
   }

}
