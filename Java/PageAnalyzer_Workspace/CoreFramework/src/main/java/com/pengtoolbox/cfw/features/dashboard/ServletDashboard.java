package com.pengtoolbox.cfw.features.dashboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class ServletDashboard extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	public ServletDashboard() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {

		if(CFW.Context.Request.hasPermission(FeatureDashboard.PERMISSION_DASHBOARDING)) {
			
			String action = request.getParameter("action");
			
			if(action == null) {
				HTMLResponse html = new HTMLResponse("Dashboard");
				StringBuffer content = html.getContent();
				
				html.addCSSFile(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "gridstack.min.css");
				html.addCSSFile(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_dashboard.css");
				
				//html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js"));
				html.addJSFileBottomAssembly(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "gridstack.all.js");
				html.addJSFileBottomAssembly(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_dashboard.js");
				
				
				content.append(CFW.Files.readPackageResource(FeatureDashboard.RESOURCE_PACKAGE, "cfw_dashboard.html"));
				
				html.addJavascriptCode("cfw_dashboard_draw();");
				
		        response.setContentType("text/html");
		        response.setStatus(HttpServletResponse.SC_OK);
			}else {
				handleDataRequest(request, response);
			}
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
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
					case "menuitems": 			jsonResponse.getContent().append(CFW.Registry.Manual.getManualPagesForUserAsJSON().toString());
	  											break;
	  				
					case "page": 				String path = request.getParameter("path");
												//ManualPage page = CFW.Registry.Manual.getPageByPath(path);
//												if(page != null) {
//													jsonResponse.getContent().append(page.toJSONObjectWithContent());
//												}else {
//													CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The page with the path '"+path+"' was not found.");
//												}
												break;
												
												
					default: 					CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
												break;
				}
				break;
						
			default: 			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The action '"+action+"' is not supported.");
								break;
								
		}
	}
		

}