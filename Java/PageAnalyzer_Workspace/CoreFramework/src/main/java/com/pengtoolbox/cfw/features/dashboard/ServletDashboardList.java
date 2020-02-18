package com.pengtoolbox.cfw.features.dashboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.datahandling.CFWForm;
import com.pengtoolbox.cfw.datahandling.CFWFormHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/************************************************************************	**************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class ServletDashboardList extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	public ServletDashboardList() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {

		if(CFW.Context.Request.hasPermission(FeatureDashboard.PERMISSION_DASHBOARDING)) {
			
			createForms();
			
			String action = request.getParameter("action");
			
			if(action == null) {
				HTMLResponse html = new HTMLResponse("Dashboard List");
				StringBuffer content = html.getContent();
				
				//html.addCSSFile(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_dashboard.css");
				
				//html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_usermgmt.js"));
				html.addJSFileBottomAssembly(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_dashboard_list.js");
				
				//content.append(CFW.Files.readPackageResource(FeatureDashboard.RESOURCE_PACKAGE, "cfw_dashboard.html"));
				
				html.addJavascriptCode("cfw_dashboardlist_initialDraw({tab: 'mydashboards'});");
				
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
		String ID = request.getParameter("id");
		String IDs = request.getParameter("ids");
		//int	userID = CFW.Context.Request.getUser().id();
		
		JSONResponse jsonResponse = new JSONResponse();

		switch(action.toLowerCase()) {
		
			case "fetch": 			
				switch(item.toLowerCase()) {
					case "mydashboards": 		jsonResponse.getContent().append(CFW.DB.Dashboards.getUserDashboardListAsJSON());
	  											break;
	  																					
					default: 					CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
												break;
				}
				break;
				
			case "delete": 			
				switch(item.toLowerCase()) {

					case "mydashboards": 	jsonResponse.setSuccess(CFW.DB.Dashboards.deleteMultipleByID(IDs));
											break;  
										
					default: 			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
										break;
				}
				break;	
			case "getform": 			
				switch(item.toLowerCase()) {
					case "editdashboard": 	createEditDashboardForm(jsonResponse, ID);
					break;
					
					default: 				CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
											break;
				}
				break;
						
			default: 			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The action '"+action+"' is not supported.");
								break;
								
		}
	}
		
	private void createForms() {
				
		//--------------------------------------
		// Create Dashboard Form
		
		CFWForm createDashboardForm = new Dashboard().toForm("cfwCreateDashboardForm", "{!cfw_dashboard_create!}");
		
		createDashboardForm.setFormHandler(new CFWFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, CFWForm form, CFWObject origin) {
								
				if(origin != null) {
					
					origin.mapRequestParameters(request);
					Dashboard dashboard = (Dashboard)origin;
					dashboard.foreignKeyUser(CFW.Context.Request.getUser().id());
					if( CFW.DB.Dashboards.create(dashboard) ) {
						CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "Dashboard created successfully!");
					}
				}
				
			}
		});
	}
	
	
	private void createEditDashboardForm(JSONResponse json, String ID) {
		
		Dashboard dashboard = CFW.DB.Dashboards.selectByID(Integer.parseInt(ID));
		
		if(dashboard != null) {
			
			CFWForm editDashboardForm = dashboard.toForm("cfwEditDashboardForm"+ID, "Update Dashboard");
			
			editDashboardForm.setFormHandler(new CFWFormHandler() {
				
				@Override
				public void handleForm(HttpServletRequest request, HttpServletResponse response, CFWForm form, CFWObject origin) {
					
					if(origin.mapRequestParameters(request)) {
						
						if(CFW.DB.Dashboards.update((Dashboard)origin)) {
							CFW.Context.Request.addAlertMessage(MessageType.SUCCESS, "Updated!");
						}
							
					}
					
				}
			});
			
			editDashboardForm.appendToPayload(json);
			json.setSuccess(true);	
		}
	}
}