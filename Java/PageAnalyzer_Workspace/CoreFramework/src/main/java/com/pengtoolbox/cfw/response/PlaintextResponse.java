package com.pengtoolbox.cfw.response;

import javax.servlet.http.HttpServletRequest;

public class PlaintextResponse extends AbstractResponse {

	public PlaintextResponse() {
		super();
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
