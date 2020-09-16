package com.winnovature.unitia.util.redis;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class RedisWrite {

	public boolean lpushtoQueue(JedisPool pool, String key, Object requestObject) {

		Jedis jedis = null;
		boolean result = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput oo = null;

		try {
			jedis = pool.getResource();
			oo = new ObjectOutputStream(bos);
			oo.writeObject(requestObject);
			long cnt = jedis.lpush(key.getBytes("utf-8"), bos.toByteArray());
			if (cnt > 0) {
				result = true;
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
			try {
				oo.close();
			} catch (Exception e) {
			}
			try {
				bos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
		return result;
	}


	
	public boolean lpushtoQueue(JedisPool pool, String key, List<Map<String,String>> requestlist) {

		Jedis jedis = null;
		boolean result = false;
	
		try {
			jedis = pool.getResource();
			add(key, requestlist, jedis);
			result=true;
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
		return result;
	}

	 private void add(String key,List<Map<String,String>> requestlist, Jedis jedis) throws Exception
	   {
	     try
	     {
	       Pipeline pipe = jedis.pipelined();
	       pipe.multi();
	 
	 
	       for(int i=0,maxsize=requestlist.size();i<maxsize;i++) 
	       {
	         Object object = requestlist.get(i);
	 
	         add(pipe,key,object);
	        
	       }
	 
	       pipe.exec();
	       pipe.sync();
	     }
	     catch (Exception e)
	     {
	    	 throw e;
	     }
	   }
	
	private void add(Pipeline pipe,String key, Object object) throws Exception {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput oo = null;

		try {
			oo = new ObjectOutputStream(bos);
			oo.writeObject(object);
			pipe.lpush(key.getBytes("utf-8"), bos.toByteArray());
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw e;
			
		} finally {

			try {
				oo.close();
			} catch (Exception e) {
			}
			try {
				bos.close();
			} catch (Exception e) {
			}
		}

		
	}



	public boolean rpushtoQueue(JedisPool pool, String key, Object requestObject) {

		Jedis jedis = null;
		boolean result = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput oo = null;

		try {
			jedis = pool.getResource();
			oo = new ObjectOutputStream(bos);
			oo.writeObject(requestObject);
			long cnt = jedis.rpush(key.getBytes("utf-8"), bos.toByteArray());
			if (cnt > 0) {
				result = true;
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
			try {
				oo.close();
			} catch (Exception e) {
			}
			try {
				bos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
		return result;
	}

}
