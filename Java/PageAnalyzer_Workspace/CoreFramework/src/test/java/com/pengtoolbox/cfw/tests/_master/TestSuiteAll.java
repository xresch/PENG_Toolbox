package com.pengtoolbox.cfw.tests._master;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.pengtoolbox.cfw.tests.validation.CFWValidationTests;
import com.pengtoolbox.cfw.tests.web.GeneralWebTests;
import com.pengtoolbox.cfw.tests.web.MenuTests;
@RunWith(Suite.class)

@Suite.SuiteClasses({
   CFWValidationTests.class,
   MenuTests.class,
   GeneralWebTests.class,
   
})

public class TestSuiteAll { 
	
}  
