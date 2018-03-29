package com.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pageanalyzer._main.PA;
import com.pageanalyzer.logging.PALogger;
import com.pageanalyzer.response.TemplateHTMLDefault;
import com.pageanalyzer.response.TemplatePlain;
import com.pageanalyzer.utils.CacheUtils;
import com.pageanalyzer.utils.HTTPUtils;

/*************************************************************************
 * 
 * @author Reto Scheiwiller, 2018
 * 
 * Distributed under the MIT license
 *************************************************************************/
public class RestAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogManager().getLogger(RestAPIServlet.class.getName());
       
	/*****************************************************************
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 ******************************************************************/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PALogger log = new PALogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Rest API");
		StringBuffer content = html.getContent();
		content.append(PA.getFileContent(request, "./resources/html/api.html"));
		
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
			
		TemplatePlain plain = new TemplatePlain(request);
		StringBuffer content = plain.getContent();

		Part harFile = request.getPart("harFile");
		if (harFile == null) {
			content.append("{\"error\": \"HAR File could not be loaded.\"}");
		}else {

			String harContents = PA.readContentsFromInputStream(harFile.getInputStream());
			int index = CacheUtils.cacheHARFile(harContents);
			
			String nodJSHost = PA.config("pa_nodejs_hostname");
			String nodJSPort = PA.config("pa_nodejs_port");
			String appName = PA.config("pa_application_name");
			String serverName = PA.config("pa_server_hostname");
			String serverPort = PA.config("pa_server_port");
			
			String harLink = "http://"+serverName+":"+serverPort+"/"+appName+"/hardownload?harindex="+index;
			
			String url = "http://"+nodJSHost+":"+nodJSPort+"/?info=all"+
												"&format=json"+
												"&ruleset=pageanalyzer"+
												"&dict=true"+
												"&verbose=true"+
												"&har="+harLink;
			
			String results = HTTPUtils.sendGETRequest(url);

			content.append(results);
			
		}
	}
}
