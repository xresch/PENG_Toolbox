package com.pengtoolbox.cfw.caching;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileAssembly.HandlingType;

public class FileDefinition {

	private HandlingType type; 
	private String path;
	private String filename;
	private String content;
	
	public FileDefinition(HandlingType type, String path, String filename) {
		this.type = type;
		this.path = path;
		this.filename = filename;
	}
	
	public FileDefinition(String content) {
		this.type = HandlingType.STRING;
		this.content = content;
		this.path = "";
		this.filename = "";
	}

	public HandlingType getType() {
		return type;
	}

	public void setType(HandlingType type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**************************************************************************
	 * Return the contents set by setContent().
	 * @return
	 **************************************************************************/
	public String getContent() {
		return content;
	}

	/**************************************************************************
	 * Set the contents which should be used when the HandlingType.STRING is used.
	 * @return
	 **************************************************************************/
	public void setContent(String content) {
		this.content = content;
	}
	
	public int hashCode(){
		return (path + filename + content).hashCode();
	}
	
	/**************************************************************************
	 * Read the contents of the file specified by this File definition and
	 * returns it as a string.
	 * @return
	 **************************************************************************/
	public String readContents(){
		
		String returnContent = "";
		switch(type) {
			case FILE:			returnContent = CFW.Files.getFileContent(null, path, filename);
								break;
				
			case JAR_RESOURCE: 	returnContent = CFW.Files.readPackageResource(path, filename);
								break;
				
			case STRING: 		returnContent = content;
								break;
				
			default: 			returnContent = "";
							break;
							
		}
		
		return returnContent;
	}
	
	public String getJavascriptTag(){
		
		switch(type) {
			case FILE:			return "<script src=\""+path+"/"+filename+"\"></script>";
				
			case JAR_RESOURCE: 	return "<script src=\"/cfw/jarresource?pkg="+path+"&file="+filename+"\"></script>";
				
			case STRING: 		return "<script>" + content + "<script>";
				
			default: 			return "";
				
		}

	}
	
	

		
}
