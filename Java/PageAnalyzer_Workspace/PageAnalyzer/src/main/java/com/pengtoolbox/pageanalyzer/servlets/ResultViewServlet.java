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
import com.pengtoolbox.pageanalyzer.response.TemplatePlain;
import com.pengtoolbox.pageanalyzer.utils.CacheUtils;
import com.pengtoolbox.pageanalyzer.utils.FileUtils;
import com.pengtoolbox.pageanalyzer.utils.H2Utils;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

public class ResultViewServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(ResultViewServlet.class.getName());

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

		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		String resultID = request.getParameter("resultid");
		
		String jsonResults = null;
		if(resultID.matches("\\d+")) {
			jsonResults = H2Utils.getResultByID(Integer.parseInt(resultID));
		}else {
			html.addAlert(PA.ALERT_ERROR, "Result ID '"+resultID+"' is not a number.");
		}
	
		
		if (jsonResults == null) {
			html.addAlert(PA.ALERT_ERROR, "Results could not be loaded.");
		}else {
									
			content.append("<div id=\"yslow-results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script>");
			javascript.append("		var YSLOW_DATA = "+jsonResults+";");
			javascript.append("</script>");
			javascript.append("<script defer>initialize();</script>");
				
		}
        
    }
	

}