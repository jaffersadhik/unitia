package com.winnovature.unitia.util.redis;

import redis.clients.jedis.JedisPool;

public class TPSRedisConnectionPool {

	private static TPSRedisConnectionPool obj=null;
	

	TPSRedisPool redispool=null;

	private TPSRedisConnectionPool(){
		
		init();
	}
	
	private void init() {

		redispool=new TPSRedisPool("tpsredis");
		
	}

	public static TPSRedisConnectionPool getInstance(){
		
		if(obj==null){
			
			obj=new TPSRedisConnectionPool();
		}
		
		return obj;
	}

	public JedisPool getPool() {
		return redispool.getPool();
	}

	}
