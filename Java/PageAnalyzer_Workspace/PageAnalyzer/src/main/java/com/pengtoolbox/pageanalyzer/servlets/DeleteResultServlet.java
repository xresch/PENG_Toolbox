package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLogger;
import com.pengtoolbox.cfw.response.TemplateJSONDefault;
import com.pengtoolbox.cfw.utils.H2Utils;

public class DeleteResultServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLogger.getLogger(DeleteResultServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLogger log = new CFWLogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
		
		TemplateJSONDefault jsonResponse = new TemplateJSONDefault(request);
		StringBuffer content = jsonResponse.getContent();
		
		String resultIDs = request.getParameter("resultids");
		
		if(resultIDs.matches("(\\d,?)+")) {
			boolean result = H2Utils.deleteResults(request, resultIDs);
			content.append("{\"result\": "+result+"}");
		}else {
			content.append("{\"result\": false, \"error\": \"The result could not be deleted: ResultID is not a number.\"}");
		}
		
		response.sendRedirect(response.encodeRedirectURL(CFWConfig.BASE_URL+"/resultlist"));
        
    }
	
}