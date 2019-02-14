package com.pengtoolbox.cfw.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLogger;

public class FileUtils {


	private static final HashMap<String,String> fileContentCache = new HashMap<String,String>();
	
	static String[] cachedFiles = new String[15];
	static int fileCounter = 0;
	
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
		CFWLogger omlogger = new CFWLogger(CFW.logger, request).method("getFileContent");
		
		if( CFWConfig.CACHING_FILE_ENABLED && FileUtils.fileContentCache.containsKey(path)){
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
				new CFWLogger(CFW.logger, request)
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
			new CFWLogger(CFW.logger, request)
				.method("writeFileContent")
				.severe("Could not write file: "+path, e);
		}
			
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
			CFW.logger.log(Level.SEVERE, "IOException: ", e);
			e.printStackTrace();
		}finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				CFW.logger.log(Level.SEVERE, "IOException", e);
				e.printStackTrace();
			}
		}
		
		String result = buffer.toString();
	
		// remove UTF-8 byte order mark if present
		result = result.replace("\uFEFF", "");
		
		return result;
	}


	/***********************************************************************
	 * Caches up to 15 files
	 * @param harContent
	 * @return the index of the cache use to retrieve the files with getHARFromCache()
	 ***********************************************************************/
	public static int cacheFile(String fileContent) {
	
		synchronized(cachedFiles) {
			if( fileCounter == cachedFiles.length-1) {
				fileCounter = 0;
			}
			cachedFiles[fileCounter] = fileContent;
			fileCounter++;
			
			return fileCounter-1;
		}
	}

	/***********************************************************************
	 * Retrieve a cached file by it's index.
	 * @param index the index of the file to be retrieved
	 ***********************************************************************/
	public static String getCachedFile(int index) {
		synchronized(cachedFiles) {
			if( index >= cachedFiles.length) {
				return null;
			}
			
			return cachedFiles[index];
		}
	}
	
}
