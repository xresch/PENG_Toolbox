package com.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pageanalyzer._main.PA;
import com.pageanalyzer.logging.PALogger;
import com.pageanalyzer.phantomjs.PhantomJSInterface;
import com.pageanalyzer.response.TemplateHTMLDefault;
import com.pageanalyzer.utils.CacheUtils;
import com.pageanalyzer.utils.HTTPUtils;
import com.pageanalyzer.yslow.YSlow;

public class AnalyzeURLServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(AnalyzeURLServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		PALogger log = new PALogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
		StringBuffer content = html.getContent();
		content.append(PA.getFileContent(request, "./resources/html/analyzeurl.html"));
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PALogger log = new PALogger(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
		StringBuffer content = html.getContent();
		content.append(PA.getFileContent(request, "./resources/html/analyzeurl.html"));
		
		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		String url = request.getParameter("url");
		
		if(url == null){
			html.addAlert(PA.ALERT_ERROR, "Please specify a URL.");
		}else {

			String harContents = PhantomJSInterface.getHARStringForWebsite(request, url);
	        	        
			String results = YSlow.instance().analyzeHarString(harContents);
			
			content.append("<div id=\"yslow-results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script>");
			javascript.append("		var YSLOW_DATA = "+results+";");
			javascript.append("</script>");
			javascript.append("<script defer>initialize();</script>");
				
		}
	}
}