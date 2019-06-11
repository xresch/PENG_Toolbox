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
import com.pengtoolbox.cfw.response.AbstractTemplateHTML.AlertType;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;
import com.pengtoolbox.pageanalyzer.phantomjs.PhantomJSInterface;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

public class AnalyzeURLServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(AnalyzeURLServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
		StringBuffer content = html.getContent();
		content.append(CFWFiles.getFileContent(request, "./resources/html/analyzeurl.html"));
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CFWLog log = new CFWLog(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
		
		//--------------------------
		// Create Content
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
		StringBuffer content = html.getContent();
		content.append(CFWFiles.getFileContent(request, "./resources/html/analyzeurl.html"));
		
		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		//--------------------------------------
		// Get Save Results Checkbox
		String resultName = request.getParameter("resultName");
		String saveResults = request.getParameter("saveResults");
		
		//--------------------------------------
		// Get URL
		String analyzeURL = request.getParameter("analyzeurl");
		
		if(analyzeURL == null){
			html.addAlert(AlertType.ERROR, "Please specify a URL.");
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
				PageAnalyzerDB.saveResults(request, resultName, results, harContents);
			}
			
			//--------------------------------------
			// Prepare Response
			content.append("<div id=\"results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script defer>");
			javascript.append("		YSLOW_RESULT = "+results+";\n");
			javascript.append("		HAR_DATA = "+harContents.replaceAll("</script>", "&lt;/script>")+";\n");
			javascript.append("		initialize();");
			javascript.append("		prepareYSlowResults(YSLOW_RESULT);");
			javascript.append("		prepareGanttData(HAR_DATA);");
			javascript.append("		RULES = CFW.array.sortArrayByValueOfObject(RULES, \"score\");");
			javascript.append("		$(\".result-view-tabs\").css(\"visibility\", \"visible\");");
			javascript.append("		draw({data: 'yslowresult', info: 'overview', view: ''})");
			javascript.append("</script>");
				
		}
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}