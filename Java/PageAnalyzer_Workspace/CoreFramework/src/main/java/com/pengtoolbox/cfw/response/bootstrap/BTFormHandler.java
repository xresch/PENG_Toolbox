package com.pengtoolbox.cfw.response.bootstrap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BTFormHandler {
	
	public abstract void handleForm( HttpServletRequest request,HttpServletResponse response, BTForm form );

}
