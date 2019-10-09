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
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser.UserDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.BTFormHandler;
import com.pengtoolbox.cfw.validation.LengthValidator;
import com.pengtoolbox.cfw.validation.NotNullOrEmptyValidator;
import com.pengtoolbox.cfw.validation.PasswordValidator;

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
		CreateUserForm createUserForm = new CreateUserForm("cfwCreateUserForm", "Create User");
		
		createUserForm.setFormHandler(new BTFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form, CFWObject origin) {
				
				if(form.mapRequestParameters(request)) {
					CreateUserForm casted = (CreateUserForm)form;
					User newUser = new User(casted.getUsername())
							.status("Active")
							.isForeign(casted.getIsForeign())
							.setInitialPassword(casted.getPassword(), casted.getRepeatedPassword());
					
					if(newUser != null) {
						if(CFW.DB.Users.create(newUser)) {
							
							User userFromDB = CFW.DB.Users.selectByUsernameOrMail(newUser.username());
							if (CFW.DB.UserGroupMap.addUserToGroup(userFromDB, CFW.DB.Groups.CFW_GROUP_USER, true)) {
								CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "User created successfully!");
								return;
							}
						}
					}
				}

			}
		});
		
		//--------------------------------------
		// Create Group Form
		
		BTForm createGroupForm = new Group("").toForm("cfwCreateGroupForm", "Create Group");
		
		createGroupForm.setFormHandler(new BTFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form, CFWObject origin) {
								
				if(origin != null) {
					
					origin.mapRequestParameters(request);
					
					if(CFW.DB.Groups.create((Group)origin)) {
						CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "Group created successfully!");
					}
				}
				
			}
		});
	}
	
	class CreateUserForm extends BTForm{
				
		protected CFWField<String> username = CFWField.newString(FormFieldType.TEXT, "Username")
				.addValidator(new LengthValidator(1, 255));
		
		protected CFWField<String> password = CFWField.newString(FormFieldType.PASSWORD, "Password")
				.addValidator(new LengthValidator(-1, 255))
				.addValidator(new PasswordValidator());
		
		protected CFWField<String> repeatedPassword = CFWField.newString(FormFieldType.PASSWORD, "Repeat Password")
				.addValidator(new NotNullOrEmptyValidator());
		
		private CFWField<Boolean> isForeign = CFWField.newBoolean(FormFieldType.BOOLEAN, UserDBFields.IS_FOREIGN.toString())
											 .setValue(false);
		
		public CreateUserForm(String formID, String submitLabel) {
			super(formID, submitLabel);
			this.addField(username);
			this.addField(password);
			this.addField(repeatedPassword);
			this.addField(isForeign);
		}
		
		public String getUsername() { return username.getValue(); }
		public String getPassword() { return password.getValue(); }
		public String getRepeatedPassword() { return repeatedPassword.getValue(); }
		public boolean getIsForeign() { return isForeign.getValue(); }
	}
}