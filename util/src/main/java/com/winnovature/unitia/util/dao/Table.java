package com.winnovature.unitia.util.dao;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class Table {

	private static final String CONCATE_TABLE_CREATE_SQL = "create table {0}(msgid varchar(100) ,ackid varchar(100) ,username varchar(30),itime timestamp default CURRENT_TIMESTAMP,scheduletime numeric(13,0),data BLOB,pstatus numeric(1,0),cc decimal(3,0),index(msgid,username,pstatus),index(ackid))";

	private static final String TABLE_CREATE_SQL = "create table {0}(msgid varchar(50) ,username varchar(30),itime timestamp default CURRENT_TIMESTAMP,scheduletime numeric(13,0),data BLOB,pstatus numeric(1,0),index(msgid,username,scheduletime,pstatus))";

	private static final String TABLE_CREATE_SQL_WITHOUT_KEY = "create table {0}(msgid varchar(50) ,username varchar(30),itime timestamp default CURRENT_TIMESTAMP,scheduletime numeric(13,0),data BLOB,pstatus numeric(1,0),index(username,scheduletime,pstatus))";

	private static Table obj=null;
	
	private Set<String> availabletable=new HashSet();
	private Table(){
		
	}
	
	public static Table getInstance(){
		
		if(obj==null){
			
			obj=new Table();
		}
		
		return obj;
	}
	
	public boolean isAvailableTable(String tablename){
	
		return availabletable.contains(tablename);
	}
	
	public void addTable(String tablename){
		
		
		Connection connection=null;
		String SQL="";
		try{
			
		
				
				connection=QueueDBConnection.getInstance().getConnection();

				SQL=TABLE_CREATE_SQL ;
				
				if(tablename.equals("concatedata")){
					SQL=CONCATE_TABLE_CREATE_SQL ;

				}

			TableExsists table=new TableExsists();
		
		if(!table.isExsists(connection, tablename)){
			
			table.create(connection,getQueuey( SQL,tablename), true);
		}
		availabletable.add(tablename);
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
	}

	private String getQueuey(String tableCreateSql, String tablename) {
		String  [] params={tablename};
		
		
		return MessageFormat.format(tableCreateSql, params);
	}
}
