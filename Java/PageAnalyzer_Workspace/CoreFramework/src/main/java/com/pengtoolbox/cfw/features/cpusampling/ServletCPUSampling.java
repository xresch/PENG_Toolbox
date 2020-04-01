package com.pengtoolbox.cfw.features.cpusampling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.features.core.FeatureCore;
import com.pengtoolbox.cfw.features.usermgmt.Permission;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class ServletCPUSampling extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	public ServletCPUSampling() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {

		if(CFW.Context.Request.hasPermission(FeatureCore.PERMISSION_APP_ANALYTICS)) {
			
			String action = request.getParameter("action");
			
			if(action == null) {
			HTMLResponse html = new HTMLResponse("CPU Sampling");
			StringBuffer content = html.getContent();

			//html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js"));
			html.addJSFileBottom(HandlingType.JAR_RESOURCE, FeatureCPUSampling.RESOURCE_PACKAGE, "cfw_cpusampling.js");
			
			//content.append(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".html", ""));
			
			html.addJavascriptCode("cfw_cpusampling_draw({tab: 'latest'});");
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			}else {
				handleDataRequest(request, response);
			}
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, CFW.L("cfw_core_error_accessdenied", "Access Denied!"));
		}
        
    }
	
	private void handleDataRequest(HttpServletRequest request, HttpServletResponse response) {
		
		String action = request.getParameter("action");
		String item = request.getParameter("item");
		//String ID = request.getParameter("id");
		//String IDs = request.getParameter("ids");
		//int	userID = CFW.Context.Request.getUser().id();
		
		JSONResponse jsonResponse = new JSONResponse();
		
		switch(action.toLowerCase()) {
		
			case "fetch": 			
				switch(item.toLowerCase()) {
					case "cpusampling": 		getCPUSampling(jsonResponse);
	  											break;
	  										
					default: 					CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
												break;
				}
				break;
						
			default: 			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The action '"+action+"' is not supported.");
								break;
								
		}
	}
		
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	private static void getCPUSampling(JSONResponse jsonResponse) {
		
		if( CFW.Context.Request.hasPermission(FeatureCore.PERMISSION_APP_ANALYTICS) ) {

			String signatures = CFWDBCPUSampleSignature.getSignatureListAsJSON();
			String timeseries =  CFWDBCPUSample.getLatestAsJSON();
			
			jsonResponse.getContent()
				.append("{\"signatures\": ").append(signatures)
				.append(",\"timeseries\": ").append(timeseries)
				.append("}");
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access to statistics denied.");
		}
		
	}
	
}