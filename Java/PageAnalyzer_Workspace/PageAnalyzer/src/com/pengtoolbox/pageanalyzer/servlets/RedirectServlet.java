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
import com.pengtoolbox.pageanalyzer.utils.CacheUtils;
import com.pengtoolbox.pageanalyzer.utils.HTTPUtils;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

//@MultipartConfig(maxFileSize=1024*1024*100, maxRequestSize=1024*1024*100)
public class RedirectServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(RedirectServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		PALogger log = new PALogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
		
		switch(request.getRequestURI()) {
		
			case "/": 			  	response.sendRedirect("/pageanalyzer/harupload");
									break;
									
			case "/pageanalyzer": 	response.sendRedirect("/pageanalyzer/harupload");
									break;
			
			default: response.sendRedirect("/pageanalyzer/harupload");
		}

		
        //response.setContentType("text/html");
        //response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PALogger log = new PALogger(logger, request).method("doPost");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze");
		StringBuffer content = html.getContent();
		content.append(PA.getFileContent(request, "./resources/html/harupload.html"));
		
		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		Part harFile = request.getPart("harFile");
		if (harFile == null) {
			html.addAlert(PA.ALERT_ERROR, "HAR File could not be loaded.");
		}else {

			log.start().method("doPost()-StreamAndCacheHarFile");
				String harContents = PA.readContentsFromInputStream(harFile.getInputStream());
			log.end();
						
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