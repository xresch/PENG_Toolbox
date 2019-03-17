package com.pengtoolbox.cfw.caching;

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
import com.pengtoolbox.cfw.logging.CFWLog;

public class StringCache {

	private static final HashMap<String,String> stringMap = new HashMap<String,String>();
	
	static String[] cachedStrings = new String[50];
	static int stringCounter = 0;

	/***********************************************************************
	 * Caches up to 15 files
	 * @param harContent
	 * @return the index of the cache use to retrieve the files with getHARFromCache()
	 ***********************************************************************/
	public static int cacheString(String fileContent) {
	
		synchronized(cachedStrings) {
			if( stringCounter == cachedStrings.length-1) {
				stringCounter = 0;
			}
			cachedStrings[stringCounter] = fileContent;
			stringCounter++;
			
			return stringCounter-1;
		}
	}

	/***********************************************************************
	 * Retrieve a cached file by it's index.
	 * @param index the index of the file to be retrieved
	 ***********************************************************************/
	public static String getCachedString(int index) {
		synchronized(cachedStrings) {
			if( index >= cachedStrings.length) {
				return null;
			}
			
			return cachedStrings[index];
		}
	}
	
}
