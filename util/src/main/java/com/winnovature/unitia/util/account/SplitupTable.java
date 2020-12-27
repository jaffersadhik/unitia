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

public class SplitupTable {

	private static String SQL="";
	
	private static Set<String> avilabletable=new HashSet();
	 static{
		 
		 StringBuffer sb=new StringBuffer();
		 
		 sb.append("create table splitup(");
		 sb.append("msgid varchar(40),");
		 sb.append("udh varchar(30),");
		 sb.append("fullmessage text,");
		 sb.append("itime timestamp default CURRENT_TIMESTAMP");	
		 sb.append(")");

		 SQL=sb.toString();


	 }
	
	private static  SplitupTable obj=null;
			
	private SplitupTable(){
		
	}
	
	public static SplitupTable getInstance(){
		
		if(obj==null){
			
			obj=new SplitupTable();
			
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
		result.add("splitup");
		return result;
	}
	
	
	public boolean isVailableTable(String tablename){
		
		return avilabletable.contains(tablename);
	}
	
	public static void main(String args[]){
		
		System.out.println(new SplitupTable().getSQL("test"));
	}
}
