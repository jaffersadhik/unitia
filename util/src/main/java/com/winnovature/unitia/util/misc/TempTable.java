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
		
	List<String> TABLES=new ArrayList<String>();
	
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

		result.add("processor");
		result.add("optin");
		result.add("optout");
		result.add("duplicate");
		result.add("shortcodepool");
		result.add("missedcallpool");
		result.add("appspool");
		result.add("dngenpool");
		result.add("submissionpool");
		result.add("requestlog");
		result.add("smppdn");
		result.add("httpdn");
		result.add("dnreceiverpool");
		result.add("logspool");
		result.add("dnpostpool");
		result.add("clientdnpool");
		result.add("schedulepool");
		result.add("kannelretrypool");
		result.add("commonpool_1");
		result.add("commonpool_2");
		result.add("otppool");
		result.add("otpretrypool");
		result.add("dngenpool");
		result.add("clientdnpool");
		result.add("dnretrypool");
		result.add("kl_kannel2_1");
		result.add("kl_kannel2_2");
		result.add("concatepool");
		result.add("concatedata");
		result.add("reroute_kannel");
		result.add("kl_vedioconkannel_1");
		result.add("kl_vedioconkannel_2");
		result.add("kl_kannelA_1");
		result.add("kl_kannelA_2");
		result.add("kl_kannelB_1");
		result.add("kl_kannelB_2");
		result.add("kl_kannelC_1");
		result.add("kl_kannelC_2");
		result.add("kl_kannelD_1");
		result.add("kl_kannelD_2");
		result.add("kl_kannelE_1");
		result.add("kl_kannelE_2");
		result.add("kl_kannelF_1");
		result.add("kl_kannelF_2");
		result.add("kl_kannelG_1");
		result.add("kl_kannelG_2");
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
