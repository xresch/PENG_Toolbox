package com.pengtoolbox.cfw.tests.various;

import org.junit.jupiter.api.Test;

import com.pengtoolbox.cfw._main.CFW;

public class CFWLocalizationTests {
	
	public static final String INTERNAL_RESOURCES_PATH = "com/pengtoolbox/cfw/resources";
	
	@Test
	public void testLoadLanguagePack() {
		
		CFW.Localization.loadLanguagePack("en_us");
	}
	
}
