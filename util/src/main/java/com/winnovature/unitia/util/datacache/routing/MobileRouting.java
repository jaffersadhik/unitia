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
import com.winnovature.unitia.util.datacache.instance.InstanceInfoMemory;
import com.winnovature.unitia.util.db.CoreDBConnection;



public class MobileRouting implements Constants
{

	/*
	 * The ClassName variable
	 */
	private static String className = "[MobileRouting]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static MobileRouting msgRouter = new MobileRouting();
	
	/*
	 * The Memory holders
	 */
	private Map mobileMap = new HashMap();
	
	PropertiesConfiguration pc = null;
	
	/*
	 * The private Constructor
	 */
	private MobileRouting()
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
		mobileMap = loadMobileRoute();
	}
		

	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpPriority = loadMobileRoute();
		
		
		if(_tmpPriority!=null){
			mobileMap = _tmpPriority;
		}
		_tmpPriority=null;
		
	}
	
	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static MobileRouting instance()
	{
		if(msgRouter == null)
			msgRouter = new MobileRouting();
		
		return msgRouter;
		
	}
	
	public Map getMobileMap()
	{
		
		return mobileMap;
	}
	
	
	/**
	 * 
	 * Method : loadLogicRoute
	 *       usage :
	 */
	private Map loadMobileRoute() 
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Map _prtyMap = new HashMap();
		
		try
		{
			String sql = "select mobile,trans_groupid,promo_groupid from mobile_router ";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				
				String mobile=resultSet.getString("mobile");
				String trans_groupid=resultSet.getString("trans_groupid");
				String promo_groupid=resultSet.getString("promo_groupid");
				if(mobile!=null&&trans_groupid!=null&&promo_groupid!=null){
					
					Map record=new HashMap();
					record.put("TRANS_GROUPID", trans_groupid);
					record.put("PROMO_GROUPID", promo_groupid);

					_prtyMap.put(mobile, record);
					

				}

			}
			if(logger.isDebugEnabled())
			logger.debug(className + "loadPriorityRoute() _prtyMap - " + _prtyMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadPriorityRoute(); Not able to load logical message route", e);
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