package com.pengtoolbox.cfw._main;

import java.util.Collection;

import com.pengtoolbox.cfw.response.bootstrap.BTForm;

public class CFWContextSession {
	
	public static SessionData getSessionData(){
		return CFW.Context.Request.getSessionData();
	}
	
	public static void addForm(BTForm form){
		CFW.Context.Request.getSessionData().addForm(form);
	}
	
	public static BTForm getForm(String formID) {
		return CFW.Context.Request.getSessionData().getForm(formID);
	}
	
	public static Collection<BTForm> getForms() {
		return CFW.Context.Request.getSessionData().getForms();
	}

}
