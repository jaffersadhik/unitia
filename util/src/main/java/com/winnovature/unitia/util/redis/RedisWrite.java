package com.winnovature.unitia.util.redis;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
