package com.pengtoolbox.javaagent.injection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileUtils {

	private static final int BUFFER_SIZE = 4096;

	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	private static void mkdirs(File outdir, String path) {
		File d = new File(outdir, path);
		if (!d.exists())
			d.mkdirs();
	}
	
	/********************************************************************************************
	 * 
	 ********************************************************************************************/	
	public static void copyResource(String resourcePath, String targetFilePath) {
		InputStream in = InjectionAgent.class.getClassLoader().getResourceAsStream(resourcePath);
    	
		File target = new File(targetFilePath);
		try {
			Files.copy(in, Paths.get(target.getPath()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			InjectionAgent.log("[ERROR] While copying resource.", e);
		}
		
	}

	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	public static String readFromFile(File file) {
		InjectionAgent.log("[INFO] Read File: "+ file.getAbsolutePath());
		
		Path path = Paths.get(file.getPath());
		
		List<String> fileContent = null;
		try {
			fileContent = Files.readAllLines(path);
		} catch (Exception e) {
			InjectionAgent.log("[ERROR] Agent.readFromFile()", e);
		}
		
		StringBuffer result = new StringBuffer("");
		for(String line : fileContent) {
			result.append(line+"\n");
		}
		
		return result.toString();
		
	}

	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	static void writeToFile(File file, String content) {
		Path path = Paths.get(file.getPath());
		
		try {
			Files.write(path, content.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
