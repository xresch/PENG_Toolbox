package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission.PermissionDBFields;

public class Group {
	
	private int id = -999;
	private String name;
	private String description;
	private boolean isDeletable = true;
	
	public Group(String name) {
		this.name = name;
	}
	
	public Group(ResultSet result) throws SQLException {
		
		this.id = result.getInt(PermissionDBFields.PK_ID.toString());
		this.name = result.getString(PermissionDBFields.NAME.toString());
		this.description = result.getString(PermissionDBFields.DESCRIPTION.toString());
		this.isDeletable = result.getBoolean(PermissionDBFields.IS_DELETABLE.toString());
		
	}

	public int id() {
		return id;
	}
	
	public Group id(int id) {
		this.id = id;
		return this;
	}
	
	public String name() {
		return name;
	}
	
	public Group name(String username) {
		this.name = username;
		return this;
	}
	
	
	
	public String description() {
		return description;
	}

	public Group description(String description) {
		this.description = description;
		return this;
	}

	public boolean isDeletable() {
		return isDeletable;
	}
	
	public Group isDeletable(boolean isDeletable) {
		this.isDeletable = isDeletable;
		return this;
	}	

	public String getKeyValueString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("\nid: "+id);
		builder.append("\nname: "+name);
		builder.append("\ndescription: "+description);
		builder.append("\nisDeletable: "+isDeletable);
		
		return builder.toString();
	}
	
	
}
