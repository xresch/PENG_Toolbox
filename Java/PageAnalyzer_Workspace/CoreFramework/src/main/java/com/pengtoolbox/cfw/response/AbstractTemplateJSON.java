package com.pengtoolbox.cfw.response;

import com.pengtoolbox.cfw._main.CFW;

public abstract class AbstractTemplateJSON extends AbstractResponse {

	public AbstractTemplateJSON() {
		super();
		CFW.Context.Request.getHttpServletResponse().setContentType("application/json");
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
