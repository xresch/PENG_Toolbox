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
import com.pengtoolbox.cfw.utils.H2Utils;

public class ResultViewServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLogger.getLogger(ResultViewServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLogger log = new CFWLogger(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "View Result");
		StringBuffer content = html.getContent();

		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		String resultID = request.getParameter("resultid");
		
		if(!resultID.matches("\\d+")) {
			html.addAlert(CFW.ALERT_ERROR, "Result ID '"+resultID+"' is not a number.");
		}
			
		content.append("<div id=\"results\"></div>");
		
		StringBuffer javascript = html.getJavascript();
		javascript.append("<script defer>");
			javascript.append("initialize();");
			javascript.append("draw({data: 'yslowresult', info: 'overview', view: ''})");
		javascript.append("</script>");

    }
	

}