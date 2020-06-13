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

public class PushCarrierSenderId 
{
	/*
	 * The ClassName variable
	 */
	private static String className = "[PushCarrierSenderId]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static PushCarrierSenderId pushCarrierSenderId = new PushCarrierSenderId();
	
	/*
	 * The Memory holders
	 */
	private Map pushCarrierSenderIdMap = new HashMap();
	

	PropertiesConfiguration pc = null;

	/*
	 * The private Constructor
	 */
	private PushCarrierSenderId()
	{
		

		load();
	}
	
	/**
	 * 
	 * Method : load
	 *       usage : loading the push carrier senderid when the object instantiate
	 */
	private synchronized void load() 
	{
		pushCarrierSenderIdMap = loadPushCarrierSenderId();
	}
	
	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpPushCarrCLI = loadPushCarrierSenderId();
		
		if(_tmpPushCarrCLI!=null&&_tmpPushCarrCLI.size()>0){
			pushCarrierSenderIdMap = _tmpPushCarrCLI;

		}
		
		_tmpPushCarrCLI=null;
	}
	
	/**
	 * 
	 * Method : getPushCarrierSenderId
	 * @param aid
	 * @param carrierId
	 * @return
	 *       usage : get the push carrier senderid
	 */
		
	
	public String getPushCarrierSenderId(String aid, String routeid, String carrierid) 
	{
		if(pushCarrierSenderIdMap == null)
			load();
			
		if(pushCarrierSenderIdMap.containsKey(aid+"~"+routeid+"~"+carrierid))			
			return (String) pushCarrierSenderIdMap.get(aid+"~"+routeid+"~"+carrierid);
		else if(pushCarrierSenderIdMap.containsKey(aid+"~NULL~"+carrierid))			
			return (String) pushCarrierSenderIdMap.get(aid+"~NULL~"+carrierid);
		else if(pushCarrierSenderIdMap.containsKey(aid+"~"+routeid+"~NULL"))
			return (String) pushCarrierSenderIdMap.get(aid+"~"+routeid+"~NULL");		
		else if(pushCarrierSenderIdMap.containsKey("NULL~"+routeid+"~"+carrierid))			
			return (String) pushCarrierSenderIdMap.get("NULL~"+routeid+"~"+carrierid);
		else if(pushCarrierSenderIdMap.containsKey("NULL~NULL~"+carrierid))			
			return (String) pushCarrierSenderIdMap.get("NULL~NULL~"+carrierid);
		else if(pushCarrierSenderIdMap.containsKey("NULL~" + routeid + "~NULL"))			
			return (String) pushCarrierSenderIdMap.get("NULL~" + routeid + "~NULL");
		else
			return null;		
	}

	/**
	 * 
	 * Method : loadPushCarrierSenderId
	 *       usage : load all the push carrier senderid from the table to the memory.
	 */
	private Map loadPushCarrierSenderId() 
	{
		Map _pushCarrierSenderIdMap = new HashMap();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			String sql = "select * from push_carrier_senderid";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
			{
				String userid = resultSet.getString("aid") != null ?resultSet.getString("aid").trim(): Constants.NULL;
				String routeid = resultSet.getString("routeid") != null ?resultSet.getString("routeid").trim(): Constants.NULL;
				String carrierId = resultSet.getString("carrierid") != null ?resultSet.getString("carrierid").trim(): Constants.NULL;
										 
				String _key = userid + "~" + routeid + "~" + carrierId;
				String _value = resultSet.getString("senderid").trim();
								
				_pushCarrierSenderIdMap.put(_key, _value);
				//_pushCarrierSenderIdMap.put(resultSet.getString("aid")+"~"+resultSet.getString("carrierid"),resultSet.getString("senderid"));
			}
			
			if(logger.isDebugEnabled())
			logger.debug(className + "loadPushCarrierSenderId() _pushCarrierSenderIdMap - " + _pushCarrierSenderIdMap);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadPushCarrierSenderId(); Not able to load push carrier senderid", e);
			
			_pushCarrierSenderIdMap = null;
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
		
		return _pushCarrierSenderIdMap;
		
	}

	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static PushCarrierSenderId instance()
	{
		if(pushCarrierSenderId == null)
			pushCarrierSenderId = new PushCarrierSenderId();
		
		return pushCarrierSenderId;
		
	}

} // end of class CountryCode