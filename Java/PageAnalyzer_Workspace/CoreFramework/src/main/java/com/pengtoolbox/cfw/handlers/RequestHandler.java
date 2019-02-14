package com.pengtoolbox.cfw.handlers;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.logging.CFWLogger;
 
public class RequestHandler extends HandlerWrapper
{
	private static Logger logger = CFWLogger.getLogger(RequestHandler.class.getName());
			
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
    	
    	//##################################
    	// Before
    	//##################################
    	
    	CFWLogger log = new CFWLogger(logger)
    			.request(request)
    			.method("handle");
    	// Used to calculate deltaStart by OMLogger.log()
    	// minus 1ms to be always first
    	
    	String startNanosHeader = request.getHeader(CFW.REQUEST_ATTR_STARTNANOS);
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
    	
    	request.setAttribute(CFW.REQUEST_ATTR_STARTNANOS, startNanos);
    	

    	//---------------------------------------
    	//ReqestID used in logging
    	
    	String requestID = request.getHeader(CFW.REQUEST_ATTR_ID);
    	if(requestID == null){
    		requestID = UUID.randomUUID().toString();
    	}
    	
    	request.setAttribute(CFW.REQUEST_ATTR_ID, requestID);
    	
    	//##################################
    	// Get Session
    	//##################################
    	HttpSession session = request.getSession();
    	
    	if(session.getAttribute(CFW.SESSION_DATA) == null) {
    		session.setAttribute(CFW.SESSION_DATA, new SessionData());
    	};
    	
    	//workaround maxInactiveInterval=-1 issue
    	session.setMaxInactiveInterval(3600);

    	//##################################
    	// Call Wrapped Handler
    	//##################################
    	this._handler.handle(target, baseRequest, request, response);
    	
    	//##################################
    	// After
    	//##################################
    	request.setAttribute(CFW.REQUEST_ATTR_ENDNANOS, System.nanoTime());
    	
    	//if(request.getAttribute(PA.REQUEST_ATTR_TEMPLATE) instanceof AbstractTemplateHTML){
    	CFW.writeLocalized(request, response);
    	
    	
    	log.end();
    	
        baseRequest.setHandled(true);
    }
}