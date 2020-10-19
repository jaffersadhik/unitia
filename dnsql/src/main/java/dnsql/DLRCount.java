package dnsql;

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
import com.winnovature.unitia.util.db.KannelStoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;


public class DLRCount {


	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
	static String SQL="select a.smsc smsc,count(*) cnt from dlr_unitia a,dlr_unitia_resp b where a.smsc=b.smsc and a.ts=b.ts  group by smsc";
	
	private static DLRCount obj=null;

	private  Map<String,Map<String,String>> kannelmap=new HashMap<String,Map<String,String>>();

	private DLRCount(){
		
		checkQueueTableAvailable();
	}
	
	
	public Map<String, Map<String, String>> getKannelmap() {
		return kannelmap;
	}


	public static DLRCount getInstance(){
		
		
		if(obj==null){
			
			obj=new DLRCount();
		}
		
		return obj;
	}
	
	
	private void checkQueueTableAvailable() {
		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "queue_count_dlr")){
				
				table.create(connection, "create table queue_count_dlr(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25) default 'production',kannelid varchar(25) default 'kannel1'  )", false);
			}
			
			
			
		}catch(Exception e){
			 e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
		
	}
	public void doProcess() throws SQLException{
		
		Map<String,Properties> map=com.winnovature.unitia.util.db.Kannel.getInstance().getKannelmap();
		
		Iterator itr=map.keySet().iterator();
		
		while(itr.hasNext()){
			
			String kannelid=itr.next().toString();
			Properties prop=map.get(kannelid);
			
			Map<String,String> result=getDLRCount(KannelStoreDBConnection.getInstance(kannelid, prop).getConnection(),prop.getProperty("mysql_tablename"));
			
			Map<String,String> emptymap=getEmptyMap(kannelid);
			
			emptymap.putAll(result);
			insertQueueintoDB(kannelid,emptymap);
		
			kannelmap.put(kannelid, result);
		}
		
		
	}

	private Map<String, String> getEmptyMap(String kannelid) {
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		try{
			connection =CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select queuename from queue_count_dlr where kannelid=?");
			statement.setString(1, kannelid);
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				result.put(resultset.getString("queuename"), "0");
			}
		}catch(Exception e){
			
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		return result;
	}


	private Map<String, String> getDLRCount(Connection connection,String tablename) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		Map<String,String> result=new HashMap<String,String>();
		
		try{
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







	


public void insertQueueintoDB(String kannelid, Map<String,String> queueCount) {
		
		Connection connection=null;
		
		try
		
		{
			

			
			connection =CoreDBConnection.getInstance().getConnection();
			Iterator itr=queueCount.keySet().iterator();
			
			while(itr.hasNext()){
				
				String queuename=itr.next().toString();
				String count=queueCount.get(queuename);
				if(updateDB(connection,kannelid, queuename, count)<1){
					
					insertQueueintoDB(connection,kannelid, queuename, count);
				}

				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}

private void insertQueueintoDB(Connection connection, String kannelid,String queuename,String count) {
		

		PreparedStatement insert=null;
		
		try
		
		{
			long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into queue_count_dlr(queuename,count ,updatetime,mode,kannelid) values(?,?,?,?,?)");
			insert.setString(1, queuename);
			insert.setString(2, count);
			insert.setString(3, ""+updatetime);
			insert.setString(4, MODE);
			insert.setString(5, kannelid);

			insert.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(insert);
		}
	}

	
	private int updateDB(Connection connection, String kannelid,String queuename,String count) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update queue_count_dlr set count=?,updatetime=? where queuename=? and mode=? and kannelid=? ");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, ""+queuename);
			update.setString(4, MODE);
			update.setString(5, kannelid);

			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}



}
