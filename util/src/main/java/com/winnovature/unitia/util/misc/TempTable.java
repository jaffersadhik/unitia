package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;

public class TempTable {

	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
		
	static List<String> TABLES=new ArrayList<String>();
	
	static{

		TABLES.add("processor");
		TABLES.add("optin");
		TABLES.add("optout");
		TABLES.add("duplicate");
		TABLES.add("shortcodepool");
		TABLES.add("missedcallpool");
		TABLES.add("appspool");
		TABLES.add("dngenpool");
		TABLES.add("submissionpool");
		TABLES.add("smppdn");
		TABLES.add("httpdn");
		TABLES.add("dnreceiverpool");
		TABLES.add("logspool");
		TABLES.add("dnpostpool");
    	TABLES.add("clientdnpool");
    	TABLES.add("schedulepool");
    	TABLES.add("kannelretrypool");
    	TABLES.add("commonpool");
    	TABLES.add("otppool");
    	TABLES.add("otpretrypool");
    	TABLES.add("dngenpool");
    	TABLES.add("clientdnpool");
    	TABLES.add("dnretrypool");

	}


	private static TempTable obj=new TempTable();
	
	private TempTable(){
		
	}
	
	public static TempTable getInstance(){
		
		if(obj==null){
			
			obj=new TempTable();
		}
		
		return obj;
	}
	public  List<String> getQueueTableName(){
		
		
		List<String> result=new ArrayList<String>();
		
		result.addAll(TABLES);
		
		result.addAll(getGroupNameTable());
		
		result.addAll(getSMSCIDTable1());

		return result;
	}


	private  List<String> getSMSCIDTable1() {

		String prefix="kannel_";
		String tablename="kannel";
		String columnname="smscid";
		return getTableName(prefix,tablename,columnname);
	}
	
	
	private  List<String> getGroupNameTable() {

		String prefix="switch_";
		String tablename="routegroup";
		String columnname="groupname";

		return getTableName(prefix,tablename,columnname);
	}


	private  List<String> getTableName(String prefix, String tablename,String columnname) {

		List<String> result=new ArrayList();
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			connection=RouteDBConnection.getInstance().getConnection();
			if(columnname.equals("groupname")){
				statement=connection.prepareStatement("select distinct "+columnname+" from "+tablename+" where mode='"+MODE+"'");

			}else{
				statement=connection.prepareStatement(" select distinct "+columnname+" from carrier a,carrier_smscid_mapping b where a.carrier=b.carrier and mode='"+MODE+"'");
			}
			resultset=statement.executeQuery();
			while(resultset.next()){
		
				if(resultset.getString(columnname)!=null){
				result.add(prefix+resultset.getString(columnname));
			
				}
				
				}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		return null;
	}


	

}
