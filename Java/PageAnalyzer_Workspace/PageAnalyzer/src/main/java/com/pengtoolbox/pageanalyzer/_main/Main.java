package com.pengtoolbox.pageanalyzer._main;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppInterface;
import com.pengtoolbox.cfw._main.CFWApplication;
import com.pengtoolbox.cfw.api.CFWAPIServlet;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.pageanalyzer.db.PAPermissions;
import com.pengtoolbox.pageanalyzer.db.Result;
import com.pengtoolbox.pageanalyzer.response.PageAnalyzerFooter;
import com.pengtoolbox.pageanalyzer.response.PageAnalyzerMenu;
import com.pengtoolbox.pageanalyzer.response.PageAnalyzerUserMenuItem;
import com.pengtoolbox.pageanalyzer.servlets.AnalyzeURLServlet;
import com.pengtoolbox.pageanalyzer.servlets.CompareServlet;
import com.pengtoolbox.pageanalyzer.servlets.CustomContentServlet;
import com.pengtoolbox.pageanalyzer.servlets.DataServlet;
import com.pengtoolbox.pageanalyzer.servlets.DeleteResultServlet;
import com.pengtoolbox.pageanalyzer.servlets.DocuServlet;
import com.pengtoolbox.pageanalyzer.servlets.GanttChartServlet;
import com.pengtoolbox.pageanalyzer.servlets.HARUploadServlet;
import com.pengtoolbox.pageanalyzer.servlets.ManageResultsServlet;
import com.pengtoolbox.pageanalyzer.servlets.RestAPIServlet;
import com.pengtoolbox.pageanalyzer.servlets.ResultListServlet;
import com.pengtoolbox.pageanalyzer.servlets.ResultViewServlet;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;
import com.pengtoolbox.pageanalyzer.yslow.YSlowExecutor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application implements CFWAppInterface {
	
	public static Logger logger = CFWLog.getLogger(Main.class.getName());
	protected static CFWLog log = new CFWLog(logger);
	
    public static void main( String[] args ) throws Exception
    {
    	
    	CFW.initializeApp(new Main(), args);

    }
    
	@Override
	public void register() {
    	CFW.Registry.Components.setDefaultMenu(PageAnalyzerMenu.class);
    	CFW.Registry.Components.setDefaultUserMenuItem(PageAnalyzerUserMenuItem.class);
    	CFW.Registry.Components.setDefaultFooter(PageAnalyzerFooter.class);
    	
    	CFW.Registry.Objects.addCFWObject(Result.class);
		
	}

	@Override
	public void initializeDB() {
		//------------------------------------
		// Initialize Database
    	//PageAnalyzerDB.initialize();
    	PAPermissions.initializePermissions();
		
	}
	
	@Override
	public void stopApp() {
		Platform.exit();
	}

	@Override
	public void startApp(CFWApplication app) {
			//------------------------------------
			// Initialize YSlow Singleton
			// prevents error on first analysis request.
			YSlow.instance();
			YSlowExecutor.instance();
			
	    	
	    	// For Testing only
	    	//CFW.DB.createTestData();
	    	
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
	        appContext.addServlet(ManageResultsServlet.class, "/manageresults");
	        appContext.addServlet(DocuServlet.class, "/docu");
	        appContext.addServlet(CustomContentServlet.class, "/custom");
	        
	        //Login, Logout and Resource Servlets
	        app.addCFWServlets(appContext);
	        
	        //###################################################################
	        // Startup
	        //###################################################################
	        app.setDefaultURL("/app/harupload");
	        try {
				app.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

    //Method from JavaFX Application startup
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/********************************************************
	 * Workaround for classloading issue
	 ********************************************************/
	public static void javafxLogWorkaround(Level level, String message, String method){
		
		log.method(method).log(level, message, null);
	}
	
	/********************************************************
	 * Workaround for classloading issue
	 ********************************************************/
	public static void javafxLogWorkaround(Level level, String message, Throwable e, String method){
		
		log.method(method).log(level, message, e);
	}

}
