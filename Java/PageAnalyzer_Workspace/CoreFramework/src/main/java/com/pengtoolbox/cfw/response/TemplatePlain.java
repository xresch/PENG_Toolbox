package com.pengtoolbox.cfw.response;

import javax.servlet.http.HttpServletRequest;

public class TemplatePlain extends AbstractTemplate {

	public TemplatePlain(HttpServletRequest request) {
		super(request);
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
