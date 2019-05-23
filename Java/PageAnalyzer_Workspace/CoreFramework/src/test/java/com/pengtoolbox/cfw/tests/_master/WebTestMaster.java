package com.pengtoolbox.cfw.tests._master;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWDefaultApp;

public class WebTestMaster {

	protected static CFWDefaultApp APP;
	protected static String TEST_URL;
	
	// context path on "/test"
	protected static ServletContextHandler testContext;
	@Before
	public void startDefaultApplication() throws Exception {
		
		APP  = new CFWDefaultApp(new String[] {});
		
		testContext = APP.createUnsecureContext("/test");
		TEST_URL = "http://localhost:"+CFW.Config.HTTP_PORT+CFW.Config.BASE_URL+"/test";
		
		//Seperate thread to not make the test thread block
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					APP.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		
		t.start();

		Thread.sleep(5000);
		
		
	}
	
	@After
	public void stopDefaultApplication() throws Exception {
		APP.stop();
	}
}
