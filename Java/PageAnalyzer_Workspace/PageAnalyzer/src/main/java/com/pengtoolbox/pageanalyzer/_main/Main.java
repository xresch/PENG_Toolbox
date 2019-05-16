package com.pengtoolbox.pageanalyzer._main;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWDefaultApp;
import com.pengtoolbox.cfw.exceptions.ShutdownException;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;
import com.pengtoolbox.pageanalyzer.servlets.AnalyzeURLServlet;
import com.pengtoolbox.pageanalyzer.servlets.CompareServlet;
import com.pengtoolbox.pageanalyzer.servlets.CustomContentServlet;
import com.pengtoolbox.pageanalyzer.servlets.DataServlet;
import com.pengtoolbox.pageanalyzer.servlets.DeleteResultServlet;
import com.pengtoolbox.pageanalyzer.servlets.DocuServlet;
import com.pengtoolbox.pageanalyzer.servlets.GanttChartServlet;
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
        // Initialization
        //################################################################### 
    	//------------------------------------
    	// Create Default App
    	CFWDefaultApp app;
    	try {
    		app = CFW.App.createApp(args);
    	}catch(ShutdownException e) {
    		//do not proceed if shutdown was registered
    		return;
    	}
        
		//------------------------------------
		// Initialize YSlow Singleton
		// prevents error on first analysis request.
		YSlow.instance();
		YSlowExecutor.instance();
		
		//------------------------------------
		// Initialize Database
    	PageAnalyzerDB.initialize();
    	
        //###################################################################
        // Create API ServletContext, no login needed
        //################################################################### 
    	ServletContextHandler apiContext = app.createUnsecureContext("/api");
    	
    	ServletHolder apiHolder = new ServletHolder(new RestAPIServlet());
        apiHolder.getRegistration().setMultipartConfig(app.getGlobalMultipartConfig());
        
        apiContext.addServlet(apiHolder, "/analyzehar");
        
        
        //###################################################################
        // Create authenticatedServletContext
        //###################################################################    	
    	ServletContextHandler appContext = app.createSecureContext("/app");
    	
        ServletHolder uploadHolder = new ServletHolder(new HARUploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(app.getGlobalMultipartConfig());
        appContext.addServlet(uploadHolder, "/");
        appContext.addServlet(uploadHolder, "/harupload");
        appContext.addServlet(DataServlet.class, "/data");
        
        appContext.addServlet(AnalyzeURLServlet.class, "/analyzeurl");
        appContext.addServlet(ResultViewServlet.class, "/resultview");
        appContext.addServlet(CompareServlet.class, "/compare");
        appContext.addServlet(DeleteResultServlet.class, "/delete");
        appContext.addServlet(ResultListServlet.class, "/resultlist");
        appContext.addServlet(GanttChartServlet.class, "/ganttchart");
        
        appContext.addServlet(DocuServlet.class, "/docu");
        appContext.addServlet(CustomContentServlet.class, "/custom");
        
        //Login, Logout and Resource Servlets
        CFW.App.addCFWServlets(appContext);
        
        //###################################################################
        // Startup
        //###################################################################
        app.setDefaultURL("/app/harupload");
        app.start();
        
    }

    //Method from JavaFX Application startup
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
