package com.pageanalyzer.phantomjs;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pageanalyzer.logging.PALogger;
import com.pageanalyzer.utils.CacheUtils;

public class PhantomJSInterface
{
	private static Logger logger = PALogger.getLogger(PhantomJSInterface.class.getName());
	
    public static String getHARStringForWebsite(HttpServletRequest request, String url)
    {
    	PALogger log = new PALogger(logger, request).method("getHARStringForWebsite");
    	
    	log.start();
    	String returnValue = "";
    	
        if (url == null)
        {
        	log.severe("Please provide a valid URL.");
        	returnValue = "{\"Error\" : \"Please provide a valid URL.\"} ";
            
        }else{
        	
        	if(!url.startsWith("http")){
        		log.warn("URL does not start with required 'http*'. Prepend 'https://' and try it.");
        		url = "https://"+url;
        	}
	        try
	        {            
	            String osName = System.getProperty("os.name" );
	            if( ! osName.startsWith("Win") )
	            {
	            	returnValue = "{\"Error\" : \"This feature is only available when running the Service on Windows.\"} ";
	            }
	            else {
	            	String command = "./resources/phantomjs/phantomjs2.1.1.exe ./resources/phantomjs/netsniff.js "+url;
	            	Runtime rt = Runtime.getRuntime();
	
	                Process proc = rt.exec(command);
	                
	                // any error message?
	                StreamCatcher errorCatcher = new StreamCatcher(proc.getErrorStream(), "ERROR");            
	                
	                // any output?
	                StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(), "OUTPUT");
	                    
	                // kick them off
	                errorCatcher.start();
	                outputCatcher.start();
	                                        
	                // any error???
	                int exitValue = proc.waitFor();
	                
	                if(exitValue != 0){
	                	log.severe("PhantomJS process returned with an error. "+errorCatcher.getCatchedData());
	                	returnValue = "{\"Error\" : \"PhantomJS process returned with an error state '"+
	                					exitValue+"' and the error output: '"+
	                					errorCatcher.getCatchedData()+"'.\"} ";
	                }else{
	                	returnValue = outputCatcher.getCatchedData();
	                }
	            }
	            
	                 
	        } catch (Throwable t) {
	        	log.severe("Internal Error: Issue with threading.", t);
	        } 
        }
        
        log.end();
        return returnValue;
    }
}
