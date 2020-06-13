package com.winnovature.unitia.util.datacache.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RRDBRouteRoundRobinTon
{
	private final static RRDBRouteRoundRobinTon obj = new RRDBRouteRoundRobinTon();
	private Map map = new ConcurrentHashMap();
	String classNameSNMP = " util.datacache.instance.RRDBRouteRoundRobinTon";

	private RRDBRouteRoundRobinTon()
	{
		
	}
	
	public static RRDBRouteRoundRobinTon getInstance()
	{
		return obj;
	}
	/*
	 * 	Key - instanceid~msgtype 
	 */
	public int getCurrentIndex(String key,int totalInstance)
	{
		Integer iobj = (Integer)map.get(key);
		if(iobj == null)
		{
			iobj = new Integer("0");
			map.put(key, iobj);
		}
		
		int i = iobj.intValue();
		if((i+1) >= totalInstance)	// Reset from begining
		{
			map.put(key, new Integer("0"));
		}
		else // Increment the index and update the map
		{
			String s = "" + (i+1);
			map.put(key, new Integer(s));
		}
		
		if(i >= totalInstance)	i=totalInstance-1;	// Just to make sure i doesnt exceed valid index(might occur during concurrency)
		
		return i;
	}
}
