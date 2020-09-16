package com.winnovature.unitia.util.redis;

import com.winnovature.unitia.util.misc.Prop;

import redis.clients.jedis.JedisPool;


public class RedisCreditPool {

	private static RedisCreditPool obj = new RedisCreditPool();

	private JedisPool pool=null;
	
	private RedisCreditPool() {

		createPool();
	}
	

	
	public static RedisCreditPool getInstance() {

		if (obj == null) {

			obj = new RedisCreditPool();
		}
		return obj;
	}

	
	private void createPool() {
		
		pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisCreditProp());
	}
	
	public JedisPool getPool() {
		
		return pool;
	}
	
	
}
