package com.pengtoolbox.cfw.tests.db.user;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.db.usermanagement.User.UserStatus;
import com.pengtoolbox.cfw.tests._master.DBTestMaster;
import com.pengtoolbox.cfw.utils.CFWSecurity;

public class TestCFWDBUserManagement extends DBTestMaster {

	@Test
	public void testCreatePasswordHash() {
		
		String salt = CFW.Security.createPasswordSalt(31);
		String hashtext = CFWSecurity.createPasswordHash("admin", salt);
		
		System.out.println("Salt: "+salt);
        System.out.println("Hashtext: "+hashtext);

        Assertions.assertTrue(salt.length() == 31);
        Assertions.assertTrue(hashtext.length() == 127);
        
	}
	
	@Test
	public void testCRUDUser() {
		
		String username = "t.testonia";
		String usernameUpdated = "t.testonia2";
		
		//--------------------------------------
		// Cleanup
		User userToDelete = CFW.DB.Users.selectByUsernameOrMail(username);
		CFW.DB.Users.deleteByID(userToDelete.id());
		
		userToDelete = CFW.DB.Users.selectByUsernameOrMail(usernameUpdated);
		CFW.DB.Users.deleteByID(userToDelete.id());
		
		Assertions.assertFalse(CFW.DB.Users.isUsernameUsed(username), "DB is cleaned up.");
		Assertions.assertFalse(CFW.DB.Users.isUsernameUsed(usernameUpdated), "DB is cleaned up.");
		
		
		//--------------------------------------
		// CREATE
		CFW.DB.Users.create(new User()
				.username(username)
				.email("t.testonia@cfw.com")
				.firstname("Testika")
				.lastname("Testonia")
				.passwordHash("hash")
				.passwordSalt("salt")
				.status(UserStatus.BLOCKED)
				.isDeletable(false)
				.isRenamable(false)
				.isForeign(true)
				);
		
		Assertions.assertTrue(CFW.DB.Users.isUsernameUsed(username));
		
		//--------------------------------------
		// SELECT
		User user = CFW.DB.Users.selectByUsernameOrMail(username);
		
		System.out.println("===== USER =====");
		System.out.println(user.toString());

		Assertions.assertTrue(user != null);
		Assertions.assertTrue(user.username().equals(username));
		Assertions.assertTrue(user.email().equals("t.testonia@cfw.com"));
		Assertions.assertTrue(user.firstname().equals("Testika"));
		Assertions.assertTrue(user.lastname().equals("Testonia"));
		Assertions.assertTrue(user.passwordHash().equals("hash"));
		Assertions.assertTrue(user.passwordSalt().equals("salt"));
		Assertions.assertTrue(user.status().equals(UserStatus.BLOCKED.toString()));
		Assertions.assertTrue(user.isDeletable() == false);
		Assertions.assertTrue(user.isRenamable() == false);
		Assertions.assertTrue(user.isForeign() == true);
		
		//--------------------------------------
		// UPDATE
		user.username(usernameUpdated)
			.email("t.testonia2@cfw.com")
			.firstname("Testika2")
			.lastname("Testonia2")
			.passwordHash("hash2")
			.passwordSalt("salt2")
			.status(UserStatus.INACTIVE)
			.isDeletable(true)
			.isRenamable(true)
			.isForeign(false);
		
		CFW.DB.Users.update(user);
		
		//--------------------------------------
		// SELECT
		User userUpdated = CFW.DB.Users.selectByUsernameOrMail(usernameUpdated);
		
		System.out.println("===== UPDATED USER =====");
		System.out.println(userUpdated.toString());
		
		Assertions.assertTrue(userUpdated != null);
		Assertions.assertTrue(userUpdated.username().equals(usernameUpdated));
		Assertions.assertTrue(userUpdated.email().equals("t.testonia2@cfw.com"));
		Assertions.assertTrue(userUpdated.firstname().equals("Testika2"));
		Assertions.assertTrue(userUpdated.lastname().equals("Testonia2"));
		Assertions.assertTrue(userUpdated.passwordHash().equals("hash2"));
		Assertions.assertTrue(userUpdated.passwordSalt().equals("salt2"));
		Assertions.assertTrue(userUpdated.status().equals(UserStatus.INACTIVE.toString()));
		Assertions.assertTrue(userUpdated.isDeletable() == true);
		Assertions.assertTrue(userUpdated.isRenamable() == true);
		Assertions.assertTrue(userUpdated.isForeign() == false);

		//--------------------------------------
		// SELECT
		User userbyMail = CFW.DB.Users.selectByUsernameOrMail("t.testonia2@cfw.com");
		
		Assertions.assertTrue( (userbyMail != null), "Select User by Mail works.");
		
		
		//--------------------------------------
		// DELETE
		CFW.DB.Users.deleteByID(userbyMail.id());
		
		Assertions.assertFalse(CFW.DB.Users.isUsernameUsed(username));
		
	}
	
	@Test
	public void testCRUDGroup() {
		
		String groupname = "Test Group";
		String groupnameUpdated = "Test GroupUPDATED";
		
		//--------------------------------------
		// Cleanup
		Group groupToDelete = CFW.DB.Groups.selectByName(groupname);
		CFW.DB.Groups.deleteByID(groupToDelete.id());
		
		groupToDelete = CFW.DB.Groups.selectByName(groupnameUpdated);
		CFW.DB.Groups.deleteByID(groupToDelete.id());
		
		Assertions.assertFalse(CFW.DB.Groups.groupExists(groupname), "DB is cleaned up.");
		Assertions.assertFalse(CFW.DB.Groups.groupExists(groupnameUpdated), "DB is cleaned up.");
		
		
		//--------------------------------------
		// CREATE
		CFW.DB.Groups.create(new Group()
				.name(groupname)
				.description("Testdescription")
				.isDeletable(false)
				);
		
		Assertions.assertTrue(CFW.DB.Groups.groupExists(groupname));
		
		//--------------------------------------
		// SELECT
		Group group = CFW.DB.Groups.selectByName(groupname);
		
		System.out.println("===== USER =====");
		System.out.println(group.toString());

		Assertions.assertTrue(group != null);
		Assertions.assertTrue(group.name().equals(groupname));
		Assertions.assertTrue(group.description().equals("Testdescription"));
		Assertions.assertTrue(group.isDeletable() == false);
		
		//--------------------------------------
		// UPDATE
		group.name(groupnameUpdated)
			.description("Testdescription2")
			.isDeletable(true);
		
		CFW.DB.Groups.update(group);
		
		//--------------------------------------
		// SELECT
		Group userUpdated = CFW.DB.Groups.selectByName(groupnameUpdated);
		
		System.out.println("===== UPDATED GROUP =====");
		System.out.println(userUpdated.toString());
		
		Assertions.assertTrue(group != null);
		Assertions.assertTrue(group.name().equals(groupname));
		Assertions.assertTrue(group.description().equals("Testdescription"));
		Assertions.assertTrue(group.isDeletable() == true);
		
		
		//--------------------------------------
		// DELETE
		CFW.DB.Groups.deleteByID(userUpdated.id());
		
		Assertions.assertFalse(CFW.DB.Groups.groupExists(groupname));
		
	}
}
