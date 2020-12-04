package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class kannelQueue {

	static{
		

			Connection connection=null;
			
			try{
				connection=CoreDBConnection.getInstance().getConnection();
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "queue_count_smscid")){
					
					table.create(connection, "create table queue_count_smscid(smscid varchar(50),queuecount numeric(10,0),status varchar(20),updatetime numeric(13,0))", false);
				}
				
				
				
			}catch(Exception e){
				 e.printStackTrace();
			}finally{
			
				Close.close(connection);
			}
			
		
	}

	private static kannelQueue object=new kannelQueue();
	private kannelQueue(){
		
		reload();
	}
	
	public static kannelQueue getInstance(){
		
		if(object==null){
			
			object=new kannelQueue();
		}
		
		return object;
	}
	Map<String,Map<String,String>> smscqueue=new HashMap<String,Map<String,String>>();
	
	public void reload() {
		Map<String,Map<String,String>> temp=getQueuecountFromDB();
		if(temp!=null){
		smscqueue=temp;
		}
		
	}
	
	
public  Map<String,Map<String,String>> getQueuecountFromDB() {
		
	Map<String,Map<String,String>> smscqueue=new HashMap<String,Map<String,String>>();
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try
		
		{
			

			
			connection =CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select smscid,queuecount,status from queue_count_smscid");
			resultset=statement.executeQuery();
			Iterator itr=smscqueue.keySet().iterator();
			
			while(resultset.next()){
				
				String smscid=resultset.getString("smscid");
				String queuecount=resultset.getString("queuecount");
				String status=resultset.getString("status");

				 Map<String,String> data=new HashMap<String,String>();
				 data.put("queued", queuecount);
				 data.put("status", status);
				 smscqueue.put(smscid, data);
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return smscqueue;
	}
	 
	 public boolean isQueued(String smscid){
		 
		 Map<String,String> data=smscqueue.get(smscid);
		 
		 if(data==null){
			 
			 return false;
		 }
		 
		 String status=data.get("status");
		 
		 if(status==null){
			 
			 return false;
		 }
		 
		 
		 if(status.equals("down")){
			 
			 return true;
		 }
		 
		 
		 String queued=data.get("queued");
		 
		 if(queued==null){
			 
			 return false;
		 }
		 
		 long lQueued=Long.parseLong(queued);
		 
		 return lQueued>2500;
	 }
}

