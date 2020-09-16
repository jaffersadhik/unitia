package com.winnovature.unitia.util.misc;

import java.text.DecimalFormat;

import com.winnovature.unitia.util.redis.RedisCreditPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class CreditProcessor
{
	
	private static String BALANCE_KEY = "prepaid:bal" ;


    public boolean hasBalance(String username, double amount) 
    {
        JedisPool pool =RedisCreditPool.getInstance().getPool();
        Jedis jedis = null;
        Double decrementedVal = null;

        try
        {
             
            jedis = pool.getResource();
            
            
            if(jedis.exists(BALANCE_KEY)){
           
            decrementedVal = jedis.hincrByFloat(BALANCE_KEY, username, -amount);

            }else{
            	
            	return false;
            	
            }
           

            
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(Integer.parseInt("2"));
                decrementedVal = Double.parseDouble(df.format(decrementedVal));

            
            if(decrementedVal < 0)
            {
            	 jedis.hincrByFloat(BALANCE_KEY, username, amount);

                return false;
            }
           

           

        }
        catch (Exception e)
        {
           e.printStackTrace();

            return false;
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

        return true;
    }


    public void returnCredit(String username, double amount)
    {
        JedisPool pool = null;
        Jedis jedis = null;

        try
        {
            pool = RedisCreditPool.getInstance().getPool();
             
            jedis = pool.getResource();

            jedis.hincrByFloat(BALANCE_KEY, username, amount);       
         
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


   
    
   
    public boolean hasBalance(String username) throws Exception
    {
        JedisPool pool = RedisCreditPool.getInstance().getPool();
        Jedis jedis = null;
        String bal = null;
        boolean isBalAvailable = false;

        try
        {
         
            jedis = pool.getResource();
            try
            {
              
                if(jedis.exists(BALANCE_KEY)){

                bal = jedis.hget(BALANCE_KEY, username);
                
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            	throw e;
            }
            
           /*  Check the val. Value <0 denoted no credit and value >=0 denotes credit availability. */
            if(bal == null) {
            	return false;
            }
            
            int balI=Integer.parseInt(bal);
             if(balI <= 0) {
             	return false;

            }
            else {
            	return true;

            }

        }
        catch (Exception e)
        {
        	e.printStackTrace();
            throw e;
            
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

    public String getBalance(String username) throws Exception
    {
        JedisPool pool = RedisCreditPool.getInstance().getPool();
        Jedis jedis = null;
        String bal = null;

        try
        {
         
            jedis = pool.getResource();
            try
            {
              
                bal = jedis.hget(BALANCE_KEY, username);

            }
            catch (Exception e)
            {
            	e.printStackTrace();
            	throw e;
            }
            
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            throw e;
            
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

        return bal;
    }

}
