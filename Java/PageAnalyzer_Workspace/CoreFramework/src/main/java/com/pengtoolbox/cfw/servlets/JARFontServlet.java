package com.pengtoolbox.cfw.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.response.TemplatePlain;
import com.pengtoolbox.cfw.utils.FileUtils;

public class JARFontServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
		String fontName = request.getParameter("name");
		String fontType = fontName.substring(fontName.lastIndexOf(".")+1);
		
		byte[] fontContent = FileUtils.readPackageResourceAsBytes(FileAssembly.CFW_JAR_RESOURCES_PATH + "/fonts/"+fontName);
		if(fontContent != null) {
			
			response.getOutputStream().write(fontContent);

			System.out.println(fontContent);
			response.addHeader("Cache-Control", "max-age="+CFWConfig.BROWSER_RESOURCE_MAXAGE);
	        //response.setContentType("application/font-"+fontType);
	        response.setStatus(HttpServletResponse.SC_OK);
	        
	        
	    }else {
	    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    }
    }
}