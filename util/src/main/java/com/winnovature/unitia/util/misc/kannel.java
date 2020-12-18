package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.queue.kannelQueue;

public class kannel {

	static Map<String,Map<String,String>> smscqueue=new HashMap<String,Map<String,String>>();
	
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
	public static void reload() {
		
		kannelQueue.reload();

}
	
	
	 
	 
	 
	 public static boolean isQueued(String smscid){
		 
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
		 
		 
		 String maxqueue=SMSCMaxQueue.getInstance().getQueue(smscid);
		 
		 long lMaxQueue=Long.parseLong(maxqueue);
		 
		 long lQueued=Long.parseLong(queued);
		 
		 return lQueued>lMaxQueue;
	 }
}

