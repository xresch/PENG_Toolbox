package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.pengtoolbox.cfw.config.Configuration;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.AbstractResponse;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWLocalization {
	
	public static Logger logger = CFWLog.getLogger(CFWLocalization.class.getName());
	
	public static final String LOCALE_LB  = "{!";
	public static final String LOCALE_RB = "!}";
	
	public static final int LOCALE_LB_SIZE  = LOCALE_LB.length();
	public static final int LOCALE_RB_SIZE = LOCALE_RB.length();
	
	static final String LANGUAGE_FOLDER_PATH = "./resources/lang/";
	
	//------------------------------------
	// Classloader
	//URL[] urls = {new File(CFWLocalization.LANGUAGE_FOLDER_PATH.toURI()).toURL()};
	//urlClassLoader = new URLClassLoader(urls);
	static URLClassLoader urlClassLoader;
	
	private static final HashMap<String,Map<String,String>> languageCache = new HashMap<String, Map<String,String>>();
	
	/******************************************************************************************
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 ******************************************************************************************/
	public static void writeLocalized(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		AbstractResponse template = CFW.Context.Request.getResponse();
		
		if(template != null){
	
			//TODO: Make language handling dynamic
			Map<String, String> langMap = loadLanguagePack("en_US");
			//ResourceBundle bundle = ResourceBundle.getBundle("language",
			//								new Locale("en", "US"), 
			//								CFWLocalization.urlClassLoader);
			
			StringBuffer sb = template.buildResponse();
			
			int fromIndex = 0;
			int leftIndex = 0;
			int rightIndex = 0;
			int length = sb.length();
			
			while(fromIndex < length && leftIndex < length){
			
				leftIndex = sb.indexOf(CFWLocalization.LOCALE_LB, fromIndex);
				
				if(leftIndex != -1){
					rightIndex = sb.indexOf(CFWLocalization.LOCALE_RB, leftIndex);
					
					if(rightIndex != -1 && (leftIndex+CFWLocalization.LOCALE_LB_SIZE) < rightIndex){
	
						String propertyName = sb.substring(leftIndex+CFWLocalization.LOCALE_LB_SIZE, rightIndex);
						if(langMap != null && langMap.containsKey(propertyName)){
							sb.replace(leftIndex, rightIndex+CFWLocalization.LOCALE_RB_SIZE, langMap.get(propertyName));
						}
						//start again from leftIndex
						fromIndex = leftIndex+1;
						
					}else{
						//TODO: Localize message
						new CFWLog(logger)
						.method("writeLocalized")
						.finer("Localization Parameter was missing the right bound");
					
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
	
	public static Map<String,String> loadLanguagePack(String localeString ) {
		
		String filename = "language_"+localeString.toLowerCase()+".json";
		
		if(CFW.Files.isFile(LANGUAGE_FOLDER_PATH+"/"+filename)){
			
			if (languageCache.containsKey(filename) && CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING)) {
				return languageCache.get(filename);
			}else {
				String jsonString = CFW.Files.getFileContent(null, LANGUAGE_FOLDER_PATH, filename);
				Gson gson = new Gson();
				@SuppressWarnings("unchecked")
				Map<String,String> languageMap = gson.fromJson(jsonString, Map.class);
				
				if(CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING) && !languageCache.containsKey(filename) ) {
					languageCache.put(filename, languageMap);
				}
				
				return languageMap;
			}
		}
		
		return null;
		
		
		
	}

}
