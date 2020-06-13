/**
 * 	@(#)PartnerMBL.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

package com.winnovature.unitia.util.datacache.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.db.CoreDBConnection;

public class ExtraParameter 
{
	String classNameSNMP = " util.datacache.account.ExtraParameter";

	/*
	 * The ClassName variable
	 */
	private static String className = "[ExtraParameter]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static ExtraParameter extraparameter = new ExtraParameter();
	
	/*
	 * The Memory holders
	 */
	private Map<String,Map> extraparametermap = new HashMap();
			
	
	PropertiesConfiguration pc = null;
	
	/*
	 * The private Constructor
	 */
	private ExtraParameter()
	{
		String identifier=classNameSNMP+" TransDAO ";

		
		load();
	}
	
	/**
	 * 
	 * Method : load
	 *       usage : loading the std codes when the object instantiate
	 */
	private synchronized void load() 
	{
		extraparametermap = loadExtraParameterNames();
	}
	
	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpICICIPinMap = loadExtraParameterNames();
		
		if(_tmpICICIPinMap!=null&&_tmpICICIPinMap.size()>0){
		extraparametermap = _tmpICICIPinMap;
		}
		_tmpICICIPinMap=null;
		
	}
	
	/**
	 * Return Pin Map
	 * @return
	 */
	public String getExtraParameterName(String aid,String parameter)
	{
		Map<String,String> extraparam=new HashMap();
		if(extraparametermap.containsKey(aid)){
			
			extraparam=extraparametermap.get(aid);
		}else{
			
			extraparam=extraparametermap.get("0");

		}
		return extraparam.get(parameter);
	}
	

	/**
	 * 
	 * Method : loadICICIPinInfo
	 *       usage : load all the MBL details from the table to the memory.
	 */
	private Map loadExtraParameterNames()	
	{
		Map<String,Map> _pinMap = new HashMap();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			String sql = "select aid,parameter,parameter_alias from EXTRA_PARAMETER_NAMES";

			connection  =CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
			{				
				if(_pinMap.containsKey(resultSet.getString("AID"))){
				Map <String,String> record =_pinMap.get(resultSet.getString("AID"));
				record.put(resultSet.getString("parameter").trim(), resultSet.getString("parameter_alias").trim());
				}else{
					Map <String,String> record =new HashMap();
					record.put(resultSet.getString("parameter").trim(), resultSet.getString("parameter_alias").trim());
					_pinMap.put(resultSet.getString("AID"), record);
				}
			}
			
			if(!_pinMap.containsKey("0")){
				_pinMap.put("0", getDefaultParameter());
			}
			if(logger.isDebugEnabled())
			logger.debug(className + "loadExtraParameterNames() _pinMap - " + _pinMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadExtraParameterNames(); Not able to load loadExtraParameterNames-", e);
			_pinMap = null;
		} 
		finally 
		{
			try
			{
				if(resultSet != null)	resultSet.close();
				if(statement != null)	statement.close();
				if(connection != null)	connection.close();
			}
			catch(Exception ignore) {
			
			}
		}//end of finally
		
		return _pinMap;
		
	}

	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static ExtraParameter instance()
	{
		if(extraparameter == null)
			extraparameter = new ExtraParameter();
		
		return extraparameter;
		
	}

	private Map<String,String> getDefaultParameter(){
		
		Map <String,String> record =new HashMap();
		record.put("param1", "param1");
		record.put("param2", "param2");
		record.put("param3", "param3");
		record.put("param4", "param4");
		record.put("param5", "param5");
		
		return record;
		

	}

} // end of class PartnerMBL