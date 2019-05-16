package com.pengtoolbox.cfw.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.utils.CFWFiles;

public class JARResourceServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
		String pkg = request.getParameter("pkg");		
		String file = request.getParameter("file");
		
		
		byte[] fontContent = CFW.Files.readPackageResourceAsBytes(pkg, file);
		if(fontContent != null) {
			
			response.addHeader("Cache-Control", "max-age="+CFWConfig.BROWSER_RESOURCE_MAXAGE);
			response.setStatus(HttpServletResponse.SC_OK);
			
			response.getOutputStream().write(fontContent);

	        //response.setContentType("application/font-"+fontType);
	        
	        
	        
	    }else {
	    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    }
		//done by RequestHandler
		//CFW.writeLocalized(request, response);
    }
}