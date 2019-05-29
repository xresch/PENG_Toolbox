package com.pengtoolbox.cfw.cli;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.FileCanReadValidator;
import com.pengtoolbox.cfw.validation.LogLevelValidator;


/**************************************************************************************
 * The AbstractArgumentsHandler provides the basic functionalities for the handling of 
 * command line arguments.
 * It provides default arguments for a configuration file and handling of log levels.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public abstract class AbstractArgumentsHandler {
	
	public static final String CONFIG_LOGLEVEL_FILE = "-config.loglevel.file";
	public static final String CONFIG_LOGLEVEL_CONSOLE = "-config.loglevel.console";
	public static final String CONFIG_FILE = "-config.file";

	private static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	protected LinkedHashMap<String,String> loadedArguments;
	protected LinkedHashMap<String,ArgumentDefinition> supportedArgumentsMap;

	protected ArrayList<String> invalidMessages;
	
	public AbstractArgumentsHandler(){
		//*********************************************
		//Initialization
		//*********************************************
		loadedArguments = new LinkedHashMap<String,String>();
		supportedArgumentsMap = new LinkedHashMap<String,ArgumentDefinition>();
		
//		//*********************************************
//		// Add default arguments
//		//*********************************************	
//		ArgumentDefinition configFile = 
//				new ArgumentDefinition(	CONFIG_FILE, 
//										CONFIG_FILE+"={filepath}",
//										"",
//										"The path to a config-file. The config-file can include all the arguments defined in this list delimited by newline. Also lines starting with �#� are considered as comments, as well blank lines are allowed.");
//		
//		new FileCanReadValidator(configFile);
//		this.addSupportedArgument(configFile.getPropertyName(), configFile);
//		
//		//####################################################################
//		
//		ArgumentDefinition configLogLevelConsole = 
//				new ArgumentDefinition(	CONFIG_LOGLEVEL_CONSOLE, 
//										CONFIG_LOGLEVEL_CONSOLE+"={ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF}",
//										"INFO",
//										"Log level(log4j2) printed to the standard output.");
//		
//		new LogLevelValidator(configLogLevelConsole);
//		this.addSupportedArgument(configLogLevelConsole.getPropertyName(), configLogLevelConsole);
//		
//		//####################################################################
//
//		ArgumentDefinition configLogLevelFile = 
//				new ArgumentDefinition(	CONFIG_LOGLEVEL_FILE, 
//										CONFIG_LOGLEVEL_FILE+"={ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF}",
//										"DEBUG",
//										"Log level(log4j2) printed to the logfile.");
//		new LogLevelValidator(configLogLevelFile);
//		this.addSupportedArgument(configLogLevelFile.getPropertyName(), configLogLevelFile);
		
	}
	
	/***********************************************************
	 * Before resolving all the arguments, first set the log 
	 * levels if specified.
	 * 
	 ***********************************************************/
