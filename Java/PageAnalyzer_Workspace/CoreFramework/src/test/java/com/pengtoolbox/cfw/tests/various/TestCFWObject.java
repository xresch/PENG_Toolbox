package com.pengtoolbox.cfw.tests.various;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBRole;
import com.pengtoolbox.cfw.db.usermanagement.Role;
import com.pengtoolbox.cfw.db.usermanagement.Role.RoleFields;
import com.pengtoolbox.cfw.tests._master.DBTestMaster;

public class TestCFWObject extends DBTestMaster{
	protected static Role testgroupA;
	
	@BeforeClass
	public static void createTestData() {
		
		//------------------------------
		// Groups
		CFW.DB.Roles.create(new Role("TestgroupA"));
		testgroupA = CFW.DB.Roles.selectByName("TestgroupA");
		testgroupA.description("TestgroupADescription");
		
		CFW.DB.Roles.update(testgroupA);
	}
	
	@Test
	public void testMapResultSet() throws SQLException {
		

		String selectByName = 
				"SELECT "
				  + Role.RoleFields.PK_ID +", "
				  + Role.RoleFields.NAME +", "
				  + Role.RoleFields.DESCRIPTION +", "
				  + Role.RoleFields.IS_DELETABLE +" "
				+" FROM "+Role.TABLE_NAME
				+" WHERE "
				+ Role.RoleFields.NAME + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, "TestgroupA");
		
		Assertions.assertNotNull(result, "Result was found.");
		
		Role group = new Role(result);

		
	}
	
	
}
