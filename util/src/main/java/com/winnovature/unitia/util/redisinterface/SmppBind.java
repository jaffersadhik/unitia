package com.winnovature.unitia.util.redisinterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.misc.FileWrite;

public class SmppBind {
	
	private static SmppBind obj=null;
	
	private Map<String,Map<String,Object>> session=new HashMap<String,Map<String,Object>> ();
	
	private SmppBind(){

	}
	
	public static SmppBind getInstance(){
		
		if(obj==null){
			
			obj=new SmppBind();
		
		}
		
		return obj;
	}
	
	
	public long getBindCount(String systemid,long increment){

		
		
		Map<String,Object> data=session.get(systemid);
		
		if(data==null){
			
			data=new HashMap<String,Object>();
			data.put("sessioncount", "0");
			data.put("ipset", new HashSet());
			session.put(systemid, data);			
		}
		
		String sessioncount=(String)data.get("sessioncount");
		
		try{
			sessioncount=""+(Long.parseLong(sessioncount)+increment);
		}catch(Exception e){
			
		}
		
		
		data.put("sessioncount", sessioncount);
		session.put(systemid, data);			

		
		return Long.parseLong(sessioncount);
	}
	
	public int getBindCount(String systemid){

		
		Map<String,Object> data=session.get(systemid);
		
		if(data==null){
			
			data=new HashMap<String,Object>();
			data.put("sessioncount", "0");
			data.put("ipset", new HashSet());
			session.put(systemid, data);			
		}
		
		String sessioncount=(String)data.get("sessioncount");
		session.put(systemid, data);			

		return Integer.parseInt(sessioncount);
	}

	public void print() {
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", "sys");
		logmap.put("logname", "livesession");
		logmap.put("livesession", session);
		new FileWrite().write(logmap);
		
	}

	public int getBindCount(String systemId, int increment, String host) {

		
		Map<String,Object> data=session.get(systemId);
		
		if(data==null){
			
			data=new HashMap<String,Object>();
			data.put("sessioncount", "0");
			data.put("ipset", new HashSet());
			session.put(systemId, data);			
		}
		
		Set ipset=(Set)data.get("ipset");
		
		ipset.add(host);
		
		data.put("ipset",ipset);

		String sessioncount=(String)data.get("sessioncount");
		
		
		try{
			sessioncount=""+(Long.parseLong(sessioncount)+increment);
		}catch(Exception e){
			
		}
		data.put("sessioncount", sessioncount);

		session.put(systemId, data);			

		return Integer.parseInt(sessioncount);
	}
}
