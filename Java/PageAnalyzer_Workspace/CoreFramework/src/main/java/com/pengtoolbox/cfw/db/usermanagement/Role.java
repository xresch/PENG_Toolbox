package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.api.APIDefinitionFetch;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWFieldChangeHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.LengthValidator;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class Role extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_ROLE";
	
	public enum RoleFields{
		PK_ID,
		NAME,
		DESCRIPTION,
		IS_DELETABLE,
		IS_RENAMABLE,
	}

	private static Logger logger = CFWLog.getLogger(Role.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, RoleFields.PK_ID.toString())
			.setPrimaryKeyAutoIncrement(this)
			.setDescription("The id of the role.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<String> name = CFWField.newString(FormFieldType.TEXT, RoleFields.NAME.toString())
			.setColumnDefinition("VARCHAR(255) UNIQUE")
			.setDescription("The name of the role.")
			.addValidator(new LengthValidator(1, 255))
			.setChangeHandler(new CFWFieldChangeHandler<String>() {
				public boolean handle(String oldValue, String newValue) {
					if(name.isDisabled()) { 
						new CFWLog(logger)
						.method("handle")
						.severe("The name cannot be changed as the field is disabled.");
						return false; 
					}
					return true;
				}
			});
	
	private CFWField<String> description = CFWField.newString(FormFieldType.TEXTAREA, RoleFields.DESCRIPTION.toString())
			.setColumnDefinition("CLOB")
			.setDescription("The description of the role.")
			.addValidator(new LengthValidator(-1, 2000000));
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.NONE, RoleFields.IS_DELETABLE.toString())
			.setDescription("Flag to define if the role can be deleted or not.")
			.setColumnDefinition("BOOLEAN")
			.setValue(true);
	
	private CFWField<Boolean> isRenamable = CFWField.newBoolean(FormFieldType.NONE, RoleFields.IS_RENAMABLE.toString())
			.setColumnDefinition("BOOLEAN DEFAULT TRUE")
			.setDescription("Flag to define if the role can be renamed or not.")
			.setValue(true)
			.setChangeHandler(new CFWFieldChangeHandler<Boolean>() {
				
				@Override
				public boolean handle(Boolean oldValue, Boolean newValue) {
					if(!newValue) {
						name.isDisabled(true);
					}else {
						name.isDisabled(false);
					}
					
					return true;
				}
			});;
	
	public Role() {
		initializeFields();
	}
	
	public Role(String name) {
		initializeFields();
		this.name.setValue(name);
	}
	
	public Role(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, name, description, isDeletable, isRenamable);
	}
	
	/**************************************************************************************
	 * Migrate Table
	 **************************************************************************************/
	public void migrateTable() {
		
		//---------------------------
		// Rename Table
		String renameTable = "ALTER TABLE IF EXISTS CFW_GROUP RENAME TO "+this.getTableName();
		CFWDB.preparedExecute(renameTable);
		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public void initDB() {
		//-----------------------------------------
		// Create Role Superuser
		//-----------------------------------------
		if(!CFW.DB.Roles.checkRoleExists(CFWDBRole.CFW_ROLE_SUPERUSER)) {
			CFW.DB.Roles.create(new Role(CFWDBRole.CFW_ROLE_SUPERUSER)
				.description("Superusers have all the privileges in the system. They are above administrators. ")
				.isDeletable(false)
			);
		}
		
		Role superuserRole = CFW.DB.Roles.selectByName(CFWDBRole.CFW_ROLE_SUPERUSER);
		
		if(superuserRole == null) {
			new CFWLog(logger)
			.method("createDefaultRoles")
			.severe("User role '"+CFWDBRole.CFW_ROLE_SUPERUSER+"' was not found in the database.");
		}
		
		superuserRole.isRenamable(false);
		CFW.DB.Roles.update(superuserRole);
		
		//-----------------------------------------
		// Create Role Admin
		//-----------------------------------------
		if(!CFW.DB.Roles.checkRoleExists(CFWDBRole.CFW_ROLE_ADMIN)) {
			CFW.DB.Roles.create(new Role(CFWDBRole.CFW_ROLE_ADMIN)
				.description("Administrators have the privileges to manage the application.")
				.isDeletable(false)
			);
		}
		
		Role adminRole = CFW.DB.Roles.selectByName(CFWDBRole.CFW_ROLE_ADMIN);
		
		if(adminRole == null) {
			new CFWLog(logger)
			.method("createDefaultRoles")
			.severe("User role '"+CFWDBRole.CFW_ROLE_ADMIN+"' was not found in the database.");
		}
		
		adminRole.isRenamable(false);
		CFW.DB.Roles.update(adminRole);
		//-----------------------------------------
		// Create User
		//-----------------------------------------
		if(!CFW.DB.Roles.checkRoleExists(CFWDBRole.CFW_ROLE_USER)) {
			CFW.DB.Roles.create(new Role(CFWDBRole.CFW_ROLE_USER)
				.description("Default User role. New users will automatically be added to this role if they are not managed by a foreign source.")
				.isDeletable(false)
			);
		}
		
		Role userRole = CFW.DB.Roles.selectByName(CFWDBRole.CFW_ROLE_USER);
		
		if(userRole == null) {
			new CFWLog(logger)
			.method("createDefaultRoles")
			.severe("User role '"+CFWDBRole.CFW_ROLE_USER+"' was not found in the database.");
		}
		
		userRole.isRenamable(false);
		CFW.DB.Roles.update(userRole);
		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
		
		
		String[] inputFields = 
				new String[] {
						RoleFields.PK_ID.toString(), 
						RoleFields.NAME.toString(),
				};
		
		String[] outputFields = 
				new String[] {
						RoleFields.PK_ID.toString(), 
						RoleFields.NAME.toString(),
						RoleFields.DESCRIPTION.toString(),
						RoleFields.IS_DELETABLE.toString(),
						RoleFields.IS_RENAMABLE.toString(),		
				};

		//----------------------------------
		// fetchJSON
		APIDefinitionFetch fetchDataAPI = 
				new APIDefinitionFetch(
						this.getClass(),
						this.getClass().getSimpleName(),
						"fetchData",
						inputFields,
						outputFields
				);
		
		apis.add(fetchDataAPI);
		
		return apis;
	}

	public int id() {
		return id.getValue();
	}
	
	public Role id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public String name() {
		return name.getValue();
	}
	
	public Role name(String name) {
		this.name.setValue(name);
		return this;
	}
	
	public String description() {
		return description.getValue();
	}

	public Role description(String description) {
		this.description.setValue(description);
		return this;
	}

	public boolean isDeletable() {
		return isDeletable.getValue();
	}
	
	public Role isDeletable(boolean isDeletable) {
		this.isDeletable.setValue(isDeletable);
		return this;
	}	
	
	public boolean isRenamable() {
		return isRenamable.getValue();
	}
	
	public Role isRenamable(boolean isRenamable) {
		this.isRenamable.setValue(isRenamable);
		return this;
	}	
	
}
