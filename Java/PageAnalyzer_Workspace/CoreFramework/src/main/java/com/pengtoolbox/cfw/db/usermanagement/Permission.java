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
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.LengthValidator;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class Permission extends CFWObject{
	
	public static final String TABLE_NAME = "CFW_PERMISSION";
	public static final String CFW_API = "API";
	public static final String CFW_CONFIG_MANAGEMENT = "Configuration Management";
	public static final String CFW_USER_MANAGEMENT = "User Management";
	
	enum PermissionFields{
		PK_ID, 
		NAME,
		DESCRIPTION,
		IS_DELETABLE,
	}
	
	private static Logger logger = CFWLog.getLogger(Permission.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, PermissionFields.PK_ID.toString())
									.setPrimaryKeyAutoIncrement(this)
									.setDescription("The id of the permission.")
									.apiFieldType(FormFieldType.NUMBER)
									.setValue(-999);
	
	private CFWField<String> name = CFWField.newString(FormFieldType.TEXT, PermissionFields.NAME.toString())
									.setColumnDefinition("VARCHAR(255) UNIQUE")
									.setDescription("The name of the permission.")
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
	
	private CFWField<String> description = CFWField.newString(FormFieldType.TEXTAREA, PermissionFields.DESCRIPTION.toString())
											.setColumnDefinition("CLOB")
											.setDescription("The description of the permission.")
											.addValidator(new LengthValidator(-1, 2000000));
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.NONE, PermissionFields.IS_DELETABLE.toString())
											.setColumnDefinition("BOOLEAN")
											.setDescription("Flag to define if the permission can be deleted or not.")
											.setValue(true);

	public Permission() {
		initializeFields();
	}
	
	public Permission(String name) {
		initializeFields();
		this.name.setValue(name);
	}
	
	public Permission(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.setPrimaryField(id);
		this.addFields(id, name, description, isDeletable);
	}
	

	/**************************************************************************************
	 * Roles have to exists
	 **************************************************************************************/
	public void initDBSecond() {
		
		//-----------------------------------------
		//
		//-----------------------------------------
		if(!CFW.DB.Permissions.checkPermissionExists(Permission.CFW_USER_MANAGEMENT)) {
			CFW.DB.Permissions.create(new Permission(Permission.CFW_USER_MANAGEMENT)
				.description("Gives the user the ability to view, create, update and delete users.")
				.isDeletable(false)
			);
			
			Permission userManagement = CFW.DB.Permissions.selectByName(Permission.CFW_USER_MANAGEMENT);
			
			if(userManagement == null) {
				new CFWLog(logger)
				.method("createDefaultPermissions")
				.severe("User permission '"+Permission.CFW_USER_MANAGEMENT+"' was not found in the database.");
			}
		}
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Permissions.checkPermissionExists(Permission.CFW_CONFIG_MANAGEMENT)) {
			CFW.DB.Permissions.create(new Permission(Permission.CFW_CONFIG_MANAGEMENT)
				.description("Gives the user the ability to view and update the configurations in the database.")
				.isDeletable(false)
			);
			
			Permission userManagement = CFW.DB.Permissions.selectByName(Permission.CFW_CONFIG_MANAGEMENT);
			
			if(userManagement == null) {
				new CFWLog(logger)
				.method("createDefaultPermissions")
				.severe("User permission '"+Permission.CFW_CONFIG_MANAGEMENT+"' was not found in the database.");
			}
		}
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Permissions.checkPermissionExists(Permission.CFW_API)) {
			CFW.DB.Permissions.create(new Permission(Permission.CFW_API)
				.description("User can access the API.")
				.isDeletable(false)
			);
			
			Permission apiPermission = CFW.DB.Permissions.selectByName(Permission.CFW_API);
			
			if(apiPermission == null) {
				new CFWLog(logger)
				.method("createDefaultPermissions")
				.severe("User permission '"+Permission.CFW_API+"' was not found in the database.");
			}
		}
	}

	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
		
		String[] inputFields = 
				new String[] {
						PermissionFields.PK_ID.toString(), 
						PermissionFields.NAME.toString(),
				};
		
		String[] outputFields = 
				new String[] {
						PermissionFields.PK_ID.toString(), 
						PermissionFields.NAME.toString(),
						PermissionFields.DESCRIPTION.toString(),
						PermissionFields.IS_DELETABLE.toString(),	
				};

		//----------------------------------
		// fetchData
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
	
	public Permission id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public String name() {
		return name.getValue();
	}
	
	public Permission name(String name) {
		this.name.setValue(name);
		return this;
	}
	
	public String description() {
		return description.getValue();
	}

	public Permission description(String description) {
		this.description.setValue(description);
		return this;
	}

	public boolean isDeletable() {
		return isDeletable.getValue();
	}
	
	public Permission isDeletable(boolean isDeletable) {
		this.isDeletable.setValue(isDeletable);
		return this;
	}	
		


	
	
}