//	protected void loadLogLevels(String[] argArray) {
//		
//		// Load default values and overwrite if specified
//		String consoleLevel = this.getValue(CONFIG_LOGLEVEL_CONSOLE).toUpperCase();
//		String fileLevel = this.getValue(CONFIG_LOGLEVEL_FILE).toUpperCase();
//		
//		for(String argument : argArray){
//			if(argument.startsWith(CONFIG_LOGLEVEL_CONSOLE)){
//				consoleLevel = argument.split("=")[1].toUpperCase();
//			}
//			if(argument.startsWith(CONFIG_LOGLEVEL_FILE)){
//				fileLevel = argument.split("=")[1].toUpperCase();
//			}
//		}
//		
//		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//		Configuration config = ctx.getConfiguration();
//		
//		ConsoleAppender consoleAppender = (ConsoleAppender)config.getAppender("CONSOLE");
//		ThresholdFilter consoleFilter = ThresholdFilter.createFilter(Level.getLevel(consoleLevel), Result.ACCEPT, Result.DENY);
//		consoleAppender.addFilter(consoleFilter);
//		
//		RollingFileAppender fileAppender = (RollingFileAppender)config.getAppender("ROLLING_FILE");
//		ThresholdFilter fileFilter = ThresholdFilter.createFilter(Level.getLevel(fileLevel), Result.ACCEPT, Result.DENY);
//		fileAppender.addFilter(fileFilter);
//	}
	
	/***********************************************************
	 * Resolves the command line arguments and stores them in
	 * the internal argument list.
	 * 
	 * @param argArray the arguments to resolve with the format
	 * "-{key}={value}"
	 * @throws ArgumentsException 
	 * 
	 ***********************************************************/
	protected abstract void resolveArguments(String[] argArray) throws ArgumentsException;
	
	/***********************************************************
	 * Resolves the command line arguments and stores them in
	 * the internal argument list.
	 * 
	 * @param argArrayList the arguments to resolve with the format
	 * "-{key}={value}"
	 * @throws ArgumentsException 
	 * 
	 ***********************************************************/
	protected void resolveArguments(ArrayList<String> argArrayList) throws ArgumentsException {
		resolveArguments(argArrayList.toArray(new String[0]));
	}

	/***********************************************************
	 * Parses the command line Arguments. First check if a
	 * config file is provided and override all other command line
	 * arguments if it is found.
	 * 
	 * @param args the arguments to parse.
	 * @throws ArgumentsException 
	 * 
	 ***********************************************************/
	public void readArguments(String[] args) throws ArgumentsException{
		
		String[] argArray = args;
		
		//-------------------------------------------
		// Check if there is a config file defined
		// and load it.
		for(String argument : argArray){
			if(argument.startsWith(CONFIG_FILE)){
				int index = argument.indexOf("=");
				String configFilePath = argument.substring(index+1);
				
				readArgumentsFromFile(configFilePath);
				return;
			}
		}
		
		//-------------------------------------------
		// If no -confi.file was specified load
		// arguments
		resolveArguments(argArray);
	}
	
	/***********************************************************
	 * Resolves the arguments from a file and stores them in
	 * the internal argument list.
	 * 
	 * @param configFilePath the path of the config file
	 * @throws ArgumentsException 
	 * 
	 ***********************************************************/
	public void readArgumentsFromFile(String configFilePath) throws ArgumentsException {
		ArrayList<String> argArrayList = new ArrayList<String>();
		
		//-------------------------------------------
		// Read config File
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(configFilePath));
			
			boolean hasMoreLines = true;
			while(hasMoreLines){
			 
				String line = bf.readLine();
				
				if(line != null && !line.trim().isEmpty() && !line.startsWith("#")){
					argArrayList.add(line);
				}else{
					if(line == null) hasMoreLines = false;
				}
			}
			
			//-------------------------------------------
			// overwrite CommandLine-Arguments
			resolveArguments(argArrayList);
			
		} catch (FileNotFoundException e) {
			new CFWLog(logger).severe("specified config file not found:"+e.getMessage());
		} catch (IOException e) {
			new CFWLog(logger).severe("error while reading config file:"+e.getMessage());
		} finally{
			if(bf != null){
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	/***********************************************************
	 * Print a usage listing with supported arguments.
	 * 
	 ***********************************************************/
	public void printUsage(){
		for(ArgumentDefinition currentArgument : supportedArgumentsMap.values()){
			System.out.println("");
			System.out.print(currentArgument.getPropertyName());
			System.out.println("\n\t\tSyntax: "+currentArgument.getSyntax());
			
			if(currentArgument.getDefaultValue() != null && !currentArgument.getDefaultValue().trim().isEmpty()){
				System.out.println("\t\tDefault: "+currentArgument.getDefaultValue());
			}
			
			System.out.println("\t\tDescription: "+currentArgument.getDescription());
		
		}
	}
	
	/***********************************************************
	 * Returns true if all arguments were correct, false otherwise.
	 ***********************************************************/
	public boolean validateArguments(){
		
		boolean isValid = true;
		invalidMessages = new ArrayList<String>();
		
		for(String argumentKey : loadedArguments.keySet()){
			String argumentValue = loadedArguments.get(argumentKey);
			
			ArgumentDefinition supportedArgument = supportedArgumentsMap.get(argumentKey);
			
			if(supportedArgument != null){
				if(!supportedArgument.validateValue(argumentValue)){
					invalidMessages.addAll(supportedArgument.getInvalidMessages());
					isValid=false;
				}
			}else{
				invalidMessages.add("The argument '"+argumentKey+"' is not supported.");
				isValid=false;
			}
		}
		
		return isValid;
	}
	
	/***********************************************************
	 * Print a list of readed arguments to standard output.
	 * Will be executed if debug is enabled.
	 * 
	 ***********************************************************/
	public void printLoadedArguments(){
		Set<String> keySet = loadedArguments.keySet();
		for(String key : keySet){
			System.out.println("Key: "+key+", Value:"+loadedArguments.get(key));
		}
	}

	/***********************************************************
	 * Add a supported Argument.
	 ***********************************************************/
	public ArgumentDefinition addSupportedArgument(String key, ArgumentDefinition value) {
		return supportedArgumentsMap.put(key, value);
	}

	
	/***********************************************************
	 * Check if the argument is supported.
	 * 
	 ***********************************************************/
	public boolean isArgumentSupported(String argument){
		Set<String> keySet = supportedArgumentsMap.keySet();
		for(String key : keySet){
			if(argument.equals(key))
				return true;
		}
		
		return false;
	}

	public boolean hasArguments() {
		return loadedArguments.size() > 0 ? true : false;
	}
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	
	public void addArgument(String key, String value){
		loadedArguments.put(key, value);
	}
	
	public void addAllArgument(LinkedHashMap<String,String> arguments){
		loadedArguments.putAll(arguments);
	}
	
	public LinkedHashMap<String,String> getLoadedArguments() {
		return loadedArguments;
	}

	public ArrayList<String> getInvalidMessages() {
		return invalidMessages;
	}

	public String getValue(String argument) {
		return loadedArguments.get(argument);
	}
	
}
