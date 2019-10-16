package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.api.APIDefinitionFetch;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class GroupPermissionMap extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_GROUP_PERMISSION_MAP";
	
	enum GroupPermissionMapFields{
		PK_ID, 
		FK_ID_PERMISSION,
		FK_ID_GROUP,
		IS_DELETABLE,
	}

	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, GroupPermissionMapFields.PK_ID)
			.setPrimaryKeyAutoIncrement(this)
			.setDescription("The id of the mapping.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
		
	private CFWField<Integer> foreignKeyGroup = CFWField.newInteger(FormFieldType.HIDDEN, GroupPermissionMapFields.FK_ID_GROUP)
			.setForeignKeyCascade(this, Group.class, GroupFields.PK_ID)
			.setDescription("The id of the group.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<Integer> foreignKeyPermission = CFWField.newInteger(FormFieldType.HIDDEN, GroupPermissionMapFields.FK_ID_PERMISSION)
			.setForeignKeyCascade(this, Permission.class, GroupPermissionMapFields.PK_ID)
			.setDescription("The id of the permission.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.HIDDEN, GroupPermissionMapFields.IS_DELETABLE)
			.setColumnDefinition("BOOLEAN")
			.setDescription("Flag to define if the mapping can be deleted or not.")
			.setValue(true);
	
	public GroupPermissionMap() {
		initializeFields();
	}
	
	public GroupPermissionMap(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, foreignKeyGroup, foreignKeyPermission,  isDeletable);
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
				
		String[] inputFields = 
				new String[] {
						GroupPermissionMapFields.PK_ID.toString(), 
						GroupPermissionMapFields.FK_ID_PERMISSION.toString(),
						GroupPermissionMapFields.FK_ID_GROUP.toString(),
				};
		
		String[] outputFields = 
				new String[] {
						GroupPermissionMapFields.PK_ID.toString(), 
						GroupPermissionMapFields.FK_ID_PERMISSION.toString(),
						GroupPermissionMapFields.FK_ID_GROUP.toString(),
						GroupPermissionMapFields.IS_DELETABLE.toString(),
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

	public int foreignKeyGroup() {
		return foreignKeyGroup.getValue();
	}
	
	public GroupPermissionMap foreignKeyGroup(int foreignKeyGroup) {
		this.foreignKeyGroup.setValue(foreignKeyGroup);
		return this;
	}	
	

	public int foreignKeyPermission() {
		return foreignKeyPermission.getValue();
	}
	
	public GroupPermissionMap foreignKeyPermission(int foreignKeyPermission) {
		this.foreignKeyPermission.setValue(foreignKeyPermission);
		return this;
	}	
	
	public boolean isDeletable() {
		return isDeletable.getValue();
	}
	
	public GroupPermissionMap isDeletable(boolean isDeletable) {
		this.isDeletable.setValue(isDeletable);
		return this;
	}	
	
	
}
