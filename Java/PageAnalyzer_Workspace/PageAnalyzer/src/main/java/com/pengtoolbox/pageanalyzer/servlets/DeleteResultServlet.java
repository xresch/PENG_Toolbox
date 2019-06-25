package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateJSONDefault;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;

public class DeleteResultServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(DeleteResultServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
		
		TemplateJSONDefault jsonResponse = new TemplateJSONDefault(request);
		StringBuffer content = jsonResponse.getContent();
		
		String resultIDs = request.getParameter("resultids");
		
		if(resultIDs.matches("(\\d,?)+")) {
			boolean result = PageAnalyzerDB.deleteResults(resultIDs);
			content.append("{\"result\": "+result+"}");
		}else {
			content.append("{\"result\": false, \"error\": \"The result could not be deleted: ResultID is not a number.\"}");
			log.severe("The result could not be deleted: ResultID is not a number.");
		}
		
		response.sendRedirect(response.encodeRedirectURL("./resultlist"));
        
    }
	
}