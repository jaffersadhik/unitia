package kannelconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.KannelDBConnection;
import com.winnovature.unitia.util.db.KannelStoreDBConnection;

import unitiaroute.Kannel;

public class DLRCount {

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
	static String SQL="select smsc,count(*) cnt from {0} group by smsc";
	
	public void doProcess() throws SQLException{
		
		Map<String,Properties> map=com.winnovature.unitia.util.db.Kannel.getInstance().getKannelMysqlmap();
		
		Iterator itr=map.keySet().iterator();
		
		while(itr.hasNext()){
			
			String kannelid=itr.next().toString();
			Properties prop=map.get(kannelid);
			
			Map<String,String> result=getDLRCount(KannelStoreDBConnection.getInstance(kannelid, prop).getConnection(),prop.getProperty("mysql_tablename"));
			
			
			insertQueueintoDB(result);
			setQueueMaxCountMap(result);
			insertQueueMaxintoDB();
			
		}
		
		
	}

	private Map<String, String> getDLRCount(Connection connection,String tablename) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		Map<String,String> result=new HashMap<String,String>();
		
		try{
			connection=KannelDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(getQuery(SQL,tablename));
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("smsc"), resultset.getString("cnt"));
			}
		}catch(Exception e){
			
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		return result;
	}
	
	
	
	

private String getQuery(String sQL2, String tablename) {
		String param[]={tablename};
		return MessageFormat.format(sQL2, param);
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
				
				insert=connection.prepareStatement("insert into queue_max_dlr(queuename,count ,updatetime,mode ) values(?,?,?,?)");
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
			
			update=connection.prepareStatement("update queue_max_dlr set count=?,updatetime=? where queuename=? and mode=?");

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
			
			insert=connection.prepareStatement("insert into queue_count_dlr(queuename,count ,updatetime,mode) values(?,?,?,?)");
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
			
			update=connection.prepareStatement("update queue_count_dlr set count=?,updatetime=? where queuename=? and mode=?");

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
