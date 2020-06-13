package com.winnovature.unitia.util.redis;

import java.util.Map;

import com.winnovature.unitia.util.dao.Insert;

public class QueueSender {

	
	
	public boolean sendR(String queuename,Map<String,String> requestObject,boolean isRetry) {
		
		boolean result=false;
		
		
		result=new RedisWrite().rpushtoQueue(RedisQueuePool.getInstance().getPool(),queuename , requestObject) ;
			
		
		if(!result&&!isRetry) {
		
			result=new Insert().insert( queuename, requestObject);
		}
		
		return result;
				
				
		}

	
	public boolean sendL(String queuename,Map<String,String> requestObject,boolean isRetry,Map<String,String > logmap) {
		
		boolean result=false;
		
		if(RedisQueuePool.getInstance().isAvailableQueue(queuename,isRetry)){
		result=new RedisWrite().lpushtoQueue(RedisQueuePool.getInstance().getPool(),queuename , requestObject) ;
		}
		
		if(!result) {
		
			if(!isRetry){
			result=new Insert().insert(queuename, requestObject);
			logmap.put("queue type","mysql");

			}
		}else{
			
			logmap.put("queue type","redis");

		}
		
		return result;
				
				
		}

}
