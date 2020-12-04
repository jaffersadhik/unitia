package com.winnovature.unitia.util.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class DLRQueue {

	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
		
	private static String SQL="select queuename,count,kannelid from queue_count_dlr where mode='"+MODE+"'";
	
	private static DLRQueue obj=new DLRQueue();
	
	private  Map<String,Map<String,String>> kannelmap=new HashMap<String,Map<String,String>>();

	
	private DLRQueue(){
		
	}
	
	public static DLRQueue getInstance(){
		
		if(obj==null){
			
			obj=new DLRQueue();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> temp=getMap();
		
		if(temp!=null){
			
			kannelmap=temp;
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
				String kannelid=resultset.getString("kannelid");

				Map<String,String> smscidmap=kannelmap.get(kannelid);
				
				if(smscidmap==null){
					smscidmap=new HashMap();
					kannelmap.put(kannelid, smscidmap);
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

	public Map<String, Map<String, String>> getKannelmap() {
		return kannelmap;
	}
	
	
}
