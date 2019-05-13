package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;

public class GanttChartServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(GanttChartServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "View Result");
		StringBuffer content = html.getContent();

		//content.append("<h1>Gantt Chart</h1>");
		//content.append("<p>Use the links in the menu to change the view. </p>");
		
		String resultID = request.getParameter("resultid");
		
		String jsonResults = null;
		if(resultID.matches("\\d+")) {
			jsonResults = PageAnalyzerDB.getHARFileByID(request, Integer.parseInt(resultID));
		}else {
			html.addAlert(CFW.ALERT_ERROR, "Result ID '"+resultID+"' is not a number.");
		}
	
		
		if (jsonResults == null) {
			html.addAlert(CFW.ALERT_ERROR, "Results could not be loaded.");
		}else {
									
			content.append("<div id=\"results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			
			javascript.append("<script defer>");
				javascript.append("initialize();");
				javascript.append("draw({data: 'har', info: 'ganttchart', view: ''})");
			javascript.append("</script>");
							
		}
        
    }
	

}