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


public class TemplateMsgRouter implements Constants
{

	/*
	 * The ClassName variable
	 */
	private static String className = "[MsgRouter]";
	
	/*
	 * The Logger Object
	 */
	Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * The Singleton Object
	 */
	private static TemplateMsgRouter msgRouter = new TemplateMsgRouter();
	
	/*
	 * The Memory holders
	 */
	private Map priorityMap = new HashMap();
	private String defaultopenroute = null;
	
	PropertiesConfiguration pc = null;
	
	/*
	 * The private Constructor
	 */
	private TemplateMsgRouter()
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
		priorityMap = loadPriorityRoute();
		this.defaultopenroute = getDefaultOpenRoute();
	}
		

	/**
	 * 
	 * Method : reload
	 *       usage : reload the memoery object
	 */
	public synchronized void reload()
	{
		//load();
		Map _tmpPriority = loadPriorityRoute();
		String defaultopenroute = getDefaultOpenRoute();
		
		
		if(_tmpPriority!=null){
		priorityMap = _tmpPriority;
		}
		if(defaultopenroute!=null){
		this.defaultopenroute = defaultopenroute;
		}
		
		_tmpPriority=null;
		
	}
	
	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage : returns the singleton object
	 */
	public static TemplateMsgRouter instance()
	{
		if(msgRouter == null)
			msgRouter = new TemplateMsgRouter();
		
		return msgRouter;
		
	}
	
	public Map getPriorityMap()
	{
		
		return priorityMap;
	}
	
	public String getDefaultTemplateRouteGroup()
	{
				return defaultopenroute;
	}
	
	/**
	 * 
	 * Method : loadLogicRoute
	 *       usage :
	 */
	private Map loadPriorityRoute() 
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Map _prtyMap = null;
		
		try
		{
			String sql = "select pid,aid,carrierid,circleid,groupid,countrycd,msgclass from msg_router where logicid<13 and upper(rtype)='TEMP'";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			_prtyMap = createPriorityMap(resultSet);
			
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
		

	/**
	 * 
	 * Method : loadRoundRobinRoute
	 *       usage : load the RoundRobin route
	 */
	private String getDefaultOpenRoute() 
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String groupid = null;
		
		try
		{
			String sql = "select groupid from msg_router where logicid=20";

			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			if(resultSet.next()){
			groupid = resultSet.getString("groupid");
			}
			
			if(logger.isDebugEnabled())
			logger.debug(className + "getDefaultOpenRoute() _rrMap - " + groupid);

		} 
		catch(Exception e)
		{
			logger.error(className + "loadRoundRobinRoute(); Not able to load round robin route", e);
			//SNMP TRAP Implementation
			groupid=null;
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
		
		return groupid;
		
	}
	
	/**
	 * 
	 * Method : createPriorityMap
	 * @param resultSet
	 * @throws Exception
	 *       usage : creating the priority route map
	 */
	private Map createPriorityMap(ResultSet resultSet) throws Exception 
	{
		Map _priorityMap = new HashMap();
		
		while(resultSet.next()) 
		{
			 
			String pid = resultSet.getString("pid") != null ?resultSet.getString("pid").trim(): NULL;
			String aid = resultSet.getString("aid") != null ?resultSet.getString("aid").trim(): NULL;
			String carrierId = resultSet.getString("carrierid") != null ?resultSet.getString("carrierid").trim(): NULL;
			String circleId = resultSet.getString("circleid") != null ?resultSet.getString("circleid").trim(): NULL;			
			String countrycd = resultSet.getString("countrycd") != null ?resultSet.getString("countrycd").trim(): NULL;
			String msgclass = resultSet.getString("msgclass") != null ?resultSet.getString("msgclass").trim(): NULL;
						 
			String _key = pid + "~" + aid + "~" + countrycd + "~" + carrierId + "~" + circleId+"~"+msgclass;
			String _value = resultSet.getString("groupid").trim();
			
			if(logger.isDebugEnabled()){
			logger.debug(className+"createPriorityMap() _key  - " + _key);
			logger.debug(className+"createPriorityMap() _value - " + _value);			
			}
			
			_priorityMap.put(_key,_value);
					
		}
		
		return _priorityMap;
		
	}

	
	
} // end of class InstanceRoute