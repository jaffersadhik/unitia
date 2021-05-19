package com.winnovature.unitia.util.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.RedisInstance;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.queue.RedisQueue;

import redis.clients.jedis.JedisPool;

public class TPSRedisConnectionPool {

	private static TPSRedisConnectionPool obj=null;
	

	RedisQueuePool redispool=null;

	private TPSRedisConnectionPool(){
		
		init();
	}
	
	private void init() {

		redispool=new RedisQueuePool("tpsredis");
		
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
