package com.winnovature.unitia.util.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.winnovature.unitia.util.redis.TPSRedisConnectionPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class TPSProcessor
{
	


    public long getTPS(String smscid) 
    {
        JedisPool pool =TPSRedisConnectionPool.getInstance().getPool();
        Jedis jedis = null;
        long decrementedVal = 0;

        try
        {
             
            jedis = pool.getResource();
            
            SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmss");
           String date=sdf.format(new Date());
            if(!jedis.hexists(smscid,date)){
                
            	jedis.hset(smscid, date, "1");

           }
            
            decrementedVal = jedis.hincrBy(smscid,date, 1);

         }
        catch (Exception e)
        {
           e.printStackTrace();

        }
        finally
        {
            if (jedis != null)
                try
                {
                	jedis.close();
                }
                catch (Exception e)
                {
                
                	e.printStackTrace();
                }
        }

        return decrementedVal;
    }




    public void delete(String smscid) 
    {
        JedisPool pool =TPSRedisConnectionPool.getInstance().getPool();
        Jedis jedis = null;

        try
        {
             
            jedis = pool.getResource();
            
            
           
            jedis.del(smscid);

         }
        catch (Exception e)
        {
           e.printStackTrace();

        }
        finally
        {
            if (jedis != null)
                try
                {
                	jedis.close();
                }
                catch (Exception e)
                {
                
                	e.printStackTrace();
                }
        }

    }


}
