package com.pengtoolbox.cfw._main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.AbstractResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWContextRequest {
	
	private static InheritableThreadLocal<HttpServletRequest> httpRequest = new InheritableThreadLocal<HttpServletRequest>();
	private static InheritableThreadLocal<HttpServletResponse> httpResponse = new InheritableThreadLocal<HttpServletResponse>();
	
	private static InheritableThreadLocal<AbstractResponse> responseContent = new InheritableThreadLocal<AbstractResponse>();
	private static InheritableThreadLocal<SessionData> sessionData = new InheritableThreadLocal<SessionData>();
	
	private static InheritableThreadLocal<HashMap<String,AlertMessage>> messageArray = new InheritableThreadLocal<HashMap<String,AlertMessage>>();
		
	public static void clearRequestContext() {
		httpRequest.set(null);
		responseContent.set(null);
		sessionData.set(null);
		messageArray.set(null);
	}
	
	public static HttpServletRequest getRequest() {
		return httpRequest.get();
	}

	public static void setRequest(HttpServletRequest request) {
		CFWContextRequest.httpRequest.set(request);
	}
	
	public static AbstractResponse getResponse() {
		return responseContent.get();
	}
	
	public static void setResponse(AbstractResponse response) {
		CFWContextRequest.responseContent.set(response);
	}
	
	public static void setHttpServletResponse(HttpServletResponse response) {
		CFWContextRequest.httpResponse.set(response);
	}
	
	public static HttpServletResponse getHttpServletResponse() {
		return httpResponse.get();
	}


	
	public static SessionData getSessionData() {
		return sessionData.get();
	}
	
	public static User getUser() {
		if(sessionData.get() != null) {
			return sessionData.get().getUser();
		}
		return null;
	}
	
	public static HashMap<String, Group> getUserGroups() {
		if(sessionData.get() != null) {
			return sessionData.get().getUserGroups();
		}
		return null;
	}
	
	public static HashMap<String, Permission> getUserPermissions() {
		if(sessionData.get() != null) {
			return sessionData.get().getUserPermissions();
		}
		return null;
	}
	
	public static boolean hasPermission(String permissionName) {
		
		if(!CFW.Properties.AUTHENTICATION_ENABLED) {
			return true;
		}
		
		if(getUserPermissions() != null && getUserPermissions().containsKey(permissionName)) {
			return true;
		}
		
		CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!");
		return false;
	}

	public static void setSessionData(SessionData sessionData) {
		CFWContextRequest.sessionData.set(sessionData);
	}

	/****************************************************************
	 * Adds a message to the message div of the template.
	 * Ignored if the message was already exists.
	 *   
	 * @param alertType alert type from OMKeys
	 *   
	 ****************************************************************/
	public static void addAlertMessage(MessageType type, String message){
		
		if(messageArray.get() == null) {
			messageArray.set(new HashMap<String,AlertMessage>());
		}
		
		messageArray.get().put(message, new AlertMessage(type, message));
				
	}
	
	public static Collection<AlertMessage> getAlertMessages() {
		if(messageArray.get() == null) {
			return new ArrayList<AlertMessage>();
		}
		return messageArray.get().values();
	}
	
	/****************************************************************
	 * Rerturns a json string for the alerts.
	 * returns "[]" if the array is empty
	 *   
	 * @param alertType alert type from OMKeys
	 *   
	 ****************************************************************/
	public static String getAlertsAsJSONArray() {
		if(messageArray.get() == null) {
			return "[]";
		}

		return CFW.JSON.toJSON(messageArray.get().values());
	}
	
	

}
