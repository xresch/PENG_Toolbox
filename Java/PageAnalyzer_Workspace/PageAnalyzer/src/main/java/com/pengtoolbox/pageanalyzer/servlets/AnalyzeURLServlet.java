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
import com.pengtoolbox.cfw.utils.FileUtils;
import com.pengtoolbox.cfw.utils.H2Utils;
import com.pengtoolbox.pageanalyzer.phantomjs.PhantomJSInterface;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

public class AnalyzeURLServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLogger.getLogger(AnalyzeURLServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLogger log = new CFWLogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
		StringBuffer content = html.getContent();
		content.append(FileUtils.getFileContent(request, "./resources/html/analyzeurl.html"));
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CFWLogger log = new CFWLogger(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
		
		//--------------------------
		// Create Content
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
		StringBuffer content = html.getContent();
		content.append(FileUtils.getFileContent(request, "./resources/html/analyzeurl.html"));
		
		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		//--------------------------------------
		// Get Save Results Checkbox
		String saveResults = request.getParameter("saveResults");
		
		//--------------------------------------
		// Get URL
		String analyzeURL = request.getParameter("analyzeurl");
		
		if(analyzeURL == null){
			html.addAlert(CFW.ALERT_ERROR, "Please specify a URL.");
		}else {

			//--------------------------
			// Create HAR for URL and
			// cut out additional strings
			String harContents = PhantomJSInterface.instance().getHARStringForWebsite(request, analyzeURL);
			
			int jsonIndex = harContents.indexOf("{");
			if(jsonIndex > 0) {
				String infoString = harContents.substring(0,jsonIndex-1);
				log.warn("PhantomJS returned Information: "+ infoString);
				harContents = harContents.substring(jsonIndex);
			}
			
			//--------------------------
			// Analyze HAR
			String results = YSlow.instance().analyzeHarString(harContents);
			
			//--------------------------------------
			// Save Results to DB
			if(saveResults != null && saveResults.trim().toLowerCase().equals("on")) {
				H2Utils.saveResults(request, results, harContents);
			}
			
			//--------------------------------------
			// Prepare Response
			content.append("<div id=\"yslow-results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script>");
			javascript.append("		var YSLOW_DATA = "+results+";\n");
			//javascript.append("		var HAR_FILE = "+harContents+";");
			javascript.append("</script>");
			javascript.append("<script defer>initialize();</script>");
				
		}
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}