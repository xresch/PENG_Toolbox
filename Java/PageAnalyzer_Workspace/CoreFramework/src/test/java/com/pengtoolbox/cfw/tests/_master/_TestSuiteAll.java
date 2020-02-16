package com.pengtoolbox.cfw.tests._master;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.pengtoolbox.cfw.tests.db.TestCFWDBUserManagement;
import com.pengtoolbox.cfw.tests.localization.LocalizationTests;
import com.pengtoolbox.cfw.tests.validation.CFWValidationTests;
import com.pengtoolbox.cfw.tests.various.CFWCommandLineTests;
import com.pengtoolbox.cfw.tests.various.FileUtilsTests;
import com.pengtoolbox.cfw.tests.web.GeneralWebTests;
import com.pengtoolbox.cfw.tests.web.MenuTests;
@RunWith(Suite.class)

@Suite.SuiteClasses({
   CFWValidationTests.class,
   MenuTests.class,
   CFWCommandLineTests.class,
   TestCFWDBUserManagement.class,
   FileUtilsTests.class,
   LocalizationTests.class,
   GeneralWebTests.class
})

public class _TestSuiteAll { 
	
}  
