package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;

public class SMSCMaxQueue {

	private static SMSCMaxQueue obj=null;
	
	private Map<String,String> smscmaxqueue=new HashMap<String,String>();
	
	private SMSCMaxQueue(){
	
		reload();
	}
	
	public static SMSCMaxQueue getInstance(){
		
		if(obj==null){
			
			obj=new SMSCMaxQueue();
		}
	
		return obj;
	}
	
	public void reload(){
	
		Map<String,String> result=new HashMap<String,String>();
		
		Connection connection=null;
		
		try{
			connection=RouteDBConnection.getInstance().getConnection();
			result.putAll(getKannelConfig(connection));
			result.putAll(getKannel(connection));

		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
		
		smscmaxqueue=result;
	}

	private Map<String,String> getKannelConfig(Connection connection) {
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		try{
			statement=connection.prepareStatement("select smscid,max_queue from kannel_config");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
			
				result.put(resultset.getString("smscid"), resultset.getString("max_queue"));
			}
		}catch(Exception e){
			
		}finally{
			
		}
		return result;
	}
	
	
	private Map<String,String> getKannel(Connection connection) {
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		try{
			statement=connection.prepareStatement("select smscid,max_queue from kannel");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
			
				result.put(resultset.getString("smscid"), resultset.getString("max_queue"));
			}
		}catch(Exception e){
			
		}finally{
			
		}
		return result;
	}
	
	
	public String getQueue(String smscid){
		
		String queuecount=smscmaxqueue.get(smscid);
		
		if(queuecount==null){
			
			queuecount="500";
		}
		
		return queuecount;
	}
}
