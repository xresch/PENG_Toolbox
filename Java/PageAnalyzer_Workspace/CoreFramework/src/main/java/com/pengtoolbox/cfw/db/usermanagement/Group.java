package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.api.APIDefinitionFetch;
import com.pengtoolbox.cfw.api.ReturnFormat;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWFieldChangeHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class Group extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_GROUP";
	
	public enum GroupFields{
		PK_ID,
		NAME,
		DESCRIPTION,
		IS_DELETABLE,
		IS_RENAMABLE,
	}

	private static Logger logger = CFWLog.getLogger(Group.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, GroupFields.PK_ID.toString())
			.setPrimaryKeyAutoIncrement(this)
			.setDescription("The id of the group.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<String> name = CFWField.newString(FormFieldType.TEXT, GroupFields.NAME.toString())
			.setColumnDefinition("VARCHAR(255) UNIQUE")
			.setDescription("The name of the group.")
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
	
	private CFWField<String> description = CFWField.newString(FormFieldType.TEXTAREA, GroupFields.DESCRIPTION.toString())
			.setColumnDefinition("CLOB")
			.setDescription("The description of the group.")
			.addValidator(new LengthValidator(-1, 2000000));
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.NONE, GroupFields.IS_DELETABLE.toString())
			.setDescription("Flag to define if the group can be deleted or not.")
			.setColumnDefinition("BOOLEAN")
			.setValue(true);
	
	private CFWField<Boolean> isRenamable = CFWField.newBoolean(FormFieldType.NONE, GroupFields.IS_RENAMABLE.toString())
			.setColumnDefinition("BOOLEAN DEFAULT TRUE")
			.setDescription("Flag to define if the group can be renamed or not.")
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
	
	public Group() {
		initializeFields();
	}
	
	public Group(String name) {
		initializeFields();
		this.name.setValue(name);
	}
	
	public Group(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, name, description, isDeletable, isRenamable);
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public void initDB() {
		//-----------------------------------------
		// Create Group Superuser
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.CFW_GROUP_SUPERUSER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.CFW_GROUP_SUPERUSER)
				.description("Superusers have all the privileges in the system. They are above administrators. ")
				.isDeletable(false)
			);
		}
		
		Group superuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_SUPERUSER);
		
		if(superuserGroup == null) {
			new CFWLog(logger)
			.method("createDefaultGroups")
			.severe("User group '"+CFWDBGroup.CFW_GROUP_SUPERUSER+"' was not found in the database.");
		}
		
		superuserGroup.isRenamable(false);
		CFW.DB.Groups.update(superuserGroup);
		
		//-----------------------------------------
		// Create Group Admin
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.CFW_GROUP_ADMIN)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.CFW_GROUP_ADMIN)
				.description("Administrators have the privileges to manage the application.")
				.isDeletable(false)
			);
		}
		
		Group adminGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_ADMIN);
		
		if(adminGroup == null) {
			new CFWLog(logger)
			.method("createDefaultGroups")
			.severe("User group '"+CFWDBGroup.CFW_GROUP_ADMIN+"' was not found in the database.");
		}
		
		adminGroup.isRenamable(false);
		CFW.DB.Groups.update(adminGroup);
		//-----------------------------------------
		// Create User
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.CFW_GROUP_USER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.CFW_GROUP_USER)
				.description("Default User group. New users will automatically be added to this group if they are not managed by a foreign source.")
				.isDeletable(false)
			);
		}
		
		Group userGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_USER);
		
		if(userGroup == null) {
			new CFWLog(logger)
			.method("createDefaultGroups")
			.severe("User group '"+CFWDBGroup.CFW_GROUP_USER+"' was not found in the database.");
		}
		
		userGroup.isRenamable(false);
		CFW.DB.Groups.update(userGroup);
		
		//-----------------------------------------
		// Create Group Foreign
		//-----------------------------------------
		Group foreignuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_FOREIGN_USER);
		
		if(!(foreignuserGroup == null)) {
			foreignuserGroup.isRenamable(true);
			foreignuserGroup.isDeletable(true);
			CFW.DB.Groups.update(foreignuserGroup);
		}
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
		
		
		String[] inputFields = 
				new String[] {
						GroupFields.PK_ID.toString(), 
						GroupFields.NAME.toString(),
				};
		
		String[] outputFields = 
				new String[] {
						GroupFields.PK_ID.toString(), 
						GroupFields.NAME.toString(),
						GroupFields.DESCRIPTION.toString(),
						GroupFields.IS_DELETABLE.toString(),
						GroupFields.IS_RENAMABLE.toString(),		
				};

		//----------------------------------
		// fetchJSON
		APIDefinitionFetch fetchJsonAPI = 
				new APIDefinitionFetch(
						this.getClass(),
						this.getClass().getSimpleName(),
						"fetchJSON",
						inputFields,
						outputFields,
						ReturnFormat.JSON
				);
		
		apis.add(fetchJsonAPI);
		
		//----------------------------------
		// fetchCSV
		APIDefinitionFetch fetchCSVAPI = 
				new APIDefinitionFetch(
						this.getClass(),
						this.getClass().getSimpleName(),
						"fetchCSV",
						inputFields,
						outputFields,
						ReturnFormat.CSV
				);
		
		apis.add(fetchCSVAPI);
		
		//----------------------------------
		// fetchXML
		APIDefinitionFetch fetchXMLAPI = 
				new APIDefinitionFetch(
						this.getClass(),
						this.getClass().getSimpleName(),
						"fetchXML",
						inputFields,
						outputFields,
						ReturnFormat.XML
				);
		
		apis.add(fetchXMLAPI);
		return apis;
	}

	public int id() {
		return id.getValue();
	}
	
	public Group id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public String name() {
		return name.getValue();
	}
	
	public Group name(String name) {
		this.name.setValue(name);
		return this;
	}
	
	public String description() {
		return description.getValue();
	}

	public Group description(String description) {
		this.description.setValue(description);
		return this;
	}

	public boolean isDeletable() {
		return isDeletable.getValue();
	}
	
	public Group isDeletable(boolean isDeletable) {
		this.isDeletable.setValue(isDeletable);
		return this;
	}	
	
	public boolean isRenamable() {
		return isRenamable.getValue();
	}
	
	public Group isRenamable(boolean isRenamable) {
		this.isRenamable.setValue(isRenamable);
		return this;
	}	
	
}
