package com.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pageanalyzer._main.PA;
import com.pageanalyzer.logging.PALogger;
import com.pageanalyzer.response.TemplateHTMLDefault;
import com.pageanalyzer.response.TemplatePlain;
import com.pageanalyzer.utils.CacheUtils;

public class HARDownloadServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(HARDownloadServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		PALogger log = new PALogger(logger, request).method("doGet");
		log.info(request.getRequestURL()+"?"+request.getQueryString());
			
		TemplatePlain plain = new TemplatePlain(request);
		StringBuffer content = plain.getContent();
		
		String harindex = request.getParameter("harindex");
		
		content.append(CacheUtils.getCachedHARFile(Integer.parseInt(harindex)) );
		
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
	

}