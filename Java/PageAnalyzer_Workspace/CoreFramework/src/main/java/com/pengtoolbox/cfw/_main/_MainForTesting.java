package com.pengtoolbox.cfw._main;

import java.util.logging.Logger;

import com.pengtoolbox.cfw.exceptions.ShutdownException;
import com.pengtoolbox.cfw.logging.CFWLog;

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
    }
}

