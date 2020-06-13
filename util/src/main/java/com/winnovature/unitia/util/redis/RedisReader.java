package com.winnovature.unitia.util.redis;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisReader {

public Map<String,String> getData(String queuename){
		
        JedisPool pool = null;
        Jedis jedis = null;
        byte[] bytes=null;
        Object result=null;
        try
        {
            pool = RedisQueuePool.getInstance().getPool();
            jedis = pool.getResource();
            List<byte[]> list=jedis.brpop(15, (queuename).getBytes("utf-8"));
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
        return (Map<String,String>)result;
    
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

}
