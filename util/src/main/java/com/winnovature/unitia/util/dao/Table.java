package com.winnovature.unitia.util.dao;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class Table {

	private static final String TABLE_CREATE_SQL = "create table {0}(msgid numeric(25,0) primary key ,username varchar(30),scheduletime numeric(13,0),data BLOB,pstatus numeric(1,0),index(scheduletime,pstatus))";

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
		
		try{
			
			connection=QueueDBConnection.getInstance().getConnection();
		TableExsists table=new TableExsists();
		
		if(!table.isExsists(connection, tablename)){
			
			table.create(connection,getQueuey( TABLE_CREATE_SQL,tablename), true);
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
