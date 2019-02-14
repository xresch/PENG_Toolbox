package com.pengtoolbox.cfw._main;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.logging.CFWLogger;
import com.pengtoolbox.cfw.response.AbstractTemplate;

public class CFW {
	
	//##############################################################################
	// GLOBAL
	//##############################################################################
	public static Logger logger = CFWLogger.getLogger(CFW.class.getName());
	protected static CFWLogger log = new CFWLogger(logger);
	
	public static final String TIME_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSS";
	public static final String REQUEST_ATTR_ID = "requestID";
	public static final String REQUEST_ATTR_TEMPLATE = "pageTemplate";
	public static final String REQUEST_ATTR_STARTNANOS = "starttime";
	public static final String REQUEST_ATTR_ENDNANOS = "endtime";
	
	public static final String SESSION_DATA = "sessionData";
	
	public static final String GLOBAL_DATASTORE_PATH = "./datastore";
	
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
	
	static final File folder = new File("./resources/lang/");
	
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
	
	
	public static void writeLocalized(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		AbstractTemplate template = CFW.getTemplate(request);
		
		if(template != null){

			//TODO: Make language handling dynamic
			ResourceBundle bundle = ResourceBundle.getBundle("language",
											new Locale("en", "US"), 
											urlClassLoader);
			
			StringBuffer sb = template.buildResponse();
			
			int fromIndex = 0;
			int leftIndex = 0;
			int rightIndex = 0;
			int length = sb.length();
			
			while(fromIndex < length && leftIndex < length){
			
				leftIndex = sb.indexOf(LOCALE_LB, fromIndex);
				
				if(leftIndex != -1){
					rightIndex = sb.indexOf(LOCALE_RB, leftIndex);
					
					if(rightIndex != -1){
						
						String propertyName = sb.substring(leftIndex+LOCALE_LB_SIZE, rightIndex);
						if(bundle.containsKey(propertyName)){
							sb.replace(leftIndex, rightIndex+LOCALE_RB_SIZE, bundle.getString(propertyName));
						}
						//start again from leftIndex
						fromIndex = leftIndex+1;
						
					}else{
						//TODO: Localize message
						new CFWLogger(logger, request)
						.method("writeLocalized")
						.warn("Localization Parameter was missing the right bound");
					
						break;
					}
					
				}else{
					//no more stuff found to replace
					break;
				}
			}
			
			response.getWriter().write(sb.toString());
			
		}
	
	}
}
