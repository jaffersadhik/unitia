package com.winnovature.unitia.util.datacache.account;

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

public class DNTable {

	private static String SQL="";
	
	private static Set<String> avilabletable=new HashSet();
	 static{
		 
		 StringBuffer sb=new StringBuffer();
		 
		 sb.append("create table {0}(");
		 sb.append("ackid numeric(20,0),");
		 sb.append("ackid_org numeric(20,0),");
		 sb.append("msgid numeric(20,0),");
		 sb.append("username varchar(16),");
		 sb.append("senderid varchar(15),");
		 sb.append("senderid_org varchar(15),");
		 sb.append("mobile numeric(15,0),");
		 sb.append("message varchar(20),");
		 sb.append("operator varchar(50),");
		 sb.append("circle varchar(50),");
		 sb.append("countrycode numeric(10,0),");
		 sb.append("smscid_org varchar(10),");
		 sb.append("smscid varchar(10),");
		 sb.append("rtime timestamp,");
		 sb.append("ktime timestamp,");
		 sb.append("itime timestamp,");
		 sb.append("expiry numeric(10,0),");
		 sb.append("carrier_stime timestamp,");
		 sb.append("carrier_dtime timestamp,");
		 sb.append("carrier_dtime_org timestamp,");
		 sb.append("carrier_sdate varchar(16),");
		 sb.append("carrier_ddate varchar(16),");
		 sb.append("carrier_stat varchar(7),");
		 sb.append("carrier_err varchar(3),");
		 sb.append("carrier_msgid varchar(30),");
		 sb.append("carrier_systemid varchar(30),");
		 sb.append("carrier_dr varchar(300),");
		 sb.append("sms_latency_slap numeric(2,0),");
		 sb.append("sms_latency_slap_org numeric(2,0),");
		 sb.append("carrier_latency_slap numeric(2,0),");
		 sb.append("paltform_latency_slap numeric(2,0),");
		 sb.append("statusid numeric(3,0),");
		 sb.append("statusid_org numeric(3,0)");
		 sb.append(")");

		 SQL=sb.toString();


	 }
	
	private static  DNTable obj=null;
			
	private DNTable(){
		
	}
	
	public static DNTable getInstance(){
		
		if(obj==null){
			
			obj=new DNTable();
			
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
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
	
		try{
			
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select delivery_tablename from billingtable_routing");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				result.add(resultset.getString("delivery_tablename"));
			}
			
			result.add("delivery");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
			
		}
		
		return result;
	}
}
