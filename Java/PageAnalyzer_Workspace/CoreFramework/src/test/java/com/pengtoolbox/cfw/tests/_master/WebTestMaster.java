package com.pengtoolbox.cfw.tests._master;

import javax.servlet.Servlet;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWDefaultApp;

public class WebTestMaster {

	protected static CFWDefaultApp APP;
	protected static String TEST_URL;
	
	// context path on "/test"
	protected static ServletContextHandler testContext;
	
	public static void addServlet(Class<? extends Servlet> clazz, String contextPath) {
		testContext.addServlet(clazz, contextPath);
//		if(!testContext.checkContextPath("/test/"+contextPath+"*")){
//			System.out.println("SERVLET ADDED!!! >> "+contextPath);
//			testContext.addServlet(clazz, contextPath);
//		}
	}
	
	@BeforeClass
	public static void startDefaultApplication() throws Exception {
		
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
	
	@AfterClass
	public static void stopDefaultApplication() throws Exception {
		APP.stop();
	}
}
