/**
 * 	@(#)RouteGroup.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

package com.winnovature.unitia.util.datacache.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.datacache.instance.InstanceInfoMemory;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class MultipleSenderId 
{
	/*
	 * The ClassName variable
	 */
	private static String className = "[MultipleSenderId]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static MultipleSenderId multipleSenderId = new MultipleSenderId();
	
	/*
	 * The Memory holders
	 */
	private Map signatureMap = new HashMap();
	
	PropertiesConfiguration pc = null;
	
	/*
	 * The private Constructor
	 */
	private MultipleSenderId()
	{
		
		
		load();
	}
	
	/**
	 * 
	 * Method : load
	 *       usage : loading the multiple senderid when the object instantiate
	 */
	private synchronized void load() 
	{
		signatureMap = loadMultipleSenderId();
	}
	
	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpCLI = loadMultipleSenderId();
		
		if(_tmpCLI!=null&&_tmpCLI.size()>0){
		signatureMap = _tmpCLI;
		}
		_tmpCLI=null;		
	}
	
	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static MultipleSenderId instance()
	{
		if(multipleSenderId == null)
			multipleSenderId = new MultipleSenderId();
		
		return multipleSenderId;
		
	}
	
	/**
	 * 
	 * Method : getSenderId
	 * @return
	 *       usage :retuns the map contains the route group
	 */
	public List getSenderId(String aid)
	{
		if(signatureMap != null && signatureMap.containsKey(aid))
		{
			return (List) signatureMap.get(aid);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 
	 * Method : loadMultipleSenderId
	 *       usage : load all the multiple senderid from the table to the memory.
	 */
	private Map loadMultipleSenderId() 
	{
		Map _signatureMap = new HashMap();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			//String sql = "select * from multiple_senderid order by aid";
			/* Airtext Proring changes on 26-07-2011 changed by sreedhar*/
			String sql = "select * from multiple_senderid where (statusflag is null or statusflag='APPROVED') order by aid";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			_signatureMap = getSinatureMap(resultSet);			
			
			if(logger.isDebugEnabled())
			logger.debug(className + "loadMultipleSenderId() _signatureMap - " + _signatureMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadMultipleSenderId(); Not able to load multiple senderid", e);
	
			_signatureMap=null;
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
		
		return _signatureMap;
		
	}
	
	private Map getSinatureMap(ResultSet rs) throws Exception
	{
		Map senderIdMap = new HashMap();
		List list = null;
		
		while(rs.next())
		{
			String aid = rs.getString("aid"); 
			
			if(senderIdMap.containsKey(aid))
				list = (ArrayList)senderIdMap.get(aid);
			else
				list = new ArrayList();
								
				
			list.add(rs.getString("senderid").toUpperCase());
			senderIdMap.put(aid,list);
			
		}
	
		return senderIdMap;
	
	}	


} // end of class RouteGroup