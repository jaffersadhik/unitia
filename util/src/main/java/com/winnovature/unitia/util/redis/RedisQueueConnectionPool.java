package com.winnovature.unitia.util.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.RedisInstance;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.queue.RedisQueue;

import redis.clients.jedis.JedisPool;

public class RedisQueueConnectionPool {

	private static RedisQueueConnectionPool obj=null;
	
	private List<RedisQueuePool> redisqueuelist=null;
	private Map<String,RedisQueuePool> redisqueuemap=null;

	private RedisQueueConnectionPool(){
		
		init();
	}
	
	private void init() {
		Log.log("RedisQueueConnectionPool init ");
		
		List<String> redisidlist=RedisInstance.getInstance().getRedisInstanceList();
		
		redisqueuelist=new ArrayList<RedisQueuePool>();
		redisqueuemap=new HashMap<String,RedisQueuePool>();
		for(int i=0;i<redisidlist.size();i++){
			
			RedisQueuePool queue1=new RedisQueuePool(redisidlist.get(i));
			redisqueuelist.add(queue1);
			redisqueuemap.put(queue1.getRedisId(), queue1);
			
		}
	}

	public static RedisQueueConnectionPool getInstance(){
		
		if(obj==null){
			
			obj=new RedisQueueConnectionPool();
		}
		
		return obj;
	}

	public JedisPool getPool(String redisid,String queuename) {
		return redisqueuemap.get(redisid).getPool();
	}

	public  boolean isAvilable(String redisid,String queuename,boolean isRetry,Map<String,Object> logmap){
		
		return redisqueuemap.get(redisid).isAvailableQueue(redisid,queuename, isRetry,logmap);
	}
	
	public String getRedisId(String queuename, boolean isRetry,Map<String,Object> logmap) {
		int pointer=RoundRobinTon.getInstance().getCurrentIndex("redisqueuepoint"+queuename+"avilability", redisqueuelist.size());
		String redisid=redisqueuelist.get(pointer).getRedisId();
		if(redisqueuelist.get(pointer).isAvailableQueue(redisid,queuename, isRetry,logmap)){
			
			return redisqueuelist.get(pointer).getRedisId();
		}
		
		return null;
	}
	
	
	
	public String getRedisId(String redisid,String queuename, boolean isRetry,Map<String,Object> logmap) {

		if(!redisqueuemap.get(redisid).isAvailableQueue(redisid,queuename, isRetry,logmap)){
			
			return redisid;
		}
		return null;
	}

	public void reload() {
	
		RedisQueue.getInstance().reload();
	}

	public String getRedisIdForReader(String queuename) {
		int pointer=RoundRobinTon.getInstance().getCurrentIndex("redisqueuepoint"+queuename+"avilabilityforreader", redisqueuelist.size());
		
		if(redisqueuelist.get(pointer).isAvailableForReadingQueue(queuename)){
			
			return redisqueuelist.get(pointer).getRedisId();
		}	
		return null;
	}
	
	public Map<String,RedisQueuePool> getPoolMap(){
		
		return redisqueuemap;
	}
	
	public List<Map<String,String>> getQueueList(){
		
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		
		for(int i=0,max=redisqueuelist.size();i<max;i++){
			
			list.add(redisqueuelist.get(i).getQueueCount());
		}
		return list;
	}
	
	public void print(){
		
		for(int i=0,max=redisqueuelist.size();i<max;i++){
			
			redisqueuelist.get(i).print();
		}
	}

	public void reloadnew() {
		

		
		for(int i=0,max=redisqueuelist.size();i<max;i++){
			
			redisqueuelist.get(i).reload();
		}
		
	}
}
