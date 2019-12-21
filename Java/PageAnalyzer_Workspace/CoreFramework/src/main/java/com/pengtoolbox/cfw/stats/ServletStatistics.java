package com.pengtoolbox.cfw.stats;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;


/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class ServletStatistics extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogManager().getLogger(ServletStatistics.class.getName());
       
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
		
		String type = request.getParameter("type");
		String starttime = request.getParameter("starttime");
		
		//-------------------------------------------
		// Fetch Data
		//-------------------------------------------
		JSONResponse jsonResponse = new JSONResponse();
		StringBuffer content = jsonResponse.getContent();

		if (type == null) {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Type was not specified");
			//content.append("{\"error\": \"Type was not specified.\"}");
		}else {

			switch(type.toLowerCase()) {
				case "cpusampling": 	getCPUSampling(jsonResponse);
										break;
				
				default: 				CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The type '"+type+"' is not supported.");
										break;
										
			}
						
		}
		
		
	
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	private static void getCPUSampling(JSONResponse jsonResponse) {
		
		JsonObject payload = new JsonObject();
		
    	
		if( CFW.Context.Request.hasPermission(Permission.CFW_VIEW_STATISTICS) ) {

			String signatures = CFWDBStatsMethodSignature.getSignatureListAsJSON();
			String timeseries =  CFWDBStatsMethod.getLatestAsJSON();
			
			jsonResponse.getContent()
				.append("{\"signatures\": ").append(signatures)
				.append(",\"timeseries\": ").append(timeseries)
				.append("}");
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access to statistics denied.");
		}
		
		
	}
}
