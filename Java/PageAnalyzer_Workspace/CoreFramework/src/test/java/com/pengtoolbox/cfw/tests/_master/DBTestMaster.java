package com.pengtoolbox.cfw.tests._master;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;

public class DBTestMaster {
	
	@BeforeClass
	public static void startDefaultApplication() throws Exception {
		CFW.initialize("./config/cfw.properties");
		CFWLog.initializeLogging();
		CFWDB.initialize();
	}
	
	@AfterClass
	public static void stopDefaultApplication() throws Exception {

	}
}
