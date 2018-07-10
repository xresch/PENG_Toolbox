package com.pengtoolbox.javaagent.injection;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class InjectionAgent {

	public static final String CUSTOM_DIR_PATH = "./resources/custom";
	public static final String INTERNAL_RESOURCES_PATH = "com/pengtoolbox/javaagent/injection/resources";
	public static final String JS_DIR_PATH = CUSTOM_DIR_PATH+"/js";
	public static final String CSS_DIR_PATH = CUSTOM_DIR_PATH+"/css";
	
	public static File dir;
	public static File contentFile;
	public static File logFile;
	
	
	public static void agentmain(String args, Instrumentation instr) {
		InjectionAgent.log("[INFO] execute agentmain()...");
		premain(args, instr);
	}
	
	public static void premain(String args, Instrumentation instr) {
		
		createFiles();		
		InjectionAgent.log("\r\n[INFO] ======================== Load SPM Extention Agent  ======================== ");
		
		copyFiles();
		
		instr.addTransformer(new BytecodeTransformer());

	}
		
	private static void createFiles() {
		
		try {
			
			//------------------------
			// Create Directories
			//------------------------
			dir = new File(CUSTOM_DIR_PATH);
			
			if(!dir.exists() && !dir.isDirectory()) {
				dir.mkdir();
			}
			
			File jsdir = new File(JS_DIR_PATH);
			
			if(!jsdir.exists() && !jsdir.isDirectory()) {
				jsdir.mkdir();
			}
			
			File cssdir = new File(CSS_DIR_PATH);
			
			if(!cssdir.exists() && !cssdir.isDirectory()) {
				cssdir.mkdir();
			}
			
			//------------------------
			// Logfile
			//------------------------
			logFile = new File(CUSTOM_DIR_PATH+"/agent.log");
				
			if(!logFile.exists()) {
				logFile.createNewFile();
			}
			
			//------------------------
			// Create content
			//------------------------
			contentFile = new File(CUSTOM_DIR_PATH+"/content.html");
			
			if(!contentFile.exists()) {
				
				InjectionAgent.log("[INFO] Create custom.html...");
				
				contentFile.createNewFile();
				FileUtils.writeToFile(contentFile, "<html><body><p>Replace this content.</p></body></html>");
			}
			
		}catch(Exception e) {
			InjectionAgent.log("[ERROR] Agent.createFile()", e);
		}
			
	}
	
	protected static void copyFiles(){ 
		 
		InjectionAgent.log("[INFO] Copy Resource Files"); 
    	    	    	     	
		
    	FileUtils.copyResource(INTERNAL_RESOURCES_PATH+"/injected.css",
    			CSS_DIR_PATH+"/injected.css");
    	
    	FileUtils.copyResource(INTERNAL_RESOURCES_PATH+"/injected.js",
				JS_DIR_PATH+"/injected.js");
    	
    
	}  


	public static void log(String message) {
		System.out.println(message);
		FileUtils.writeToFile(logFile, "\n"+message);
	}
	
	public static void log(String message, Throwable e) {
		
		StringBuffer errorBuffer = new StringBuffer(e.toString());
		
		for(StackTraceElement s : e.getStackTrace()) {
			errorBuffer.append("\n"+s.toString());
		}
		
		message += errorBuffer.toString();
		
		InjectionAgent.log(message);
	}

}
