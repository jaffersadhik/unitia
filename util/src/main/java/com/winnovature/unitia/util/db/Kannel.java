package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Kannel {

	private static Kannel obj=new Kannel();
	
	Map<String,Properties> kannelmap=new HashMap<String,Properties>();
	
	private Kannel(){
		
		init();
		
		reload();
	}
	
	private void reload() {
		
		
	}

	public static Kannel getInstance(){
		
		if(obj==null){
			
			obj=new Kannel();
		}
		
		return obj;
	}
	
	
	private void init(){
		
		Connection connection = null;
		try {
			connection = RouteDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "kannel_instance")){
				

				String create_sql="create table kannel_instance(kannelid varchar(50) primary key,kannel_host varchar(100),sendsms_port varchar(10),status_port varchar(10),mysql_host  varchar(100),mysql_port varchar(10),mysql_username varchar(50),mysql_password varchar(50),mysql_schema varchar(50),mysql_tablename varchar(50))";

				if(table.create(connection, create_sql, false)){
					
					String insert_sql="insert into kannel_instance(kannelid,kannel_host,sendsms_port,status_port,mysql_host,mysql_port,mysql_username,mysql_password,mysql_schema,mysql_tablename) values('kannel1','kannel1','13012','13000','unitiadb','3306','root','kannel','mysql','dlr_unitia') ";
					
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
