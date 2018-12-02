package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.response.TemplateHTMLDefault;
import com.pengtoolbox.pageanalyzer.response.TemplateJSONDefault;
import com.pengtoolbox.pageanalyzer.response.TemplatePlain;
import com.pengtoolbox.pageanalyzer.utils.CacheUtils;
import com.pengtoolbox.pageanalyzer.utils.FileUtils;
import com.pengtoolbox.pageanalyzer.utils.H2Utils;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

public class DeleteResultServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(DeleteResultServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		PALogger log = new PALogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
		
		TemplateJSONDefault jsonResponse = new TemplateJSONDefault(request);
		StringBuffer content = jsonResponse.getContent();
		
		String resultIDs = request.getParameter("resultids");
		
		if(resultIDs.matches("(\\d,?)+")) {
			boolean result = H2Utils.deleteResults(resultIDs);
			content.append("{\"result\": "+result+"}");
		}else {
			content.append("{\"result\": false, \"error\": \"The result could not be deleted: ResultID is not a number.\"}");
		}
		
		response.sendRedirect(response.encodeRedirectURL(PA.BASE_URL+"/resultlist"));
        
    }
	
}