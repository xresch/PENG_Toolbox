package com.pengtoolbox.pageanalyzer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.servlets.HARUploadServlet;

/*************************************************************************
 * 
 * @author Reto Scheiwiller, 2018
 * 
 * Distributed under the MIT license
 *************************************************************************/
public class HTTPUtils {

	private static Logger logger = PALogger.getLogger(HTTPUtils.class.getName());

	public static String sendGETRequest(String url) {

		StringBuffer buffer = new StringBuffer();
		BufferedReader in = null;
		try {
			URL getURL = new URL(url);
			
	        URLConnection connection = getURL.openConnection();
	        //logger.info(connection.getHeaderField("Response"));
	        
	        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	
	        while ((inputLine = in.readLine()) != null) {
	        	buffer.append(inputLine);
	        	buffer.append("\n");
	        }
        
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "Exception occured:", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception occured:", e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Exception occured:", e);
				}
			}
		}
        
		return buffer.toString();

	}
}
