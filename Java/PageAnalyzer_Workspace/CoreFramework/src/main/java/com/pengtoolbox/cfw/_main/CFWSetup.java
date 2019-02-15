package com.pengtoolbox.cfw._main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.pengtoolbox.cfw.logging.CFWLogger;
import com.pengtoolbox.cfw.servlets.LoginServlet;
import com.pengtoolbox.cfw.servlets.LogoutServlet;

/***********************************************************************
 * Setup class for the Core Framework
 ***********************************************************************/
public class CFWSetup {
	
	public static void initialize(String configFilePath) throws IOException{
		
		//---------------------------------------
		// Logging
		
		File logFolder = new File("./log");
		if(!logFolder.isDirectory()) {
			logFolder.mkdir();
		}
		
		System.setProperty("java.util.logging.config.file", "./config/logging.properties");

		CFWLogger log = new CFWLogger(CFW.logger).method("initialize").start();
				
		//------------------------------------
		// Classloader
		URL[] urls = {CFW.folder.toURI().toURL()};
		CFW.urlClassLoader = new URLClassLoader(urls);
		
		//------------------------------------
		// Load Configuration
		CFWConfig.loadConfiguration(configFilePath);
		log.end();
	}
	
	/***********************************************************************
	 * Add AuthenticationServlets
	 ***********************************************************************/
	public static void addAuthenticationServlets(ServletContextHandler servletContextHandler, 
												 String loginServletPath,
												 String logoutServletPath) {
		
        if(CFWConfig.AUTHENTICATION_ENABLED) {
            servletContextHandler.addServlet(LoginServlet.class, loginServletPath);
            servletContextHandler.addServlet(LogoutServlet.class,  logoutServletPath);
        }
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
	    HashSessionIdManager idmanager = new HashSessionIdManager();
	    server.setSessionIdManager(idmanager);
	    HashSessionManager manager = new HashSessionManager();
	    
	    manager.setHttpOnly(false);
	    manager.setUsingCookies(true);
	
	    SessionHandler sessionHandler = new SessionHandler(manager);
	    
	    // workaround maxInactiveInterval=-1 issue
	    // set inactive interval in RequestHandler
	    manager.setMaxInactiveInterval(3600);
	    
	    return sessionHandler;
	}

}
