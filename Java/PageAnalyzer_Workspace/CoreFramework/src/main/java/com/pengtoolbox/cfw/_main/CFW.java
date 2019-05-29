package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.response.AbstractTemplate;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.cfw.utils.CFWTime;
import com.pengtoolbox.cfw.validation.CFWValidation;

public class CFW {
	
	//##############################################################################
	// Hierarchical Binding
	//##############################################################################
	public class App extends CFWApp {} 
	public class Config extends CFWConfig {}
	public class DB extends CFWDB {}
	public class HTTP extends CFWHttp {}
	public class Files extends CFWFiles {}
	public class Localization extends CFWLocalization {}
	public class Time extends CFWTime {}
	public class Validation extends CFWValidation {}
	
	//##############################################################################
	// GLOBAL
	//##############################################################################

	public static final String REQUEST_ATTR_ID = "requestID";
	public static final String REQUEST_ATTR_TEMPLATE = "pageTemplate";
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
	
	public static AbstractTemplate getTemplate(HttpServletRequest request){
		
		Object o = request.getAttribute(CFW.REQUEST_ATTR_TEMPLATE);
		
		if(o != null){
			if(o instanceof AbstractTemplate){
				return (AbstractTemplate)o;
			}else{
				return null;
			}
			
		}else{
			return null;
		}

	}
	
	public static void initialize(String configFilePath) throws IOException{
					
		//------------------------------------
		// Classloader
		CFW.Files.addAllowedPackage("com.pengtoolbox.cfw.resources");
		
		//------------------------------------
		// Load Configuration
		CFW.Config.loadConfiguration(configFilePath);
		

		
	}
}
