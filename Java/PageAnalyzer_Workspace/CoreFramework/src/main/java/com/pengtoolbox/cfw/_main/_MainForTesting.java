package com.pengtoolbox.cfw._main;

import java.util.logging.Logger;

import org.eclipse.jetty.servlet.ServletContextHandler;

import com.pengtoolbox.cfw.db.config.Configuration;
import com.pengtoolbox.cfw.exceptions.ShutdownException;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.tests.assets.mockups.TestMenu;
import com.pengtoolbox.cfw.tests.assets.servlets.FormTestServlet;
import com.pengtoolbox.cfw.tests.assets.servlets.GeneralTestServlet;

public class _MainForTesting implements CFWAppInterface {
		
	public static Logger logger = CFWLog.getLogger(_MainForTesting.class.getName());
	protected static CFWLog log = new CFWLog(logger);
	
    public static void main( String[] args ) throws Exception
    {
    	_MainForTesting main = new _MainForTesting();
    	CFW.initializeApp(main, args);
        //###################################################################
        // Initialization
        //################################################################### 
    	
    }

	@Override
	public void register() {
		CFW.Registry.Components.setDefaultMenu(TestMenu.class);
		
	}

	@Override
	public void startDB() {
		//###################################################################
        // Change Config
        //################################################################### 
    	Configuration config = CFW.DB.Config.selectByName(Configuration.FILE_CACHING).value("false");
    	CFW.DB.Config.update(config);
		
	}

	@Override
	public void startApp(CFWDefaultApp app) {

        //###################################################################
        // Create API ServletContext, no login needed
        //################################################################### 
    	ServletContextHandler testContext = app.createUnsecureContext("/test");
    	
        testContext.addServlet(GeneralTestServlet.class, "/general");
        testContext.addServlet(FormTestServlet.class, "/form");
        //###################################################################
        // Startup
        //###################################################################
        app.setDefaultURL("/test/general");
        try {
			app.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void stopApp() {
		// TODO Auto-generated method stub
		
	}
}

