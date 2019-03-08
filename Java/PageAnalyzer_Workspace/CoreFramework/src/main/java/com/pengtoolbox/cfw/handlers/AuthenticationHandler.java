package com.pengtoolbox.cfw.handlers;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
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
    	
    	if(CFWConfig.AUTHENTICATION_ENABLED) {
    		
    		//##################################
        	// Get Session
        	//##################################
        	HttpSession session = request.getSession();
        	
        	SessionData data = (SessionData)session.getAttribute(CFW.SESSION_DATA); 
        	if(data.isLoggedIn()) {

	        	//##################################
	        	// Call Wrapped Handler
	        	//##################################
	        	this._handler.handle(target, baseRequest, request, response);
	        	
        	}else {
        		
        		if(request.getRequestURI().toString().endsWith("/login")) {
        			this._handler.handle(target, baseRequest, request, response);
        		}else {
        			response.sendRedirect(response.encodeRedirectURL(CFWConfig.BASE_URL+"/login"));
        		}
        	}
	
    	}else {
    		this._handler.handle(target, baseRequest, request, response);
    	}
    	

    	
    }
}