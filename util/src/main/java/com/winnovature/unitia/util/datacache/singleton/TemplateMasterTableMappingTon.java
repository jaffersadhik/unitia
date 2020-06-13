package com.winnovature.unitia.util.datacache.singleton;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.dao.GenericDAO;


public class TemplateMasterTableMappingTon
{
static Log log = LogFactory.getLog(TemplateMasterTableMappingTon.class);
	
	private static TemplateMasterTableMappingTon singleton = new TemplateMasterTableMappingTon();
	private Map records = null;

	
	private TemplateMasterTableMappingTon()
	{
		
	}
	
	public static TemplateMasterTableMappingTon getInstance()
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
			Map map = dao.getTemplateMasterMapping();
					
			if(map != null &&map.size()>0 )
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
