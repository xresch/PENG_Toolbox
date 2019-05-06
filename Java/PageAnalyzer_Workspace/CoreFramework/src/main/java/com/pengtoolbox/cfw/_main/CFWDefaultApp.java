package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.handlers.AuthenticationHandler;
import com.pengtoolbox.cfw.handlers.HTTPSRedirectHandler;
import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;

public class CFWDefaultApp {
	
	private Server server;
	private MultipartConfigElement globalMultipartConfig;
	
	private ArrayList<ContextHandler> unsecureContextArray = new ArrayList<ContextHandler>();
	private ArrayList<ContextHandler> secureContextArray = new ArrayList<ContextHandler>();
	
	private String defaultURL = "/";
	
	public CFWDefaultApp() throws IOException {
		
        //###################################################################
        // Initialize
        //###################################################################
    	
    	//---------------------------------------
    	// General 
	    CFWSetup.initialize("./config/cfw.properties");
	    
        server = CFWSetup.createServer();
        
    	//---------------------------------------
    	// Database    	
    	CFWDB.initialize();
    	
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
        ServletContextHandler servletContext = new ServletContextHandler();   
		
        new HandlerChainBuilder(unsecureContextHandler)
	        .chain(new GzipHandler())
	        .chain(CFWSetup.createSessionHandler(server))
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
       
        ServletContextHandler servletContext = new ServletContextHandler();
        
        new HandlerChainBuilder(secureContext)
        	.chain(new GzipHandler())
        	.chain(CFWSetup.createSessionHandler(server))
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
        handlerArray.add(new HTTPSRedirectHandler());
        handlerArray.addAll(unsecureContextArray);
        handlerArray.add(rewriteHandler);
        handlerArray.addAll(secureContextArray);
        handlerArray.add(CFWSetup.createResourceHandler());
        handlerArray.add(CFWSetup.createCFWHandler());
        handlerArray.add(new DefaultHandler());
        
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(handlerArray.toArray(new Handler[] {}));
        server.setHandler(handlerCollection);
        
        //###################################################################
        // Startup
        //###################################################################
        server.start();
        server.join();
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
