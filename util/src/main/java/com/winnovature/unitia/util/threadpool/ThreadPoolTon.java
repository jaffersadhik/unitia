package com.winnovature.unitia.util.threadpool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.dao.Table;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.processor.ScheduleProcessor;

public class ThreadPoolTon {

	private static final String WORKERPOOL = "create table workerpool(poolname varchar(50) PRIMARY KEY,pooltype varchar(3) default 'sms',poolsize varchar(2) default '2',maxpoolsize varchar(2) default '4',keepalivetime varchar(10) default '20',queuesize varchar(2) default '25',tablecount varchar(1) default '1',itime timestamp default CURRENT_TIMESTAMP)";

	private static ThreadPoolTon obj=null;
	
	
	Map<String,DBReceiver> dbreader=new HashMap<String,DBReceiver>();

	Map<String,RedisReceiver> redisreceiver=new HashMap<String,RedisReceiver>();
	
	Map<String,ThreadPool> pool=new HashMap<String,ThreadPool>();
	
	private ThreadPoolTon(){
		init();
	}
	
	private void init() {
		
		loadworkerpooltable();
		
		Map<String,Map<String,String>> config=getConfig();
		
		Iterator itr=config.keySet().iterator();
		
		while(itr.hasNext()){
			
			String poolname=itr.next().toString();
			Map<String,String> configmap=config.get(poolname);
			
			pool.put(poolname, new ThreadPool(poolname, configmap));
			
			
		}
		
	}
	
	
	private void loadworkerpooltable() {

		Connection connection =null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			 TableExsists table=new TableExsists();
		if (!table.isExsists(connection, table.getTableName(WORKERPOOL))) {
			if (table.create(connection, WORKERPOOL, false)) {

				table.insertworkerpool(connection);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(connection);
		}

	}

	
	public void reload(){
		
		Iterator itr=pool.keySet().iterator();
		
		Map<String,Map<String,String>> config=getConfig();

		
		while(itr.hasNext()){
			
			String poolname=itr.next().toString();
			
			ThreadPool poolworker=pool.get(poolname);
			
			Map<String,String> configmap=config.get(poolname);
			
			if(configmap==null){
	
				insertConfig(poolname,configmap);
				configmap=getDefaultConfig();
			}
			
			poolworker.resetConfig(configmap);
			
				
		}
		
		checkRedisDBReceiverAvailability();
	}

	
	private void checkRedisDBReceiverAvailability() {


		Iterator itr=pool.keySet().iterator();
		

		
		while(itr.hasNext()){
			
			String poolname=itr.next().toString();
			
			if(!redisreceiver.containsKey(poolname)){
				
				RedisReceiver obj=new RedisReceiver(poolname);
				redisreceiver.put(poolname,obj );
				obj.start();
			}
			
			if(!dbreader.containsKey(poolname)){
				
				if(!Table.getInstance().isAvailableTable(poolname)){
					
					Table.getInstance().addTable(poolname);
				}
				DBReceiver obj=new DBReceiver(poolname);
				
				dbreader.put(poolname, obj);
				obj.start();
				
			}
		}
	}

	private void insertConfig(String poolname, Map<String, String> configmap) {
		
		Connection connection=null;
		PreparedStatement statement=null;
		
		try{
		
			statement=connection.prepareStatement("insert into workerpool(poolname,poolsize,maxpoolsize,queuesize,tablecount) values(?,?,?,?,?)");
		    statement.setString(1, configmap.get("poolname"));  
		    statement.setString(2, configmap.get("poolsize"));  
		    statement.setString(3, configmap.get("maxpoolsize"));  
		    statement.setString(4, configmap.get("queuesize"));  
		    statement.setString(5, configmap.get("tablecount"));  
		    statement.execute();
			
		}catch(Exception e){
			
		}finally{
			Close.close(statement);
			Close.close(connection);
		}
		
	}

	private Map<String, String> getDefaultConfig() {
		Map<String,String> configmap =new HashMap<String,String>();
		
		configmap.put("poolsize", "1");
		configmap.put("maxpoolsize", "10");
		configmap.put("tablecount", "1");
		configmap.put("keepalivetime", "20");
		configmap.put("queuesize", "5");

		return configmap;
	}

	private Map<String, Map<String, String>> getConfig() {
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, Map<String, String>>  result=new HashMap<String, Map<String, String>>();
		try{

			connection=CoreDBConnection.getInstance().getConnection();
			statement =connection.prepareStatement("select poolname,poolsize,maxpoolsize,queuesize,keepalivetime,tablecount from workerpool");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				Map<String, String> data=new HashMap();
				data.put("poolsize", resultset.getString("poolsize"));
				data.put("maxpoolsize", resultset.getString("maxpoolsize"));
				data.put("queuesize", resultset.getString("queuesize"));
				data.put("keepalivetime", resultset.getString("keepalivetime"));
				data.put("tablecount", resultset.getString("tablecount"));
				result.put(resultset.getString("poolname"), data);
				
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return result;
	}

	public static ThreadPoolTon getInstance(){
		
		if(obj==null){
			
			obj=new ThreadPoolTon();
		
			
		}
		
		return obj;
	}
	
	public void doProcess(String poolname,String pooltype,Map<String,String> record){
	

		ThreadPool pool=getPool().get(poolname);
		
		if(pool==null){
			
			getPool().put(poolname,new ThreadPool(poolname, getDefaultConfig()));
			
			 pool=getPool().get(poolname);
		}
		
		if(pooltype.equals("sms")){
			
			pool.runTask(new SMSWorker( poolname, pooltype, record));
			
		}else if(pooltype.equals("billing")){
			
			pool.runTask(new BillingWorker( poolname, pooltype, record));
		}else if(pooltype.equals("schedule")){
			
			pool.runTask(new ScheduleWorker( poolname, pooltype, record));
		}
		
	}

	public Map<String, ThreadPool> getPool() {
		return pool;
	}

	
	
	public boolean isAvailable(String poolname){
		
		return pool.get(poolname).isAvailable();
	}
}
