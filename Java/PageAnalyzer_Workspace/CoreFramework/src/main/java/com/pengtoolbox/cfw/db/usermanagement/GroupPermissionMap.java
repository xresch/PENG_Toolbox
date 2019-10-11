package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;
import com.pengtoolbox.cfw.db.usermanagement.Permission.PermissionFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class GroupPermissionMap extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_GROUP_PERMISSION_MAP";
	
	enum GroupPermissionMapFields{
		PK_ID, 
		FK_ID_PERMISSION,
		FK_ID_GROUP,
		IS_DELETABLE,
	}

	private static Logger logger = CFWLog.getLogger(GroupPermissionMap.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, GroupPermissionMapFields.PK_ID)
			.setPrimaryKeyAutoIncrement(this)
			.setValue(-999);
		
	private CFWField<Integer> foreignKeyGroup = CFWField.newInteger(FormFieldType.HIDDEN, GroupPermissionMapFields.FK_ID_GROUP)
			.setForeignKeyCascade(this, Group.class, GroupFields.PK_ID)
			.setValue(-999);
	
	private CFWField<Integer> foreignKeyPermission = CFWField.newInteger(FormFieldType.HIDDEN, GroupPermissionMapFields.FK_ID_PERMISSION)
			.setForeignKeyCascade(this, Permission.class, PermissionFields.PK_ID)
			.setValue(-999);
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.HIDDEN, GroupPermissionMapFields.IS_DELETABLE)
			.setColumnDefinition("BOOLEAN")
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
