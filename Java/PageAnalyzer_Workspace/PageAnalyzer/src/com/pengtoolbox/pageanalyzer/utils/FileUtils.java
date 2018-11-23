package com.pengtoolbox.pageanalyzer.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer.logging.PALogger;

public class FileUtils {


	//##############################################################################
	// CACHES
	//##############################################################################
	public static boolean CACHING_FILE_ENABLED = true;
	private static final HashMap<String,String> fileContentCache = new HashMap<String,String>();
	
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
		PALogger omlogger = new PALogger(PA.logger, request).method("getFileContent");
		
		if( FileUtils.CACHING_FILE_ENABLED && FileUtils.fileContentCache.containsKey(path)){
			omlogger.finest("Read file content from cache");
			return FileUtils.fileContentCache.get(path);
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
				FileUtils.fileContentCache.put(path, content);
				
				// remove UTF-8 byte order mark if present
				content = content.replace("\uFEFF", "");
				
				return content;
				
			} catch (IOException e) {
				//TODO: Localize message
				new PALogger(PA.logger, request)
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
		
		try{
			Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			
			
		} catch (IOException e) {
			//TODO: Localize message
			new PALogger(PA.logger, request)
				.method("writeFileContent")
				.severe("Could not write file: "+path, e);
		}
			
	}
	
}
