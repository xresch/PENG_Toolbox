package com.pengtoolbox.cfw._main;

import javax.servlet.http.HttpServletRequest;

public class CFWContextRequest {
	
	private static InheritableThreadLocal<HttpServletRequest> request = null;
	private static InheritableThreadLocal<SessionData> sessionData = null;
	
	public static HttpServletRequest getRequest() {
	
	if(CFWContextRequest.request == null) {
			return null;
		}
		return request.get();
	}

	public static void setRequest(HttpServletRequest request) {
		
		if(CFWContextRequest.request == null) {
			CFWContextRequest.request = new InheritableThreadLocal<HttpServletRequest>();
		}
		CFWContextRequest.request.set(request);
		
	}
	
	public static SessionData getSessionData() {
		if(CFWContextRequest.sessionData == null) {
			return null;
		}
		return sessionData.get();
	}

	public static void setSessionData(SessionData sessionData) {
		
		if(CFWContextRequest.sessionData == null) {
			CFWContextRequest.sessionData = new InheritableThreadLocal<SessionData>();
		}
		CFWContextRequest.sessionData.set(sessionData);
		
	}

}
