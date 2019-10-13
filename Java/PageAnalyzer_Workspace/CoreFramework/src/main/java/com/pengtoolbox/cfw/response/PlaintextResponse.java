package com.pengtoolbox.cfw.response;

import com.pengtoolbox.cfw._main.CFW;

public class PlaintextResponse extends AbstractResponse {

	public PlaintextResponse() {
		super();
		CFW.Context.Request.getHttpServletResponse().setContentType("text/plain");
	}

	@Override
	public StringBuffer buildResponse() {
		return this.content;
	}
	
	@Override
	public int getEstimatedSizeChars() {
		
		int size = this.content.length();
		
		return size;
	}

}
