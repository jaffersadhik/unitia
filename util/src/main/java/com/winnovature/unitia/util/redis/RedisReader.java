package com.winnovature.unitia.util.redis;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisReader {

	private static String MODE="";
	
	static {
		
		String mode=System.getenv("mode");
		
		if(mode==null||mode.trim().length()<1){
			
			mode="production";
		}
		
		
		MODE=mode+"_";

	}
public Map<String,Object> getData(String queuename,String redisid){
		

    long start=System.currentTimeMillis();
    
        JedisPool pool = null;
        Jedis jedis = null;
        byte[] bytes=null;
        Object result=null;
        try
        {
            pool = RedisQueueConnectionPool.getInstance().getPool(redisid,queuename);
            jedis = pool.getResource();
            List<byte[]> list=jedis.brpop(15, (MODE+queuename).getBytes("utf-8"));
           if(list!=null){
            bytes = list.get(1);
            }
            if(bytes==null || bytes.length == 0)
            {
            	result=null;
            }
            else
            {
            	result=consume(bytes);
            }
        }
        catch (Exception e)
        {

        	System.err.println(new Date()+" \t "+ErrorMessage.getMessage(e));
        	
        	try {
				Thread.sleep(1000L);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        }finally{
        	if (jedis != null){
				try {
					jedis.close();
					
				}
				catch (Exception e) {

					e.printStackTrace();
				}
		}
        }
        
        long end=System.currentTimeMillis();
        
//    	stats(queuename,redisid,start,end);

        return (Map<String,Object>)result;
    
	}
	


	 private void stats(String queuename,String redisid,long start,long end) {
	
		 Map<String,Object> logmap1=new HashMap<String,Object>();
		 
			logmap1.put("username", "sys");
			logmap1.put("redisreadtime",""+ (end-start));
			logmap1.put("queuename", queuename);
			logmap1.put("redisid", redisid);
			logmap1.put("logname", "redisreadtime");


	        new FileWrite().write(logmap1);
	
}


	private Object consume(byte[] bytes) throws Exception
	    {
	        ByteArrayInputStream bis = null;
	        ObjectInput in = null;

	        bis = new ByteArrayInputStream(bytes);

	        in = new ObjectInputStream(bis);
	        Object dtoobj =  in.readObject();
	        in.close();
	        bis.close();
	        return dtoobj;
	    }


	public Map<String, Object> getData(String queuename) {

        long start=System.currentTimeMillis();
        String redisid="NULL";
        JedisPool pool = null;
        Jedis jedis = null;
        byte[] bytes=null;
        Object result=null;
        try
        {
        	redisid=RedisQueueConnectionPool.getInstance().getRedisIdForReader(queuename);
        	
        	if(queuename.startsWith("smppdn_")){
        		
        		redisid="redisqueue1";
        	}
        	if(redisid!=null){
            pool = RedisQueueConnectionPool.getInstance().getPool(redisid,queuename);
            jedis = pool.getResource();
            List<byte[]> list=jedis.brpop(0, (MODE+queuename).getBytes("utf-8"));
           if(list!=null){
            bytes = list.get(1);
            }
            if(bytes==null || bytes.length == 0)
            {
            	result=null;
            }
            else
            {
            	result=consume(bytes);
            }
        	}
        }
        catch (Exception e)
        {

        	e.printStackTrace();
        	
        }finally{
        	if (jedis != null){
				try {
					jedis.close();
					
				}
				catch (Exception e) {

					e.printStackTrace();
				}
		}
        }
        
        long end=System.currentTimeMillis();
        
     	stats(queuename,redisid,start,end);

        return (Map<String,Object>)result;
    
	
	}

}
