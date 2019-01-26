package com.pengtoolbox.pageanalyzer.handlers;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer._main.SessionData;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
 
public class AuthenticationHandler extends HandlerWrapper
{
	private static Logger logger = PALogger.getLogger(AuthenticationHandler.class.getName());
			
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
    	
    	if(PA.CONFIG_AUTHENTICATION_ENABLED) {
    		
    		//##################################
        	// Get Session
        	//##################################
        	HttpSession session = request.getSession();
        	
        	SessionData data = (SessionData)session.getAttribute(PA.SESSION_DATA); 
        	if(data.isLoggedIn()) {

	        	//##################################
	        	// Call Wrapped Handler
	        	//##################################
	        	this._handler.handle(target, baseRequest, request, response);
	        	
        	}else {
        		
        		if(request.getRequestURI().toString().endsWith("/login")) {
        			this._handler.handle(target, baseRequest, request, response);
        		}else {
        			response.sendRedirect(response.encodeRedirectURL(PA.BASE_URL+"/login"));
        		}
        	}
	
    	}else {
    		this._handler.handle(target, baseRequest, request, response);
    	}
    	

    	
    }
}