package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.features.config.Configuration;
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
	
	private static int localeFilesID = 0;
	private static HashMap<String, FileDefinition> localeFiles = new HashMap<String, FileDefinition>();
	
	//------------------------------------
	// Classloader
	//URL[] urls = {new File(CFWLocalization.LANGUAGE_FOLDER_PATH.toURI()).toURL()};
	//urlClassLoader = new URLClassLoader(urls);
	static URLClassLoader urlClassLoader;
	
	private static final HashMap<String,Properties> languageCache = new HashMap<String, Properties>();
	
	/******************************************************************************************
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 ******************************************************************************************/
	public static void registerLocaleFile(Locale locale, FileDefinition propertiesFileDefinition) {
		localeFiles.put(locale.getLanguage()+localeFilesID, propertiesFileDefinition);
		localeFilesID++;
	}
	
	public static String getLocaleIdentifier(Locale[] locales) {
		StringBuilder builder = new StringBuilder();
		
		for(Locale locale : locales) {
			builder.append(locale.getLanguage().toLowerCase()).append("_"); 
		}
		builder.deleteCharAt(builder.length()-1);
		
		return builder.toString();
	}
	/******************************************************************************************
	 * 
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 ******************************************************************************************/
	public static Properties getLanguagePack(Locale[] locales) {
		
		String identifier = CFW.Localization.getLocaleIdentifier(locales);
		
		if (languageCache.containsKey(identifier) && CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING)) {
			return languageCache.get(identifier);
		}else {
			
			Properties mergedPorperties = new Properties();
	
			for(Locale locale : locales) {
				String language = locale.getLanguage().toLowerCase(); 
				for(Entry<String, FileDefinition> entry : localeFiles.entrySet()) {
					if(entry.getKey().toLowerCase().startsWith(language)) {
						
						FileDefinition def = entry.getValue();
	
						StringReader reader = null;
						try {
							Properties currentProps = new Properties();
							String propertiesString = def.readContents();
							if(propertiesString != null) {
								reader = new StringReader(propertiesString) ;
								currentProps.load( reader );
								mergedPorperties.putAll(currentProps);
							}
							
						} catch (IOException e) {
							new CFWLog(logger)
								.method("getLocaleProperties")
								.severe("Error while reading language pack.", e);
						}finally {
							if(reader!= null) {
								reader.close();
							}
						}
					}
				}
			}
			
			if(CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING) && !languageCache.containsKey(identifier) ) {
				languageCache.put(identifier, mergedPorperties);
			}
			return mergedPorperties;
		}
	}
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
			Properties langMap = getLanguagePack(new Locale[] {Locale.ENGLISH});
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
							sb.replace(leftIndex, rightIndex+CFWLocalization.LOCALE_RB_SIZE, langMap.getProperty(propertyName));
						}
						//start again from leftIndex
						fromIndex = leftIndex+1;
						
					}else{
						//TODO: Localize message
						new CFWLog(logger)
							.method("writeLocalized")
							.finest("Localization Parameter was missing the right bound");
					
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
