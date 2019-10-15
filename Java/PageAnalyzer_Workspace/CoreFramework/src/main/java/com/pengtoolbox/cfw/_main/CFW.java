package com.pengtoolbox.cfw._main;

import java.util.ArrayList;

import com.pengtoolbox.cfw.api.CFWRegistryAPI;
import com.pengtoolbox.cfw.cli.ArgumentsException;
import com.pengtoolbox.cfw.cli.CFWCommandLineInterface;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWRegistryObjects;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.config.CFWDBConfig;
import com.pengtoolbox.cfw.db.config.Configuration;
import com.pengtoolbox.cfw.db.spaces.CFWSpace;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroupPermissionMap;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUserGroupMap;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.utils.CFWEncryption;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.cfw.utils.CFWJson;
import com.pengtoolbox.cfw.utils.CFWTime;
import com.pengtoolbox.cfw.validation.CFWValidation;

public class CFW {
	
	//##############################################################################
	// Hierarchical Binding
	//##############################################################################

	public static class DB extends CFWDB {
		public static class Config extends CFWDBConfig{};
		public static class Users extends CFWDBUser{};
		public static class Groups extends CFWDBGroup{};
		public static class UserGroupMap extends CFWDBUserGroupMap{};
		public static class Permissions extends CFWDBPermission{};
		public static class GroupPermissionMap extends CFWDBGroupPermissionMap{};
	}
	
	public static class Context {
		public static class Request extends CFWContextRequest{};
		public static class Session extends CFWContextSession{};
	}
	
	public class CLI extends CFWCommandLineInterface {}
	public class Encryption extends CFWEncryption {}
	public class Files extends CFWFiles {}
	public class HTTP extends CFWHttp {}
	public class JSON extends CFWJson {}
	public class Localization extends CFWLocalization {}
	public class Properties extends CFWProperties {}
	public class Registry {
		public class Components extends CFWRegistryComponents {} 
		public class Objects extends CFWRegistryObjects {} 
		public class API extends CFWRegistryAPI {}
	
	}
	public class Time extends CFWTime {}
	public class Validation extends CFWValidation {}
	
	
	//##############################################################################
	// GLOBAL
	//##############################################################################

	public static final String REQUEST_ATTR_ID = "requestID";
	//public static final String REQUEST_ATTR_TEMPLATE = "pageTemplate";
	public static final String REQUEST_ATTR_STARTNANOS = "starttime";
	public static final String REQUEST_ATTR_ENDNANOS = "endtime";
	
	public static final String SESSION_DATA = "sessionData";
		
	
	//##############################################################################
	// PATHS
	//##############################################################################
	public static final String PATH_RESOURCES_HTML = "./resources/html";
	public static final String PATH_TEMPLATE_FOOTER = PATH_RESOURCES_HTML+"/default_template/footer.html";
	public static final String PATH_TEMPLATE_SUPPORTINFO = PATH_RESOURCES_HTML+"/default_template/supportInfoModal.html";
	
	
	private static void initializeCore(String[] args) throws Exception{
		
		//------------------------------------
		// Command Line Arguments
		CFW.CLI.readArguments(args);

		if (!CFW.CLI.validateArguments()) {
			System.out.println("Issues loading arguments: \n"+CFW.CLI.getInvalidMessagesAsString());
			CFW.CLI.printUsage();
			throw new ArgumentsException(CFW.CLI.getInvalidMessages());
		}
	    
		//------------------------------------
		// Add allowed Packages
		CFW.Files.addAllowedPackage("com.pengtoolbox.cfw.resources");
		
		//------------------------------------
		// Load Configuration
		CFW.Properties.loadProperties(CFW.CLI.getValue(CFW.CLI.CONFIG_FILE));
		

		
	}

	/***********************************************************************
	 * Create an instance of the CFWDefaultApp.
	 * @param args command line arguments
	 * @throws Exception 
	 ***********************************************************************/
	public static void initializeApp(CFWAppInterface appToStart, String[] args) throws Exception {
		
	    //--------------------------------
	    // Initialize Core
		CFW.initializeCore(args);
		
	    //--------------------------------
	    // Handle Shutdown reguest.
	    if (CFW.CLI.isArgumentLoaded(CLI.STOP)) {
    		CFWApplication.stop();
    		appToStart.stopApp();
			System.exit(0);
    		return;
	    }
		
	    //--------------------------------
	    // Register Components
	    doRegister(appToStart);
		
	    //--------------------------------
	    // Start Database 	
    	initializeDatabase(appToStart);
		
	    //--------------------------------
	    // Start Application
		CFWApplication app = new CFWApplication(args);
		appToStart.startApp(app);
		
	}
	
	/***********************************************************************
	 * Starts and initializes the Database. Iterates over all Objects in the 
	 * Registry and add
	 * @param CFWAppInterface application to start
	 ***********************************************************************/
	private static void doRegister(CFWAppInterface appToStart) {
		
		//---------------------------
		// Register  
		CFW.Registry.Objects.addCFWObject(Configuration.class);
		CFW.Registry.Objects.addCFWObject(User.class);
		CFW.Registry.Objects.addCFWObject(Group.class);
		CFW.Registry.Objects.addCFWObject(Permission.class);
		CFW.Registry.Objects.addCFWObject(com.pengtoolbox.cfw.db.usermanagement.UserGroupMap.class);
		CFW.Registry.Objects.addCFWObject(com.pengtoolbox.cfw.db.usermanagement.GroupPermissionMap.class);
		CFW.Registry.Objects.addCFWObject(CFWSpace.class);
		//---------------------------
		// Application Register
		appToStart.register();
		
		//---------------------------
		// Register APIs
		ArrayList<CFWObject> objectArray = CFW.Registry.Objects.getCFWObjectInstances();
		
		for(CFWObject object : objectArray) {
			CFW.Registry.API.addAll(object.getAPIDefinitions());
		}
		//System.out.println("============ API Registry Entries =============");
		//System.out.println(CFW.Registry.API.getJSONArray());
	}
	/***********************************************************************
	 * Starts and initializes the Database. Iterates over all Objects in the 
	 * Registry and add
	 * @param CFWAppInterface application to start
	 ***********************************************************************/
	private static void initializeDatabase(CFWAppInterface appToStart) {
		
		//---------------------------
		// Start Database 
		CFW.DB.startDatabase(); 
		
		//---------------------------
		// Iterate over Registered Objects
    	ArrayList<CFWObject> objectArray = CFW.Registry.Objects.getCFWObjectInstances();
    	
    	for(CFWObject object : objectArray) {
    		if(object.getTableName() != null) {
    			object.migrateTable();
    			
    		}
    	}
    	
    	for(CFWObject object : objectArray) {
    		if(object.getTableName() != null) {
    			object.createTable();
    			
    		}
    	}
		
    	for(CFWObject object : objectArray) {
    		if(object.getTableName() != null) {
    			object.initDB();
    			
    		}
    	}
    	for(CFWObject object : objectArray) {
    		if(object.getTableName() != null) {
    			object.initDBSecond();
    		}
    	}
    	
    	for(CFWObject object : objectArray) {
    		if(object.getTableName() != null) {
    			object.initDBThird();
    		}
    	}
    	
		//---------------------------
		// Reset Admin PW if configured
    	CFWDB.resetAdminPW();
    
		//---------------------------
		// Do Application init
		appToStart.initializeDB();
		
	}
}
