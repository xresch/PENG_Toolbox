package com.pengtoolbox.cfw.tests.various;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.utils.CFWHttpPacScriptMethods;

public class TestCFWScriptEngine {
	
	@Test
	public void printAvailableEngines() {
		
		CFW.Scripting.printAvailableEngines();
	}

	@Test
	public void testRunJavascript() {
		
		//--------------------------------
		// With parameter List
		Object result = CFW.Scripting.executeJavascript("function myFunc(astring, anumber){ return astring+' '+anumber}", "myFunc", "Test", 123);
		
		System.out.println(result);
		Assertions.assertEquals("Test 123", result, "The method returned the expected value.");
		
		//--------------------------------
		// With parameter List
		Object result2 = CFW.Scripting.executeJavascript("function myFunc(astring, anumber){ return astring+' '+anumber}", "myFunc('Hello', 456)");
		
		System.out.println(result2);
		Assertions.assertEquals("Hello 456", result2, "The method returned the expected value.");
	}
	
	@Test
	public void testRunJavascriptWithAdditionalMethods() {
		//--------------------------------
		// Call CFWHttpPacScriptMethods.myIpAddress();
		Object result = CFW.Scripting.executeJavascript("", CFWHttpPacScriptMethods.class, "CFWHttpPacScriptMethods.myIpAddress();");
		System.out.println("CFWHttpPacScriptMethods.myIpAddress(): "+result);
		
		//--------------------------------
		// Call myIpAddress();
		result = CFW.Scripting.executeJavascript("function myFunc(astring, anumber){ return astring+' '+anumber}", CFWHttpPacScriptMethods.class, "myIpAddress();");
		System.out.println("myIpAddress(): "+result);
		
		//--------------------------------
		// With parameter List
		result = CFW.Scripting.executeJavascript("function myFunc(astring, anumber){ return astring+' '+anumber}", CFWHttpPacScriptMethods.class, "myFunc('Hello', 456)");
		
		System.out.println(result);
		Assertions.assertEquals("Hello 456", result, "The method returned the expected value.");
	}
	
}
