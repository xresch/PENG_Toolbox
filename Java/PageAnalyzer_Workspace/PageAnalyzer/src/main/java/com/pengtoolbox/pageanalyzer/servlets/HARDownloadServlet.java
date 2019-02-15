package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLogger;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.response.TemplatePlain;
import com.pengtoolbox.cfw.utils.FileUtils;

public class HARDownloadServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLogger.getLogger(HARDownloadServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLogger log = new CFWLogger(logger, request).method("doGet");
		log.info(request.getRequestURL()+"?"+request.getQueryString());
			
		TemplatePlain plain = new TemplatePlain(request);
		StringBuffer content = plain.getContent();
		
		String harindex = request.getParameter("resultid");
		
		content.append(FileUtils.getCachedFile(Integer.parseInt(harindex)) );
		
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	

}