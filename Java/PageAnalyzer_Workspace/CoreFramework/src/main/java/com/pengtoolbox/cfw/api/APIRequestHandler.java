package com.pengtoolbox.cfw.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.datahandling.CFWObject;

public abstract class APIRequestHandler {
	

	public abstract void handleRequest(HttpServletRequest request, HttpServletResponse response, APIDefinition definition);

}
