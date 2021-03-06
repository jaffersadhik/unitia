package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Properties;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class ConfigParams {

	
	
	private static Properties prop=new Properties();
	
	static{
		
		prop.put(ConfigKey.PREFIX_START_NUMBER, "9~7~8~6");
		prop.put(ConfigKey.HTTP_CONNECTION_TIMEOUT, "1000");
		prop.put(ConfigKey.HTTP_RESPONSE_TIMEOUT, "1000");
		prop.put(ConfigKey.DEFAULT_CREDIT_POINTS, "1.0");
		prop.put(ConfigKey.TRAI_BLOCKLOUT_END, "09:00");
		prop.put(ConfigKey.TRAI_BLOCKLOUT_START, "20:45");
		prop.put(ConfigKey.MAX_SCHEDULE_TIME_ALLOWED_MINS, "10080");
		prop.put(ConfigKey.SPCECIAL_CHAR_WORD_COUNT, "3");

		prop.put(ConfigKey.MAX_MOBILE_LENGTH_ALLOWED, "15");
		prop.put(ConfigKey.MAX_SENDERID_LENGTH_ALLOWED, "10");
		prop.put(ConfigKey.MIN_MOBILE_LENGTH_ALLOWED, "7");
		prop.put(ConfigKey.MAX_UDH_LENGTH_ALLOWED, "20");
		prop.put(ConfigKey.MAX_MSG_LENGTH_ALLOWED, "4000");

		prop.put(ConfigKey.LOGMODE, "1");
		prop.put(ConfigKey.LOADBALANCER_DN_IP, "Unitia-8042041824a6b544.elb.ap-south-1.amazonaws.com");
		prop.put(ConfigKey.LOADBALANCER_DN_PORT, "9301");
		prop.put(ConfigKey.OTPDNWAITTIMEINMS, "10000");
		prop.put(ConfigKey.KANNEL_USERNAME, "test");
		prop.put(ConfigKey.KANNEL_PASSWORD, "pass123");
		prop.put(ConfigKey.KANNEL_RESPONSE, "Accepted for delivery");
		prop.put(ConfigKey.GRACE_STOP, "0");

		prop.put(ConfigKey.MAX_COMMONPOOL, "5000");
		prop.put(ConfigKey.MAX_SUBMISSIONPOOL, "5000");
		prop.put(ConfigKey.MAX_DNRECEIVERPOOL, "5000");
		prop.put(ConfigKey.MAX_DNPOSTPOOL, "5000");
		prop.put(ConfigKey.MAX_HTTPDN, "5000");
		prop.put(ConfigKey.MAX_KANNELRETRYPOOL, "5000");
		prop.put(ConfigKey.MAX_LOGSPOOL, "5000");
		prop.put(ConfigKey.MAX_SMPPDN, "200");
	}
	
	
	private static  ConfigParams obj=null;

	private ConfigParams(){
		
		init();
		reload();
	}
	
	public void reload() {
		
		

		Properties prop=new Properties();
		Connection connection =null;
		PreparedStatement statement =null;
		ResultSet resultset=null;
		try{
		
			connection =CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select * from apps_properties");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				prop.put(resultset.getString("apps_key"), resultset.getString("apps_value"));
			}
		}catch(Exception e){
			prop=null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		if(prop!=null){
			
			this.prop=prop;
		}
	
		
	}

	private void init() {
		
		Connection connection =null;
		
		try{
			
			connection= CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "apps_properties")){
			
				table.create(connection, "create table apps_properties(apps_key varchar(100),apps_value varchar(100))", false);
			
				insert(connection);
			}
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
		
	}

	private void insert(Connection connection) {

		PreparedStatement statement=null;
		try{
			
			connection.setAutoCommit(false);
			statement=connection.prepareStatement("insert into apps_properties(apps_key,apps_value) values(?,?)");
			
			Enumeration<Object> itr=prop.keys();
			
			while(itr.hasMoreElements()){
				
				String key=itr.nextElement().toString();
				String value=prop.getProperty(key);
				statement.setString(1, key);
				statement.setString(2, value);

				statement.addBatch();
			}
			
			statement.executeBatch();
			connection.commit();
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
	}

	public static ConfigParams getInstance(){
		
		if(obj==null){
			
			obj=new ConfigParams();
		}
		
		return obj;
	}
	
	public String getProperty(String key){
		try{
		return prop.get(key).toString();
		}catch(Exception e){
			
			System.err.println("key : "+key);
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static void main(String args[]){
		
		Enumeration<Object> itr=prop.keys();
	
		while(itr.hasMoreElements()){
			
			String key=itr.nextElement().toString();
			String value=prop.getProperty(key);
			
			System.out.println(key+":"+value);
		}
	}
}
