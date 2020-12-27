package com.winnovature.unitia.util.redis;

import com.winnovature.unitia.util.misc.Prop;

import redis.clients.jedis.JedisPool;


public class RedisSmppBindPool {

	private static RedisSmppBindPool obj = new RedisSmppBindPool();

	private JedisPool pool=null;
	
	private RedisSmppBindPool() {
		createPool();

	}
	

	
	public static RedisSmppBindPool getInstance() {

		if (obj == null) {

			obj = new RedisSmppBindPool();
		}
		return obj;
	}

	
	private void createPool() {
		
		pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisSmppBindProp());
	}
	
	public JedisPool getPool() {
		
		return pool;
	}
	
	
}
