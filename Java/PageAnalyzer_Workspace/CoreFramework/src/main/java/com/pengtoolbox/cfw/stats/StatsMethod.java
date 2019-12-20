package com.pengtoolbox.cfw.stats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.api.APIDefinitionFetch;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class StatsMethod extends CFWObject {
	
	public static final String TABLE_NAME = "CFW_STATS_METHOD";
	
	public enum StatsMethodFields{
		PK_ID,
		TIME,
		FK_ID_SIGNATURE,
		FK_ID_PARENT,
		COUNT,
		PERIOD,
	}

	private static Logger logger = CFWLog.getLogger(StatsMethod.class.getName());
	
	private CFWField<Integer> id = CFWField.newInteger(FormFieldType.NONE, StatsMethodFields.PK_ID.toString())
			.setPrimaryKeyAutoIncrement(this)
			.setDescription("The id of the statistic.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	private CFWField<Timestamp> time = CFWField.newTimestamp(FormFieldType.NONE, StatsMethodFields.TIME)
			.setDescription("The date and time of when the statistic was written to the database.")
			.setValue(new Timestamp(new Date().getTime()));
	
	private CFWField<Integer> foreignKeySignature = CFWField.newInteger(FormFieldType.NONE, StatsMethodFields.FK_ID_SIGNATURE)
			.setForeignKeyCascade(this, StatsMethodSignature.class, StatsMethodFields.PK_ID)
			.setDescription("The id of the method signature.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(null);
	
	private CFWField<Integer> foreignKeyParent = CFWField.newInteger(FormFieldType.NONE, StatsMethodFields.FK_ID_PARENT)
			.setForeignKeyCascade(this, StatsMethodSignature.class, StatsMethodFields.PK_ID)
			.setDescription("The id of the method signature.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(null);

	private CFWField<Integer> count = CFWField.newInteger(FormFieldType.NONE, StatsMethodFields.COUNT)
			.setDescription("The count of the statistic.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(null);
	
	private CFWField<Integer> period = CFWField.newInteger(FormFieldType.NONE, StatsMethodFields.PERIOD)
			.setDescription("The period in seconds represented by this statistics.")
			.apiFieldType(FormFieldType.NUMBER)
			.setValue(-999);
	
	public StatsMethod() {
		initializeFields();
	}
		
	public StatsMethod(ResultSet result) throws SQLException {
		initializeFields();
		this.mapResultSet(result);	
	}
	
	private void initializeFields() {
		this.setTableName(TABLE_NAME);
		this.addFields(id, time, foreignKeySignature, foreignKeyParent, count, period);
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
	public void initDB() {

	}
	
	/**************************************************************************************
	 * 
	 **************************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		ArrayList<APIDefinition> apis = new ArrayList<APIDefinition>();
		
		String[] inputFields = 
				new String[] {
						StatsMethodFields.PK_ID.toString(),
						StatsMethodFields.TIME.toString(), 
						StatsMethodFields.FK_ID_SIGNATURE.toString(),
						StatsMethodFields.FK_ID_PARENT.toString(),
						StatsMethodFields.PERIOD.toString()
				};
		
		String[] outputFields = 
				new String[] {
						StatsMethodFields.PK_ID.toString(),
						StatsMethodFields.TIME.toString(), 
						StatsMethodFields.FK_ID_SIGNATURE.toString(),
						StatsMethodFields.FK_ID_PARENT.toString(),
						StatsMethodFields.COUNT.toString(),
						StatsMethodFields.PERIOD.toString()	
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
	
	public StatsMethod id(int id) {
		this.id.setValue(id);
		return this;
	}
	
	public Timestamp time() {
		return time.getValue();
	}
	
	public StatsMethod time(Timestamp time) {
		this.time.setValue(time);
		return this;
	}
	
	public int foreignKeySignature() {
		return foreignKeySignature.getValue();
	}
	
	public StatsMethod foreignKeySignature(int foreignKeySignature) {
		this.foreignKeySignature.setValue(foreignKeySignature);
		return this;
	}
	
	public int foreignKeyParent() {
		return foreignKeyParent.getValue();
	}
	
	public StatsMethod foreignKeyParent(int foreignKeyParent) {
		this.foreignKeyParent.setValue(foreignKeyParent);
		return this;
	}

	public int count() {
		return count.getValue();
	}
	
	public StatsMethod count(int count) {
		this.count.setValue(count);
		return this;
	}
	
	public int period() {
		return period.getValue();
	}
	
	public StatsMethod period(int period) {
		this.period.setValue(period);
		return this;
	}
	
}
