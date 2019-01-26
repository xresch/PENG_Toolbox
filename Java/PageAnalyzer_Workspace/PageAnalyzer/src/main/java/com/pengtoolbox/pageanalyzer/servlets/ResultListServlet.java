package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer._main.SessionData;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.response.TemplateHTMLDefault;
import com.pengtoolbox.pageanalyzer.utils.H2Utils;

public class ResultListServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(ResultListServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		PALogger log = new PALogger(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "View Result");
		StringBuffer content = html.getContent();

		String userID = "";
		
		if(PA.CONFIG_AUTHENTICATION_ENABLED) {
			SessionData data = (SessionData) request.getSession().getAttribute(PA.SESSION_DATA); 
			if(data.isLoggedIn()) {
				userID = data.getUsername();
			}
		}else {
			userID = "anonymous";
		}
		String jsonResults = H2Utils.getResultListForUser(userID);
		
		//TODO: Check User
		
		if (jsonResults == null) {
			html.addAlert(PA.ALERT_ERROR, "Results could not be loaded.");
		}else {
									
			content.append("<div id=\"resultlist\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script>");
			javascript.append("		var RESULT_LIST_DATA = "+jsonResults+";");
			javascript.append("</script>");
			javascript.append("<script defer>initialize();</script>");
				
		}
        
    }
	

}