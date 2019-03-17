package com.pengtoolbox.cfw.caching;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.utils.FileUtils;

public class FileAssembly {
	
	/** Static field to store the assembled results by their file names. */
	private static final HashMap<String,FileAssembly> assemblyCache = new HashMap<String, FileAssembly>();
	
	private LinkedHashMap<String, HandlingType> fileSet = new LinkedHashMap<String, HandlingType>();
	
	private String inputName = "";
	private String assemblyName = "";
	private String assemblyServletPath = "";
	private String filetype = "";
	private String contentType = "";
	private String assemblyContent = "";
	public static final String CFW_JAR_RESOURCES_PATH = "com/pengtoolbox/cfw/resources";
	public enum HandlingType {FILE, JAR_RESOURCE, STRING};
	
	/***********************************************************************
	 * Constructor for the FileAssembler.
	 * @param name used for building the assembled file name
	 * @param filetype the file type e.g. "js", "css"
	 ***********************************************************************/
	public FileAssembly(String name, String filetype) {
		this.inputName = name;
		this.filetype = filetype;
		this.determineContentType();
	}
	
	/***********************************************************************
	 * Constructor for the FileAssembler.
	 * @param type the 
	 * @param filetype the file type e.g. "js", "css"
	 ***********************************************************************/
	public FileAssembly addFile(HandlingType type, String fileDefinition) {
		fileSet.put(fileDefinition, type);
		return this;
	}
	
	/***********************************************************************
	 * Assembles and stores the file in the permanent cache of 
	 * {@link com.pengtoolbox.cfw.utils.FileUtils FileUtils}.
	 * 
	 * @return the filename that can be used for retrieving the file content.
	 ***********************************************************************/
	public FileAssembly assemble() {
		
		//--------------------------------
		// Initialize
		Set<String> keySet = fileSet.keySet();
		int hashCode = keySet.toString().hashCode();
		assemblyName = inputName + "_" + hashCode + "." + filetype;
		assemblyServletPath = CFWConfig.BASE_URL + "/assembly?name="+URLEncoder.encode(assemblyName);
		//--------------------------------
		// Initialize
		if(!FileAssembly.hasAssembly((assemblyName)) || !CFWConfig.CACHING_FILE_ENABLED ) {
			
			StringBuffer concatenatedFile = new StringBuffer();
			for(String fileDefinition : fileSet.keySet()) {
				
				HandlingType type = fileSet.get(fileDefinition);
				String content = "";
				
				switch(type) {
					case FILE:			content = FileUtils.getFileContent(null, fileDefinition);
										break;
						
					case JAR_RESOURCE: 	content = FileUtils.readPackageResource(fileDefinition);
										break;
						
					case STRING: 		content = fileDefinition;
										break;
						
					default: 			content = "";
										break;
				}
				
				if(content != null) {
					concatenatedFile.append(content).append("\n");
				}
			}
			
			assemblyContent = concatenatedFile.toString();
		}
		
		return this;
	}
	
	/***********************************************************************
	 * Store this instance in the cache.
	 * @return the name of the assembly
	 ***********************************************************************/
	public FileAssembly cache() {
		if(!FileAssembly.hasAssembly((assemblyName)) || !CFWConfig.CACHING_FILE_ENABLED ) {
			assemblyCache.put(assemblyName, this);
		}
		return this;
	}
	
	/***********************************************************************
	 * Check if the cache contains the specific assembly.
	 * @return true or false
	 ***********************************************************************/
	public static boolean hasAssembly(String assemblyName) {
		return assemblyCache.containsKey(assemblyName);
	}
	
	/***********************************************************************
	 * Check if the cache contains the specific assembly.
	 * @return FileAssembler instance or null
	 ***********************************************************************/
	public static FileAssembly getAssemblyFromCache(String assemblyName) {
		return assemblyCache.get(assemblyName);
	}
	
	/***********************************************************************
	 * Tries to determine the content type of the assembly and returns it 
	 * as a string.
	 * @return string determined content type
	 ***********************************************************************/
	public void determineContentType() {
		
		switch(filetype) {
			case "js":		contentType = "text/javascript";
							break;
			
			case "css":		contentType = "text/css";
							break;
			
			case "html":	contentType = "text/html";
							break;
			
			case "json":	contentType = "application/json";
							break;
			
			case "xml":		contentType = "application/xml";
							break;
			
			default:		contentType = "text/"+filetype;
							break;
		}
	}

	public String getAssemblyName() {
		return assemblyName;
	}

	public String getAssemblyServletPath() {
		return assemblyServletPath;
	}
	
	public String getFiletype() {
		return filetype;
	}
	
	public String getContentType() {
		return contentType;
	}

	public String getAssemblyContent() {
		return assemblyContent;
	}	
	
}
