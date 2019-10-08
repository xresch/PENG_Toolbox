package com.pengtoolbox.cfw.db.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.CustomValidator;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class Config extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_CONFIG";
	
	public enum ConfigFields{
		PK_ID,
		NAME,
		DESCRIPTION,
		TYPE,
		VALUE,
		OPTIONS
	}

	private static Logger logger = CFWLog.getLogger(Config.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, ConfigFields.PK_ID.toString())
									.setPrimaryKeyAutoIncrement()
									.setValue(-999);
	
	private CFWField<String> name = CFWField.newString(FormFieldType.NONE, ConfigFields.NAME.toString())
									.setColumnDefinition("VARCHAR(255) UNIQUE")
									.addValidator(new LengthValidator(1, 255))
									;
	
	private CFWField<String> description = CFWField.newString(FormFieldType.TEXTAREA, ConfigFields.DESCRIPTION.toString())
											.setColumnDefinition("VARCHAR(4096)")
											.addValidator(new LengthValidator(-1, 4096));
	
	private CFWField<String> type = CFWField.newString(FormFieldType.NONE, ConfigFields.TYPE.toString())
			.setColumnDefinition("VARCHAR(32)")
			.addValidator(new CustomValidator() {

				@Override
				public boolean validate(Object value) {
					String stringVal = value.toString();
					if(stringVal.toUpperCase().equals("TEXT")
					|| stringVal.toUpperCase().equals("BOOLEAN")
					|| stringVal.toUpperCase().equals("NUMBER")) {
						return true;
					}
					
					new CFWLog(logger)
						.method("CustomValidator.validate")
						.severe("The value of '"+type+"' must be one of: 'TEXT', 'BOOLEAN', 'INTEGER'.");
					return false; 

				}
				
			});
	
	private CFWField<String> value = CFWField.newString(FormFieldType.NONE, ConfigFields.VALUE.toString())
			.setColumnDefinition("VARCHAR(1024)")
			.addValidator(new LengthValidator(1, 1024))
			;
	
	private CFWField<Object[]> options = CFWField.newArray(FormFieldType.NONE, ConfigFields.OPTIONS.toString())
			.setColumnDefinition("ARRAY");
	
	public Config() {
		initializeFields();
	}
	
	public Config(String name) {
		initializeFields();
		this.name.setValue(name);
	}
	
	public Config(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.setPrimaryField(id);
		this.addFields(id, name, description, type, value, options);
	}

	public int id() {
		return id.getValue();
	}
	
	public Config id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public String name() {
		return name.getValue();
	}
	
	public Config name(String name) {
		this.name.setValue(name);
		return this;
	}
	
	public String description() {
		return description.getValue();
	}

	public Config description(String description) {
		this.description.setValue(description);
		return this;
	}

	public String type() {
		return type.getValue();
	}

	public Config type(String type) {
		this.type.setValue(type);
		return this;
	}

	public String value() {
		return value.getValue();
	}

	public Config value(String value) {
		this.value.setValue(value);
		return this;
	}

	public Object[] options() {
		return options.getValue();
	}

	public Config options(Object[] options) {
		this.options.setValue(options);
		return this;
	}
	
	



	
	
}
