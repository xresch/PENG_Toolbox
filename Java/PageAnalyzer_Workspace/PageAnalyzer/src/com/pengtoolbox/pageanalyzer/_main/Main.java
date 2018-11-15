package com.pengtoolbox.pageanalyzer._main;

import java.io.File;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.pengtoolbox.pageanalyzer.handlers.AuthenticationHandler;
import com.pengtoolbox.pageanalyzer.handlers.RequestHandler;
import com.pengtoolbox.pageanalyzer.servlets.AnalyzeURLServlet;
import com.pengtoolbox.pageanalyzer.servlets.CustomContentServlet;
import com.pengtoolbox.pageanalyzer.servlets.DocuServlet;
import com.pengtoolbox.pageanalyzer.servlets.HARDownloadServlet;
import com.pengtoolbox.pageanalyzer.servlets.HARUploadServlet;
import com.pengtoolbox.pageanalyzer.servlets.LoginServlet;
import com.pengtoolbox.pageanalyzer.servlets.LogoutServlet;
import com.pengtoolbox.pageanalyzer.servlets.RestAPIServlet;
import com.pengtoolbox.pageanalyzer.utils.HandlerChainBuilder;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
    public static void main( String[] args ) throws Exception
    {

        //###################################################################
        // Initialize
        //###################################################################
    	
    	//---------------------------------------
    	// General 
	    PA.initialize();
	    
        Server server = new Server(Integer.parseInt(PA.config("pa_server_port")));
        
    	//---------------------------------------
    	// Login
    	
    	File logFolder = new File("./log");
    	if(!logFolder.isDirectory()) {
    		logFolder.mkdir();
    	}
    	
    	System.setProperty("java.util.logging.config.file", "./config/logging.properties");
    	
    	//---------------------------------------
    	// Datastore 
    	File datastoreFolder = new File(PA.GLOBAL_DATASTORE_PATH);
    	if(!datastoreFolder.isDirectory()) {
    		datastoreFolder.mkdir();
    	}
    	

        //###################################################################
        // Create ServletContext
        //###################################################################
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
                
        servletContextHandler.setContextPath(PA.BASE_URL);
        
        // 100MB
        int maxSize = 1024*1024*100;
        MultipartConfigElement multipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
        
        ServletHolder uploadHolder = new ServletHolder(new HARUploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(multipartConfig);
        servletContextHandler.addServlet(uploadHolder, "/harupload");
        
        ServletHolder apiHolder = new ServletHolder(new RestAPIServlet());
        apiHolder.getRegistration().setMultipartConfig(multipartConfig);
        servletContextHandler.addServlet(apiHolder, "/api");

        servletContextHandler.addServlet(HARDownloadServlet.class, "/hardownload");
        servletContextHandler.addServlet(AnalyzeURLServlet.class, "/analyzeurl");
        servletContextHandler.addServlet(DocuServlet.class, "/docu");

        servletContextHandler.addServlet(CustomContentServlet.class, "/custom");
        
        if(PA.configAsBoolean("pa_enable_authentication")) {
            servletContextHandler.addServlet(LoginServlet.class, "/login");
            servletContextHandler.addServlet(LogoutServlet.class, "/logout");
        }
        
        //-------------------------------
        // Create Session Manager
        //-------------------------------
        HashSessionIdManager idmanager = new HashSessionIdManager();
        server.setSessionIdManager(idmanager);
        HashSessionManager manager = new HashSessionManager();
        
        manager.setHttpOnly(false);
        manager.setUsingCookies(true);

        SessionHandler sessionHandler = new SessionHandler(manager);
        
        
        // workaround maxInactiveInterval=-1 issue
        // set inactive interval in RequestHandler
        manager.setMaxInactiveInterval(3600);
        
        
        //-------------------------------
        // Create Rewrite Handler
        //-------------------------------
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        rewriteHandler.setOriginalPathAttribute("requestedPath");

        RedirectRegexRule mainRedirect = new RedirectRegexRule();
        mainRedirect.setRegex("^/$|"+PA.BASE_URL+"|"+PA.BASE_URL+"/");
        mainRedirect.setReplacement(PA.BASE_URL+"/harupload");
        rewriteHandler.addRule(mainRedirect);	
        
        //-------------------------------
        // Create HandlerChain
        //-------------------------------
        GzipHandler servletGzipHandler = new GzipHandler();
        RequestHandler requestHandler = new RequestHandler();
        AuthenticationHandler authenticationHandler = new AuthenticationHandler();
        
        new HandlerChainBuilder(rewriteHandler)
        	.chain(servletGzipHandler)
        	.chain(sessionHandler)
	        .chain(requestHandler)
	        .chain(authenticationHandler)
            .chain(servletContextHandler);
        
        //###################################################################
        // Create ResourceHandler
        //###################################################################
        
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
        
        
        //###################################################################
        // Create Handler Collection
        //###################################################################
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[] {rewriteHandler, resourceContextHandler, new DefaultHandler() });
        server.setHandler(handlerCollection);
        
        //###################################################################
        // Startup
        //###################################################################
        server.start();
        server.join();
       
    }
    

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
