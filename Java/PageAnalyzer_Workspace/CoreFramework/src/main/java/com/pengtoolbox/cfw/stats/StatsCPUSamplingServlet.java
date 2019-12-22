package com.pengtoolbox.cfw.stats;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWForm;
import com.pengtoolbox.cfw.datahandling.CFWFormHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.usermanagement.Role;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.db.usermanagement.User.UserFields;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.validation.LengthValidator;
import com.pengtoolbox.cfw.validation.NotNullOrEmptyValidator;
import com.pengtoolbox.cfw.validation.PasswordValidator;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class StatsCPUSamplingServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	public StatsCPUSamplingServlet() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {

		HTMLResponse html = new HTMLResponse("CPU Sampling");
		
		StringBuffer content = html.getContent();
		
		if(CFW.Context.Request.hasPermission(Permission.CFW_USER_MANAGEMENT)) {
			
			//html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js"));
			html.addJSFileBottomAssembly(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_cpusampling.js");
			
			//content.append(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".html", ""));
			
			html.addJavascriptCode("cfw_cpusampling_draw({tab: 'latest'});");
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
        
    }
	
	
}