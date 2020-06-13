package com.winnovature.unitia.util.datacache.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.constants.Constants;
import com.winnovature.unitia.util.db.CoreDBConnection;



public class SenderidRouting implements Constants
{

	/*
	 * The ClassName variable
	 */
	private static String className = "[SenderidRouting]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static SenderidRouting msgRouter = new SenderidRouting();
	
	/*
	 * The Memory holders
	 */
	private Map senderidMap = new HashMap();
	
	PropertiesConfiguration pc = null;
	
	/*
	 * The private Constructor
	 */
	private SenderidRouting()
	{
	
		
		load();
	}
	
	/**
	 * 
	 * Method : load
	 *       usage : loading the msg router when the object instantiate
	 */
	private synchronized void load() 
	{
		senderidMap = loadSenderidRoute();
	}
		

	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpPriority = loadSenderidRoute();
		
		
		if(_tmpPriority!=null){
			senderidMap = _tmpPriority;
		}
		_tmpPriority=null;
		
	}
	
	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static SenderidRouting instance()
	{
		if(msgRouter == null)
			msgRouter = new SenderidRouting();
		
		return msgRouter;
		
	}
	
	public Map getSenderidMap()
	{
		
		return senderidMap;
	}
	
	
	/**
	 * 
	 * Method : loadLogicRoute
	 *       usage :
	 */
	private Map loadSenderidRoute() 
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Map _prtyMap = new HashMap();
		
		try
		{
			String sql = "select senderid,groupid from senderid_router ";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				
				String senderid=resultSet.getString("senderid").toUpperCase();
				String groupid=resultSet.getString("groupid");
				if(senderid!=null&&groupid!=null){
					
					_prtyMap.put(senderid, groupid);
					

				}

			}
			if(logger.isDebugEnabled())
			logger.debug(className + "loadSenderidRoute() _prtyMap - " + _prtyMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadSenderidRoute(); Not able to load logical message route", e);
			//SNMP TRAP Implementation

			_prtyMap=null;
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
		
		return _prtyMap;
		
	}
		

	
	
	

	
} // end of class InstanceRoute