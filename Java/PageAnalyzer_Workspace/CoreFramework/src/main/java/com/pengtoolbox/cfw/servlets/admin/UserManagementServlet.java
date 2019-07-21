package com.pengtoolbox.cfw.servlets.admin;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class UserManagementServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(UserManagementServlet.class.getName());
	
	public UserManagementServlet() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
		
		HTMLResponse html = new HTMLResponse("User Management");
		
		StringBuffer content = html.getContent();
		
		if(CFW.Context.Request.getUserPermissions() != null
		&& CFW.Context.Request.getUserPermissions().containsKey(CFWDBPermission.CFW_USER_MANAGEMENT)) {
			
			html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js"));
			
			content.append(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".html", "cfw_usermgmt.html"));
			
			html.addJavascriptCode("CFW.usermgmt.draw({tab: 'users'});");
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
        
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
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Please Provide a value in each password field.");
		}else {
			User currentUser = CFW.Context.Request.getUser();
			
			if(currentUser.changePassword(oldpassword, newpassword, repeatpassword)){
				boolean isUpdateSuccessful = CFW.DB.Users.update(currentUser);
				if(isUpdateSuccessful) {
					CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "Password changed successfully.");
				}
			}
		}
		
		HTMLResponse html = new HTMLResponse("Change Password");
		StringBuffer content = html.getContent();
		content.append(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".html", "changepassword.html"));
		
		
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
	}
}