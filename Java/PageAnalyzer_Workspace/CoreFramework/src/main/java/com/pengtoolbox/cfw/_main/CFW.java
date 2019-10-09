package com.pengtoolbox.cfw._main;

import java.io.IOException;

import com.pengtoolbox.cfw.cli.CFWCommandLineInterface;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.config.CFWDBConfig;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroupPermissionMap;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUserGroupMap;
import com.pengtoolbox.cfw.utils.CFWEncryption;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.cfw.utils.CFWJson;
import com.pengtoolbox.cfw.utils.CFWTime;
import com.pengtoolbox.cfw.validation.CFWValidation;

public class CFW {
	
	//##############################################################################
	// Hierarchical Binding
	//##############################################################################
	public class App extends CFWApp {} 
	public class Properties extends CFWProperties {}
	public static class Context {
		public static class Request extends CFWContextRequest{};
		public static class Session extends CFWContextSession{};
	}
	public static class DB extends CFWDB {
		public static class Config extends CFWDBConfig{};
		public static class Users extends CFWDBUser{};
		public static class Groups extends CFWDBGroup{};
		public static class UserGroupMap extends CFWDBUserGroupMap{};
		public static class Permissions extends CFWDBPermission{};
		public static class GroupPermissionMap extends CFWDBGroupPermissionMap{};
	}
	public class HTTP extends CFWHttp {}
	public class Files extends CFWFiles {}
	public class Localization extends CFWLocalization {}
	public class Encryption extends CFWEncryption {}
	public class Time extends CFWTime {}
	public class Validation extends CFWValidation {}
	public class CLI extends CFWCommandLineInterface {}
	public class JSON extends CFWJson {}
	
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
	
	public static void initialize(String configFilePath) throws IOException{
					
		//------------------------------------
		// Classloader
		CFW.Files.addAllowedPackage("com.pengtoolbox.cfw.resources");
		
		//------------------------------------
		// Load Configuration
		CFW.Properties.loadConfiguration(configFilePath);
		

		
	}
}
