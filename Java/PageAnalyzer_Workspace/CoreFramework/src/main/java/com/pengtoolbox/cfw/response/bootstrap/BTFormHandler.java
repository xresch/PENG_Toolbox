package com.pengtoolbox.cfw.response.bootstrap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.datahandling.CFWObject;

public abstract class BTFormHandler {
	
	public abstract void handleForm( HttpServletRequest request,HttpServletResponse response, BTForm form, CFWObject origin);

}
