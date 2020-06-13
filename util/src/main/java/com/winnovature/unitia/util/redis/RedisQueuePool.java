package com.winnovature.unitia.util.redis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.threadpool.SMSWorkerPoolRouter;
import com.winnovature.unitia.util.threadpool.ThreadPoolTon;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisQueuePool {

	private static RedisQueuePool obj = new RedisQueuePool();

	private JedisPool pool=null;
	
	private Set<String> unavailablequeue=new HashSet<String>();
	
	private Set<String> unavailableretryqueue=new HashSet<String>();
	

	private Map<String, String> queueCount=new HashMap<String, String>();
		
	private RedisQueuePool() {

		createPool();
		checkQueueTableAvailable();
		reload();
		
		new T().start();
	}

	private void checkQueueTableAvailable() {
		
		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "redis_queue_count")){
				
				table.create(connection, "create table redis_queue_count(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0))", false);
			}
		}catch(Exception e){
			
		}finally{
		
			Close.close(connection);
		}
		
	}

	public void reload() {
		
		setQueueCountMap();
	}
	
	public static RedisQueuePool getInstance() {

		if (obj == null) {

			obj = new RedisQueuePool();
		}
		return obj;
	}

	
	private void createPool() {
		
		pool= new RedisPool().createJedisPool(Prop.getInstance().getRedisQueueProp());
	}
	
	public JedisPool getPool() {
		
		return pool;
	}
	public long getQueueCount(String queuename) {
		if(queueCount.containsKey(queuename)) {
		return Long.parseLong(queueCount.get(queuename));
		}else {
			
		return 0;	
		}
		
	}

	

	private boolean isAvailable() {
		Jedis jedis=null;
	try {
	     jedis = pool.getResource();
	    return true;
	} catch (Exception e) {
	    // Not connected
	}finally {
		try {
			
			jedis.close();
			
		}catch(Exception ignore) {
			
		}
	}
	
	return false;
	}
	
	
	private Set<String> getKey(JedisPool pool) {
		Jedis jedis=null;
	
		try{
	    
			jedis = pool.getResource();

			return jedis.keys("*");
	     
			}catch (Exception e) {
			}finally {
	

		try {
			
			jedis.close();
			
		}catch(Exception ignore) {
			
		}
	}
	
	
	return null;
	}
	

	private void setQueueCountMap() {

		if(isAvailable()) {
		Set<String> keys=getKey(pool);
		
		Map<String,String> queuecountmap=getQueueMap(pool,keys);
		
		this.queueCount=queuecountmap;
		
		}else {
			
			this.queueCount=new HashMap();
		}
	}


	
	private long getCount(JedisPool pool, String queuename) {
		Jedis jedis=null;
	try {
	     jedis = pool.getResource();
	     
	     return jedis.llen(queuename);

	} catch (Exception e) {
	    // Not connected
	}finally {
		try {
			
			jedis.close();
			
		}catch(Exception ignore) {
			
		}
	}
	
	return 0;
	
	}

	private Map<String, String> getQueueMap(JedisPool pool, Set<String> keys) {

		Map<String, String> queuemap=new HashMap<String, String>();
		
		Set<String> unavailablequeue=new HashSet<String>();
		
		Set<String> unavailableretryqueue=new HashSet<String>();

		long totalqueue=0L;
		
		if(keys!=null) {
			
			Iterator itr=keys.iterator();
			
			while(itr.hasNext()) {
				
				String queuename=itr.next().toString();
				long count= getCount(pool,queuename);
				
				long maxcount=Long.parseLong(ConfigParams.getInstance().getProperty(ConfigKey.MAX_QUEUE));

				long maxretrycount=Long.parseLong(ConfigParams.getInstance().getProperty(ConfigKey.MAX_RETRY_QUEUE));

				if(count>maxcount){
					unavailablequeue.add(queuename);
				}
				if(count> maxretrycount){
					unavailableretryqueue.add(queuename);
				}
				totalqueue+=count;
				queuemap.put(queuename, Long.toString(count));
			}
		}
		
		this.unavailablequeue=unavailablequeue;
		this.unavailableretryqueue=unavailableretryqueue;
		queuemap.put("allqueuecount", Long.toString(totalqueue));
		return queuemap;
	}
	
	public boolean isAvailableQueue(String queuename,boolean isRetry) {
		
		if(isRetry){
			return !unavailableretryqueue.contains(queuename);

		}else{
			return !unavailablequeue.contains(queuename);
		}
	}

	class T extends Thread{
		
		long updateTime=0;
		public void run(){
			
			while(true){
				
				try{
				obj.reload();
				long currenttime=System.currentTimeMillis();
				long diff=currenttime-updateTime;
				if(diff>(1*1000)){

					obj.insertQueueintoDB(currenttime);
					updateTime=currenttime;

				}
				
				ThreadPoolTon.getInstance().reload();
				SMSWorkerPoolRouter.getInstance().reload();
				
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				gotosleep();
			}
		}
	}

	public void gotosleep() {
		
		try{
			
			Thread.sleep(10L);
		}catch(Exception e){
			
		}
		
	}

	public void insertQueueintoDB(long updatetime) {
		
		Connection connection=null;
		PreparedStatement statement=null;
		PreparedStatement statement1=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("insert into redis_queue_count(queuename,count ,updatetime ) values(?,?,?)");
			statement1=connection.prepareStatement("delete from redis_queue_count where updatetime<?");
			connection.setAutoCommit(false);
			
			Iterator itr=queueCount.keySet().iterator();
			
			while(itr.hasNext()){
				
				String queuename=itr.next().toString();
				String count=queueCount.get(queuename);
				statement.setString(1, queuename);
				statement.setString(2, count);
				statement.setString(3, ""+updatetime);
				statement.addBatch();
				
			}
			
			statement.executeBatch();
			connection.commit();
			statement1.setLong(1, updatetime);
			statement1.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(statement1);
			Close.close(statement);
			Close.close(connection);
		}
	}
	
	
}
