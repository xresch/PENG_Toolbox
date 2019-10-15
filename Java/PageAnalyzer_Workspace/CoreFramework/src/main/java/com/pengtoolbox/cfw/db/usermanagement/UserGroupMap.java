package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.api.APIDefinitionFetch;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;
import com.pengtoolbox.cfw.db.usermanagement.User.UserFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class UserGroupMap extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_USER_GROUP_MAP";
	
	enum UserGroupMapFields{
		PK_ID, 
		FK_ID_USER,
		FK_ID_GROUP,
		IS_DELETABLE
	}

	private static Logger logger = CFWLog.getLogger(UserGroupMap.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, UserGroupMapFields.PK_ID)
			.setPrimaryKeyAutoIncrement(this)
			.setDescription("The id of the mapping.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<Integer> foreignKeyUser = CFWField.newInteger(FormFieldType.HIDDEN, UserGroupMapFields.FK_ID_USER)
			.setForeignKeyCascade(this, User.class, UserFields.PK_ID)
			.setDescription("The id of the user.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<Integer> foreignKeyGroup = CFWField.newInteger(FormFieldType.HIDDEN, UserGroupMapFields.FK_ID_GROUP)
			.setForeignKeyCascade(this, Group.class, GroupFields.PK_ID)
			.setDescription("The id of the group.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.HIDDEN, UserGroupMapFields.IS_DELETABLE)
			.setColumnDefinition("BOOLEAN")
			.setDescription("Flag to define if the mapping can be deleted or not.")
			.setValue(true);
	
	public UserGroupMap() {
		initializeFields();
	}
	
	public UserGroupMap(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, foreignKeyUser, foreignKeyGroup, isDeletable);
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
				
		String[] inputFields = 
				new String[] {
						UserGroupMapFields.PK_ID.toString(), 
						UserGroupMapFields.FK_ID_USER.toString(),
						UserGroupMapFields.FK_ID_GROUP.toString(),
				};
		
		String[] outputFields = 
				new String[] {
						UserGroupMapFields.PK_ID.toString(), 
						UserGroupMapFields.FK_ID_USER.toString(),
						UserGroupMapFields.FK_ID_GROUP.toString(),
						UserGroupMapFields.IS_DELETABLE.toString(),
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

	public int foreignKeyUser() {
		return foreignKeyUser.getValue();
	}
	
	public UserGroupMap foreignKeyUser(int foreignKeyUser) {
		this.foreignKeyUser.setValue(foreignKeyUser);
		return this;
	}	
	
	public int foreignKeyGroup() {
		return foreignKeyGroup.getValue();
	}
	
	public UserGroupMap foreignKeyGroup(int foreignKeyGroup) {
		this.foreignKeyGroup.setValue(foreignKeyGroup);
		return this;
	}	
	
	
	public boolean isDeletable() {
		return isDeletable.getValue();
	}
	
	public UserGroupMap isDeletable(boolean isDeletable) {
		this.isDeletable.setValue(isDeletable);
		return this;
	}	
	
	
	
	
}
