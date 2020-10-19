package dnsql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.KannelDBConnection;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.reader.QueueTon;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import dnsql.DLRCount;
import dnsql.PollerStartup;
import dnsql.TableCount;



public class T  extends Thread{

	static String SQL_COUNT ="select count(*) cnt from {0}";
	
	static List<String> TABLES=new ArrayList<String>();
	
	static{

		TABLES.add("missedcallpool");
		TABLES.add("appspool");
		TABLES.add("dngenpool");
		TABLES.add("submissionpool");
		TABLES.add("smppdn");
		TABLES.add("httpdn");
		TABLES.add("dnreceiverpool");
		TABLES.add("logspool");
		TABLES.add("dnpostpool");
    	TABLES.add("clientdnpool");
    	TABLES.add("schedulepool");
    	TABLES.add("kannelretrypool");
    	TABLES.add("commonpool");
    	TABLES.add("otppool");
    	TABLES.add("otpretrypool");
    	TABLES.add("dngenpool");
    	TABLES.add("clientdnpool");
    	TABLES.add("dnretrypool");
    	TABLES.add("dlr_unitia");

	}

	private void tableCountCheck() {
		
		Map<String,String> result=new HashMap<String,String>();
		
		Connection connection=null;
		
		try{
			connection=KannelDBConnection.getInstance().getConnection();
			for(int i=0,max=TABLES.size();i<max;i++){
		
				String tablename=TABLES.get(i);
				
				result.put(tablename, getCount(connection,tablename));
			}
			 TableCount tablecount=new TableCount() ;
			 tablecount.insertQueueintoDB(result);
			 tablecount.setQueueMaxCountMap(result);
			 tablecount.insertQueueMaxintoDB();
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
		
	}
	
	

	private String getCount(Connection connection, String tablename) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		String result="0";
		try{
			statement=connection.prepareStatement(getSQL(tablename));
			resultset=statement.executeQuery();
			
			if(resultset.next()){
				result=resultset.getString("cnt");
			}
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
		}
		return result;
	}

	private String getSQL(String tablename) {
		String param[]={tablename};
		return MessageFormat.format(SQL_COUNT,param );
	}


	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueueConnectionPool.getInstance().reload();
				
				tableCountCheck();
				
				RedisQueueConnectionPool.getInstance().print();

				QueueTon.getInstance().checkQueueAvailablity();
				
				com.winnovature.unitia.util.db.Kannel.getInstance().reload();
				com.winnovature.unitia.util.misc.kannel.reload();
				DLRCount.getInstance().doProcess();
				PollerStartup.updateUsers();
				gotosleep();
					
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(10L);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}





