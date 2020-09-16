package kannelconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class TableCount {

	private static Map<String,String> queuemaxCount=new HashMap<String,String>();
	
	private static String MODE="";
	
	static {
		
		String mode=System.getenv("mode");
		
		if(mode==null||mode.trim().length()<1){
			
			MODE="production";
		}else{
			
			MODE=mode;
		}
		
		

	}
	
	
public void insertQueueMaxintoDB() {
		

		
		Connection connection=null;
		
		try
		
		{
			

			
			connection =CoreDBConnection.getInstance().getConnection();
			Iterator itr=queuemaxCount.keySet().iterator();
			
			while(itr.hasNext()){
				
				String queuename=itr.next().toString();
				String count=queuemaxCount.get(queuename);
				if(updateMaxDB(connection, queuename, count)<1){
					
					insertQueueMaxintoDB(connection, queuename, count);
				}

				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	
		
	}



	private void insertQueueMaxintoDB(Connection connection, String queuename, String count) {
		

		

			PreparedStatement insert=null;
			
			try
			
			{
							long updatetime=System.currentTimeMillis();
				
				insert=connection.prepareStatement("insert into queue_max_mysql(queuename,count ,updatetime,mode ) values(?,?,?,?)");
				insert.setString(1, queuename);
				insert.setString(2, count);
				insert.setString(3, ""+updatetime);
				insert.setString(4, MODE);

				insert.execute();
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				Close.close(insert);
			}
		
		
	}



	private int updateMaxDB(Connection connection, String queuename, String count) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update queue_max_mysql set count=?,updatetime=? where queuename=? and mode=?");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, ""+queuename);
			update.setString(4, MODE);
			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}


	
 public void setQueueMaxCountMap(Map<String,String> queueCount) {
		
		Iterator itr=queueCount.keySet().iterator();
		
		while(itr.hasNext()){
			
			String queuename=itr.next().toString();
			
			String count=queueCount.get(queuename);
			
			String maxcount=queuemaxCount.get(queuename);
			
			if(maxcount==null){
				
				queuemaxCount.put(queuename, count);
				
				continue;
			}
			
			try{
				int countInt=Integer.parseInt(count);
				
				int maxcountInt=Integer.parseInt(maxcount);
				
				if(countInt>maxcountInt){
					
					queuemaxCount.put(queuename, count);
					
				}
			}catch(Exception e){
				
			}
		}
		
	}


public void insertQueueintoDB(Map<String,String> queueCount) {
		
		Connection connection=null;
		
		try
		
		{
			

			
			connection =CoreDBConnection.getInstance().getConnection();
			Iterator itr=queueCount.keySet().iterator();
			
			while(itr.hasNext()){
				
				String queuename=itr.next().toString();
				String count=queueCount.get(queuename);
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

private void insertQueueintoDB(Connection connection,String queuename,String count) {
		

		PreparedStatement insert=null;
		
		try
		
		{
			long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into queue_count_mysql(queuename,count ,updatetime,mode) values(?,?,?,?)");
			insert.setString(1, queuename);
			insert.setString(2, count);
			insert.setString(3, ""+updatetime);
			insert.setString(4, MODE);

			insert.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(insert);
		}
	}

	
	private int updateDB(Connection connection,String queuename,String count) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update queue_count_mysql set count=?,updatetime=? where queuename=? and mode=?");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, ""+queuename);
			update.setString(4, MODE);
			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}



}
