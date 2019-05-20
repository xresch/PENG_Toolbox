package com.pengtoolbox.cfw.tests.web.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.AbstractTemplateHTML.AlertType;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;

public class TestServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(TestServlet.class.getName());

	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
		
		CFWLog log = new CFWLog(logger, request).method("doGet");
		
		TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Test Page");
		StringBuffer content = html.getContent();
		
		//--------------------------
		//Add messages manually
		//--------------------------
		html.addAlert(AlertType.INFO, "this is an info.");
		html.addAlert(AlertType.WARNING, "this is a warning.");
		html.addAlert(AlertType.ERROR, "this is an error.");
		html.addAlert(AlertType.SUCCESS, "this is a success.");
		
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
		String cannotReadFile = CFW.Files.getFileContent(request, "./resources/this_file_does_not_exists.txt");

		//------------------------------
		// Test Localization
		//------------------------------
		content.append("<p><strong>Localization Test(success):</strong> {!cfw_lang_test_key!}<p>");
		content.append("<p><strong>Localization Test(fail):</strong> {!lang.does.not.exist!}<p>");
		
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