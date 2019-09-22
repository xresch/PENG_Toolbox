package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pengtoolbox.cfw._main.CFWObject;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup.GroupDBFields;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;
import com.pengtoolbox.cfw.response.bootstrap.CFWField.FormFieldType;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class Group extends CFWObject {
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, GroupDBFields.PK_ID.toString())
									.setValue(-999);
	
	private CFWField<String> name = CFWField.newString(FormFieldType.TEXT, GroupDBFields.NAME.toString())
									.addValidator(new LengthValidator(1, 2000000000));
	
	private CFWField<String> description = CFWField.newString(FormFieldType.TEXTAREA, GroupDBFields.DESCRIPTION.toString())
											.addValidator(new LengthValidator(-1, 2000000000));
	
	private CFWField<Boolean> isDeletable = CFWField.newBoolean(FormFieldType.NONE, GroupDBFields.IS_DELETABLE.toString())
											.setValue(true);
	
	private CFWField<Boolean> isRenamable = CFWField.newBoolean(FormFieldType.NONE, GroupDBFields.IS_DELETABLE.toString())
			.setValue(true);
	
	public Group(String name) {
		initializeFields();
		this.name.setValue(name);
	}
	
	public Group(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		
		this.addFields(id, name, description, isDeletable);
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
