package com.pageanalyzer._main;

import javax.servlet.MultipartConfigElement;
import javax.servlet.SessionCookieConfig;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.pageanalyzer.handlers.RequestHandler;
import com.pageanalyzer.servlets.AnalyzeURLServlet;
import com.pageanalyzer.servlets.CustomContentServlet;
import com.pageanalyzer.servlets.DocuServlet;
import com.pageanalyzer.servlets.HARDownloadServlet;
import com.pageanalyzer.servlets.HARUploadServlet;
import com.pageanalyzer.servlets.RestAPIServlet;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
    public static void main( String[] args ) throws Exception
    {

        //###################################################################
        // Initialize
        //###################################################################
    	System.setProperty("java.util.logging.config.file", "./config/logging.properties");
    	
	    PA.initialize();
	    
        Server server = new Server(Integer.parseInt(PA.config("pa_server_port")));

        //###################################################################
        // Create ServletContext
        //###################################################################
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
                
        servletContextHandler.setContextPath("/"+PA.config("pa_application_name"));
        
        // 100MB
        int maxSize = 1024*1024*100;
        MultipartConfigElement multipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
        
        ServletHolder uploadHolder = new ServletHolder(new HARUploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(multipartConfig);
        servletContextHandler.addServlet(uploadHolder, "/harupload");
        servletContextHandler.addServlet(uploadHolder, "/");
        
        ServletHolder apiHolder = new ServletHolder(new RestAPIServlet());
        apiHolder.getRegistration().setMultipartConfig(multipartConfig);
        servletContextHandler.addServlet(apiHolder, "/api");

        servletContextHandler.addServlet(HARDownloadServlet.class, "/hardownload");
        servletContextHandler.addServlet(AnalyzeURLServlet.class, "/analyzeurl");
        servletContextHandler.addServlet(DocuServlet.class, "/docu");
        servletContextHandler.addServlet(CustomContentServlet.class, "/custom");
        
        //-------------------------------
        // Create HandlerChain
        //-------------------------------
        GzipHandler servletGzipHandler = new GzipHandler();
        RequestHandler requestHandler = new RequestHandler();
        SessionHandler sessionHandler = new SessionHandler();
        
        SessionCookieConfig sessionConfig = sessionHandler.getSessionManager().getSessionCookieConfig();
        sessionConfig.setMaxAge(3600); //doesn't work

        servletGzipHandler.setHandler(sessionHandler);
        sessionHandler.setHandler(requestHandler);
        requestHandler.setHandler(servletContextHandler);
        
        //###################################################################
        // Create ResourceHandler
        //###################################################################
        ResourceHandler resource_handler = new ResourceHandler();
        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        resource_handler.setDirectoriesListed(true);
        //resource_handler.setWelcomeFiles(new String[]{ "/"+PA.config("pa_application_name")+"/harupload" });
        resource_handler.setResourceBase("./resources");
 
        // Add the ResourceHandler to the server.
        GzipHandler resourceGzipHandler = new GzipHandler();
        server.setHandler(resourceGzipHandler);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        resourceGzipHandler.setHandler(handlers);
        
        //###################################################################
        // Create HandlerCollection
        //###################################################################
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[] { servletGzipHandler, resourceGzipHandler, new DefaultHandler() });
        server.setHandler(handlerCollection);
        
        // Start things up!
        server.start();
        server.join();
       
    }
    
    public void testPhantomJS(){
    	
    	
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
