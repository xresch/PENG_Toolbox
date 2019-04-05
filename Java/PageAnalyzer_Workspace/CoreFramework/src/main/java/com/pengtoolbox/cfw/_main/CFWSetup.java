package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.servlets.AssemblyServlet;
import com.pengtoolbox.cfw.servlets.JARResourceServlet;
import com.pengtoolbox.cfw.servlets.LoginServlet;
import com.pengtoolbox.cfw.servlets.LogoutServlet;
import com.pengtoolbox.cfw.utils.FileUtils;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;

/***********************************************************************
 * Setup class for the Core Framework
 ***********************************************************************/
public class CFWSetup {
	public static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	public static void initialize(String configFilePath) throws IOException{
		
		//---------------------------------------
		// Logging
		
		CFWLog log = new CFWLog(logger).method("initialize").start();
				
		//------------------------------------
		// Classloader
		URL[] urls = {CFW.LANGUAGE_FOLDER.toURI().toURL()};
		CFW.urlClassLoader = new URLClassLoader(urls);
		
		//------------------------------------
		// Classloader
		FileUtils.addAllowedPackage("com.pengtoolbox.cfw.resources");
		
		//------------------------------------
		// Load Configuration
		CFWConfig.loadConfiguration(configFilePath);
		//log.end();
				
	}
	
	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  LoginServlet on /login
	 *  LogoutServlet on /logout
	 *  AssemblyServlet on /assembly
	 *  JARFontServlet on /jarfont
	 ***********************************************************************/
	public static void addCFWServlets(ServletContextHandler servletContextHandler) {
		
		//-----------------------------------------
		// Authentication Servlets
	    if(CFWConfig.AUTHENTICATION_ENABLED) {
	        servletContextHandler.addServlet(LoginServlet.class, "/login");
	        servletContextHandler.addServlet(LogoutServlet.class,  "/logout");
	    }
                
	}
	
	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  AssemblyServlet on /assembly
	 *  JARFontServlet on /jarfont
	 ***********************************************************************/
	public static HandlerWrapper createCFWHandler() {
		
		ContextHandler contextHandler = new ContextHandler("/cfw");
		
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		
		//-----------------------------------------
		// Resource Servlets

		servletContextHandler.addServlet(AssemblyServlet.class, "/assembly"); 
		servletContextHandler.addServlet(JARResourceServlet.class, "/jarresource");
		
        GzipHandler servletGzipHandler = new GzipHandler();
        RequestHandler requestHandler = new RequestHandler();

         new HandlerChainBuilder(contextHandler)
         	 .chain(servletGzipHandler)
	         .chain(requestHandler)
	         .chain(servletContextHandler);
		
		return contextHandler;
	}
	
	/***********************************************************************
	 * Setup and returns a ResourceHandler
	 ***********************************************************************/
	public static ContextHandler createResourceHandler() {
    
	    ResourceHandler resourceHandler = new ResourceHandler();
	    // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
	    // In this example it is the current directory but it can be configured to anything that the jvm has access to.
	    resourceHandler.setDirectoriesListed(false);
	    //resource_handler.setWelcomeFiles(new String[]{ "/"+PA.config("pa_application_name")+"/harupload" });
	    resourceHandler.setResourceBase("./resources");
	
	    // Add the ResourceHandler to the server.
	    ContextHandler resourceContextHandler = new ContextHandler();
	    resourceContextHandler.setContextPath("/resources");
	    
	    GzipHandler resourceGzipHandler = new GzipHandler();
	    
	    resourceContextHandler.setHandler(resourceGzipHandler);
	    resourceGzipHandler.setHandler(resourceHandler);
	    
	    return resourceContextHandler;
	}
	
	/***********************************************************************
	 * Setup and returns a SessionHandler
	 ***********************************************************************/
	public static SessionHandler createSessionHandler(Server server) {
		DefaultSessionIdManager	 idmanager = new DefaultSessionIdManager(server);
	    server.setSessionIdManager(idmanager);
	
	    SessionHandler sessionHandler = new SessionHandler();
	    // workaround maxInactiveInterval=-1 issue
	    // set inactive interval in RequestHandler
	    sessionHandler.setMaxInactiveInterval(3600);
	    sessionHandler.setHttpOnly(false);
	    sessionHandler.setUsingCookies(true);
	    
        // Explicitly set Session Cache and null Datastore.
        // This is normally done by default,
        // but is done explicitly here for demonstration.
        // If more than one context is to be deployed, it is
        // simpler to use SessionCacheFactory and/or
        // SessionDataStoreFactory instances set as beans on 
        // the server.
        SessionCache cache = new DefaultSessionCache(sessionHandler);
        cache.setSessionDataStore(new NullSessionDataStore());
        sessionHandler.setSessionCache(cache);
	    
	    return sessionHandler;
	}

}
