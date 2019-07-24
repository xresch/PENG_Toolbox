package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import com.pengtoolbox.cfw.cli.ArgumentsException;
import com.pengtoolbox.cfw.exceptions.ShutdownException;
import com.pengtoolbox.cfw.handlers.AuthenticationHandler;
import com.pengtoolbox.cfw.handlers.HTTPSRedirectHandler;
import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;

public class CFWDefaultApp {
	
	private Server server;
	private MultipartConfigElement globalMultipartConfig;
	
	private ArrayList<ContextHandler> unsecureContextArray = new ArrayList<ContextHandler>();
	private ArrayList<ContextHandler> secureContextArray = new ArrayList<ContextHandler>();
	
	private String defaultURL = "/";
		
	public static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	public static WebAppContext applicationContext;
	
	public CFWDefaultApp(String[] args) throws Exception {
		
        //###################################################################
        // Initialize
        //###################################################################
    	
		CFW.CLI.readArguments(args);

		if (!CFW.CLI.validateArguments()) {
			System.out.println("Issues loading arguments: \n"+CFW.CLI.getInvalidMessagesAsString());
			CFW.CLI.printUsage();
			throw new ArgumentsException(CFW.CLI.getInvalidMessages());
		}
    	//---------------------------------------
    	// General 
	    CFW.initialize(CFW.CLI.getValue(CFW.CLI.CONFIG_FILE));
    	
	    if (args.length == 1) {
	    	
	    	switch(args[0]) {
	    		case "-stop":  this.stop(); 
	    					   throw new ShutdownException();
	    	}
            
        }
	    
    	//---------------------------------------
    	// Create Server 
        server = CFW.App.createServer();
        applicationContext = new WebAppContext();
        applicationContext.setContextPath("/");
        applicationContext.setServer(server);
        applicationContext.setSessionHandler(CFW.App.createSessionHandler());
        applicationContext.setErrorHandler(CFW.App.createErrorHandler());
    	//---------------------------------------
    	// Database    	
    	CFW.DB.initialize();
    	
    	//---------------------------------------
    	// Default Multipart Config max 100MB
        int maxSize = 1024*1024*100;
        globalMultipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
         
	}
	
	/**************************************************************************************************
	 * Returns a ServletContextHandler that can be accesses without a user login.
	 * Adds several handlers like gzipHandler, SessionHandler and RequestHandler.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public ServletContextHandler createUnsecureContext(String relativePath){

        //----------------------------------
        // Build Handler Chain
        ContextHandler unsecureContextHandler = new ContextHandler(CFWConfig.BASE_URL+""+relativePath);	
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);   
		
        new HandlerChainBuilder(unsecureContextHandler)
	        .chain(new GzipHandler())
	    	.chain(new RequestHandler())
	        .chain(servletContext);
        
        unsecureContextArray.add(unsecureContextHandler);
        return servletContext;
	}
	
	/**************************************************************************************************
	 * Returns a ServletContextHandler that can be accesses with a prior user login.
	 * Adds several handlers like gzipHandler, SessionHandler, AuthenticationHandler and RequestHandler.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public ServletContextHandler createSecureContext(String relativePath){

        //-------------------------------
        // Create HandlerChain
        //-------------------------------
        ContextHandler secureContext = new ContextHandler(CFWConfig.BASE_URL+""+relativePath);
       
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        
        new HandlerChainBuilder(secureContext)
        	.chain(new GzipHandler())
	        .chain(new RequestHandler())
	        .chain(new AuthenticationHandler())
	        .chain(servletContext);
       
        secureContextArray.add(secureContext);
        
        return servletContext;
	}
	
	/**************************************************************************************************
	 * @throws Exception
	 **************************************************************************************************/
	public void start() throws Exception {
		
        //-------------------------------
        // Create Rewrite Handler
        //-------------------------------
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        rewriteHandler.setOriginalPathAttribute("requestedPath");

        RedirectRegexRule mainRedirect = new RedirectRegexRule();
        mainRedirect.setRegex("^/$"+
        					 "|"+CFWConfig.BASE_URL+"/?$"+
        					 "|"+CFWConfig.BASE_URL+"/app/?$");
        mainRedirect.setReplacement(CFWConfig.BASE_URL+defaultURL);
        rewriteHandler.addRule(mainRedirect);	
        
		// TODO Auto-generated method stub
        //###################################################################
        // Create Handler Collection
        //###################################################################
        
        //Connect all relevant Handlers
        ArrayList<Handler> handlerArray = new ArrayList<Handler>();
        handlerArray.add(new ShutdownHandler(CFW.Config.APPLICATION_ID, true, true));
        handlerArray.add(new HTTPSRedirectHandler());
        handlerArray.addAll(unsecureContextArray);
        handlerArray.add(rewriteHandler);
        handlerArray.addAll(secureContextArray);
        handlerArray.add(CFWApp.createResourceHandler());
        handlerArray.add(CFWApp.createCFWHandler());
        
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(handlerArray.toArray(new Handler[] {}));
        server.setHandler(handlerCollection);
        
        //###################################################################
        // Startup
        //###################################################################
        server.start();
        server.join();
	}
	
	/**************************************************************************************************
	 * 
	 **************************************************************************************************/
	public void stop() {
		
		System.out.println("Try to stop running application instance.");
		
		//----------------------------------
		// Resolve Port to use
		String protocol = "http";
		int port = CFW.Config.HTTP_PORT;
		if(!CFW.Config.HTTP_ENABLED && CFW.Config.HTTPS_ENABLED) {
			protocol = "https";
			port = CFW.Config.HTTPS_PORT;
		}
		
		//----------------------------------
		// Try Stop 
        try {
        	URL url = new URL(protocol, "localhost", port, "/shutdown?token="+CFW.Config.APPLICATION_ID);
        	 HttpURLConnection connection = (HttpURLConnection)url.openConnection();
             connection.setRequestMethod("POST");

             if(connection.getResponseCode() == 200) {
            	 System.out.println("Shutdown successful.");
             }else {
            	 System.err.println("Jetty returned response code HTTP "+connection.getResponseCode());
             }
             
        } catch (IOException ex) {
            System.err.println("Stop Jetty failed: " + ex.getMessage());
        }
	}

	public Server getServer() {
		return server;
	}

	public MultipartConfigElement getGlobalMultipartConfig() {
		return globalMultipartConfig;
	}

	public String getDefaultURL() {
		return defaultURL;
	}

	/**************************************************************************************************
	 * Set the Default URL to which an incomplete URL should be redirected to.
	 **************************************************************************************************/
	public void setDefaultURL(String defaultURL) {
		this.defaultURL = defaultURL;
	}

	
	
	
	
	

}
