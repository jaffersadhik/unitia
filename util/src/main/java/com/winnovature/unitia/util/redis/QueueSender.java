package com.winnovature.unitia.util.redis;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.dao.Insert;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

public class QueueSender {

	private static String MODE="";
	
	static {
		
		String mode=System.getenv("mode");
		
		if(mode==null||mode.trim().length()<1){
			
			MODE="production";
		}else{
			
			MODE=mode;
		}
		
		
		MODE=MODE+"_";

	}
	
	

	
	public boolean sendL(String queuename,Map<String,Object> requestObject,boolean isRetry,Map<String,Object > logmap) {
		
		boolean result=false;
		
		String redisid=null;
		
		long start=System.currentTimeMillis();
		
		if(queuename.equals("otppool")||queuename.equals("kannelretrypool")||queuename.equals("otpretrypool")||queuename.equals("dnretrypool")||queuename.startsWith("smppdn_")){
			
			redisid="redisqueue1";
			if(!RedisQueueConnectionPool.getInstance().isAvilable(redisid, queuename, isRetry)){
				
				redisid=null;
			}
		}else{
			
			redisid=RedisQueueConnectionPool.getInstance().getRedisId(queuename,isRetry);
		}
		if(redisid!=null){
			
			if(queuename.startsWith("smppdn_")){
			
				redisid=RedisQueueConnectionPool.getInstance().getRedisId(redisid,queuename,isRetry);

			}
			if(redisid!=null){
				result=new RedisWrite().lpushtoQueue(RedisQueueConnectionPool.getInstance().getPool(redisid,queuename),MODE+queuename , requestObject) ;
			}
		}
		
		if(!result) {
		
			if(!isRetry){
			result=new Insert().insert(queuename, requestObject);
			logmap.put("queue type","mysql");

			}
		}else{
			
			logmap.put("queue type","redis");

		}
		

		long end=System.currentTimeMillis();
		


		return result;
				
				
		}

	

}
