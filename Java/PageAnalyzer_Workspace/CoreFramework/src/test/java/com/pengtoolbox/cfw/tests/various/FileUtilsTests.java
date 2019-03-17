package com.pengtoolbox.cfw.tests.various;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.caching.FileAssembly.HandlingType;
import com.pengtoolbox.cfw.utils.FileUtils;

class FileUtilsTests {
	
	public static final String INTERNAL_RESOURCES_PATH = "com/pengtoolbox/cfw/resources";
	
	@Test
	public void testFileAssembly() {
		
		FileAssembly assembler = new FileAssembly("common", "js");
			
		String assemblyName = assembler.addFile(HandlingType.FILE, "./testdata/test.css")
				.addFile(HandlingType.JAR_RESOURCE, FileAssembly.CFW_JAR_RESOURCES_PATH +"./junit_test.js")
				.addFile(HandlingType.STRING, "/* just some comment */")
				.assemble()
				.cache()
				.getAssemblyName();
		
		FileAssembly cachedAssembly = FileAssembly.getAssemblyFromCache(assemblyName);
		Assertions.assertNotNull(cachedAssembly, "Assembly is not null");
		
		Assertions.assertTrue( cachedAssembly.getAssemblyContent().contains(".test{display: block;}"),
				"Contains the CSS string, FILE successfully loaded.");
		
		Assertions.assertTrue( cachedAssembly.getAssemblyContent().contains("function test(){alert('JUnit');}"),
				"Contains the javascript string, JAR_RESOURCE successfully loaded.");
		
		Assertions.assertTrue( cachedAssembly.getAssemblyContent().contains("/* just some comment */"),
				"Contains the manual string, STRING successfully loaded.");
		
	}
	
	@Test
	public void testReadPackageResource() {
		String content = FileUtils.readPackageResource(INTERNAL_RESOURCES_PATH +"/cfw.js");
		
		Assertions.assertNotNull(content);
		Assertions.assertTrue(content.length() > 0);
	}

}
