package com.pengtoolbox.cfw.tests.db.user;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.User;
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
		if(userToDelete != null) {
			CFW.DB.Users.deleteByID(userToDelete.id());
		}
		
		userToDelete = CFW.DB.Users.selectByUsernameOrMail(usernameUpdated);
		if(userToDelete != null) {
			CFW.DB.Users.deleteByID(userToDelete.id());
		}
		
		Assertions.assertFalse(CFW.DB.Users.checkUsernameExists(username), "User doesn't exist, checkUsernameExists(string) works.");
		Assertions.assertFalse(CFW.DB.Users.checkUsernameExists(userToDelete), "User doesn't exist, checkUsernameExists(user) works.");
		
		//--------------------------------------
		// CREATE
		CFW.DB.Users.create(new User()
				.username(username)
				.email("t.testonia@cfw.com")
				.firstname("Testika")
				.lastname("Testonia")
				.passwordHash("hash")
				.passwordSalt("salt")
				.status("BLOCKED")
				.isDeletable(false)
				.isRenamable(false)
				.isForeign(true)
				);
		
		Assertions.assertTrue(CFW.DB.Users.checkUsernameExists(username), "User created successfully, checkUsernameExists(string) works.");
		
		//--------------------------------------
		// SELECT BY USERNAME
		User user = CFW.DB.Users.selectByUsernameOrMail(username);
		
		System.out.println("===== USER =====");
		System.out.println(user.getKeyValueString());

		Assertions.assertTrue(user != null);
		Assertions.assertTrue(user.username().equals(username));
		Assertions.assertTrue(user.email().equals("t.testonia@cfw.com"));
		Assertions.assertTrue(user.firstname().equals("Testika"));
		Assertions.assertTrue(user.lastname().equals("Testonia"));
		Assertions.assertTrue(user.passwordHash().equals("hash"));
		Assertions.assertTrue(user.passwordSalt().equals("salt"));
		Assertions.assertTrue(user.status().equals("BLOCKED"));
		Assertions.assertTrue(user.isDeletable() == false);
		Assertions.assertTrue(user.isRenamable() == false);
		Assertions.assertTrue(user.isForeign() == true);
		
		//--------------------------------------
		// CHECK NOT DELETABLE
		Assertions.assertFalse(CFW.DB.Users.deleteByID(user.id()), "The user is not deleted, returns false.");
		Assertions.assertTrue(CFW.DB.Users.checkUsernameExists(user.username()), "The user still exists.");
		
		//--------------------------------------
		// UPDATE
		user.username(usernameUpdated)
			.email("t.testonia2@cfw.com")
			.firstname("Testika2")
			.lastname("Testonia2")
			.passwordHash("hash2")
			.passwordSalt("salt2")
			.status("INACTIVE")
			.isDeletable(true)
			.isRenamable(true)
			.isForeign(false);
		
		CFW.DB.Users.update(user);
		
		//--------------------------------------
		// SELECT UPDATED USER
		User updatedUser = CFW.DB.Users.selectByUsernameOrMail(usernameUpdated);
		
		System.out.println("===== UPDATED USER =====");
		System.out.println(updatedUser.getKeyValueString());
		
		Assertions.assertTrue(CFW.DB.Users.checkUsernameExists(updatedUser), "User exists, checkUsernameExists(user) works.");
		Assertions.assertTrue(updatedUser != null);
		Assertions.assertTrue(updatedUser.username().equals(usernameUpdated));
		Assertions.assertTrue(updatedUser.email().equals("t.testonia2@cfw.com"));
		Assertions.assertTrue(updatedUser.firstname().equals("Testika2"));
		Assertions.assertTrue(updatedUser.lastname().equals("Testonia2"));
		Assertions.assertTrue(updatedUser.passwordHash().equals("hash2"));
		Assertions.assertTrue(updatedUser.passwordSalt().equals("salt2"));
		Assertions.assertTrue(updatedUser.status().equals("INACTIVE"));
		Assertions.assertTrue(updatedUser.isDeletable() == true);
		Assertions.assertTrue(updatedUser.isRenamable() == true);
		Assertions.assertTrue(updatedUser.isForeign() == false);

		
		//--------------------------------------
		// SELECT BY Mail
		Assertions.assertTrue(CFW.DB.Users.checkEmailExists(updatedUser), "Email exists, checkEmailExists(User) works.");
		Assertions.assertTrue(CFW.DB.Users.checkEmailExists("t.testonia2@cfw.com"), "Email exists, checkEmailExists(String) works.");
		
		User userbyMail = CFW.DB.Users.selectByUsernameOrMail("t.testonia2@cfw.com");
		
		Assertions.assertTrue( (userbyMail != null), "Select User by Mail works.");
		
		//--------------------------------------
		// SELECT BY ID

		User userbyID = CFW.DB.Users.selectByID(userbyMail.id());
		
		Assertions.assertTrue( (userbyID != null), "Select User by ID works.");
		
		
		//--------------------------------------
		// DELETE
		CFW.DB.Users.deleteByID(userbyMail.id());
		
		Assertions.assertFalse(CFW.DB.Users.checkUsernameExists(username));
		
	}
	
	@Test
	public void testCRUDGroup() {
		
		String groupname = "Test Group";
		String groupnameUpdated = "Test GroupUPDATED";
		
		//--------------------------------------
		// Cleanup
		Group groupToDelete = CFW.DB.Groups.selectByName(groupname);
		if(groupToDelete != null) {
			CFW.DB.Groups.deleteByID(groupToDelete.id());
		}
		
		groupToDelete = CFW.DB.Groups.selectByName(groupnameUpdated);
		if(groupToDelete != null) {
			CFW.DB.Groups.deleteByID(groupToDelete.id());
		}
		
		Assertions.assertFalse(CFW.DB.Groups.checkGroupExists(groupname), "Group doesn't exists, checkGroupExists(String) works.");
		Assertions.assertFalse(CFW.DB.Groups.checkGroupExists(groupToDelete), "Group doesn't exist, checkGroupExists(Group) works.");
		
		
		//--------------------------------------
		// CREATE
		CFW.DB.Groups.create(new Group()
				.name(groupname)
				.description("Testdescription")
				.isDeletable(false)
				);
		
		Assertions.assertTrue(CFW.DB.Groups.checkGroupExists(groupname), "Group created successfully, checkGroupExists(String) works.");

		//--------------------------------------
		// SELECT BY NAME
		Group group = CFW.DB.Groups.selectByName(groupname);
		
		System.out.println("===== USER =====");
		System.out.println(group.getKeyValueString());

		Assertions.assertTrue(CFW.DB.Groups.checkGroupExists(group), "Group created successfully, checkGroupExists(Group) works.");
		Assertions.assertTrue(group != null);
		Assertions.assertTrue(group.name().equals(groupname));
		Assertions.assertTrue(group.description().equals("Testdescription"));
		Assertions.assertTrue(group.isDeletable() == false);
		
		//--------------------------------------
		// CHECK NOT DELETABLE
		Assertions.assertFalse(CFW.DB.Groups.deleteByID(group.id()), "The group is not deleted, returns false.");
		Assertions.assertTrue(CFW.DB.Groups.checkGroupExists(group), "The group still exists.");
		
		//--------------------------------------
		// UPDATE
		group.name(groupnameUpdated)
			.description("Testdescription2")
			.isDeletable(true);
		
		CFW.DB.Groups.update(group);
		
		//--------------------------------------
		// SELECT UPDATED GROUP
		Group updatedGroup = CFW.DB.Groups.selectByName(groupnameUpdated);
		
		System.out.println("===== UPDATED GROUP =====");
		System.out.println(updatedGroup.getKeyValueString());
		
		Assertions.assertTrue(group != null);
		Assertions.assertTrue(group.name().equals(groupnameUpdated));
		Assertions.assertTrue(group.description().equals("Testdescription2"));
		Assertions.assertTrue(group.isDeletable() == true);
		
		//--------------------------------------
		// SELECT BY ID
		Group groupByID = CFW.DB.Groups.selectByID(updatedGroup.id());
		
		Assertions.assertTrue(groupByID != null, "Group is selected by ID.");
		//--------------------------------------
		// DELETE
		CFW.DB.Groups.deleteByID(updatedGroup.id());
		
		Assertions.assertFalse(CFW.DB.Groups.checkGroupExists(groupname));
		
	}
	
	@Test
	public void testCRUDUserGroupMap() {
		
		//--------------------------------------
		// Test checkIsUserInGroup()
		System.out.println(testuser);
		Assertions.assertTrue(CFW.DB.UserGroupMap.checkIsUserInGroup(testuser, testgroup), "checkIsUserInGroup() finds the testuser.");
		Assertions.assertFalse(CFW.DB.UserGroupMap.checkIsUserInGroup(98921, testgroup.id()), "checkIsUserInGroup() cannot find not existing user.");
	
		//--------------------------------------
		// Test  addUserToGroup()
		CFW.DB.Users.create(new User().username("newUser"));
		User newUser = CFW.DB.Users.selectByUsernameOrMail("newUser");
		CFW.DB.UserGroupMap.addUserToGroup(newUser, testgroup);
		
		Assertions.assertTrue(CFW.DB.UserGroupMap.checkIsUserInGroup(newUser, testgroup), "User was added to the group.");
		
		//--------------------------------------
		// Test  removeUserFromGroup
		CFW.DB.UserGroupMap.removeUserFromGroup(newUser, testgroup);
		
		Assertions.assertFalse(CFW.DB.UserGroupMap.checkIsUserInGroup(newUser, testgroup), "User was removed to the group.");
		
		
		
		
	}
}
