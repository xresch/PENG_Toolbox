package com.pengtoolbox.cfw.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLog;

public class FileUtils {


	private static final HashMap<String,String> permanentStringFileCache = new HashMap<String,String>();
	private static final HashMap<String,byte[]> permanentByteFileCache = new HashMap<String,byte[]>();
	
	public static Logger logger = CFWLog.getLogger(FileUtils.class.getName());
	
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
		CFWLog omlogger = new CFWLog(logger, request).method("getFileContent");
		
		if( CFWConfig.CACHING_FILE_ENABLED && FileUtils.permanentStringFileCache.containsKey(path)){
			omlogger.finest("Read file content from cache");
			return FileUtils.permanentStringFileCache.get(path);
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
				FileUtils.permanentStringFileCache.put(path, content);
				
				// remove UTF-8 byte order mark if present
				content = content.replace("\uFEFF", "");
				
				return content;
				
			} catch (IOException e) {
				//TODO: Localize message
				new CFWLog(CFW.logger, request)
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
			new CFWLog(logger, request)
				.method("writeFileContent")
				.severe("Could not write file: "+path, e);
		}
			
	}
	
	/*************************************************************
	 * Read a resource from the package.
	 * @param path
	 * @return content as string or null if not found.
	 *************************************************************/
	public static String readPackageResource(String resourcePath) {
		
		String fileContent = null;
		
		if( CFWConfig.CACHING_FILE_ENABLED && FileUtils.permanentStringFileCache.containsKey(resourcePath)){
			new CFWLog(logger).finest("Read package resource content from cache");
			return FileUtils.permanentStringFileCache.get(resourcePath);
		}else{
			
			InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(resourcePath);
			fileContent = readContentsFromInputStream(in);
			FileUtils.permanentStringFileCache.put(resourcePath, fileContent);
		}
		
		return fileContent;

	}
	
	/*************************************************************
	 * Read a resource from the package.
	 * @param path
	 * @return content as string or null if not found.
	 *************************************************************/
	public static byte[] readPackageResourceAsBytes(String resourcePath) {
		
		byte[] fileContent = null;
		
		if( CFWConfig.CACHING_FILE_ENABLED && FileUtils.permanentByteFileCache.containsKey(resourcePath)){
			new CFWLog(logger).finest("Read package resource content from cache");
			return FileUtils.permanentByteFileCache.get(resourcePath);
		}else{
			
			InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(resourcePath);
			fileContent = readBytesFromInputStream(in);
			FileUtils.permanentByteFileCache.put(resourcePath, fileContent);
		}
		
		return fileContent;

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

	/*************************************************************
	 * 
	 * @param path
	 * @return content as string or null if not found.
	 *************************************************************/
	public static byte[] readBytesFromInputStream(InputStream inputStream) {
		
		if(inputStream == null) {
			return null;
		}
		
		InputStreamReader reader = null;
		StringBuffer stringBuffer = new StringBuffer();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			byte[] buffer = new byte[1];
			//DataInputStream dis = new DataInputStream(inputStream);
			
			for (int nChunk = inputStream.read(buffer); nChunk!=-1; nChunk = inputStream.read(buffer))
			{
				stringBuffer.append(buffer);
				os.write(buffer);
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
			
		return os.toByteArray();
	}

	/***********************************************************************
	 * Caches up to 15 files
	 * @param harContent
	 * @return the index of the cache use to retrieve the files with getHARFromCache()
	 ***********************************************************************/
	public static int temporarlyCacheFile(String fileContent) {
	
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
	public static String getTemporarlyCachedFile(int index) {
		synchronized(cachedFiles) {
			if( index >= cachedFiles.length) {
				return null;
			}
			
			return cachedFiles[index];
		}
	}
	
	/***********************************************************************
	 * Caches a file permanently in memory.
	 * @param filename the name of the file
	 * @param fileContent a String representation of the file.
	 * @return nothing
	 ***********************************************************************/
	public static void permanentlyCacheFile(String filename, String fileContent) {
		FileUtils.permanentStringFileCache.put(filename, fileContent);
	}
	
	/***********************************************************************
	 * Check if the files is in the cache.
	 * @param filename the name of the file
	 * @return true or false
	 ***********************************************************************/
	public static boolean isFilePermanentlyCached(String filename) {
		return FileUtils.permanentStringFileCache.containsKey(filename);
	}
	
	/***********************************************************************
	 * Retrieve a cached file by it's index.
	 * @param index the index of the file to be retrieved
	 ***********************************************************************/
	public static String getPermanentlyCachedFile(String filename) {
		return FileUtils.permanentStringFileCache.get(filename);
	}
	
}
