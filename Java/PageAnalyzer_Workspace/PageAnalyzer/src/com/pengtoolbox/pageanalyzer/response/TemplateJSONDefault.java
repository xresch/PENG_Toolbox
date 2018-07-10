package com.pengtoolbox.pageanalyzer.response;

import javax.servlet.http.HttpServletRequest;

public class TemplateJSONDefault extends AbstractTemplateJSON {

	public TemplateJSONDefault(HttpServletRequest request) {
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
