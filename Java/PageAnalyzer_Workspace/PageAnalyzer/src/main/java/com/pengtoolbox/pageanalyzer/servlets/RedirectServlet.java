package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLog;

//@MultipartConfig(maxFileSize=1024*1024*100, maxRequestSize=1024*1024*100)
public class RedirectServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(RedirectServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
		
		switch(request.getRequestURI()) {
		
			case "/": 			  	response.sendRedirect(response.encodeRedirectURL(CFWConfig.BASE_URL+"/harupload"));
									break;
									
			case "/pageanalyzer": 	response.sendRedirect(response.encodeRedirectURL(CFWConfig.BASE_URL+"/harupload"));
									break;
			
			default: response.sendRedirect(response.encodeRedirectURL(CFWConfig.BASE_URL+"/harupload"));
		}

		
        //response.setContentType("text/html");
        //response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
}