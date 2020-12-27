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

public class ClientDNTable {

	private static String SQL="";
	
	private static Set<String> avilabletable=new HashSet();
	 static{
		 
		 StringBuffer sb=new StringBuffer();
		 
		 sb.append("create table {0}(");
		 sb.append("ackid varchar(40),");
		 sb.append("username varchar(16),");
		 sb.append("itime timestamp default CURRENT_TIMESTAMP,");
		 sb.append("rtime timestamp,");
		 sb.append("ctime timestamp,");
		 sb.append("sms_latency_slap varchar(2),");
		 sb.append("statusid varchar(3)");
		 sb.append(")");

		 SQL=sb.toString();


	 }
	
	private static  ClientDNTable obj=null;
			
	private ClientDNTable(){
		
	}
	
	public static ClientDNTable getInstance(){
		
		if(obj==null){
			
			obj=new ClientDNTable();
			
		}
		
		return obj;
	}
	
	public void reload(){
		
		Connection connection=null;
		
		try{
			
		
			connection=BillingDBConnection.getInstance().getConnection();
			
		Set<String> tableset=getDNTableSet();
		
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

	private Set<String> getDNTableSet() {
	
		Set<String> result=new HashSet<String>();
		
		
	
		try{
			
			
			
			result.add("clientdn");
			
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
