package com.pengtoolbox.cfw.servlets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.response.TemplatePlain;

public class AssemblyServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {

		String assemblyName = request.getParameter("name");
		TemplatePlain plain = new TemplatePlain(request);
		
		if(FileAssembly.hasAssembly(assemblyName)) {
			FileAssembly assembly = FileAssembly.getAssemblyFromCache(assemblyName);
			
			plain.getContent().append(assembly.getAssemblyContent());
			
			response.addHeader("Cache-Control", "max-age="+CFWConfig.BROWSER_RESOURCE_MAXAGE);
	        response.setContentType(assembly.getContentType());
	        response.setStatus(HttpServletResponse.SC_OK);
	        
	        
	    }else {
	    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    }
		
		CFW.writeLocalized(request, response);
    }
}