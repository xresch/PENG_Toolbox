package com.pengtoolbox.cfw.handlers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWProperties;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.logging.CFWLog;
 
public class AuthenticationHandler extends HandlerWrapper
{
	private static Logger logger = CFWLog.getLogger(AuthenticationHandler.class.getName());
			
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
    	
    	if(CFWProperties.AUTHENTICATION_ENABLED) {
    		
    		//##################################
        	// Get Session
        	//##################################

        	SessionData data = CFW.Context.Request.getSessionData(); 
        	
        	if(data.isLoggedIn()) {

	        	//##################################
	        	// Call Wrapped Handler
	        	//##################################
	        	this._handler.handle(target, baseRequest, request, response);
	        	
        	}else {
        		
        		if(request.getRequestURI().toString().endsWith("/login")
        		   || request.getRequestURI().toString().contains("/login;jsessionid")) {
        			this._handler.handle(target, baseRequest, request, response);
        		}else {
        			CFW.HTTP.redirectToURL(response, "./login?url="+URLEncoder.encode(request.getRequestURI()+"?"+request.getQueryString()) );
        		}
        	}
	
    	}else {
    		if(CFW.Context.Request.getUser() == null) {
    			CFW.Context.Request.getSessionData().setUser(CFW.DB.Users.selectByUsernameOrMail("anonymous"));
    			CFW.Context.Session.getSessionData().triggerLogin();
    		}
    		
    		this._handler.handle(target, baseRequest, request, response);
    	}
    	

    	
    }
}