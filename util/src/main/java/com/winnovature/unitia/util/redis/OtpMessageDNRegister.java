package com.winnovature.unitia.util.redis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.ToJsonString;

import redis.clients.jedis.Jedis;

public class OtpMessageDNRegister {

	public boolean isRegister(String msgid){

		Jedis jedis = null;
	
		String result=null;
		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			result= jedis.hget("messagedn", msgid);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			if (jedis != null) {
				try {
					jedis.close();

				} catch (Exception e) {
				}
			}
		
		}
		return result==null?false:true;
	
	}
	
	public void register(String msgid){

		Jedis jedis = null;
	
		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			
			jedis.hset("messagedn", msgid,""+System.currentTimeMillis());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			if (jedis != null) {
				try {
					jedis.close();

				} catch (Exception e) {
				}
			}
		
		}
	
	}
	

	public void removeOldAckid(){

		Jedis jedis = null;
	
		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			Map<String, String>  data=jedis.hgetAll("messagedn");
			
			if(data!=null&&data.size()>0){
			List<String> expiredackidlist=getList(data);
			
			if(expiredackidlist.size()>0){
				
				delete(expiredackidlist, jedis);
			}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			if (jedis != null) {
				try {
					jedis.close();

				} catch (Exception e) {
				}
			}
		
		}
	
	}

	private List<String> getList(Map<String, String> data) {
		
		List<String> ackidlist=new ArrayList<String>();
		
		Iterator itr=data.keySet().iterator();
		
		while(itr.hasNext()){
			
			String ackid=itr.next().toString();
			
			String value=data.get(ackid);
			
			if(expired(value)){
			
				ackidlist.add(ackid);
			}
			
		}
		return ackidlist;
	}

	private boolean expired(String value) {
		try{
			
			return Long.parseLong(value)<(System.currentTimeMillis()-5*60*1000);
			
		}catch(Exception e){
			
			return true;
		}
	}
	
	
	
	 private void delete(List<String> ackidlist, Jedis jedis)
	   {
	     try
	     {
	       for(int i=0,maxsize=ackidlist.size();i<maxsize;i++) 
	       {
	         String ackid = ackidlist.get(i);
	 
	         jedis.hdel("messagedn", ackid);
	        
	       }
	 
	      }
	     catch (Exception e)
	     {
	    	 e.printStackTrace();
	     }
	   }

	public void add(Map<String, Object> msgmap) {
		


		Jedis jedis = null;
	
		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			List<Map<String,Object>>  datalist=getData(jedis, msgmap);
			if(datalist==null){
				
				datalist=new ArrayList<Map<String,Object>>();
				
				if(!isKeyExsist(jedis, getKey(msgmap))){
					
					setExpiry(jedis, getKey(msgmap));
				}
			}
			datalist.add(msgmap);
			
			jedis.hset(getKey(msgmap), msgmap.get(MapKeys.MSGID).toString(), ToJsonString.toString(datalist));
		
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			if (jedis != null) {
				try {
					jedis.close();

				} catch (Exception e) {
				}
			}
		
		}
		
	}

	
	private void setExpiry(Jedis jedis, String key) {
		
		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			jedis.expire(key, 7*60);
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			
			
		} 
		
	}

	private boolean isKeyExsist(Jedis jedis, String key) {

		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			return jedis.exists(key);
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			
			
		} 
	
		return false;
	}

	private List<Map<String,Object>> getData(Jedis jedis,Map<String, Object> msgmap){

	
		try {
			jedis =RedisSmppBindPool.getInstance().getPool().getResource();
	
			String datalistjson=jedis.hget(getKey(msgmap),msgmap.get(MapKeys.MSGID).toString());
			
			if(datalistjson!=null){
				
				return ToJsonString.toList(datalistjson);
				
			}
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			
			
		} 
		
		return null;
	
	}

	private String getKey(Map<String, Object> msgmap) {
		
		try{
			
			String ktime=(String)msgmap.get(MapKeys.KTIME);
		
		
			SimpleDateFormat sdf=new SimpleDateFormat("HHmm");
		
			return "messagednlist_"+sdf.format(new Date(Long.parseLong(ktime)));
			
		}catch(Exception e){
		
			return null;
		}
		
		
	}

	
	
	}
