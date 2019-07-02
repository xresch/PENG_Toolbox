package com.pengtoolbox.cfw.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWContextRequest;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.login.LoginFacade;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.utils.CFWFiles;

public class LoginServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(LoginServlet.class.getName());
	
	public LoginServlet() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
			
		TemplateHTMLDefault html = new TemplateHTMLDefault("Login");
		StringBuffer content = html.getContent();
		content.append(CFW.Files.readPackageResource(FileAssembly.CFW_JAR_RESOURCES_PATH + ".html", "login.html"));
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CFWLog log = new CFWLog(logger).method("doPost");
		log.info(request.getRequestURL().toString());
		
		//--------------------------
		// Get Credentials
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		//--------------------------
		// Check authorization
		if(username == null || password == null){
			TemplateHTMLDefault html = new TemplateHTMLDefault("Login");
			StringBuffer content = html.getContent();
			content.append(CFW.Files.readPackageResource(FileAssembly.CFW_JAR_RESOURCES_PATH + ".html", "login.html"));
			
			CFWContextRequest.addAlert(MessageType.ERROR, "Please specify a username and password.");
		}else {
			if(LoginFacade.getInstance().checkCredentials(username, password)) {
				//Login success
				SessionData data = CFW.Context.Request.getSessionData(); 
				data.setUser(new User(username));
				data.triggerLogin();
				
				response.sendRedirect(response.encodeRedirectURL("./harupload"));
				
			}else {
				//Login Failure
				TemplateHTMLDefault html = new TemplateHTMLDefault("Login");
				StringBuffer content = html.getContent();
				content.append(CFWFiles.readPackageResource(FileAssembly.CFW_JAR_RESOURCES_PATH + ".html", "login.html"));
				
				CFWContextRequest.addAlert(MessageType.ERROR, "Username or password invalid.");
			}
			
		}
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}