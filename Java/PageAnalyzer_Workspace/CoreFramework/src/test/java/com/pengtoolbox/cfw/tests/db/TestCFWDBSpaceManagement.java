package com.pengtoolbox.cfw.tests.db;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.db.spaces.SpaceGroup;
import com.pengtoolbox.cfw.tests._master.DBTestMaster;

public class TestCFWDBSpaceManagement extends DBTestMaster {

	
	@BeforeClass
	public static void fillWithTestData() {
		
		
	}

	@Test
	public void testCRUDSpaceGroup() {
		
		String spacegroupname = "Test Spacegroup";
		String spacegroupnameUpdated = "Test SpacegroupUPDATED";
		
		//--------------------------------------
		// Cleanup
		SpaceGroup spacegroupToDelete = CFW.DB.SpaceGroups.selectByName(spacegroupname);
		if(spacegroupToDelete != null) {
			CFW.DB.SpaceGroups.deleteByID(spacegroupToDelete.id());
		}

		spacegroupToDelete = CFW.DB.SpaceGroups.selectByName(spacegroupnameUpdated);
		if(spacegroupToDelete != null) {
			CFW.DB.SpaceGroups.deleteByID(spacegroupToDelete.id());
		}
		Assertions.assertFalse(CFW.DB.SpaceGroups.checkSpaceGroupExists(spacegroupname), "Config doesn't exists, checkConfigExists(String) works.");
		Assertions.assertFalse(CFW.DB.SpaceGroups.checkSpaceGroupExists(spacegroupToDelete), "Config doesn't exist, checkConfigExists(Config) works.");
		
		
		//--------------------------------------
		// CREATE
		CFW.DB.SpaceGroups.create(
				new SpaceGroup(spacegroupname)
				.description("Testdescription")
		);
		
		Assertions.assertTrue(CFW.DB.SpaceGroups.checkSpaceGroupExists(spacegroupname), "Config created successfully, checkConfigExists(String) works.");

		//--------------------------------------
		// SELECT BY NAME
		SpaceGroup spacegroup = CFW.DB.SpaceGroups.selectByName(spacegroupname);
		
		System.out.println("===== CONFIG =====");
		System.out.println(spacegroup.getFieldsAsKeyValueString());

		Assertions.assertTrue(spacegroup != null);
		Assertions.assertTrue(spacegroup.name().equals(spacegroupname));
		Assertions.assertTrue(spacegroup.description().equals("Testdescription"));
		
		//--------------------------------------
		// UPDATE
		spacegroup.name(spacegroupnameUpdated)
			.description("Testdescription2");
		
		CFW.DB.SpaceGroups.update(spacegroup);
		
		//--------------------------------------
		// SELECT UPDATED CONFIG
		SpaceGroup updatedConfig = CFW.DB.SpaceGroups.selectByName(spacegroupnameUpdated);
		
		System.out.println("===== UPDATED CONFIG =====");
		System.out.println(updatedConfig.getFieldsAsKeyValueString());
		
		Assertions.assertTrue(spacegroup != null);
		Assertions.assertTrue(spacegroup.name().equals(spacegroupnameUpdated));
		Assertions.assertTrue(spacegroup.description().equals("Testdescription2"));
		
		//--------------------------------------
		// SELECT BY ID
		SpaceGroup spacegroupByID = CFW.DB.SpaceGroups.selectByID(updatedConfig.id());
		
		Assertions.assertTrue(spacegroupByID != null, "Config is selected by ID.");
		
		//--------------------------------------
		// DELETE
		CFW.DB.SpaceGroups.deleteByID(updatedConfig.id());
		Assertions.assertFalse(CFW.DB.SpaceGroups.checkSpaceGroupExists(spacegroupname));
				
	}
	
}
