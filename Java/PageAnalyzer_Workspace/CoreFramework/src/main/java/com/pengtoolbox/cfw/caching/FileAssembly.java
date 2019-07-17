package com.pengtoolbox.cfw.caching;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.pengtoolbox.cfw._main.CFW;

public class FileAssembly {
	
	/** Static field to store the assembled results by their file names. */
	private static final LinkedHashMap<String,FileAssembly> assemblyCache = new LinkedHashMap<String, FileAssembly>();
	
	private ArrayList<FileDefinition> fileArray = new ArrayList<FileDefinition>();
	
	private String inputName = "";
	private String assemblyName = "";
	private String assemblyServletPath = "";
	private String filetype = "";
	private String contentType = "";
	private String assemblyContent = "";
	public static final String CFW_JAR_RESOURCES_PATH = "com.pengtoolbox.cfw.resources";
	
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
	 * Add a file to the Assembly.
	 * @param type the handling type of the file.
	 * @param filetype the file type e.g. "js", "css"
	 ***********************************************************************/
	public FileAssembly addFile(HandlingType type, String path, String filename) {
		FileDefinition fileDef = new FileDefinition(type, path, filename);
		fileArray.add(fileDef);
		return this;
	}
	
	public FileAssembly addFileContent(String content) {
		FileDefinition fileDef = new FileDefinition(content);
		fileArray.add(fileDef);
		return this;
	}
	/***********************************************************************
	 * Check if the assembly has any files added.
	 * @param data.type the 
	 * @param filetype the file type e.g. "js", "css"
	 ***********************************************************************/
	public boolean hasFiles() {
		return (fileArray.size() > 0);
	}
	
	
	/***********************************************************************
	 * Assembles and stores the file in the permanent cache of 
	 * {@link com.pengtoolbox.cfw.utils.CFWFiles FileUtils}.
	 * 
	 * @return the filename that can be used for retrieving the file content.
	 ***********************************************************************/
	public FileAssembly assemble() {
		
		//--------------------------------
		// Initialize
		int hashCode = 0;
		for(FileDefinition fileDef : fileArray) {
			hashCode += fileDef.hashCode();
		}
		
		assemblyName = inputName + "_" + hashCode + "." + filetype;
		assemblyServletPath = "/cfw/assembly?name="+URLEncoder.encode(assemblyName);
		//--------------------------------
		// Initialize
		if(!FileAssembly.hasAssembly((assemblyName)) || !CFW.Config.CACHING_FILE_ENABLED ) {
			
			StringBuffer concatenatedFile = new StringBuffer();
			for(FileDefinition fileDef : fileArray) {
				
				
				String content = fileDef.readContents();
				
				
				
				if(content != null  && !content.isEmpty()) {
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
		if(!FileAssembly.hasAssembly((assemblyName)) || !CFW.Config.CACHING_FILE_ENABLED ) {
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
