package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.StatusLogDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class StatusLogTable {

	private static String SQL="";
	
	private static Set<String> avilabletable=new HashSet();
	 static{
		 
		 StringBuffer sb=new StringBuffer();
		 
		 sb.append("create table {0}(");
		 sb.append("ackid varchar(40),");
		 sb.append("msgid varchar(40),");
		 sb.append("username varchar(16),");
		 sb.append("itime datetime default CURRENT_TIMESTAMP,");
		 sb.append("rtime datetime,");
		 sb.append("customerip varchar(50),");
		 sb.append("order numberic(2,0),");
		 sb.append("nextlevel varchar(50)");
			
		 sb.append(")");

		 SQL=sb.toString();


	 }
	
	private static  StatusLogTable obj=null;
			
	private StatusLogTable(){
		
	}
	
	public static StatusLogTable getInstance(){
		
		if(obj==null){
			
			obj=new StatusLogTable();
			
		}
		
		return obj;
	}
	
	public void reload(){
		
		Connection connection=null;
		
		try{
			
		
			connection=StatusLogDBConnection.getInstance().getConnection();
			
		Set<String> tableset=getSubmissionTableSet();
		
		Iterator itr=tableset.iterator();
		
		while(itr.hasNext()){
			
			String tablename=itr.next().toString();
			
			if(!avilabletable.contains(tablename)){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "mysql."+tablename)){
				
					if(table.create(connection, getSQL("mysql."+tablename), false)){
						
						avilabletable.add(tablename);
						
					}
				}else{
					
					avilabletable.add(tablename);

				}
				
			}
		}
		
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}

	private String getSQL(String tablename) {
		String params[]={tablename};
		return MessageFormat.format(SQL, tablename);
	}

	private Set<String> getSubmissionTableSet() {
	
		Set<String> result=new HashSet<String>();
		
	
		try{
			
		
		
			result.add("reportlog_status");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			
			
		}
		
		return result;
	}
	
	
	public boolean isVailableTable(String tablename){
		
		return avilabletable.contains(tablename);
	}
	
	
}
