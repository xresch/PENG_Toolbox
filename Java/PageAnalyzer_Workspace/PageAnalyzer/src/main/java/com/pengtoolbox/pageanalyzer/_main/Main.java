package com.pengtoolbox.pageanalyzer._main;

import java.io.File;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw._main.CFWSetup;
import com.pengtoolbox.cfw.handlers.AuthenticationHandler;
import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.utils.H2Utils;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;
import com.pengtoolbox.pageanalyzer.servlets.AnalyzeURLServlet;
import com.pengtoolbox.pageanalyzer.servlets.CompareServlet;
import com.pengtoolbox.pageanalyzer.servlets.CustomContentServlet;
import com.pengtoolbox.pageanalyzer.servlets.DataServlet;
import com.pengtoolbox.pageanalyzer.servlets.DeleteResultServlet;
import com.pengtoolbox.pageanalyzer.servlets.DocuServlet;
import com.pengtoolbox.pageanalyzer.servlets.GanttChartServlet;
import com.pengtoolbox.pageanalyzer.servlets.HARDownloadServlet;
import com.pengtoolbox.pageanalyzer.servlets.HARUploadServlet;
import com.pengtoolbox.pageanalyzer.servlets.RestAPIServlet;
import com.pengtoolbox.pageanalyzer.servlets.ResultListServlet;
import com.pengtoolbox.pageanalyzer.servlets.ResultViewServlet;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;
import com.pengtoolbox.pageanalyzer.yslow.YSlowExecutor;

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
	    CFWSetup.initialize("./config/pageanalyzer.properties");
	    
        Server server = new Server(CFWConfig.SERVER_PORT);
        
		//------------------------------------
		// Initialize YSlow Singleton
		// prevents error on first analysis request.
		YSlow.instance();
		YSlowExecutor.instance();
		
    	//---------------------------------------
    	// Datastore 
    	File datastoreFolder = new File(CFW.GLOBAL_DATASTORE_PATH);
    	if(!datastoreFolder.isDirectory()) {
    		datastoreFolder.mkdir();
    	}
    	
    	H2Utils.initialize();

        //###################################################################
        // Create ServletContext
        //###################################################################
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
                
        servletContextHandler.setContextPath(CFWConfig.BASE_URL);
        
        // 100MB
        int maxSize = 1024*1024*100;
        MultipartConfigElement multipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
        
        ServletHolder uploadHolder = new ServletHolder(new HARUploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(multipartConfig);
        servletContextHandler.addServlet(uploadHolder, "/harupload");
        
        ServletHolder apiHolder = new ServletHolder(new RestAPIServlet());
        apiHolder.getRegistration().setMultipartConfig(multipartConfig);
        servletContextHandler.addServlet(apiHolder, "/api");

        servletContextHandler.addServlet(DataServlet.class, "/data");
        
        servletContextHandler.addServlet(HARDownloadServlet.class, "/hardownload");
        servletContextHandler.addServlet(AnalyzeURLServlet.class, "/analyzeurl");
        servletContextHandler.addServlet(ResultViewServlet.class, "/resultview");
        servletContextHandler.addServlet(CompareServlet.class, "/compare");
        servletContextHandler.addServlet(DeleteResultServlet.class, "/delete");
        servletContextHandler.addServlet(ResultListServlet.class, "/resultlist");
        servletContextHandler.addServlet(GanttChartServlet.class, "/ganttchart");
        
        servletContextHandler.addServlet(DocuServlet.class, "/docu");
        servletContextHandler.addServlet(CustomContentServlet.class, "/custom");
        
        CFWSetup.addAuthenticationServlets(servletContextHandler, "/login", "/logout");

        //-------------------------------
        // Create Session Handler
        //-------------------------------
        SessionHandler sessionHandler = CFWSetup.createSessionHandler(server);
        
        //-------------------------------
        // Create Rewrite Handler
        //-------------------------------
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        rewriteHandler.setOriginalPathAttribute("requestedPath");

        RedirectRegexRule mainRedirect = new RedirectRegexRule();
        mainRedirect.setRegex("^/$|"+CFWConfig.BASE_URL+"|"+CFWConfig.BASE_URL+"/");
        mainRedirect.setReplacement(CFWConfig.BASE_URL+"/harupload");
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
        // Create Handler Collection
        //###################################################################
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[] {rewriteHandler, CFWSetup.createResourceHandler(), new DefaultHandler() });
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
