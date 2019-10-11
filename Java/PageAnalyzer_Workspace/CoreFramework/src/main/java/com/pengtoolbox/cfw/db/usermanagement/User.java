package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWFieldChangeHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.EmailValidator;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class User extends CFWObject {
	
	private static Logger logger = CFWLog.getLogger(User.class.getName());
	
	public static String TABLE_NAME = "CFW_USER";
	
	public enum UserFields{
		PK_ID, 
		USERNAME,
		EMAIL, 
		FIRSTNAME, 
		LASTNAME, 
		PASSWORD_HASH,
		PASSWORD_SALT,
		DATE_CREATED, 
		STATUS, 
		IS_DELETABLE, 
		IS_RENAMABLE, 
		IS_FOREIGN
	}
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, UserFields.PK_ID)
								   .setPrimaryKeyAutoIncrement(this)
								   .setValue(-999);
	
	private CFWField<String> username = CFWField.newString(FormFieldType.TEXT, UserFields.USERNAME)
			.setColumnDefinition("VARCHAR(255) UNIQUE")
			.addValidator(new LengthValidator(1, 255))
			.setChangeHandler(new CFWFieldChangeHandler<String>() {
				public boolean handle(String oldValue, String newValue) {
					if(username.isDisabled()) { 
						new CFWLog(logger)
						.method("handle")
						.severe("The username cannot be changed as the field is disabled.");
						return false; 
					}
					
					if( oldValue != null && !oldValue.equals(newValue)) {
						hasUsernameChanged = true;
					}
					return true;
				}
			});
	
	private CFWField<String> email = CFWField.newString(FormFieldType.EMAIL, UserFields.EMAIL)
			.setColumnDefinition("VARCHAR(255) UNIQUE")
			.addValidator(new LengthValidator(-1, 255))
			.addValidator(new EmailValidator());

	private CFWField<String> firstname = CFWField.newString(FormFieldType.TEXT, UserFields.FIRSTNAME)
			.setColumnDefinition("VARCHAR(255)")
			.addValidator(new LengthValidator(-1, 255));
	
	private CFWField<String> lastname = CFWField.newString(FormFieldType.TEXT, UserFields.LASTNAME)
			.setColumnDefinition("VARCHAR(255)")
			.addValidator(new LengthValidator(-1, 255));
	
	private CFWField<String> passwordHash = CFWField.newString(FormFieldType.NONE, UserFields.PASSWORD_HASH)
			.setColumnDefinition("VARCHAR(127)")
			.addValidator(new LengthValidator(-1, 255));
	
	private CFWField<String> passwordSalt = CFWField.newString(FormFieldType.NONE, UserFields.PASSWORD_SALT)
			.setColumnDefinition("VARCHAR(31)")
			.addValidator(new LengthValidator(-1, 255));
	
	private CFWField<String> status = CFWField.newString(FormFieldType.SELECT, UserFields.STATUS)
			.setColumnDefinition("VARCHAR(15)")
			.setOptions(new String[] {"Active", "Inactive"})
			.setDescription("Active users can login, inactive users are prohibited to login.")
			.addValidator(new LengthValidator(-1, 15))
			.setValue("Active");
				
	private CFWField<Timestamp> dateCreated = CFWField.newTimestamp(FormFieldType.NONE, UserFields.DATE_CREATED)
			.setValue(new Timestamp(new Date().getTime()));
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.NONE, UserFields.IS_DELETABLE)
			.setValue(true)
			.setChangeHandler(new CFWFieldChangeHandler<Boolean>() {
				@Override
				public boolean handle(Boolean oldValue, Boolean newValue) {
					if(newValue) {
						status.isDisabled(false);
					}else {
						status.isDisabled(true);
					}
					
					return true;
				}
			});;;
												

	private CFWField<Boolean> isRenamable = CFWField.newBoolean(FormFieldType.NONE, UserFields.IS_RENAMABLE)
			.setValue(true)
			.setChangeHandler(new CFWFieldChangeHandler<Boolean>() {
				
				@Override
				public boolean handle(Boolean oldValue, Boolean newValue) {
					if(newValue) {
						username.isDisabled(false);
					}else {
						username.isDisabled(true);
					}
					
					return true;
				}
			});;
	
	//Username and password is managed in another source, like LDAP or CSV
	private CFWField<Boolean> isForeign = CFWField.newBoolean(FormFieldType.BOOLEAN, UserFields.IS_FOREIGN)
					.setDescription("Foreign users are managed by other authentication providers like LDAP. Password in database is ignored.")
					.setValue(false);
	
	private boolean hasUsernameChanged = false;
	
	public User() {
		initializeFields();
	}
	
	public User(String username) {
		initializeFields();
		this.username.setValue(username);
	}
	
	public User(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);

	}
		
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, 
				username, 
				email, 
				firstname, 
				lastname, 
				passwordHash,
				passwordSalt,
				dateCreated,
				status,
				isDeletable,
				isRenamable,
				isForeign
				);
	}
	
	public void initDBSecond() {
		
		//-----------------------------------------
		// Create anonymous user 
		//-----------------------------------------
		if(!CFW.Properties.AUTHENTICATION_ENABLED) {
			String initialPassword = CFW.Encryption.createPasswordSalt(32);
			if(!CFW.DB.Users.checkUsernameExists("anonymous")) {
			    CFW.DB.Users.create(
					new User("anonymous")
					.setInitialPassword(initialPassword, initialPassword)
					.isDeletable(true)
					.isRenamable(false)
					.status("ACTIVE")
					.isForeign(false)
				);
			}
		
			User anonUser = CFW.DB.Users.selectByUsernameOrMail("anonymous");
			
			if(anonUser == null) {
				new CFWLog(logger)
				.method("createDefaultUsers")
				.severe("User 'anonymous' was not found in the database.");
			}
		}
		//-----------------------------------------
		// Create default admin user
		//-----------------------------------------
		
		if(!CFW.DB.Users.checkUsernameExists("admin")) {
			
		    CFW.DB.Users.create(
				new User("admin")
				.isDeletable(false)
				.isRenamable(false)
				.setInitialPassword("admin", "admin")
				.status("ACTIVE")
				.isForeign(false)
			);
		}
		
		User adminUser = CFW.DB.Users.selectByUsernameOrMail("admin");
		
		if(adminUser == null) {
			new CFWLog(logger)
			.method("createDefaultUsers")
			.severe("User 'admin' was not found in the database.");
		}
		
		
		
		//-----------------------------------------
		// Add Admin to group Superuser
		//-----------------------------------------
		Group superuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_SUPERUSER);
		
		if(!CFW.DB.UserGroupMap.checkIsUserInGroup(adminUser, superuserGroup)) {
			CFW.DB.UserGroupMap.addUserToGroup(adminUser, superuserGroup, false);
		}
		
		//Needed for Upgrade
		CFW.DB.UserGroupMap.updateIsDeletable(adminUser.id(), superuserGroup.id(), false);

		if(!CFW.DB.UserGroupMap.checkIsUserInGroup(adminUser, superuserGroup)) {
			new CFWLog(logger)
			.method("createDefaultUsers")
			.severe("User 'admin' is not assigned to group 'Superuser'.");
		}
		
		//-----------------------------------------
		// Upgrade Step: Superuser permissions undeletable
		//-----------------------------------------
		HashMap<String, Permission> permissions = CFW.DB.GroupPermissionMap.selectPermissionsForGroup(superuserGroup);
		
		for(Permission p : permissions.values()) {
			CFW.DB.GroupPermissionMap.updateIsDeletable(p.id(), superuserGroup.id(), false);
		}
		
	}
	
	public int id() {
		return id.getValue();
	}
	
	public User id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public String username() {
		return username.getValue();
	}
	
	public User username(String username) {
		this.username.setValue(username);
		return this;
	}
	
	public String email() {
		return email.getValue();
	}
	
	public User email(String email) {
		this.email.setValue(email);
		return this;
	}
	
	public String firstname() {
		return firstname.getValue();
	}
	
	public User firstname(String firstname) {
		this.firstname.setValue(firstname);
		return this;
	}
	
	public String lastname() {
		return lastname.getValue();
	}
	
	public User lastname(String lastname) {
		this.lastname.setValue(lastname);
		return this;
	}
	
	
	public User setInitialPassword(String password, String repeatedPassword) {
		
		if(!password.equals(repeatedPassword)) {
			new CFWLog(logger)
			.method("setInitialPassword")
			.severe("The two provided passwords are not equal.");
			return null;
		}
		
		this.passwordSalt(CFW.Encryption.createPasswordSalt(31));
		this.passwordHash(CFW.Encryption.createPasswordHash(password, this.passwordSalt()) );
		
		return this;
	}
	
	public boolean changePassword(String oldPassword, String password, String repeatedPassword) {
		
		if(!passwordValidation(oldPassword)) {
			new CFWLog(logger)
			.method("changePassword")
			.severe("The provided old password was wrong.");
			return false;
		}
		
		if(!password.equals(repeatedPassword)) {
			new CFWLog(logger)
			.method("changePassword")
			.severe("The two provided passwords are not equal.");
			return false;
		}else {
			this.passwordSalt(CFW.Encryption.createPasswordSalt(31));
			this.passwordHash(CFW.Encryption.createPasswordHash(password, this.passwordSalt()) );
			
			return true;
		}
	}
	
	/**************************************************************************
	 * Validate if the correct password for the user account was provided.
	 * @param password
	 * @return true if correct password, false otherwise
	 **************************************************************************/
	public boolean passwordValidation(String providedPassword) {
		String providedPasswordHash = CFW.Encryption.createPasswordHash(providedPassword, this.passwordSalt());
		return (providedPasswordHash.equals(this.passwordHash()));
	}
		
	
	public String passwordHash() {
		return passwordHash.getValue();
	}

	public User passwordHash(String passwordHash) {
		this.passwordHash.setValue(passwordHash);
		return this;
	}

	public String passwordSalt() {
		return passwordSalt.getValue();
	}

	public User passwordSalt(String passwordSalt) {
		this.passwordSalt.setValue(passwordSalt);
		return this;
	}

	public Timestamp dateCreated() {
		return dateCreated.getValue();
	}
	
	public User dateCreated(Timestamp creationDate) {
		this.dateCreated.setValue(creationDate);
		return this;
	}
	
	public boolean isDeletable() {
		return isDeletable.getValue();
	}
	
	public User isDeletable(boolean isDeletable) {
		this.isDeletable.setValue(isDeletable);
		return this;
	}
	
	public String status() {
		return status.getValue();
	}
		
	public User status(String status) {
		this.status.setValue(status);
		return this;
	}
	
	public boolean isRenamable() {
		return isRenamable.getValue();
	}
	
	public User isRenamable(boolean isRenamable) {
		this.isRenamable.setValue(isRenamable);
		return this;
	}
	
	public boolean hasUsernameChanged() {
		return hasUsernameChanged;
	}
	
	public boolean isForeign() {
		return isForeign.getValue();
	}
	
	public User isForeign(boolean isForeign) {
		this.isForeign.setValue(isForeign);
		return this;
	}
	
}
