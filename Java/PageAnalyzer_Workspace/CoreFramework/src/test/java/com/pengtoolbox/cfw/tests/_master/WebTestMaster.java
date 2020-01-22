package com.pengtoolbox.cfw.tests._master;

import javax.servlet.Servlet;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletMapping;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppInterface;
import com.pengtoolbox.cfw._main.CFWApplication;

public class WebTestMaster {

	protected static CFWApplication APP;
	protected static String TEST_URL;
	
	// context path on "/test"
	protected static ServletContextHandler testContext;
	
	public static void addServlet(Class<? extends Servlet> clazz, String contextPath) {
		
		boolean alreadyExists = false;
		for(ServletMapping mappings : testContext.getServletHandler().getServletMappings()) {
			
			for(String pathSpec : mappings.getPathSpecs()) {
				if(contextPath.equals(pathSpec)) {
					alreadyExists = true;
					break;
				}
			}
			if(alreadyExists) {
				break;
			}
		}
		
		if(!alreadyExists) {
			System.out.println("ADD SERVLET:"+contextPath);
			testContext.addServlet(clazz, contextPath);
		}
	}
	
	@BeforeClass
	public static void startDefaultApplication() throws Exception {
		
		CFW.initializeApp(new CFWAppInterface() {
			
			@Override
			public void stopApp() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void startApp(CFWApplication app) {
				APP  = app;
				
				testContext = APP.getUnsecureContext("/test");
				TEST_URL = "http://localhost:"+CFW.Properties.HTTP_PORT+CFW.Properties.BASE_URL+"/test";
				
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

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void register() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void initializeDB() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void startTasks() {
				// TODO Auto-generated method stub
				
			}
		}, new String[] {});
		
		
		
	}
	
	@AfterClass
	public static void stopDefaultApplication() throws Exception {
		System.out.println("========== ALERTS =========");
		System.out.println(CFW.Context.Request.getAlertsAsJSONArray());
		APP.stop();
	}
}
