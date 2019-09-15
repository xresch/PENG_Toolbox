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
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.BTFormHandler;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;
import com.pengtoolbox.cfw.response.bootstrap.CFWField.FormFieldType;

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
		
		createForms();
		
		HTMLResponse html = new HTMLResponse("User Management");
		
		StringBuffer content = html.getContent();
		
		if(CFW.Context.Request.hasPermission(CFWDBPermission.CFW_USER_MANAGEMENT)) {
			
			//html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js"));
			html.addJSFileBottomAssembly(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js");
			
			content.append(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".html", "cfw_usermgmt.html"));
			
			html.addJavascriptCode("cfw_usermgmt_draw({tab: 'users'});");
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
        
    }
	
	private void createForms() {
		
		//--------------------------------------
		// Create User Form
		BTForm createUserForm = new BTForm("cfw-createUserForm", "Create User");
		
		createUserForm.addChild(new CFWField<String>(FormFieldType.TEXT, "Username"));
		createUserForm.addChild(new CFWField<String>(FormFieldType.PASSWORD, "Password"));
		createUserForm.addChild(new CFWField<String>(FormFieldType.PASSWORD, "Repeat Password"));
		createUserForm.setFormHandler(new BTFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form) {
				
				String username = request.getParameter("Username");
				String password = request.getParameter("Password");
				String repeatedPassword = request.getParameter("Repeat Password");
				
				User newUser = new User(username).setInitialPassword(password, repeatedPassword);
				
				if(newUser != null) {
					if(CFW.DB.Users.create(newUser)) {
						CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "User created successfully!");
					}
				}
				
			}
		});
		
		//--------------------------------------
		// Create Group Form
		
		BTForm createGroupForm = new BTForm("cfw-createGroupForm", "Create Group");
		
		createGroupForm.addChild(new CFWField<String>(FormFieldType.TEXT, "Name"));
		createGroupForm.addChild(new CFWField<String>(FormFieldType.TEXTAREA, "Description"));
		
		createGroupForm.setFormHandler(new BTFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form) {
				
				String name = request.getParameter("Name");
				String description = request.getParameter("Description");
				
				Group newGroup = new Group(name).description(description);
				
				if(newGroup != null) {
					if(CFW.DB.Groups.create(newGroup)) {
						CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "Group created successfully!");
					}
				}
				
			}
		});
	}
	
}