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
import com.winnovature.unitia.util.db.TableExsists;

public class SubmissionTable {

	private static String SQL="";
	
	private static Set<String> avilabletable=new HashSet();
	 static{
		 
		 StringBuffer sb=new StringBuffer();
		 
		 sb.append("create table {0}(");
		 sb.append("ackid varchar(40),");
		 sb.append("msgid varchar(40),");
		 sb.append("username varchar(16),");
		 sb.append("routeclass varchar(3),");
		 sb.append("routegroup varchar(30),");
		 sb.append("featurecode varchar(3),");
		 sb.append("udh varchar(30),");
		 sb.append("totalmsgcount varchar(3),");
		 sb.append("senderid varchar(15),");
		 sb.append("senderid_org varchar(15),");
		 sb.append("mobile varchar(15),");
		 sb.append("param1 varchar(300),");
		 sb.append("param2 varchar(300),");
		 sb.append("param3 varchar(300),");
		 sb.append("param4 varchar(300),");
		 sb.append("fullmessage text,");
		 sb.append("operator varchar(50),");
		 sb.append("circle varchar(50),");
		 sb.append("countrycode varchar(10),");
		 sb.append("smscid_org varchar(10),");
		 sb.append("smscid varchar(10),");
		 sb.append("itime datetime default CURRENT_TIMESTAMP,");
		 sb.append("rtime datetime,");
		 sb.append("rtime_org datetime,");
		 sb.append("ktime datetime,");
		 sb.append("stime datetime,");
		 sb.append("credit numeric(4,2),");
		 sb.append("customerip varchar(16),");
		 sb.append("statusid varchar(3),");
		 sb.append("auth_id varchar(11),");
		 sb.append("pattern_id varchar(11),");
		 sb.append("attempttype varchar(1) default '0' ");

		 sb.append(")");

		 SQL=sb.toString();


	 }
	
	private static  SubmissionTable obj=null;
			
	private SubmissionTable(){
		
	}
	
	public static SubmissionTable getInstance(){
		
		if(obj==null){
			
			obj=new SubmissionTable();
			
		}
		
		return obj;
	}
	
	public void reload(){
		
		Connection connection=null;
		
		try{
			
		
			connection=BillingDBConnection.getInstance().getConnection();
			
		Set<String> tableset=getSubmissionTableSet();
		
		Iterator itr=tableset.iterator();
		
		while(itr.hasNext()){
			
			String tablename=itr.next().toString();
			
			if(!avilabletable.contains(tablename)){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, tablename)){
				
					if(table.create(connection, getSQL(tablename), false)){
						
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
			
		
		
			
			result.add("submission");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			
			
		}
		
		return result;
	}
	
	
	public boolean isVailableTable(String tablename){
		
		return avilabletable.contains(tablename);
	}
	
	public static void main(String args[]){
		
		System.out.println(new SubmissionTable().getSQL("test"));
	}
}
