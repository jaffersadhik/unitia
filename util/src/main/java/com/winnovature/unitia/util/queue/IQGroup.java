package com.winnovature.unitia.util.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.RoundRobinTon;

public class IQGroup {

	
private static List<String> LIST=new ArrayList<String>();
	
	
	static{
		
		
		String count=ConfigParams.getInstance().getProperty(ConfigKey.IQ_GROUP_COUNT);
		
		int intCount=1;
		if(count==null){
			count="1";
		}
		try{
			intCount=Integer.parseInt(count);
			if(intCount>26){
				intCount=26;
			}
			
			if(intCount<1){
				intCount=1;
			}
		}catch(Exception e){
			
			
		}
		
		for(int i=65;i<(65+intCount);i++){
			 LIST.add(""+(char)i);
		 }
	}
	
	private static IQGroup obj=new IQGroup();
	
	private static String CREATE_SQL="create table group_iq(id INT PRIMARY KEY AUTO_INCREMENT,queuename varchar(200),redisid varchar(200),groupname varchar(2),itime decimal(20,0)) ";
	
	private static String SQL="select id ,queuename,redisid,groupname from group_iq";
	
	private static String DELETE_SQL_WITH_ID="delete from group_iq where id=?";
	
	private static String DELETE_SQL_WITH_ITIME="delete from group_iq where itime<?";

	private static String QUEUE_SQL="select  queuename,redisid from queue_count_redis  where count>0  and queuename like 'production_commonpool_%'";

	private static String INSERT_SQL="insert into group_iq(queuename,redisid,groupname,itime)values(?,?,?,?) ";

	Map<String,List<String>> GROUPDATA= new HashMap<String,List<String>>();

	private IQGroup(){
		
		init();
	}
	
	private void init() {
		
		TableExsists table=new TableExsists();
		Connection connection=null;
		try{
		
			connection=CoreDBConnection.getInstance().getConnection();
			
			if(!table.isExsists(connection, "group_iq")){
			
				table.create(connection, CREATE_SQL, false);
			}
		
		}catch(Exception e){
			
		}finally{
			
		}
	}

	public static IQGroup getInstance(){
		
		
		if(obj==null){
			
			obj=new IQGroup();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		Map<String,List<String>> data= new HashMap<String,List<String>>();
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(SQL);
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				String id=resultset.getString("id");
				String queuename=resultset.getString("queuename");
				String redisid=resultset.getString("redisid");
				String groupname=resultset.getString("groupname");

				List<String> list=data.get(groupname);
				
				if(list==null){
					
					list=new ArrayList<String>();
					data.put(groupname, list);
				}
			
				list.add(redisid+"~"+queuename+"~"+id);

			}
			
			deleteRecords(connection);
			
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
			
		}
		
		GROUPDATA=data;
		
	}
	
	
	public void deleteRecords(String dbid){
		
		
		Connection connection=null;
		PreparedStatement statement=null;
		
		try{
			
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(DELETE_SQL_WITH_ID);
			
			statement.setString(1,dbid);
			statement.execute();
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
			
		}
	}
	
	
	public void deleteRecords(Connection connection){
		
		
		PreparedStatement statement=null;
		
		long time=System.currentTimeMillis()-(10*60*1000);
		
		try{
			
			statement=connection.prepareStatement(DELETE_SQL_WITH_ITIME);
			
			statement.setString(1,""+time);
			statement.execute();
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
			
		}
	}
	
	public List<String> getData(String groupname){
		
		return GROUPDATA.get(groupname);
	}
	
	public void allocateGroupForQueues(){
	
			Connection connection=null;
			PreparedStatement statement=null;
			ResultSet resultset=null;
			
			try{
				
				connection=CoreDBConnection.getInstance().getConnection();
				
				List<Map<String, String>> data=getQueueName(connection);
				
				List<Map<String, String>> freshdata=getFreshData(data);
				
				statement=connection.prepareStatement(INSERT_SQL);
				
				connection.setAutoCommit(false);
				
				for(int i=0;i<freshdata.size();i++){
					
					Map<String, String> record=freshdata.get(i);
					
					String redisid=record.get("redisid");
					String queuename=record.get("queuename");
					
					statement.setString(1,queuename);
					statement.setString(2,redisid);
					int index=RoundRobinTon.getInstance().getCurrentIndex("IQ_GROUP_INDEX", LIST.size());
					statement.setString(3,LIST.get(index));
					statement.setString(4,""+System.currentTimeMillis());

					statement.addBatch();
				}
				
				statement.executeBatch();
				
			}catch(Exception e){
				
			}finally{
				
				Close.close(resultset);
				Close.close(statement);
				Close.close(connection);
				
			}
			
		}

	private List<Map<String, String>> getFreshData(List<Map<String, String>> data) {
		
		List<Map<String, String>> result=new ArrayList<Map<String, String>> ();
		
		for(int i=0;i<data.size();i++){
			
			Map<String,String> record=data.get(i);
			String pattern=record.get("redisid")+"~"+record.get("queuename")+".*";
			
			if(!isRunning(pattern)){
				
				result.add(record);
			}
		}
		return result;
	

	}
	
	private boolean isRunning(String pattern) {
		
		Iterator itr=GROUPDATA.keySet().iterator();
		
		while(itr.hasNext()){
		
			List<String> list=GROUPDATA.get(itr.next());
			
			
			for(int i=0;i<list.size();i++){
				
				String str=list.get(i);
				
				if(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(str).matches()){
					
					return true;
				}
			}
		}
		return false;
		
	}

	private List<Map<String, String>> getQueueName(Connection connection) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		List<Map<String, String>> data=new ArrayList<Map<String, String>>();
		try{
			
			statement=connection.prepareStatement(QUEUE_SQL);
			resultset =statement.executeQuery();
			
			while(resultset.next()){
				
				String queuename=resultset.getString("queuename");
				String redisid=resultset.getString("redisid");
		
				Map<String,String> records=new HashMap<String,String>();
				records.put("queuename", queuename);
				records.put("redisid", redisid);
				data.add(records);
			}
			
			
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			
		}
		return data;
	}

}
