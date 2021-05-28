package com.winnovature.unitia.util.dao;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.ParameterKey;
import com.winnovature.unitia.util.misc.RoundRobinTon;


public class Insert {

	static String INSERT_SQL_PATTERN="insert into {0}(msgid,username,scheduletime,pstatus,data) values(?,?,?,?,?)";
	
	static String CONCATE_INSERT_SQL_PATTERN="insert into {0}(msgid,username,scheduletime,pstatus,data,cc,ackid) values(?,?,?,?,?,?,?)";

	List<String> tlist=new ArrayList();

	public Insert(){
		

		tlist.add("t1");
		tlist.add("t2");
		tlist.add("t3");
		tlist.add("t4");
		
	}
	public boolean insert(String tablename, Object requestObject,String username,String msgid,String scheduletime,String smscid) {
		
		
		if(tablename.startsWith("smppdn_")){
			
			tablename="smppdn";
		}
		if(!Table.getInstance().isAvailableTable(tablename)){
			
			Table.getInstance().addTable(tablename);
		}
		Connection connection=null;
		PreparedStatement statement=null;

		try {
		
			String param2=username;
			
			if(tablename.startsWith("httpdn")){
				param2=getTname(username);
			}else if(tablename.startsWith("reroute_kannel")){
				
				param2=smscid;
			}
					
				
				
				connection=QueueDBConnection.getInstance().getConnection();
					
			statement=connection.prepareStatement(getQuery(tablename));
			statement.setString(1, msgid);
			statement.setString(2, param2);
			if(scheduletime==null||scheduletime.trim().length()<1||scheduletime.trim().length()>13){
				scheduletime="0";
			}
			statement.setString(3, scheduletime);
			statement.setString(4, "0");
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            
            oos.writeObject(requestObject);
            
            byte[] Bytes = bos.toByteArray();

			statement.setBytes(5, Bytes);
			statement.execute();
			
			return true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
			Close.close(statement);
			Close.close(connection);

			
		}
		
		return false;
	}


	
	public boolean insertforConcate(String tablename, List<Map<String,Object>> datalist) {
		
		
		if(tablename.startsWith("smppdn_")){
			
			tablename="smppdn";
		}
		if(!Table.getInstance().isAvailableTable(tablename)){
			
			Table.getInstance().addTable(tablename);
		}
		Connection connection=null;
		PreparedStatement statement=null;

		try {
		
				
				connection=QueueDBConnection.getInstance().getConnection();
					
			statement=connection.prepareStatement(getQueryforConcate(tablename));
			
			for(int i=0;i<datalist.size();i++){
				Map<String,Object> requestObject=datalist.get(i);

				statement.setString(1, requestObject.get(MapKeys.MSGID).toString());
				statement.setString(2, requestObject.get(MapKeys.POLLER_USERNAME).toString());
				
				statement.setString(3, "0");
				statement.setString(4, "0");
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            
	            oos.writeObject(requestObject);
	            
	            byte[] Bytes = bos.toByteArray();

				statement.setBytes(5, Bytes);

				statement.setString(6, (String) requestObject.get(MapKeys.CONCATE_CC));
				
				statement.setString(7, (String) requestObject.get(MapKeys.ACKID));

				statement.addBatch();
			}
			statement.executeBatch();
			
			return true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
			Close.close(statement);
			Close.close(connection);

			
		}
		
		return false;
	}

	
	
	public boolean insertA(List<Map<String,Object>> requestlist) {
		
		
		String	tablename="schedulepool";
		
		if(!Table.getInstance().isAvailableTable(tablename)){
			
			Table.getInstance().addTable(tablename);
		}
		Connection connection=null;
		PreparedStatement statement=null;

		try {
		
				
				connection=QueueDBConnection.getInstance().getConnection();
					
				connection.setAutoCommit(false);
				
				statement=connection.prepareStatement(getQuery(tablename));

			for(int i=0,max=requestlist.size();i<max;i++){
				
				Map<String,Object> requestObject=requestlist.get(i);
			
			statement.setString(1, requestObject.get(MapKeys.MSGID).toString());
			statement.setString(2, requestObject.get(MapKeys.USERNAME).toString());
			String scheduletime=(String)requestObject.get(MapKeys.SCHEDULE_TIME);
			if(scheduletime==null||scheduletime.trim().length()<1||scheduletime.trim().length()>13){
				scheduletime="0";
			}
			statement.setString(3, scheduletime);
			statement.setString(4, "0");
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            
            oos.writeObject(requestObject);
            
            byte[] Bytes = bos.toByteArray();

			statement.setBytes(5, Bytes);
			
			statement.addBatch();
			}
			
			statement.executeBatch();
			
			connection.commit();
			
			return true;
			
		}catch(Exception e) {
			
			e.printStackTrace();

		
			if(connection !=null){
				try{
				connection.rollback();
				}catch(Exception e1){}
			}
		}finally {
			
			Close.close(statement);
			Close.close(connection);

			
		}
		
		return false;
	}


	
	private String getQueryforConcate(String tablename) {

		String params[]= {tablename};
		
		return MessageFormat.format(CONCATE_INSERT_SQL_PATTERN, params);
	}
	
	
	private String getQuery(String tablename) {

		String params[]= {tablename};
		
		return MessageFormat.format(INSERT_SQL_PATTERN, params);
	}

	public boolean insert(String queuename, List<Map<String, Object>> requestlist) {
		

		
		if(!Table.getInstance().isAvailableTable(queuename)){
			
			Table.getInstance().addTable(queuename);
		}
		Connection connection=null;
		PreparedStatement statement=null;

		try {
		
				
			connection=QueueDBConnection.getInstance().getConnection();

			connection.setAutoCommit(false);
			statement=connection.prepareStatement(getQuery(queuename));
			
			for(int i=0,max=requestlist.size();i<max;i++){
				
				Map<String, Object> requestObject=requestlist.get(i);
				statement.setString(1, (String)requestObject.get(MapKeys.MSGID));
				
				if(	queuename.equals("httpdn")||queuename.equals("reroute_kannel")){
					
					statement.setString(2,  (String)requestObject.get(MapKeys.POLLER_USERNAME));

				}else{
					statement.setString(2,  (String)requestObject.get(MapKeys.USERNAME));

				}
				String scheduletime= (String)requestObject.get(MapKeys.SCHEDULE_TIME);
				
			
				
				if(scheduletime==null||scheduletime.trim().length()<1||scheduletime.trim().length()>13){
					scheduletime="0";
				}
				statement.setString(3,  (String)scheduletime);
				statement.setString(4, "0");
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            
	            oos.writeObject(ParameterKey.getInstance().getObject(requestObject));
	            
	            byte[] Bytes = bos.toByteArray();

				statement.setBytes(5, Bytes);
				statement.addBatch();

			}
			statement.executeBatch();
			connection.commit();
			return true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
			Close.close(statement);
			Close.close(connection);

			
		}
		
		return false;
	
	}
	
	
	private String getTname(String username) {
		
		int pointer=RoundRobinTon.getInstance().getCurrentIndex("dnhttppost"+username, tlist.size());
		
		return username+"_"+tlist.get(pointer);
	}
	
}
