package com.winnovature.unitia.util.datacache.routing;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.dao.GenericDAO;


public class RoutingTemplate
{
static Log log = LogFactory.getLog(RoutingTemplate.class);
	
	private static RoutingTemplate singleton = new RoutingTemplate();
	private Map records = null;

	
	private RoutingTemplate()
	{
		
	}
	
	public static RoutingTemplate getInstance()
	{
		return singleton;
	}
	
	public Map getInMemTemplateMasterMapping()
	{
		
		if(records == null)
			initializeTemplateMasterMappingMap();
		
		return records;
	}

	
	private void initializeTemplateMasterMappingMap()
	{
		
		try
		{
			reloadFromDB();
		}
		catch (Exception e)
		{
			//Logger.error("[InMemLLNPTon] " +e);
		}			
	}
	
	public synchronized void reloadFromDB() throws Exception
	{
		if(log.isDebugEnabled())
		log.debug("[TemplateMasterTableMappingTon].reloadFromDB()");
		GenericDAO dao = new GenericDAO();
		try
		{
			Map map = dao.getRoutingTemplate();
					
			if(map != null)
				records = map;
			else
			{
				log.error("[TemplateMasterTableMappingTon] ERROR : ***COULD NOT LOAD TEMPLATE_MASTER Mapping List FROM DATABASE******");
			}
				 
		}
		catch (Exception e)
		{
			log.error(e);
			throw e;
		}
	}
	
}
