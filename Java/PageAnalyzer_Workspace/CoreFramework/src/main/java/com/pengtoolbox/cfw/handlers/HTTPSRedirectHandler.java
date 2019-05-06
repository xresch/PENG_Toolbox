package com.pengtoolbox.cfw.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;

import com.pengtoolbox.cfw._main.CFWConfig;


public class HTTPSRedirectHandler extends SecuredRedirectHandler {

	@Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
		if(CFWConfig.HTTP_ENABLED && CFWConfig.HTTP_REDIRECT_TO_HTTPS) {
			super.handle(target, baseRequest, request, response);
		}
    }
	
}
