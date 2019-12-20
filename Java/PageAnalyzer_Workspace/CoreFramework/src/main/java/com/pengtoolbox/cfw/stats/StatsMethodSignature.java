package com.pengtoolbox.cfw.stats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.api.APIDefinitionFetch;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.validation.LengthValidator;

/**************************************************************************************************************
 * Class to represent method signatures.
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class StatsMethodSignature extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_STATS_METHOD_SIGNATURE";
	
	public enum StatsMethodSignatureFields{
		PK_ID,
		SIGNATURE
	}

	private static Logger logger = CFWLog.getLogger(StatsMethodSignature.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.HIDDEN, StatsMethodSignatureFields.PK_ID.toString())
			.setPrimaryKeyAutoIncrement(this)
			.setDescription("The id of the method signature.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
		
	private CFWField<String> signature = CFWField.newString(FormFieldType.TEXT, StatsMethodSignatureFields.SIGNATURE.toString())
			.setColumnDefinition("VARCHAR UNIQUE")
			.setDescription("The signature of the method.")
			.addValidator(new LengthValidator(0, 255));
	
	public StatsMethodSignature() {
		initializeFields();
	}
	
	public StatsMethodSignature(String name) {
		initializeFields();
		this.signature.setValue(name);
	}
	
	public StatsMethodSignature(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, signature);
	}
	
	/**************************************************************************************
	 * Migrate Table
	 **************************************************************************************/
	public void migrateTable() {
		
		
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public void updateTable() {
						
	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
		
		
		String[] inputFields = 
				new String[] {
						StatsMethodSignatureFields.PK_ID.toString(), 
						StatsMethodSignatureFields.SIGNATURE.toString(),
				};
		
		String[] outputFields = 
				new String[] {
						StatsMethodSignatureFields.PK_ID.toString(), 
						StatsMethodSignatureFields.SIGNATURE.toString()
				};

		//----------------------------------
		// fetchJSON
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
	
	public StatsMethodSignature id(int id) {
		this.id.setValue(id);
		return this;
	}
	
}
