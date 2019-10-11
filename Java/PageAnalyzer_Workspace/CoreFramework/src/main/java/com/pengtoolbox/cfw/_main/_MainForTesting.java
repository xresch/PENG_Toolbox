package com.pengtoolbox.cfw._main;

import java.util.logging.Logger;

import org.eclipse.jetty.servlet.ServletContextHandler;

import com.pengtoolbox.cfw.db.config.Configuration;
import com.pengtoolbox.cfw.exceptions.ShutdownException;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.tests.assets.mockups.TestMenu;
import com.pengtoolbox.cfw.tests.assets.servlets.FormTestServlet;
import com.pengtoolbox.cfw.tests.assets.servlets.GeneralTestServlet;

public class _MainForTesting {
		
	public static Logger logger = CFWLog.getLogger(_MainForTesting.class.getName());
	protected static CFWLog log = new CFWLog(logger);
	
    public static void main( String[] args ) throws Exception
    {
    	
        //###################################################################
        // Initialization
        //################################################################### 
    	
    	//------------------------------------
    	// Create Default App
    	CFWDefaultApp app;
    	try {
    		app = CFW.App.createApp(args);
    	}catch(ShutdownException e) {
    		//do not proceed if shutdown was registered
    		return;
    	}
    	
    	CFW.App.setDefaultMenu(TestMenu.class);
    	
        //###################################################################
        // Change Config
        //################################################################### 
    	Configuration config = CFW.DB.Config.selectByName(Configuration.FILE_CACHING).value("false");
    	CFW.DB.Config.update(config);
    	
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
        app.start();
    }
}

