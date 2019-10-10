package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWFieldChangeHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
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
			.setPrimaryKeyAutoIncrement()
			.setValue(-999);
	
	private CFWField<String> name = CFWField.newString(FormFieldType.TEXT, GroupFields.NAME.toString())
			.setColumnDefinition("VARCHAR(255) UNIQUE")
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
			.addValidator(new LengthValidator(-1, 2000000));
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.NONE, GroupFields.IS_DELETABLE.toString())
			.setColumnDefinition("BOOLEAN")
			.setValue(true);
	
	private CFWField<Boolean> isRenamable = CFWField.newBoolean(FormFieldType.NONE, GroupFields.IS_RENAMABLE.toString())
			.setColumnDefinition("BOOLEAN DEFAULT TRUE")
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
		this.setPrimaryField(id);
		this.addFields(id, name, description, isDeletable, isRenamable);
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
