package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.cfw.utils.HTTPUtils;

//@MultipartConfig(maxFileSize=1024*1024*100, maxRequestSize=1024*1024*100)
public class HARUploadNodeJSServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(HARUploadNodeJSServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze");
		StringBuffer content = html.getContent();
		content.append(CFWFiles.getFileContent(request, "./resources/html/harupload.html"));
		
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
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze");
		StringBuffer content = html.getContent();
		content.append(CFWFiles.getFileContent(request, "./resources/html/harupload.html"));
		
		content.append("<h1>Results</h1>");
		content.append("<p>Use the links in the menu to change the view. </p>");
		
		Part harFile = request.getPart("harFile");
		if (harFile == null) {
			html.addAlert(CFW.ALERT_ERROR, "HAR File could not be loaded.");
		}else {

			log.start().method("doPost()-StreamAndCacheHarFile");
				String harContents = CFWFiles.readContentsFromInputStream(harFile.getInputStream());
				int index = CFWFiles.temporarlyCacheFile(harContents);
			log.end();
			
			String nodJSHost = CFWConfig.configAsString("pa_nodejs_hostname", "");
			String nodJSPort = CFWConfig.configAsString("pa_nodejs_port", "");
			String appName = CFWConfig.configAsString("pa_application_name", "");
			String serverName = CFWConfig.configAsString("pa_server_hostname", "");
			String serverPort = CFWConfig.configAsString("pa_server_port", "");
			
			String harLink = "http://"+serverName+":"+serverPort+"/"+appName+"/hardownload?harindex="+index;
			
			String url = "http://"+nodJSHost+":"+nodJSPort+"/?info=all"+
												"&format=json"+
												"&ruleset=pageanalyzer"+
												"&dict=true"+
												"&verbose=true"+
												"&har="+harLink;
			
			log.start().method("doPost()-NodeJSAnalysis");
				String results = HTTPUtils.sendGETRequest(url);
			log.end();
			
			content.append("<div id=\"yslow-results\"></div>");
			
			StringBuffer javascript = html.getJavascript();
			javascript.append("<script>");
			javascript.append("		var YSLOW_DATA = "+results+";");
			javascript.append("</script>");
			javascript.append("<script defer>initialize();</script>");
				
		}
	}
}