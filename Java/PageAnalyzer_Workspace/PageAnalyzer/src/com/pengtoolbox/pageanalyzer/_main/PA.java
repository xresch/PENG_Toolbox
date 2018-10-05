package com.pengtoolbox.pageanalyzer._main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.response.AbstractTemplate;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;
import com.pengtoolbox.pageanalyzer.yslow.YSlowExecutor;

public class PA {
	
	//##############################################################################
	// GLOBAL
	//##############################################################################
	public static Logger logger = PALogger.getLogger(PA.class.getName());
	protected static PALogger log = new PALogger(logger);
	
	public static final String TIME_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSS";
	public static final Properties CONFIG = new Properties();
	
	public static final String REQUEST_ATTR_ID = "requestID";
	public static final String REQUEST_ATTR_TEMPLATE = "pageTemplate";
	public static final String REQUEST_ATTR_STARTNANOS = "starttime";
	public static final String REQUEST_ATTR_ENDNANOS = "endtime";
 
	
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
	
	private static final File folder = new File("./resources/lang/");
	
	private static URLClassLoader urlClassLoader;
	
	//##############################################################################
	// CACHES
	//##############################################################################
	
	private static boolean CACHING_FILE_ENABLED = true;
	private static final HashMap<String,String> fileContentCache = new HashMap<String,String>();

	//##############################################################################
	// METHODS
	//##############################################################################
	
	public static void initialize() throws IOException{
		
		PALogger log = new PALogger(logger).method("initialize").start();
		
		//------------------------------------
		// Initialize YSlow Singleton
		// prevents error on first analysis request.
		YSlow.instance();
		YSlowExecutor.instance();
		
		//------------------------------------
		// Classloader
		URL[] urls = {folder.toURI().toURL()};
		urlClassLoader = new URLClassLoader(urls);
		
		//------------------------------------
		// Load Configuration
		CONFIG.load(new FileReader(new File("./config/pageanalyzer.properties")));
		
		String om_caching_file_enabled = CONFIG.getProperty("pa_caching_file_enabled");
		if(om_caching_file_enabled != null 
		&& om_caching_file_enabled.toLowerCase().equals("true")){
			CACHING_FILE_ENABLED = true;
			System.out.println("cache files");
		}else{
			CACHING_FILE_ENABLED = false;
			System.out.println("don't cache files");
		}
		
		log.end();
	}
	
	public static String config(String key){
		
		return (String)CONFIG.get(key);
	}
	
	public static int configAsInt(String key){
		
		return Integer.parseInt((String)CONFIG.get(key));
	}
	
	public static AbstractTemplate getTemplate(HttpServletRequest request){
		
		Object o = request.getAttribute(PA.REQUEST_ATTR_TEMPLATE);
		
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
	
	/***********************************************************************
	 * Returns the file content of the given file path as a string.
	 * If it fails to read the file it will handle the exception and
	 * will add an alert to the given request.
	 * A file once loaded will 
	 * 
	 * @param request the request that is currently handled
	 * @param path the path 
	 * 
	 * @return String content of the file or null if an exception occurred.
	 * 
	 ***********************************************************************/
	public static String getFileContent(HttpServletRequest request, String path){
		PALogger omlogger = new PALogger(logger, request).method("getFileContent");
		
		if( CACHING_FILE_ENABLED && fileContentCache.containsKey(path)){
			omlogger.finest("Read file content from cache");
			return fileContentCache.get(path);
		}else{
			omlogger.finest("Read from disk into cache");
			
			try{
				List<String> fileContent = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
				
				StringBuffer contentBuffer = new StringBuffer();
				
				for(String line : fileContent){
					contentBuffer.append(line);
					contentBuffer.append("\n");
				}
				String content = contentBuffer.toString();
				fileContentCache.put(path, content);
				
				// remove UTF-8 byte order mark if present
				content = content.replace("\uFEFF", "");
				
				return content;
				
			} catch (IOException e) {
				//TODO: Localize message
				new PALogger(logger, request)
					.method("getFileContent")
					.severe("Could not read file: "+path, e);
				
				return null;
			}
			
		}
	}
	
	/***********************************************************************
	 * Write a string to a file.
	 * 
	 * 
	 * @param request the request that is currently handled
	 * @param path the path 
	 * @param content to be written
	 *   
	 * @return String content of the file or null if an exception occurred.
	 * 
	 ***********************************************************************/
	public static void writeFileContent(HttpServletRequest request, String path, String content){
		PALogger omlogger = new PALogger(logger, request).method("writeFileContent");
		
		omlogger.finest("Read from disk into cache");
			
		try{
			Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			
			
		} catch (IOException e) {
			//TODO: Localize message
			new PALogger(logger, request)
				.method("writeFileContent")
				.severe("Could not write file: "+path, e);
		}
			
	}
	
	
	public static String currentTimestamp(){
		
		return PA.formatDate(new Date());
	}
	
	public static String formatDate(Date date){
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat(PA.TIME_FORMAT);

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
	
	//public static void localizeString(HttpServletRequest request, String localize){
		
		
	//}
	
	public static void writeLocalized(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		AbstractTemplate template = PA.getTemplate(request);
		
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
						new PALogger(logger, request)
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

	public static Cookie getRequestCookie(HttpServletRequest request, String cookieKey) {
		
		if(request.getCookies() != null){
			for(Cookie cookie : request.getCookies()){
				if(cookie.getName().equals(cookieKey)){
					return cookie;
				}
			}
		}
		
		return null;
		
	}
	
	/*************************************************************
	 * 
	 * @param path
	 * @return content as string or null if not found.
	 *************************************************************/
	public static String readContentsFromInputStream(InputStream inputStream) {
		
		if(inputStream == null) {
			return null;
		}
		
		BufferedReader reader = null;
		String line = "";
		StringBuffer buffer = new StringBuffer();
		
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			while( (line = reader.readLine()) != null) {
				buffer.append(line).append("\n");
				//line = reader.readLine();
			}
			 
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException: ", e);
			e.printStackTrace();
		}finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "IOException", e);
				e.printStackTrace();
			}
		}
		
		String result = buffer.toString();

		// remove UTF-8 byte order mark if present
		result = result.replace("\uFEFF", "");
		
		return result;
	}
}
