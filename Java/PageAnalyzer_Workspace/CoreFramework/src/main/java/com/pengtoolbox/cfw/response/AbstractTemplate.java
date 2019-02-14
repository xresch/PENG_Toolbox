package com.pengtoolbox.cfw.response;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLogger;

public abstract class AbstractTemplate {

	protected StringBuffer content = new StringBuffer();
	protected HttpServletRequest request;
	
	public AbstractTemplate(HttpServletRequest request){
		this.request = request;
		request.setAttribute(CFW.REQUEST_ATTR_TEMPLATE, this);
	}
	

	//##############################################################################
	// Class Methods
	//##############################################################################
	public abstract StringBuffer buildResponse();
	public abstract int getEstimatedSizeChars();
	
	//##############################################################################
	// Getters
	//##############################################################################
	public StringBuffer getContent() { return content;}

	//##############################################################################
	// Setters
	//##############################################################################
	public void setContent(StringBuffer content) {this.content = content;}
	
}
