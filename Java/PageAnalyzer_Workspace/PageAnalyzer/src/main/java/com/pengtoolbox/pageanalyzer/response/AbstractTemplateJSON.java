package com.pengtoolbox.pageanalyzer.response;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractTemplateJSON extends AbstractTemplate {

	public AbstractTemplateJSON(HttpServletRequest request) {
		super(request);
	}

}
