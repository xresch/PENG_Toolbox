package com.pengtoolbox.cfw.features.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.features.config.Configuration;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.AbstractResponse;
import com.pengtoolbox.cfw.utils.LinkedProperties;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWLocalization {
	
	public static Logger logger = CFWLog.getLogger(CFWLocalization.class.getName());
	
	public static final String LOCALE_LB  = "{!";
	public static final String LOCALE_RB = "!}";
	
	public static final int LOCALE_LB_SIZE  = LOCALE_LB.length();
	public static final int LOCALE_RB_SIZE = LOCALE_RB.length();
	
	private static int localeFilesID = 0;
	
	// key consists of {language}+{contextPath}+{localeFilesID}
	private static LinkedHashMap<String, FileDefinition> localeFiles = new LinkedHashMap<String, FileDefinition>();
		
	private static final LinkedHashMap<String,Properties> languageCache = new LinkedHashMap<String, Properties>();
	
	// contains all properties
	private static LinkedProperties globalProperties = new LinkedProperties();
	
	/******************************************************************************************
	 * 
	 ******************************************************************************************/
	public static Properties getAllProperties() {
		if (CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING)) {
			return globalProperties;
		}else {
			//--------------------------
			// Merged
			LinkedProperties mergedPorperties = new LinkedProperties();
			
			for(Entry<String, FileDefinition> entry : localeFiles.entrySet()) {

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
						.method("getAllProperties")
						.severe("Error while reading language pack.", e);
				}finally {
					if(reader!= null) {
						reader.close();
					}
				}
			}
			
			return mergedPorperties;
			
		}
	}
	
	/******************************************************************************************
	 * 
	 * @param locale 
	 * @param contextPath the absolute path of the context the language pack should be loaded. 
	 *        e.g. "/app/yourservlet"
	 * @throws IOException
	 ******************************************************************************************/
	public static void registerLocaleFile(Locale locale, String contextPath, FileDefinition propertiesFileDefinition) {
		String id = locale.getLanguage()+contextPath+"-"+localeFilesID;
		localeFiles.put(id.toLowerCase(), propertiesFileDefinition);
		localeFilesID++;
		
		//------------------------
		// Add to all Properties
		if (CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING)) {

			StringReader reader = null;
			try {
				Properties currentProps = new Properties();
				String propertiesString = propertiesFileDefinition.readContents();
				if(propertiesString != null) {
					reader = new StringReader(propertiesString) ;
					currentProps.load( reader );
					globalProperties.putAll(currentProps);
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
	
	/******************************************************************************************
	 * 
	 ******************************************************************************************/
	public static Locale[] getLocalesForRequest() {
		
		ArrayList<Locale> localeArray = new ArrayList<Locale>();
		
		// fall back to english
		localeArray.add(Locale.ENGLISH);
		
		Locale defaultLanguage = Locale.forLanguageTag(CFW.DB.Config.getConfigAsString(Configuration.LANGUAGE).toLowerCase());
		if(defaultLanguage != null) {
			localeArray.add(defaultLanguage);
		}
		
		HttpServletRequest request = CFW.Context.Request.getRequest();
		if(request != null) {
			Locale browserLanguage = request.getLocale();
			if(browserLanguage != null) {
				localeArray.add(browserLanguage);
			}
		}
		
		return localeArray.toArray(new Locale[localeArray.size()]);
	}
	
	/******************************************************************************************
	 * 
	 ******************************************************************************************/
	public static String getLocaleIdentifierForRequest() {
		return getLocaleIdentifier(getLocalesForRequest());
	}
	/******************************************************************************************
	 * 
	 ******************************************************************************************/
	public static String getLocaleIdentifier(Locale[] locales) {
		StringBuilder builder = new StringBuilder();
		
		for(Locale locale : locales) {
			builder.append(locale.getLanguage()).append("_"); 
		}
		
		HttpServletRequest request = CFW.Context.Request.getRequest();
		if(request != null) {
			builder.append(request.getRequestURI());
		}else {
			builder.deleteCharAt(builder.length()-1);
		}
		
		return builder.toString().toLowerCase();
	}
	
	/******************************************************************************************
	 * 
	 ******************************************************************************************/
	public static Properties getLanguagePackeByIdentifier(String localeIdentifier) {
		return languageCache.get(localeIdentifier);
	}
	/******************************************************************************************
	 * 
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 ******************************************************************************************/
	public static Properties getLanguagePackForRequest() {
		return getLanguagePack(getLocalesForRequest());
	}
	
	/******************************************************************************************
	 * 
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 ******************************************************************************************/
	public static Properties getLanguagePack(Locale[] locales) {
		
		//------------------------------
		// Initialize
		String cacheID = CFW.Localization.getLocaleIdentifier(locales);
		
		String requestURI = "";
		if(CFW.Context.Request.getRequest() != null) {
			requestURI = CFW.Context.Request.getRequest().getRequestURI();
		}
		//------------------------------
		// Check is Cached
		if (languageCache.containsKey(cacheID) && CFW.DB.Config.getConfigAsBoolean(Configuration.FILE_CACHING)) {
			return languageCache.get(cacheID);
		}else {
			
			LinkedProperties mergedPorperties = new LinkedProperties();
	
			Locale lastlocale = null;
			for(Locale locale : locales) {
				
				//----------------------------
				// Skip reoccuring language
				if(lastlocale != null && locale.getLanguage().equals(lastlocale.getLanguage()) ) {
					lastlocale = locale;
					continue;
				}else {
					lastlocale = locale;
				}
				
				String language = locale.getLanguage().toLowerCase(); 
				for(Entry<String, FileDefinition> entry : localeFiles.entrySet()) {
					String entryID = entry.getKey();

					if( (language+requestURI).startsWith(entryID.substring(0, entryID.lastIndexOf('-') )) ) {
						
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
			
			languageCache.put(cacheID, mergedPorperties);
			
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
	
			Properties langMap = getLanguagePack(getLocalesForRequest());
			
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