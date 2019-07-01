package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Group {
	
	private int id;
	private String name;
	private String description;
	private boolean isDeletable = true;
	
	public Group() {
	
	}
	
	public Group(ResultSet result) throws SQLException {
		int col = 1;
		this.id(result.getInt(col++))
		.name(result.getString(col++))
		.description(result.getString(col++))
		.isDeletable(result.getBoolean(col++));
		
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
