package com.pengtoolbox.cfw.utils;

import java.util.List;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWScripting {
	
	public static Logger logger = CFWLog.getLogger(CFWScripting.class.getName());
	
	/******************************************************************************************************
	 * Print all available scripting engines. 
	 ******************************************************************************************************/
	public static void printAvailableEngines() {
		
		ScriptEngineManager mgr = new ScriptEngineManager();
	    List<ScriptEngineFactory> factories = mgr.getEngineFactories();
	   
	    for (ScriptEngineFactory factory : factories){
			System.out.println("ScriptEngineFactory Info");
			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();
			System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
			List<String> engNames = factory.getNames();
			for (String name : engNames)
			{
			    System.out.printf("\tEngine Alias: %s\n", name);
			}
			System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
	    }
	}
	
	
	/******************************************************************************************************
	 * Execute a javascript call with defined parameters.
	 * 
	 * @param script that should be loaded containing the methods that should be executed.
	 * @param methodName the name of the method to be executed
	 * @param parameters values to be passed to method.
	 ******************************************************************************************************/
	public static Object executeJavascript(String script, String methodName, Object... parameters) {
		
		//-----------------------------------
		// Create Engine
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		Invocable invocableEngine = (Invocable) engine;
		
		//-----------------------------------
		// Execute Script Engine
		try {
			engine.eval(script);
			Object result = invocableEngine.invokeFunction(methodName, parameters);
			
			return result;
			
		} catch (NoSuchMethodException e) {
			new CFWLog(logger)
				.method("executeJavascript")
				.severe("The method '"+methodName+"' doesn't exist. ", e);
		} catch (ScriptException e) {
			new CFWLog(logger)
			.method("executeJavascript")
			.severe("An exception occured while executing a javascript: "+e.getMessage(), e);
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/******************************************************************************************************
	 * Execute a javascript call with defined parameters.
	 * 
	 * @param script that should be loaded containing the methods that should be executed.
	 * @param methodCallWithParams a string representation of the function call, e.g. "foobar('Test')"
	 ******************************************************************************************************/
	public static Object executeJavascript(String script, String methodCallWithParams) {
		
		//-----------------------------------
		// Create Engine
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		
		//-----------------------------------
		// Execute Script Engine
		try {
			engine.eval(script);
			Object result = engine.eval(methodCallWithParams);
			
			return result;
			
		} catch (ScriptException e) {
			new CFWLog(logger)
			.method("executeJavascript")
			.severe("An exception occured while executing a javascript: "+e.getMessage(), e);
			e.printStackTrace();
		}
		
		return null;
	}

}
