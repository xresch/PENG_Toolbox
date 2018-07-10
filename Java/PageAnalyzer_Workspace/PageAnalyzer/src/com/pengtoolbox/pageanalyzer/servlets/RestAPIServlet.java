package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.LogManager;
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
import com.pengtoolbox.pageanalyzer.utils.HTTPUtils;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

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
			
			String results = YSlow.instance().analyzeHarString(harContents);

			content.append(results);
			
		}
	}
}
