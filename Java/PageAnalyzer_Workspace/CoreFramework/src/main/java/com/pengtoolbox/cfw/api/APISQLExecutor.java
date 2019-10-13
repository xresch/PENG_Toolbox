package com.pengtoolbox.cfw.api;

import java.sql.ResultSet;

import com.pengtoolbox.cfw.datahandling.CFWObject;

public abstract class APISQLExecutor {
	/***********************************************************
	 * Execute an SQL statement
	 * @param definition
	 * @param instance
	 * @return the ResultSet of the SQL
	 ***********************************************************/
	public abstract ResultSet execute(APIDefinitionSQL definition, CFWObject instance);
}