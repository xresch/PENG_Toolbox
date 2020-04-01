package com.pengtoolbox.cfw.features.dashboard;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWSQL;
import com.pengtoolbox.cfw.features.api.APIDefinition;
import com.pengtoolbox.cfw.features.api.APIRequestHandler;
import com.pengtoolbox.cfw.features.api.ReturnFormat;
import com.pengtoolbox.cfw.features.dashboard.Dashboard.DashboardFields;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.PlaintextResponse;
import com.pengtoolbox.cfw.utils.CFWArrayUtils;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class APIDashboardExport extends APIDefinition{
	
	private static final String APIFORMAT = "APIFORMAT";
	private static final String[] inputFieldnames = new String[] {DashboardFields.PK_ID.toString()};
	private static final String[] outputFieldnames = new Dashboard().getFieldnames();
	public APIDashboardExport(String apiName, 
						      String actionName) {

		super(Dashboard.class, apiName, actionName, inputFieldnames, outputFieldnames);

		this.setDescription("Export one or all dashboards as a JSON string that can be inported through the import API.");
		
		this.setRequestHandler(new APIRequestHandler() {
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void handleRequest(HttpServletRequest request, HttpServletResponse response, APIDefinition definition) {
				
				JSONResponse json = new JSONResponse();
											
				//----------------------------------
				// Create Response
				String id = request.getParameter(DashboardFields.PK_ID.toString());
				json.getContent().append( CFW.DB.Dashboards.getJsonArrayForExport(id) );
				
				json.setSuccess(true);

			}
		});		
	}
	
}
