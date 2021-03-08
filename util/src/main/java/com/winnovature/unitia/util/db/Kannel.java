package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Kannel {

	private static Kannel obj=new Kannel();
	
	Map<String,Properties> kannelmap=new HashMap<String,Properties>();
	
	private Kannel(){
		
		init();
		
		reload();
	}
	
	public Map<String, Properties> getKannelmap() {
		return kannelmap;
	}
	
	public Map<String, Properties> getKannelMysqlmap() {
		
		Map<String, Properties> result=new HashMap<String,Properties>();
		Iterator itr=kannelmap.keySet().iterator();
		
		while(itr.hasNext()){
			
			Properties prop=kannelmap.get(itr.next());
			
			if(!result.containsKey(prop.get("url"))){
				
				result.put(prop.get("url").toString(), prop);
			}
			
		}
		
		return result;
	}

	public void reload() {

		String sql="select kannelid,kannel_host,sendsms_port,status_port,mysql_host,mysql_port,mysql_username,mysql_password,mysql_schema,mysql_tablename,sqlbox from  kannel_instance where status='1'";

		Connection connection = null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,Properties> kannelprop=new HashMap<String,Properties>();
		try {
			connection = RouteDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(sql);	
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				Properties prop=getProperties();
				String kannelid=resultset.getString("kannelid");
				String kannel_host=resultset.getString("kannel_host");
				String sendsms_port=resultset.getString("sendsms_port");
				String status_port=resultset.getString("status_port");
				String mysql_host=resultset.getString("mysql_host");
				String mysql_port=resultset.getString("mysql_port");
				String mysql_username=resultset.getString("mysql_username");
				String mysql_password=resultset.getString("mysql_password");
				String mysql_schema=resultset.getString("mysql_schema");
				String mysql_tablename=resultset.getString("mysql_tablename");
				String sqlbox=resultset.getString("sqlbox");

				prop.put("kannelid", kannelid);
				prop.put("sqlbox", sqlbox);
				prop.put("kannel_status", "http://"+kannel_host+":"+status_port+"/status.xml");
				prop.put("username", mysql_username);
				prop.put("password", mysql_password);
				prop.put("url", "jdbc:mysql://"+mysql_host+":"+mysql_port+"/"+mysql_schema+"?useLegacyDatetimeCode=false&serverTimezone=Asia/Kolkata&useSSL=false");
				prop.put("kannel_host", kannel_host);
				prop.put("sendsms_port", sendsms_port);
				prop.put("status_port", status_port);
				prop.put("mysql_host", mysql_host);
				prop.put("mysql_port", mysql_port);
				prop.put("mysql_username", mysql_username);
				prop.put("mysql_password", mysql_password);
				prop.put("mysql_schema", mysql_schema);
				prop.put("mysql_tablename", mysql_tablename);

				kannelprop.put(kannelid, prop);
			}

			
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}


		this.kannelmap=kannelprop;
	}

	private Properties getProperties() {
		
		Properties prop=new Properties();
		prop.put("driverClassName", "com.mysql.jdbc.Driver");
		prop.put("maxIdle", "2");
		prop.put("minIdle", "1");
		prop.put("initialSize", "1");
		prop.put("minEvictableIdleTimeMillis", "50000");
		prop.put("timeBetweenEvictionRunsMillis", "1000");
		prop.put("numTestsPerEviction", "-1");
		prop.put("MaxWaitMillis", "3000");
		prop.put("poolPreparedStatements", "true");
		prop.put("maxTotal", "20");

	

		return prop;
	}

	public static Kannel getInstance(){
		
		if(obj==null){
			
			obj=new Kannel();
		}
		
		return obj;
	}
	
	public List<String> getKannelIdList(){
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		List<String> result=new ArrayList<String>();
		try{
			connection = RouteDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select distinct kannelid from kannel_instance");
			resultset=statement.executeQuery();
			while(resultset.next()){
			
				result.add(resultset.getString("kannelid"));
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return result;
	}
	private void init(){
		
		Connection connection = null;
		try {
			connection = RouteDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "kannel_instance")){
				

				String create_sql="create table kannel_instance(kannelid varchar(50) primary key,kannel_host varchar(100),sendsms_port varchar(10),status_port varchar(10),mysql_host  varchar(100),mysql_port varchar(10),mysql_username varchar(50),mysql_password varchar(50),mysql_schema varchar(50),mysql_tablename varchar(50),sqlbox varchar(1) default '0')";

				if(table.create(connection, create_sql, false)){
					
					String insert_sql="insert into kannel_instance(kannelid,kannel_host,sendsms_port,status_port,mysql_host,mysql_port,mysql_username,mysql_password,mysql_schema,mysql_tablename) values('kannel1','kannel1','13012','13000','kannel1db','3306','root','kannel','mysql','dlr_unitia') ";
					
					insert(connection,insert_sql);
				}
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	}

	private void insert(Connection connection, String sql) {
		
		PreparedStatement statement=null;
		
		try{
			statement=connection.prepareStatement(sql);
			statement.execute();	
		}catch(Exception e){
			
			System.err.println(sql);
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
	}
}
