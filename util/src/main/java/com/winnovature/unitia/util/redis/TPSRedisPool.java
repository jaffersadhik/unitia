package com.winnovature.unitia.util.redis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.misc.RouterLog;
import com.winnovature.unitia.util.queue.RedisQueue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class TPSRedisPool {


	private static String MODE="";
	
	static {
		
		String mode=System.getenv("mode");
		
		if(mode==null||mode.trim().length()<1){
			
			mode="production";
		}
		
		
		MODE=mode+"_";

	}
	private JedisPool pool=null;
	
	private String redisid=null;
		
	public TPSRedisPool(String redisid) {

		Log.log("RedisQueuePool init redisid "+redisid);

		this.redisid=redisid;
		createPool();
		
	}
	
	




	private void createPool() {
		if(redisid.equals("redisqueue1")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueue1Prop());
	
		}else if(redisid.equals("redisqueue2")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueue2Prop());
	
		}else if(redisid.equals("redisqueue3")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueue3Prop());
	
		}else if(redisid.equals("redisqueue4")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueue4Prop());
	
		}else if(redisid.equals("redisqueue5")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueue5Prop());
	
		}else if(redisid.equals("redisqueue6")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueue6Prop());
	
		}else if(redisid.equals("rq2")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRQ2Prop());
	
		}else if(redisid.equals("rq3")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRQ3Prop());
	
		}else if(redisid.equals("rq4")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRQ4Prop());
	
		}else if(redisid.equals("rq5")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRQ5Prop());
	
		}else if(redisid.equals("rq6")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getRQ6Prop());
	
		}else if(redisid.equals("tpsredis")){
			pool= new RedisPool().createJedisPool(Prop.getInstance().getTpsedisProp());
	
		}
		
		Log.log("RedisQueuePool createPool pool "+pool);
	}
	
	public JedisPool getPool() {
		
		return pool;
	}
}
