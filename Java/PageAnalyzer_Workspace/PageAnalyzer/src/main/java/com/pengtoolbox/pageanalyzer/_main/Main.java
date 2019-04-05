package com.pengtoolbox.pageanalyzer._main;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw._main.CFWSetup;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.handlers.AuthenticationHandler;
import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;
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
	    CFWSetup.initialize("./config/cfw.properties");
	    
        Server server = new Server(CFWConfig.SERVER_PORT);
        
		//------------------------------------
		// Initialize YSlow Singleton
		// prevents error on first analysis request.
		YSlow.instance();
		YSlowExecutor.instance();
		
    	//---------------------------------------
    	// Database    	
    	CFWDB.initialize();
    	PageAnalyzerDB.initialize();
    	//---------------------------------------
    	// Multipart Config max 100MB
        int maxSize = 1024*1024*100;
        MultipartConfigElement multipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
        
        //###################################################################
        // Create unsecuredServletContext
        //################################################################### 
        
    	ServletContextHandler apiServletContext = new ServletContextHandler();
    	ServletHolder apiHolder = new ServletHolder(new RestAPIServlet());
        apiHolder.getRegistration().setMultipartConfig(multipartConfig);
        apiServletContext.addServlet(apiHolder, "/analyzehar");
        
        //----------------------------------
        // Build Handler Chain
        SessionHandler apiSessionHandler = CFWSetup.createSessionHandler(server);
        ContextHandler apiContext = new ContextHandler(CFWConfig.BASE_URL+"/api");
        GzipHandler apiGzipHandler = new GzipHandler();
        RequestHandler apiRequestHandler = new RequestHandler();
        
        new HandlerChainBuilder(apiContext)
	        .chain(apiGzipHandler)
	        .chain(apiSessionHandler)
	    	.chain(apiRequestHandler)
	        .chain(apiServletContext);
        
        //###################################################################
        // Create authenticatedServletContext
        //###################################################################    	
    	ServletContextHandler securedServletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
    	
        ServletHolder uploadHolder = new ServletHolder(new HARUploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(multipartConfig);
        securedServletContext.addServlet(uploadHolder, "/harupload");
        
        securedServletContext.addServlet(DataServlet.class, "/data");
        
        securedServletContext.addServlet(HARDownloadServlet.class, "/hardownload");
        securedServletContext.addServlet(AnalyzeURLServlet.class, "/analyzeurl");
        securedServletContext.addServlet(ResultViewServlet.class, "/resultview");
        securedServletContext.addServlet(CompareServlet.class, "/compare");
        securedServletContext.addServlet(DeleteResultServlet.class, "/delete");
        securedServletContext.addServlet(ResultListServlet.class, "/resultlist");
        securedServletContext.addServlet(GanttChartServlet.class, "/ganttchart");
        
        securedServletContext.addServlet(DocuServlet.class, "/docu");
        securedServletContext.addServlet(CustomContentServlet.class, "/custom");
        
        CFWSetup.addCFWServlets(securedServletContext);
        
        //###################################################################
        // Create pageAnalyzer Handler Collection
        //###################################################################         
        
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
        mainRedirect.setRegex("^/$"+
        					 "|"+CFWConfig.BASE_URL+"/?$"+
        					 "|"+CFWConfig.BASE_URL+"/app/?$");
        mainRedirect.setReplacement(CFWConfig.BASE_URL+"/app/harupload");
        rewriteHandler.addRule(mainRedirect);	
        
        //-------------------------------
        // Create HandlerChain
        //-------------------------------
        System.out.println("CFWConfig.BASE_URL: "+CFWConfig.BASE_URL);
        ContextHandler pageanalyzerContext = new ContextHandler(CFWConfig.BASE_URL+"/app");
        AuthenticationHandler authenticationHandler = new AuthenticationHandler();
        GzipHandler servletGzipHandler = new GzipHandler();
        RequestHandler requestHandler = new RequestHandler();
        
        new HandlerChainBuilder(pageanalyzerContext)
        	.chain(servletGzipHandler)
        	.chain(sessionHandler)
	        .chain(requestHandler)
	        .chain(authenticationHandler)
	        .chain(securedServletContext);
        
        //###################################################################
        // Create Handler Collection
        //###################################################################
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[] {apiContext, rewriteHandler, pageanalyzerContext, CFWSetup.createResourceHandler(), CFWSetup.createCFWHandler(), new DefaultHandler() });
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
