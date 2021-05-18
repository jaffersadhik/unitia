package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.TableExsists;

public class SplitupTable {

	private static String SQL="";
	
	private static Set<String> avilabletable=new HashSet();
	 static{
		 
		 StringBuffer sb=new StringBuffer();
		 
		 sb.append("create table {0}(");
		 sb.append("ackid varchar(40),");
		 sb.append("msgid varchar(40),");
		 sb.append("username varchar(16),");
		 sb.append("featurecode varchar(3),");
		 sb.append("udh varchar(30),");
		 sb.append("totalmsgcount varchar(3),");
		 sb.append("senderid varchar(15),");
		 sb.append("senderid_org varchar(15),");
		 sb.append("mobile varchar(15),");
		 sb.append("smscid_org varchar(50),");
		 sb.append("smscid varchar(50),");
		 sb.append("itime datetime default CURRENT_TIMESTAMP,");
		 sb.append("rtime datetime,");
		 sb.append("rtime_org datetime,");
		 sb.append("ktime datetime,");
		 sb.append("stime datetime,");
		 sb.append("credit numeric(4,2),");
		 sb.append("pattern_id varchar(11),");
		 sb.append("attempttype varchar(1) default '0', ");
		 sb.append("carrier_stime datetime,");
		 sb.append("carrier_dtime datetime,");
		 sb.append("carrier_dtime_org datetime,");
		 sb.append("carrier_sdate varchar(16),");
		 sb.append("carrier_ddate varchar(16),");
		 sb.append("carrier_stat varchar(50),");
		 sb.append("carrier_err varchar(5),");
		 sb.append("carrier_msgid varchar(50),");
		 sb.append("carrier_systemid varchar(50),");
		 sb.append("carrier_dr varchar(600),");
		 sb.append("statusid varchar(3),");
		 sb.append("statusid_org varchar(3),");
		 sb.append("templateid varchar(30),");
		 sb.append("entityid varchar(30),");
		 sb.append("dlttype varchar(30),");
		 sb.append("interfacetype varchar(10),");
		 sb.append("carrier_stime_org datetime");
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
		result.add("splitup_delivery");
		return result;
	}
	
	
	public boolean isVailableTable(String tablename){
		
		return avilabletable.contains(tablename);
	}
	
	public static void main(String args[]){
		
		System.out.println(new SplitupTable().getSQL("test"));
	}
}
