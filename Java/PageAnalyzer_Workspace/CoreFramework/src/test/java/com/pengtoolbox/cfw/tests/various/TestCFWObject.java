package com.pengtoolbox.cfw.tests.various;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;
import com.pengtoolbox.cfw.tests._master.DBTestMaster;

public class TestCFWObject extends DBTestMaster{
	protected static Group testgroupA;
	
	@BeforeClass
	public static void createTestData() {
		
		//------------------------------
		// Groups
		CFW.DB.Groups.create(new Group("TestgroupA"));
		testgroupA = CFW.DB.Groups.selectByName("TestgroupA");
		testgroupA.description("TestgroupADescription");
		
		CFW.DB.Groups.update(testgroupA);
	}
	
	@Test
	public void testMapResultSet() throws SQLException {
		

		String selectByName = 
				"SELECT "
				  + Group.GroupFields.PK_ID +", "
				  + Group.GroupFields.NAME +", "
				  + Group.GroupFields.DESCRIPTION +", "
				  + Group.GroupFields.IS_DELETABLE +" "
				+" FROM "+CFWDBGroup.TABLE_NAME
				+" WHERE "
				+ Group.GroupFields.NAME + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, "TestgroupA");
		
		Assertions.assertNotNull(result, "Result was found.");
		
		Group group = new Group(result);

		
	}
	
	
}
