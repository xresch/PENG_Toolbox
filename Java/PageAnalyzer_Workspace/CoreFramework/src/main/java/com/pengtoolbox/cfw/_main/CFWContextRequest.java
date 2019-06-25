package com.pengtoolbox.cfw._main;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.AbstractResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class CFWContextRequest {
	
	private static InheritableThreadLocal<HttpServletRequest> request = new InheritableThreadLocal<HttpServletRequest>();
	private static InheritableThreadLocal<AbstractResponse> response = new InheritableThreadLocal<AbstractResponse>();
	private static InheritableThreadLocal<SessionData> sessionData = new InheritableThreadLocal<SessionData>();
	
	private static InheritableThreadLocal<ArrayList<AlertMessage>> messageArray = new InheritableThreadLocal<ArrayList<AlertMessage>>();
	
	private static Logger logger = CFWLog.getLogger(CFWContextRequest.class.getName());
	
	public static void clearRequestContext() {
		request.set(null);
		response.set(null);
		sessionData.set(null);
		messageArray.set(null);
	}
	
	public static HttpServletRequest getRequest() {
		return request.get();
	}

	public static void setRequest(HttpServletRequest request) {
		CFWContextRequest.request.set(request);
	}
	
	public static AbstractResponse getResponse() {
		return response.get();
	}

	public static void setResponse(AbstractResponse response) {
		CFWContextRequest.response.set(response);
		
	}
	
	public static SessionData getSessionData() {
		return sessionData.get();
	}

	public static void setSessionData(SessionData sessionData) {
		CFWContextRequest.sessionData.set(sessionData);
	}

	/****************************************************************
	 * Adds a message to the message div of the template.
	 *   
	 * @param alertType alert type from OMKeys
	 *   
	 ****************************************************************/
	public static void addAlert(MessageType type, String message){
		
		if(messageArray.get() == null) {
			messageArray.set(new ArrayList<AlertMessage>());
		}
		
		messageArray.get().add(new AlertMessage(type, message));
				
	}
	
	public static ArrayList<AlertMessage> getMessages() {
		return messageArray.get();
	}
	
	

}
