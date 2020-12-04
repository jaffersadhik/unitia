package com.winnovature.unitia.util.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class RedisQueue {

	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
		
	private static String SQL="select queuename,count,redisid from queue_count_redis";
	
	private static RedisQueue obj=new RedisQueue();
	
	private  Map<String,Map<String,String>> redisqueuemap=new HashMap<String,Map<String,String>>();

	
	private RedisQueue(){
		
	}
	
	public static RedisQueue getInstance(){
		
		if(obj==null){
			
			obj=new RedisQueue();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> temp=getMap();
		
		if(temp!=null){
			
			redisqueuemap=temp;
		}
		
	}

	private Map<String, Map<String, String>> getMap() {
		
		Map<String,Map<String,String>> kannelmap=new HashMap<String,Map<String,String>>();
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(SQL);
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String smscid=resultset.getString("queuename");
				String count=resultset.getString("count");
				String redisid=resultset.getString("redisid");

				Map<String,String> smscidmap=kannelmap.get(redisid);
				
				if(smscidmap==null){
					smscidmap=new HashMap();
					kannelmap.put(redisid, smscidmap);
				}
				
				smscidmap.put(smscid, count);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		return kannelmap;
	}

	public boolean isQueued(String redisid,String queuename,boolean isRetry){
	
		Map<String,String> map=redisqueuemap.get(redisid);
		
		if(map==null){
			
			return false;
		}
		
		
		String count=map.get(queuename);
		
		int size=0;
		
		try{
			
			size=Integer.parseInt(count);
			
		}catch(Exception e){
			
		}
		
		if(queuename.startsWith("smpp")){
			
			if(size>100){
				
				return true;
			}else{
				
				return false;
			}
		}else{
			
			if(isRetry){
				
				if(size>6000){
							
					return true;
				}else{
					
					return false;
					
				}
			}else{
				
				
				if(size>10){
					
					return true;
					
				}else{
					
					return false;
				}
			}
		}
	}
	
	
}
