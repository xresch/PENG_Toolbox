package com.pengtoolbox.pageanalyzer.yslow;

import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.logging.CFWLogger;

import javafx.application.Platform;

public class YSlow {

	private static YSlow INSTANCE = null;
	private static Logger logger = Logger.getLogger(YSlow.class.getName());
	
	/***********************************************************************
	 * 
	 ***********************************************************************/
	private YSlow(){
		
		//ExecutionContextPool.initializeExecutors(10);
	}
	
	/***********************************************************************
	 * 
	 ***********************************************************************/
	public static YSlow instance(){
		
		if(INSTANCE == null){
			INSTANCE = new YSlow();
			
		}
		return INSTANCE;
		
	}

	/***********************************************************************
	 * 
	 ***********************************************************************/
	public String analyzeHarString(String harString){
		
		CFWLogger log = new CFWLogger(logger);
		
		log.start().method("analyzeHarString");
		ExecutionContext context = ExecutionContextPool.lockContext();
		//----------------------------------------------
		// Execute the Java FX Application.
		// It will set the Result on the singelton instance		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				YSlowExecutor.analyzeHARString(context, harString);
			}
		});
		
		//----------------------------------------------
		//wait for result, max 50 seconds
		for(int i = 0; !context.isResultUpdated() && i < 100; i++){
			try {
				System.out.println("wait");
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log.severe("Thread was interrupted.", e);
			}
		}
		String result = context.getResult();
		ExecutionContextPool.releaseContext(context);
		log.end();
		
		return result;
	}

	
}
