package com.pageanalyzer.utils;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/*************************************************************************
 * 
 * @author Reto Scheiwiller, 2018
 * 
 * Distributed under the MIT license
 *************************************************************************/
public class CacheUtils {
	
	private static String[] harFiles = new String[15];
	private static int harFileCounter = 0;

	private static Logger logger = LogManager.getLogManager().getLogger(CacheUtils.class.getName());
	
	/***********************************************************************
	 * Caches up to 15 har files
	 * @param harContent
	 * @return the index of the cache use to retrieve the files with getHARFromCache()
	 ***********************************************************************/
	public static int cacheHARFile(String harContent) {
	
		synchronized(harFiles) {
			if( harFileCounter == harFiles.length-1) {
				harFileCounter = 0;
			}
			harFiles[harFileCounter] = harContent;
			harFileCounter++;
			
			return harFileCounter-1;
		}
	}
	
	public static String getCachedHARFile(int index) {
		synchronized(harFiles) {
			if( index >= harFiles.length) {
				return null;
			}
			
			return harFiles[index];
		}
	}
}
