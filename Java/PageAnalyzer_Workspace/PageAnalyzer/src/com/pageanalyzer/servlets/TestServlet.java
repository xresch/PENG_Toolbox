package com.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pageanalyzer._main.PA;
import com.pageanalyzer.handlers.RequestHandler;
import com.pageanalyzer.logging.PALogger;
import com.pageanalyzer.response.TemplateHTMLDefault;

public class TestServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(TestServlet.class.getName());

	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
		
		PALogger log = new PALogger(logger, request).method("doGet");
		
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Test Page");
		StringBuffer content = html.getContent();
		
		//--------------------------
		//Add messages manually
		//--------------------------
		html.addAlert(PA.ALERT_INFO, "this is an info.");
		html.addAlert(PA.ALERT_WARN, "this is a warning.");
		html.addAlert(PA.ALERT_ERROR, "this is an error.");
		html.addAlert(PA.ALERT_SUCCESS, "this is an success.");
		
		//------------------------------
		//Add messages by log exception
		//------------------------------
		Throwable severe = new ArrayIndexOutOfBoundsException("You went over the bounds.");
		log.severe("Test - Oops!!!Something went severly wrong...", severe);
		
		Throwable warn = new NumberFormatException("The format is Wrong!!!");
		log.warn("Test - Oops!!! some warning...", warn);
		
		//------------------------------
		// Test cannot read file
		//------------------------------
		String cannotReadFile = PA.getFileContent(request, "./resources/this_file_does_not_exists.txt");
		
		//------------------------------
		// Test Localization
		//------------------------------
		content.append("<p><strong>Localization Test(success):</strong> {!lang_global_size!}, {!lang_global_top!}, {!lang_global_bottom!}, {!lang_global_left!}{!lang_global_right!}<p>");
		content.append("<p><strong>Localization Test(fail):</strong> {!lang.does.not.exist!}<p>");
		
		String htmlfile = request.getParameter("file");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
//        List<String> fileContent = Files.readAllLines(Paths.get("./resources/html/"+htmlfile), Charset.forName("UTF-8"));
//        
//        StringBuffer content = html.getContent();
//        
//        for(String line : fileContent){
//        	content.append(line);
//        	content.append("\n");
//    	}
        
    }
}