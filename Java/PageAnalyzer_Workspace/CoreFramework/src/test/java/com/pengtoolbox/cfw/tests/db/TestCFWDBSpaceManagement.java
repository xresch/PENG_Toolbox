package com.pengtoolbox.cfw.tests.db;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWHierarchy;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.spaces.Space;
import com.pengtoolbox.cfw.db.spaces.Space.SpaceFields;
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
	
	@Test
	public void testSpaceHierarchy() {
		

		int spacegroupid = CFW.DB.SpaceGroups.selectByName(SpaceGroup.CFW_SPACEGROUP_TESTSPACE).id();
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Spaces.checkSpaceExists("MySpace")) {
			CFW.DB.Spaces.create(
					new Space(spacegroupid, "MySpace")
						.description("A space for spacing away.")
						.isDeletable(true)
						.isRenamable(true)
			);
		}
		
		Space parentSpace = CFW.DB.Spaces.selectByName("MySpace");
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		for(int i = 0; i < 10; i++) {
			String spacename = "SubSpace"+i;
			if(!CFW.DB.Spaces.checkSpaceExists(spacename)) {
				
				Space subSpace = new Space(spacegroupid, spacename)
					.description("A sub space for spacing away.")
					.isDeletable(true)
					.isRenamable(true);
				
				if(subSpace.setParent(parentSpace)) {
					CFW.DB.Spaces.create(subSpace);
					parentSpace = CFW.DB.Spaces.selectByName(spacename);
					System.out.println(parentSpace.getFieldsAsKeyValueString());
				}
			}
		}
		//-----------------------------------------
		// All subelements of MySpace including MySpace
		//-----------------------------------------
		parentSpace = CFW.DB.Spaces.selectByName("MySpace");
		String csv = new CFWHierarchy(parentSpace)
						.createFetchHierarchyQuery(
								new String[] {
										SpaceFields.PK_ID.toString(),
										SpaceFields.NAME.toString(),
								})
						.getAsCSV();
		
		System.out.println("============= HIERARCHY RESULTS =============");
		System.out.println(csv);
		Assertions.assertTrue(csv.contains("MySpace"), "Root element is in list.");
		Assertions.assertTrue(csv.contains("SubSpace9"), "Last subelement is in list.");
		
		//-----------------------------------------
		// All subelements of SubSpace6 including SubSpace6
		//-----------------------------------------
		parentSpace = CFW.DB.Spaces.selectByName("SubSpace6");
		csv = new CFWHierarchy(parentSpace).createFetchHierarchyQuery(
				new String[] {
						SpaceFields.PK_ID.toString(),
						SpaceFields.NAME.toString(),
						})
				.getAsCSV();
	
		System.out.println("============= HIERARCHY RESULTS =============");
		System.out.println(csv);
		Assertions.assertTrue(csv.contains("SubSpace6"), "List contains selected start element is in list.");
		Assertions.assertTrue(csv.contains("SubSpace9"), "Last subelement is in list.");
		Assertions.assertTrue(!csv.contains("MySpace"), "Root element is NOT in list.");
		Assertions.assertTrue(!csv.contains("SubSpace5"), "Element before start element is NOT in list.");
		
	}
	
}
