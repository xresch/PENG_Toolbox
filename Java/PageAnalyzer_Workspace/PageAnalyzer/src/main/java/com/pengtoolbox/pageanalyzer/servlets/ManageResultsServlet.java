package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw._main.CFWContextRequest;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.pageanalyzer.db.PAPermissions;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;

public class ManageResultsServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(ManageResultsServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
		
		TemplateHTMLDefault html = new TemplateHTMLDefault("Manage Results");
		StringBuffer content = html.getContent();
		
		if(CFW.Context.Request.getUserPermissions() != null
		&& CFW.Context.Request.getUserPermissions().containsKey(PAPermissions.MANAGE_RESULTS)) {
					
			String jsonResults = PageAnalyzerDB.getAllResults();
			
			//TODO: Check User
			
			if (jsonResults == null || jsonResults.isEmpty()) {
				CFWContextRequest.addAlert(AlertMessage.MessageType.ERROR, "Results could not be loaded.");
			}else {
										
				content.append("<div id=\"results\"></div>");
				
				StringBuffer javascript = html.getJavascript();
				javascript.append("<script defer>");
					javascript.append("initialize();");
					javascript.append("draw({data: 'allresults', info: 'resultlist', view: ''})");
				javascript.append("</script>");
					
			}
		}else {
			CFW.Context.Request.addAlert(MessageType.ERROR, "Access denied");
		}
        
    }
	

}