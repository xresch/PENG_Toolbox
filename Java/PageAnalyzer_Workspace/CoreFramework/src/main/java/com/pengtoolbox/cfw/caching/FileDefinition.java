package com.pengtoolbox.cfw.caching;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public int hashCode(){
		return (path + filename + content).hashCode();
	}
	
	

		
}
