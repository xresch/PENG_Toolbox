package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer._main.SessionData;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.phantomjs.PhantomJSInterface;
import com.pengtoolbox.pageanalyzer.response.TemplateHTMLDefault;
import com.pengtoolbox.pageanalyzer.utils.CacheUtils;
import com.pengtoolbox.pageanalyzer.utils.HTTPUtils;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

public class LogoutServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = PALogger.getLogger(LogoutServlet.class.getName());

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		PALogger log = new PALogger(logger, request).method("doGet");
		log.info(request.getRequestURL().toString());
		
		SessionData data = (SessionData)request.getSession().getAttribute(PA.SESSION_DATA); 	
		data.setLoggedIn(false);
		data.setUsername("");
		
		response.sendRedirect(response.encodeRedirectURL(PA.BASE_URL+"/login"));
        
    }
	
}