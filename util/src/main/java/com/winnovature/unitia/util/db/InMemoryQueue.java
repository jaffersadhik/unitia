package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.Prop;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class InMemoryQueue {


	static String ID=ACKIdGenerator.getAckId();
	
	static boolean availability =false;
	


	private void checkQueueTableAvailable() {

		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "queue_count_inmemory")){
				
				table.create(connection, "create table queue_count_inmemory(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0),appid varchar(50),unique(appid,queuename)  )", false);
				
				availability=true;
				
			}
		}catch(Exception e){
			 e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
		
	}


	
	public void insertQueueintoDB(Map<String,BlockingQueue> availavleTable) {
		
		Connection connection=null;
		
		try
		
		{
			

			if(!availability){
				
				checkQueueTableAvailable();
			}
			
			connection =CoreDBConnection.getInstance().getConnection();
			Iterator itr=availavleTable.keySet().iterator();
			
			while(itr.hasNext()){
				
				String queuename=itr.next().toString();
				String count=""+availavleTable.get(queuename).size();
				if(updateDB(connection, queuename, count)<1){
					
					insertQueueintoDB(connection, queuename, count);
				}

				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}

public void insertQueueintoDB(Connection connection,String queuename,String count) {
		

		PreparedStatement insert=null;
		
		try
		
		{
						long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into queue_count_inmemory(queuename,count ,updatetime,appid ) values(?,?,?,?)");
			insert.setString(1, queuename);
			insert.setString(2, count);
			insert.setString(3, ""+updatetime);
			insert.setString(4, ID);

			insert.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(insert);
		}
	}

	
	public int updateDB(Connection connection,String queuename,String count) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update queue_count_inmemory set count=?,updatetime=? where queuename=? and appid=?");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, ""+queuename);
			update.setString(4, ID);
			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}



	
	
}
