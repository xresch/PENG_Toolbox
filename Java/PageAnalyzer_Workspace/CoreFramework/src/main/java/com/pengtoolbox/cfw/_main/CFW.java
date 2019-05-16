package com.pengtoolbox.cfw._main;

import java.io.File;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.AbstractTemplate;

public class CFW {
	
	//##############################################################################
	// Hierarchical Binding
	//##############################################################################
	public class Setup extends CFWSetup {}
	public class Config extends CFWConfig {}
	public class DB extends CFWDB {}
	public class HTTP extends CFWHttp {}
	public class Localization extends CFWLocalization {}
	
	//##############################################################################
	// GLOBAL
	//##############################################################################
	public static Logger logger = CFWLog.getLogger(CFW.class.getName());
	protected static CFWLog log = new CFWLog(logger);
	
	public static final String TIME_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSS";
	public static final String REQUEST_ATTR_ID = "requestID";
	public static final String REQUEST_ATTR_TEMPLATE = "pageTemplate";
	public static final String REQUEST_ATTR_STARTNANOS = "starttime";
	public static final String REQUEST_ATTR_ENDNANOS = "endtime";
	
	public static final String SESSION_DATA = "sessionData";
		
	//##############################################################################
	// ALERT TYPES
	//##############################################################################
	public static final int ALERT_SUCCESS = 1;
	public static final int ALERT_INFO = 1 << 1;
	public static final int ALERT_WARN = 1 << 2;
	public static final int ALERT_ERROR = 1 << 3;
	
	//##############################################################################
	// PATHS
	//##############################################################################
	public static final String PATH_RESOURCES_HTML = "./resources/html";
	public static final String PATH_TEMPLATE_MENU = PATH_RESOURCES_HTML+"/default_template/menu.html";
	public static final String PATH_TEMPLATE_FOOTER = PATH_RESOURCES_HTML+"/default_template/footer.html";
	public static final String PATH_TEMPLATE_SUPPORTINFO = PATH_RESOURCES_HTML+"/default_template/supportInfoModal.html";
	
	//##############################################################################
	// Localization
	//##############################################################################
	public static final String LOCALE_LB  = "{!"; 
	public static final String LOCALE_RB = "!}"; 
	
	public static final int LOCALE_LB_SIZE  = LOCALE_LB.length();
	public static final int LOCALE_RB_SIZE = LOCALE_RB.length(); 
	
	static final File LANGUAGE_FOLDER = new File("./resources/lang/");
	
	static URLClassLoader urlClassLoader;
	

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
	
	public static String currentTimestamp(){
		
		return CFW.formatDate(new Date());
	}
	
	public static String formatDate(Date date){
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat(CFW.TIME_FORMAT);

		return dateFormatter.format(date);
	}
	
	/********************************************************
	 * Workaround for classloading issue
	 ********************************************************/
	public static void javafxLogWorkaround(Level level, String message, String method){
		
		log.method(method).log(level, message, null);
	}
	
	/********************************************************
	 * Workaround for classloading issue
	 ********************************************************/
	public static void javafxLogWorkaround(Level level, String message, Throwable e, String method){
		
		log.method(method).log(level, message, e);
	}
}
