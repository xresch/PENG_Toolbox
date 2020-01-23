package com.pengtoolbox.cfw._main;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

import com.pengtoolbox.cfw.api.CFWRegistryAPI;
import com.pengtoolbox.cfw.cli.ArgumentsException;
import com.pengtoolbox.cfw.cli.CFWCommandLineInterface;
import com.pengtoolbox.cfw.config.CFWDBConfig;
import com.pengtoolbox.cfw.config.Configuration;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWRegistryObjects;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.spaces.CFWDBSpace;
import com.pengtoolbox.cfw.db.spaces.CFWDBSpaceGroup;
import com.pengtoolbox.cfw.db.spaces.Space;
import com.pengtoolbox.cfw.db.spaces.SpaceGroup;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBRole;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBRolePermissionMap;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUserRoleMap;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.Role;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.feature.cpusampling.CPUSamplingFeature;
import com.pengtoolbox.cfw.feature.cpusampling.StatsCPUSample;
import com.pengtoolbox.cfw.feature.cpusampling.StatsCPUSampleSignature;
import com.pengtoolbox.cfw.feature.cpusampling.StatsCPUSamplingAggregationTask;
import com.pengtoolbox.cfw.feature.cpusampling.StatsCPUSamplingTask;
import com.pengtoolbox.cfw.mail.CFWMail;
import com.pengtoolbox.cfw.schedule.CFWSchedule;
import com.pengtoolbox.cfw.utils.CFWDump;
import com.pengtoolbox.cfw.utils.CFWEncryption;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.cfw.utils.CFWTime;
import com.pengtoolbox.cfw.utils.json.CFWJson;
import com.pengtoolbox.cfw.validation.CFWValidation;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFW {
	
	//##############################################################################
	// Hierarchical Binding
	//##############################################################################

	public static class DB extends CFWDB {
		public static class Config extends CFWDBConfig{};
		public static class Users extends CFWDBUser{};
		public static class Roles extends CFWDBRole{};
		public static class UserRoleMap extends CFWDBUserRoleMap{};
		public static class Permissions extends CFWDBPermission{};
		public static class RolePermissionMap extends CFWDBRolePermissionMap{};
		public static class Spaces extends CFWDBSpace{};
		public static class SpaceGroups extends CFWDBSpaceGroup{};
	}
	
	public static class Context {
		public static class Request extends CFWContextRequest{};
		public static class Session extends CFWContextSession{};
	}
	
	public class CLI extends CFWCommandLineInterface {}
	public class Dump extends CFWDump {}
	public class Encryption extends CFWEncryption {}
	public class Files extends CFWFiles {}
	public class HTTP extends CFWHttp {}
	public class JSON extends CFWJson {}
	public class Localization extends CFWLocalization {}
	public class Mail extends CFWMail {}
	public class Properties extends CFWProperties {}
	public class Registry {
		public class Features extends CFWRegistryFeatures {} 
		public class Components extends CFWRegistryComponents {} 
		public class Objects extends CFWRegistryObjects {} 
		public class API extends CFWRegistryAPI {}
	
	}
	public class Schedule extends CFWSchedule {}
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
	    
	    ArrayList<CFWAppFeature> features = CFW.Registry.Features.getFeatureInstances();
	    
	    //--------------------------------
	    // Start Database 	
    	initializeDatabase(appToStart, features);
		

	    //--------------------------------
	    // Start Application
		CFWApplication app = new CFWApplication(args);
		
		for(CFWAppFeature feature : features) {
			feature.addFeature(app);
		}
		
		appToStart.startApp(app);
		
	    //--------------------------------
	    // Start Scheduled Tasks
    	initializeScheduledTasks(appToStart, features);
    	
		
	}
	
	/***********************************************************************
	 * Starts and initializes the Database. Iterates over all Objects in the 
	 * Registry and add
	 * @param features 
	 * @param CFWAppInterface application to start
	 ***********************************************************************/
	private static void doRegister(CFWAppInterface appToStart) {
		
		//---------------------------
		// Register Objects 
		CFW.Registry.Objects.addCFWObject(Configuration.class);
		CFW.Registry.Objects.addCFWObject(User.class);
		CFW.Registry.Objects.addCFWObject(Role.class);
		CFW.Registry.Objects.addCFWObject(Permission.class);
		CFW.Registry.Objects.addCFWObject(com.pengtoolbox.cfw.db.usermanagement.UserRoleMap.class);
		CFW.Registry.Objects.addCFWObject(com.pengtoolbox.cfw.db.usermanagement.RolePermissionMap.class);
		CFW.Registry.Objects.addCFWObject(SpaceGroup.class);
		CFW.Registry.Objects.addCFWObject(Space.class);
		
		//---------------------------
		// Register Features
		CFW.Registry.Features.addFeature(CPUSamplingFeature.class);		

		//---------------------------
		// Application Register
		appToStart.register();
		
		//---------------------------
		// Feature Register
		ArrayList<CFWAppFeature> features = CFW.Registry.Features.getFeatureInstances();
		for(CFWAppFeature feature : features) {
			feature.register();
		}
		
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
	 * @param features 
	 * @param CFWAppInterface application to start
	 ***********************************************************************/
	private static void initializeDatabase(CFWAppInterface appToStart, ArrayList<CFWAppFeature> features) {
		
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
    			object.updateTable();
    			
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
		// Feature Initialize
		for(CFWAppFeature feature : features) {
			feature.initializeDB();
		}
		
		//---------------------------
		// Do Application init
		appToStart.initializeDB();
		
	}
	
	/***********************************************************************
	 * Starts and initializes the scheduled tasks.
	 * @param features 
	 * @param CFWAppInterface application to start
	 ***********************************************************************/
	private static void initializeScheduledTasks(CFWAppInterface appToStart, ArrayList<CFWAppFeature> features) {
		
		//---------------------------
		// Feature Initialize
		for(CFWAppFeature feature : features) {
			feature.startTasks();
		}
		
		//---------------------------
		// Do Application init
		appToStart.startTasks();
		
	
	}
	
}
