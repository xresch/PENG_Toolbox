package com.pengtoolbox.cfw._main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWHttp {
	
	public static Logger logger = CFWLog.getLogger(CFWHttp.class.getName());
	
	
	/******************************************************************************************************
	 * Redirects to the referer of the request.
	 * @throws IOException 
	 ******************************************************************************************************/
	public static void redirectToReferer( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(request.getHeader("referer")));
	}
	
	/******************************************************************************************************
	 * Redirects to the specified url.
	 * @throws IOException 
	 ******************************************************************************************************/
	public static void redirectToURL(HttpServletResponse response, String url ) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(url));
	}
	
	/******************************************************************************************************
	 * Send a HTTP GET request and returns the result as a String.
	 * @param url used for the request.
	 * @return String response
	 ******************************************************************************************************/
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
			CFWHttp.logger.log(Level.SEVERE, "Exception occured:", e);
		} catch (IOException e) {
			CFWHttp.logger.log(Level.SEVERE, "Exception occured:", e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					CFWHttp.logger.log(Level.SEVERE, "Exception occured:", e);
				}
			}
		}
	    
		return buffer.toString();
	
	}
	
	/******************************************************************************************************
	 * Creates a map of all cookies in a request.
	 * @param request
	 * @return HashMap containing the key value pairs of the cookies in the response
	 ******************************************************************************************************/
	public static HashMap<String,String> getCookiesAsMap(HttpServletRequest request) {
		
		HashMap<String,String> cookieMap = new HashMap<String,String>();
		
		for(Cookie cookie : request.getCookies()) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
		
		return cookieMap;
	}
	
	/******************************************************************************************************
	 * Get the cookie
	 * @param url used for the request.
	 * @return String response
	 ******************************************************************************************************/
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
	
	
}
