package queuecheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.db.TableExsists;


public class TPSSmscidMax {


	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
	
	private static TPSSmscidMax obj=null;


	private TPSSmscidMax(){
		
		checkQueueTableAvailable();
	}
	
	

	public static TPSSmscidMax getInstance(){
		
		
		if(obj==null){
			
			obj=new TPSSmscidMax();
		}
		
		return obj;
	}
	
	
	private void checkQueueTableAvailable() {
		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "tps_max_smscid")){
				
				table.create(connection, "create table tps_max_smscid(smscid varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25) default 'production')", false);
			}
			
			
			
		}catch(Exception e){
			 e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
		
	}
	public void doProcess() throws SQLException{
			
		Connection connection=null;
		try{
			connection=CoreDBConnection.getInstance().getConnection();

			Map<String,String> result=getTPSCount(connection);
			Map<String,Map<String,String>> map=TPSSmscidCount.getInstance().getResult();
			
			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				try{
					String key=itr.next().toString();
					String count=map.get(key).get("tps");
					
					if(result.containsKey(key)){
						
						String amx=result.get(key);
						long cc=Long.parseLong(count);
						long max=Long.parseLong(amx);
						if(cc>max){
							result.put(key, count);

						}
					}else{
						result.put(key, count);
					}
				}catch(Exception e){
					
				}
			}
			
			insertQueueintoDB(connection,result);
		
		}catch(Exception e){
			
		}finally{
			Close.close(connection);
		}
		
		
	}



	private Map<String, String> getTPSCount(Connection connection) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		
		try{
			statement=connection.prepareStatement(getQuery("select smscid,count from tps_max_smscid",""));
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("smscid"), resultset.getString("count"));
			}
		}catch(Exception e){
			
		}finally{
			Close.close(resultset);
			Close.close(statement);
		}
		return result;
	}
	
	
	
	

private String getQuery(String sQL2, String tablename) {
		String param[]={tablename};
		return MessageFormat.format(sQL2, param);
	}







	


public void insertQueueintoDB(Connection connection, Map<String,String> queueCount) {
		
		
		try
		
		{
			

			
			Iterator itr=queueCount.keySet().iterator();
			
			while(itr.hasNext()){
				
				String queuename=itr.next().toString();
				String count=queueCount.get(queuename);
				if(updateDB(connection, queuename, count)<1){
					
					insertQueueintoDB(connection,queuename, count);
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
			
			insert=connection.prepareStatement("insert into tps_max_smscid(smscid,count ,updatetime,mode) values(?,?,?,?)");
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
			
			update=connection.prepareStatement("update tps_max_smscid set count=?,updatetime=? where smscid=? and mode=?");

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
