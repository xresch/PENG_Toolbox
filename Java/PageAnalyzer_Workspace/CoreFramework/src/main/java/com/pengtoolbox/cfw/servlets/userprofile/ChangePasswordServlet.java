package com.pengtoolbox.cfw.servlets.userprofile;

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

public class ChangePasswordServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(ChangePasswordServlet.class.getName());
	
	public ChangePasswordServlet() {
	
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
		content.append(CFW.Files.readPackageResource(FileAssembly.CFW_JAR_RESOURCES_PATH + ".html", "changepassword.html"));
		
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
		// Get passwords
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		String repeatpassword = request.getParameter("repeatpassword");
		
		if(oldpassword == null || oldpassword.isEmpty()
		|| newpassword == null || newpassword.isEmpty()
		|| repeatpassword == null || repeatpassword.isEmpty()) {
			CFW.Context.Request.addAlert(MessageType.ERROR, "Please Provide a value in each password field.");
		}else {
			User currentUser = CFW.Context.Request.getUser();
			
			if(currentUser.changePassword(oldpassword, newpassword, repeatpassword)){
				boolean isUpdateSuccessful = CFW.DB.Users.update(currentUser);
				if(isUpdateSuccessful) {
					CFW.Context.Request.addAlert(MessageType.SUCCESS, "Password changed successfully.");
				}
			}
		}
		
		TemplateHTMLDefault html = new TemplateHTMLDefault("Change Password");
		StringBuffer content = html.getContent();
		content.append(CFW.Files.readPackageResource(FileAssembly.CFW_JAR_RESOURCES_PATH + ".html", "changepassword.html"));
		
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}