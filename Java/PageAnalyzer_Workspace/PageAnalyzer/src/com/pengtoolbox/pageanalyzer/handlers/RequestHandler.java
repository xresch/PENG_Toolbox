package com.pengtoolbox.pageanalyzer.handlers;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
 
public class RequestHandler extends HandlerWrapper
{
	private static Logger logger = PALogger.getLogger(RequestHandler.class.getName());
			
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
    	
    	//##################################
    	// Before
    	//##################################
    	
    	PALogger log = new PALogger(logger)
    			.request(request)
    			.method("handle");
    	// Used to calculate deltaStart by OMLogger.log()
    	// minus 1ms to be always first
    	
    	String startNanosHeader = request.getHeader(PA.REQUEST_ATTR_STARTNANOS);
    	long startNanos = -1;
    	
    	if(startNanosHeader != null){
	    	try{
	    		startNanos = Long.parseLong(startNanosHeader);
	    		log.start();
	    		
	    	}catch(Exception e){
	    		startNanos = System.nanoTime()-1000000;
	    		
	        	log.start(startNanos);
	    	}
    	}else{
    		startNanos = System.nanoTime()-1000000;
    		
    		log.start(startNanos);
    	}
    	
    	request.setAttribute(PA.REQUEST_ATTR_STARTNANOS, startNanos);
    	

    	//---------------------------------------
    	//ReqestID used in logging
    	
    	String requestID = request.getHeader(PA.REQUEST_ATTR_ID);
    	if(requestID == null){
    		requestID = UUID.randomUUID().toString();
    	}
    	
    	request.setAttribute(PA.REQUEST_ATTR_ID, requestID);
    	
    	//##################################
    	// Get Session
    	//##################################
    	HttpSession session = request.getSession();
    	//workaround maxInactiveInterval=-1 issue
    	session.setMaxInactiveInterval(3600);

    	//##################################
    	// Call Wrapped Handler
    	//##################################
    	this._handler.handle(target, baseRequest, request, response);
    	
    	//##################################
    	// After
    	//##################################
    	request.setAttribute(PA.REQUEST_ATTR_ENDNANOS, System.nanoTime());
    	
    	//if(request.getAttribute(PA.REQUEST_ATTR_TEMPLATE) instanceof AbstractTemplateHTML){
    	PA.writeLocalized(request, response);
    	
    	
    	log.end();
    	
        baseRequest.setHandled(true);
    }
}