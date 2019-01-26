package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer._main.SessionData;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.login.LoginFacade;
import com.pengtoolbox.pageanalyzer.response.TemplateHTMLDefault;
import com.pengtoolbox.pageanalyzer.utils.FileUtils;

public class LoginServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(LoginServlet.class.getName());
	
	public LoginServlet() {
		

		
	}
	

		
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
		content.append(FileUtils.getFileContent(request, "./resources/html/login.html"));
		
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
		
		//--------------------------
		// Create Content
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(username == null || password == null){
			TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
			StringBuffer content = html.getContent();
			content.append(FileUtils.getFileContent(request, "./resources/html/login.html"));
			
			html.addAlert(PA.ALERT_ERROR, "Please specify a username and password.");
		}else {
			
			if(LoginFacade.getInstance().checkCredentials(username, password)) {
				//Login success
				HttpSession session = request.getSession();
				SessionData data = (SessionData)session.getAttribute(PA.SESSION_DATA); 
				data.setLoggedIn(true);
				data.setUsername(username);
				
				response.sendRedirect(response.encodeRedirectURL(PA.BASE_URL+"/harupload"));
			}else {
				//Login Failure
				TemplateHTMLDefault html = new TemplateHTMLDefault(request, "Analyze URL");
				StringBuffer content = html.getContent();
				content.append(FileUtils.getFileContent(request, "./resources/html/login.html"));
				
				html.addAlert(PA.ALERT_ERROR, "Username or password invalid.");
			}
			
		}
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}