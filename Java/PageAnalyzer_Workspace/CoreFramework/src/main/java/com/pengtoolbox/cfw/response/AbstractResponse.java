package com.pengtoolbox.cfw.response;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;

public abstract class AbstractResponse {

	protected StringBuffer content = new StringBuffer();
	protected HttpServletRequest request;
	
	private static Logger logger = CFWLog.getLogger(AbstractResponse.class.getName());
	
	public AbstractResponse(){
		this.request = CFW.Context.Request.getRequest();
		
		CFW.Context.Request.setResponse(this);
		
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
