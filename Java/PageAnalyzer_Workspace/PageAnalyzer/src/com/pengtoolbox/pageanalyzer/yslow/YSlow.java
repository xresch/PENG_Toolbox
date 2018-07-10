package com.pengtoolbox.pageanalyzer.yslow;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.pengtoolbox.pageanalyzer.logging.PALogger;

import javafx.application.Platform;

public class YSlow {

	private Object lock = new Object();
	private String result = null;
	private static YSlow INSTANCE = null;
	
	private static YSlowExecutor executor = null;
	private YSlowExecutorThread thread;
	
	private boolean isResultUpdated;
	
	private static Logger logger = Logger.getLogger(YSlow.class.getName());
    
	
	private YSlow(){
		
		thread = new YSlowExecutorThread();
		thread.start();
		
		//wait for executor to initialize, max 50 seconds
		for(int i = 0; executor == null && i < 100; i++){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static YSlow instance(){
		
		if(INSTANCE == null){
			INSTANCE = new YSlow();
			
		}
		return INSTANCE;
		
	}
	
	public String analyzeHarString(String harString){
		
		PALogger log = new PALogger(logger);
		
		log.start().method("analyzeHarString");
		
		// Execute the Java FX Application.
		// It will set the Result on the singelton instance
		synchronized(lock){
			this.result = null;
			this.isResultUpdated = false;
			
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					executor.analyzeHARString(harString);
				}
			});
			
			//wait for result, max 50 seconds
			for(int i = 0; !isResultUpdated && i < 100; i++){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.severe("Thread was interrupted.", e);
				}
			}
		}
		
		log.end();
		return result;
	}

	public static void setExecutor(YSlowExecutor exec) {
		executor = exec;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
		this.isResultUpdated = true;
	}
	
	
	
}
