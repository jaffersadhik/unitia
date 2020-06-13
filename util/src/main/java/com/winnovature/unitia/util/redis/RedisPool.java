package com.winnovature.unitia.util.redis;

import java.util.Properties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

public class RedisPool {

	public JedisPool createJedisPool(Properties prop) {
		try {

			String mDb = prop.getProperty("db");
			String mIp = prop.getProperty("ip");
			String mPass = prop.getProperty("password");
			String mPort = prop.getProperty("port");

			String maxpool = prop.getProperty("maxpool");
			String maxWait = prop.getProperty("maxwait");
			String timeout = prop.getProperty("timeout");

			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(Integer.parseInt(maxpool));
			config.setMaxWaitMillis(Long.parseLong(maxWait) * 1000l);
			if (mPass != null && mPass.trim().length() == 0)
				mPass = null;
			JedisPool ajedisPool = new JedisPool(config, mIp, Integer.parseInt(mPort), Integer.parseInt(timeout) * 1000,
					mPass, Integer.parseInt(mDb));
			return ajedisPool;
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

}
