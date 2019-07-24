package com.pengtoolbox.cfw.servlets.admin;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/*************************************************************************
 * 
 * @author Reto Scheiwiller, 2018
 * 
 * Distributed under the MIT license
 *************************************************************************/
public class APIUserMgmtSevlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogManager().getLogger(APIUserMgmtSevlet.class.getName());
       
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//-------------------------------------------
		// Initialize
		//-------------------------------------------
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
		
		String action = request.getParameter("action");
		String item = request.getParameter("item");
		String ID = request.getParameter("id");
		String IDs = request.getParameter("ids");
		
		String userID, groupID, permissionID;
		//-------------------------------------------
		// Fetch Data
		//-------------------------------------------
		JSONResponse jsonResponse = new JSONResponse();
		StringBuffer content = jsonResponse.getContent();

		if(CFW.Context.Request.hasPermission(CFWDBPermission.CFW_USER_MANAGEMENT)) {
			
			if (action == null) {
				CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Parameter 'data' was not specified.");
				//content.append("{\"error\": \"Type was not specified.\"}");
			}else {
	
				switch(action.toLowerCase()) {
					
					case "fetch": 			
						switch(item.toLowerCase()) {
							case "users": 			content.append(CFW.DB.Users.getUserListAsJSON());
										  			break;
										  		
							case "user": 			content.append(CFW.DB.Users.getUserAsJSON(ID));
					  								break;			
					  							
							case "usergroupmap": 	content.append(CFW.DB.UserGroupMap.getGroupMapForUserAsJSON(ID));
					  								break;		
					  							
							case "groups": 			content.append(CFW.DB.Groups.getGroupListAsJSON());
							  			   			break;
							
							case "group": 			content.append(CFW.DB.Groups.getGroupAsJSON(ID));
													break;	
													
							case "grouppermissionmap": 	content.append(CFW.DB.GroupPermissionMap.getPermissionMapForGroupAsJSON(ID));
														break;	
								
							case "permissions":		content.append(CFW.DB.Permissions.getPermissionListAsJSON());
		  			   								break;  
		  			   							
							default: 				CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
													break;
						}
						break;
					
					case "delete": 			
						switch(item.toLowerCase()) {
							case "users": 		jsonResponse.setSuccess(CFW.DB.Users.deleteMultipleByID(IDs));
										  		break;
										  
							case "groups": 		jsonResponse.setSuccess(CFW.DB.Groups.deleteMultipleByID(IDs));
												break;  
												
							case "permissions": jsonResponse.setSuccess(CFW.DB.Permissions.deleteMultipleByID(IDs));
		  			   							break;  
		  			   							
							default: 			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
												break;
						}
						break;
					
					case "update": 			
						switch(item.toLowerCase()) {
							case "usergroupmap": 		userID = request.getParameter("itemid");
														groupID = request.getParameter("listitemid");
														jsonResponse.setSuccess(CFW.DB.UserGroupMap.toogleUserInGroup(userID, groupID));
														break;
														
							case "grouppermissionmap": 	groupID = request.getParameter("itemid");
														permissionID = request.getParameter("listitemid");
														jsonResponse.setSuccess(CFW.DB.GroupPermissionMap.tooglePermissionInGroup(permissionID, groupID));
														break;
			
							default: 			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of item '"+item+"' is not supported.");
												break;
						}
						break;
					default: 				CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The value of action '"+action+"' is not supported.");
											break;
											
				}
							
			}
		
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
	}
}
