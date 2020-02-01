package com.pengtoolbox.cfw.tests.web;


import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.google.gson.JsonArray;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.features.manual.ManualPage;

public class ManualPagesTests {

	@Test
	public void testMenuRegistry() {
		
		//---------------------------
		// Test Menu Hierarchy
		CFW.Registry.Manual.addManualPage(new ManualPage("Top Page"), null);
		CFW.Registry.Manual.addManualPage(new ManualPage("A"), "Top Page");
		CFW.Registry.Manual.addManualPage(new ManualPage("B"), "Top Page | A");
		CFW.Registry.Manual.addManualPage(new ManualPage("C"), "Top Page | A | B" );
		
		//---------------------------
		// Test Menu Hierarchy 2
		CFW.Registry.Manual.addManualPage(new ManualPage("Top Item 2"), null);
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Item"), "Top Item 2");
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Sub Item"), " Top Item 2 | Sub Item ");
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Sub Item 2"), "Top Item 2 | Sub Item ");
		
		//---------------------------
		// Test Override
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Item"), "Top Item 2");
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Sub Item"), " Top Item 2 | Sub Item ");
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Sub Item 2"), "Top Item 2 | Sub Item ");
		
		//---------------------------
		// Test addChild combo
		CFW.Registry.Manual.addManualPage(new ManualPage("User Top")
				.addChild(new ManualPage("User A")
							.addChild(new ManualPage("User B"))
						 )
				, null);
		
		CFW.Registry.Manual.addManualPage(new ManualPage("User C"), "User Top | User A | User B");
		
		//---------------------------
		// Dump and Check
		
		String dump = CFW.Registry.Manual.dumpManualPageHierarchy();
		System.out.println(dump);
		
		Assertions.assertTrue(dump.contains("|      |--> C"), 
				"Item C is present.");
		
		Assertions.assertTrue(dump.contains("    |--> Sub Sub Item"), 
				"Sub Sub Item is present and on correct level.");
		
		Assertions.assertTrue(dump.contains("    |--> Sub Sub Item 2"), 
				"Sub Sub Item 2 is present and on correct level.");
		
		Assertions.assertTrue(dump.contains("|--> User C"), 
				"User C is present and on correct level.");

		
		//---------------------------
		// Create and Check Menu
		JsonArray pagesArray = CFW.Registry.Manual.getManualPagesForUserAsJSON();
		System.out.println("========= JSON =========\n"+pagesArray.toString());
		
	}
	
	


}
