package com.pengtoolbox.cfw.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFW.Config;
import com.pengtoolbox.cfw._main.CFWContextRequest;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.login.LoginFacade;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class LoginServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(LoginServlet.class.getName());
	
	public LoginServlet() {
	
	}
	
	protected void createLoginPage( HttpServletRequest request, HttpServletResponse response ) {
		HTMLResponse html = new HTMLResponse("Login");
		StringBuffer content = html.getContent();
		
		String loginHTML = CFW.Files.readPackageResource(FileAssembly.CFW_JAR_RESOURCES_PATH + ".html", "login.html");
		
		String url = request.getParameter("url");

		if(url == null) { url = "";}
		loginHTML = loginHTML.replace("urlvalue", url);
		
		content.append(loginHTML);
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
		
		createLoginPage(request, response);
        
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
		String url = request.getParameter("url");
		
		//--------------------------
		// Check authorization
		if(username == null || password == null){
			
			CFWContextRequest.addAlert(MessageType.ERROR, "Please specify a username and password.");
			createLoginPage(request, response);
			
		}else {
			User user = LoginFacade.getInstance().checkCredentials(username, password);
			if(user != null) {
				//Login success
				SessionData data = CFW.Context.Request.getSessionData(); 
				data.setUser(user);
				data.triggerLogin();
				
				if(url == null || url.isEmpty()) {
					url = Config.BASE_URL;
				}
				CFW.HTTP.redirectToURL(response, url);
				
			}else {
				//Login Failure
				createLoginPage(request, response);
				CFWContextRequest.addAlert(MessageType.ERROR, "Username or password invalid.");
			}
			
		}
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}