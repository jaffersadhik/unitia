/**
 * 	@(#)Credit.java	1.0
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.constants.Constants;
import com.winnovature.unitia.util.datacache.instance.InstanceInfoMemory;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;


public class Credit 
{
	String classNameSNMP = " util.datacache.account.Credit";


	/*
	 * The ClassName variable
	 */
	private static String className = "[Credit]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static Credit credit = new Credit();
	
	/*
	 * The Memory holders
	 */
	private Map creditGroupMap = new HashMap();
	
	private Map creditConfigMap = new HashMap();
	
	private final static String NULL = "~*~NULL~*~";
	
	
	PropertiesConfiguration pc = null;
	
	/*
	 * The private Constructor
	 */
	private Credit()
	{
		String identifier=classNameSNMP+" Credit ";

		
		
		load();
	}
	
	/**
	 * 
	 * Method : load
	 *       usage : loading the std codes when the object instantiate
	 */
	private synchronized void load() 
	{
		creditGroupMap = loadCreditGroup();
		creditConfigMap = loadCreditConfig();
	}
	
	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpCreditGrpMap = loadCreditGroup();
		Map _tmpCreditConfMap = loadCreditConfig();
		
		if(_tmpCreditGrpMap!=null&&_tmpCreditGrpMap.size()>0){
		creditGroupMap = _tmpCreditGrpMap;
		}
		if(_tmpCreditConfMap!=null&&_tmpCreditConfMap.size()>0){
		creditConfigMap = _tmpCreditConfMap;
		}
		_tmpCreditGrpMap= null;
		_tmpCreditConfMap=null;
		
	}
	
	/**
	 * 
	 * Method : findDeducePoints
	 * @param aid
	 * @param carrierId
	 * @return
	 *       usage :
	 */
	/*public double findDeducePoints(int aid, int carrierId)
	{		
		return findCreditPoints(aid, findCreditGroupId(carrierId));			
	}*/
	
	/**
	 * Add this patch for SMS Blaster - 25 Feb 2009.
	 */
	public double findDeducePoints(String pid, String aid, String carrierId) throws Exception
	{		
		//double crdPts = 0;
		
		String creditPoints = null;
		String creditGroupID = findCreditGroupId(pid,aid,carrierId);
		if(logger.isDebugEnabled())
		logger.debug(className + "findDeducePoints() creditGroupID - " + creditGroupID);
		
		if(StringUtils.isNotBlank(creditGroupID))
		{
			creditPoints =  findCreditPoints(pid, aid, creditGroupID);			
		}
		
		if(logger.isDebugEnabled())
		logger.debug(className + "findDeducePoints() creditPoints - " + creditPoints);
		
		if(StringUtils.isBlank(creditGroupID) || StringUtils.isBlank(creditPoints))		
		{			
			
			creditPoints = ConfigParams.getInstance().getProperty(ConfigKey.DEFAULT_CREDIT_POINTS);
			if(logger.isDebugEnabled())
			logger.debug(className + "findDeducePoints() PICKING DEFAULT CREDIT POINTS - " + creditPoints);
		}
		
		return Double.parseDouble(creditPoints);		
		
	}
	
	/**
	 * 
	 * Method : findCreditGroupId
	 * @param carrierId
	 * @return
	 *       usage : find credit group id
	 */
	private String findCreditGroupId(String pid, String aid, String carrierId) throws Exception
	{
		
		/*if(creditGroupMap != null && creditGroupMap.containsKey(String.valueOf(carrierId)))
			return Integer.parseInt(creditGroupMap.get(String.valueOf(carrierId)).toString());
		else
			return 0;*/
		
		String grpId = null;
			
		if(creditGroupMap.containsKey(carrierId+"~"+pid+"~"+aid))			
			grpId =  (String) creditGroupMap.get(carrierId+"~"+pid+"~"+aid);
		else if(creditGroupMap.containsKey(carrierId+"~"+pid+"~NULL"))			
			grpId = (String) creditGroupMap.get(carrierId+"~"+pid+"~NULL");
		else if(creditGroupMap.containsKey(carrierId+"~NULL~NULL"))
			grpId =  (String) creditGroupMap.get(carrierId+"~NULL~NULL");		
		
		return grpId;
		
		
	}
	
	/**
	 * 
	 * Method : findCreditPoints
	 * @param aid
	 * @param creditGroupId
	 * @return
	 *       usage : find the credit points for the account
	 */
	private String findCreditPoints(String pid, String aid, String creditGroupId) throws Exception
	{
			
	/*	if(creditConfigMap != null && creditConfigMap.containsKey(aid+"~"+creditGroupId))
			return Double.parseDouble(creditConfigMap.get(aid+"~"+creditGroupId).toString());
		else
			return 0;*/
		
		String crPoints = null;
		
		if(creditConfigMap.containsKey(creditGroupId+"~"+pid+"~"+aid))			
			crPoints =  (String) creditConfigMap.get(creditGroupId+"~"+pid+"~"+aid);
		else if(creditConfigMap.containsKey(creditGroupId+"~"+pid+"~NULL"))			
			crPoints = (String) creditConfigMap.get(creditGroupId+"~"+pid+"~NULL");
		else if(creditConfigMap.containsKey(creditGroupId+"~NULL~NULL"))
			crPoints = (String) creditConfigMap.get(creditGroupId+"~NULL~NULL");
		
		return crPoints;
	}
	/**
	 * 
	 * Method : loadCreditConfig
	 *       usage : load all the credit group from the table to the memory.
	 */
	private Map loadCreditConfig() 
	{
		Map _creditConfMap = new HashMap();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			String sql = "select pid,aid,cr_groupid,cr_points from credit_config";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
			{
				//_creditConfMap.put(resultSet.getString("aid")+"~"+resultSet.getString("cr_groupid"), resultSet.getString("cr_points"));
				
				String groupid = StringUtils.isNotBlank(resultSet.getString("CR_GROUPID"))? resultSet.getString("CR_GROUPID").trim(): Constants.NULL;
				String pid = StringUtils.isNotBlank(resultSet.getString("PID")) ? resultSet.getString("PID").trim(): Constants.NULL;
				String aid = StringUtils.isNotBlank(resultSet.getString("AID")) ? resultSet.getString("AID").trim(): Constants.NULL;
				
				String _key = groupid + "~" + pid + "~" + aid;
				String _value = resultSet.getString("CR_POINTS").trim();
				
	
				_creditConfMap.put(_key, _value);
			}
			
			if(logger.isDebugEnabled())
			logger.debug(className + "loadCreditConfig() _creditConfMap - " + _creditConfMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadCreditConfig(); Not able to load credit config", e);
			//SNMP TRAP Implementation

			_creditConfMap = null;
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
		
		return _creditConfMap;
		
	}


	/**
	 * 
	 * Method : loadCreditGroup
	 *       usage : load all the credit group from the table to the memory.
	 */
	private Map loadCreditGroup() 
	{
		Map _creditGrpMap = new HashMap();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			String sql = "select * from credit_group";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
			{
				//_creditGrpMap.put(resultSet.getString("carrierid"), resultSet.getString("cr_groupid"));
				
				String carrierid = StringUtils.isNotBlank(resultSet.getString("CARRIERID"))? resultSet.getString("CARRIERID").trim(): Constants.NULL;
				String pid = StringUtils.isNotBlank(resultSet.getString("PID")) ? resultSet.getString("PID").trim(): Constants.NULL;
				String aid = StringUtils.isNotBlank(resultSet.getString("AID")) ? resultSet.getString("AID").trim(): Constants.NULL;
				
				String _key = carrierid + "~" + pid + "~" + aid;
				String _value = resultSet.getString("CR_GROUPID").trim();
				_creditGrpMap.put(_key, _value);
			}
			
			if(logger.isDebugEnabled())
			logger.debug(className + "loadCreditGroup() _creditGrpMap - " + _creditGrpMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadCreditGroup(); Not able to load credit group", e);
			//SNMP TRAP Implementation

			_creditGrpMap = null;
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
		
		return _creditGrpMap;
		
	}

	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static Credit instance()
	{
		if(credit == null)
			credit = new Credit();
		
		return credit;
		
	}

}
 // end of class Credit
