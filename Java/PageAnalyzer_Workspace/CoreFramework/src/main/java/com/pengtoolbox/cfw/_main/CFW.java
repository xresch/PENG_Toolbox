package com.pengtoolbox.cfw._main;

import java.io.IOException;

import com.pengtoolbox.cfw.cli.CFWCommandLineInterface;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroups;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.cfw.utils.CFWSecurity;
import com.pengtoolbox.cfw.utils.CFWTime;
import com.pengtoolbox.cfw.validation.CFWValidation;

public class CFW {
	
	//##############################################################################
	// Hierarchical Binding
	//##############################################################################
	public class App extends CFWApp {} 
	public class Config extends CFWConfig {}
	public static class Context {
		public static class Request extends CFWContextRequest{};
	}
	public static class DB extends CFWDB {
		public static class Users extends CFWDBUser{};
		public static class Groups extends CFWDBGroups{};
	}
	public class HTTP extends CFWHttp {}
	public class Files extends CFWFiles {}
	public class Localization extends CFWLocalization {}
	public class Security extends CFWSecurity {}
	public class Time extends CFWTime {}
	public class Validation extends CFWValidation {}
	public class CLI extends CFWCommandLineInterface {}
	
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
	public static final String PATH_TEMPLATE_MENU = PATH_RESOURCES_HTML+"/default_template/menu.html";
	public static final String PATH_TEMPLATE_FOOTER = PATH_RESOURCES_HTML+"/default_template/footer.html";
	public static final String PATH_TEMPLATE_SUPPORTINFO = PATH_RESOURCES_HTML+"/default_template/supportInfoModal.html";
	
	//##############################################################################
	// METHODS
	//##############################################################################
	
//	public static AbstractResponse getTemplate(HttpServletRequest request){
//		
//		Object o = request.getAttribute(CFW.REQUEST_ATTR_TEMPLATE);
//		
//		if(o != null){
//			if(o instanceof AbstractResponse){
//				return (AbstractResponse)o;
//			}else{
//				return null;
//			}
//			
//		}else{
//			return null;
//		}
//
//	}
	
	public static void initialize(String configFilePath) throws IOException{
					
		//------------------------------------
		// Classloader
		CFW.Files.addAllowedPackage("com.pengtoolbox.cfw.resources");
		
		//------------------------------------
		// Load Configuration
		CFW.Config.loadConfiguration(configFilePath);
		

		
	}
}
