package com.winnovature.unitia.util.datacache.singleton;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.dao.GenericDAO;


public class MessageEncryptTemplateMappingTon
{
static Log log = LogFactory.getLog(MessageEncryptTemplateMappingTon.class);
	
	private static MessageEncryptTemplateMappingTon singleton = new MessageEncryptTemplateMappingTon();
	private Map records = null;

	
	private MessageEncryptTemplateMappingTon()
	{
		initializeTemplateMasterMappingMap();
	}
	
	public static MessageEncryptTemplateMappingTon getInstance()
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
	
	public Map getAidWiseMap() {
		return (Map)records.get("aidwisemap");
	}
	
	public Map getIdWiseMap() {
		return (Map)records.get("idwisemap");
	}
	
	public synchronized void reloadFromDB() throws Exception
	{
		if(log.isDebugEnabled())
		log.debug("[TemplateMasterTableMappingTon].reloadFromDB()");
		GenericDAO dao = new GenericDAO();
		try
		{
			Map map = dao.getEncryptEnabledTemplates();
					
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
