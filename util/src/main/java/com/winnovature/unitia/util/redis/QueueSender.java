package com.winnovature.unitia.util.redis;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dao.Insert;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.RouterLog;

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
		queuename=getQueueName(queuename,requestObject);
		if(queuename.startsWith("smppdn_")){
			
			redisid="redisqueue1";
			if(!RedisQueueConnectionPool.getInstance().isAvilable(redisid, queuename, isRetry,logmap)){
				
				redisid=null;
			}
		}else if(queuename.equals("otppool")||queuename.equals("kannelretrypool")||queuename.equals("otpretrypool")||queuename.equals("dnretrypool")){
			
			redisid="redisqueue1";
			
			Map<String,Object> logmap1=new HashMap<String,Object>();

			logmap1.put("username","sys");

			logmap1.put("request", requestObject);
			
			logmap1.put("logname", queuename+"_receiver");

			new FileWrite().write(logmap1);
			
		}else{
		
			
			redisid=RedisQueueConnectionPool.getInstance().getRedisId(queuename,isRetry,logmap);
		}
		

		if(redisid!=null){
			
		
				result=new RedisWrite().lpushtoQueue(RedisQueueConnectionPool.getInstance().getPool(redisid,queuename),MODE+queuename , requestObject) ;
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




	private String getQueueName(String queuename, Map<String, Object> requestObject) {

		if(queuename.equalsIgnoreCase("commonpool")||queuename.startsWith("kl_")){
			
			return queuename+"_"+PushAccount.instance().getPushAccount(requestObject.get(MapKeys.USERNAME).toString()).get("priority");
		}
		return queuename;
	}

	

}
