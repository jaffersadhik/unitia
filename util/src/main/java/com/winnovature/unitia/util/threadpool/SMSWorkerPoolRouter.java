package com.winnovature.unitia.util.threadpool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.MapKeys;

public class SMSWorkerPoolRouter {

	private static SMSWorkerPoolRouter obj=null;
	
	private Map<String,String> poolname=new HashMap<String,String>();
	
	private SMSWorkerPoolRouter(){
		
		checkTable();
		
		loadTable();
	}
	
	private void loadTable() {

		Map<String,String> result=new HashMap<String,String>();
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select superadmin,admin,username,poolname from workerpoolrouter_sms");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
			
				String superadmin=resultset.getString("superadmin");
				
				String admin=resultset.getString("superadmin");
				
				String username=resultset.getString("superadmin");
				
				String poolname =resultset.getString("poolname");
				if(poolname==null||poolname.trim().length()<1){
					
					continue;
				}
				if(superadmin==null&&admin==null&&username==null){
					
					continue;
				}
				
				if(superadmin==null){
					
					superadmin="";
				}
				
				if(admin==null){
					
					admin="";
				}
				
				
				if(username==null){
					
					username="";
				}
				
				
				result.put("~"+superadmin.trim().toLowerCase()+"~"+admin.trim().toLowerCase()+"~"+username.trim().toLowerCase()+"~", poolname.trim().toLowerCase());
			}
			
		}catch(Exception e){
			
			result=null;
		}finally{
	
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		if(result!=null){
			
			poolname=result;
		}
		
	}

	public void reload(){
		
		loadTable();
	}
	private void checkTable(){
		
		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "workerpoolrouter_sms")){
				
				table.create(connection, "create table workerpoolrouter_sms(id INT PRIMARY KEY AUTO_INCREMENT,superadmin varchar(16),admin varchar(16),username varchar(16),poolname varchar(25))", false);
			}
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
	}
	
	public static SMSWorkerPoolRouter getInstance(){
		
		if(obj==null){
		
			obj=new SMSWorkerPoolRouter();
		}
		
		return obj;
		
	}
	
	
	public String getPoolName(Map<String ,String > accountmap){
		
		String superadmin=accountmap.get(MapKeys.SUPERADMIN);
		String admin=accountmap.get(MapKeys.ADMIN);
		String username=accountmap.get(MapKeys.USERNAME);
		for(int i=1;i<4;i++){
			
			String key=getKey(i,superadmin,admin,username);
			
			if(poolname.containsKey(key)){
				
				return poolname.get(key);
			}
		}
		
		if(!accountmap.get(MapKeys.DUPLICATE_TYPE).equals("0")){
			
			return "duplicatepool";
			
		}else if(!accountmap.get(MapKeys.OPTIN_TYPE).equals("0")){
		
			return "optinpool";

		}else if(accountmap.get(MapKeys.OTP_YN).equals("1")){
		
			return "otppool";
		}
		else if(accountmap.get(MapKeys.MSGCLASS).equals("2")){
			
			if(accountmap.get(MapKeys.BILLTYPE).toLowerCase().equals("credit")||accountmap.get(MapKeys.BILLTYPE).toLowerCase().equals("prepaid")){
				
				return "creditpromopool";
			}else{
				
				return "promopool";
			}
			
		}else{
			
			if(accountmap.get(MapKeys.BILLTYPE).toLowerCase().equals("credit")||accountmap.get(MapKeys.BILLTYPE).toLowerCase().equals("prepaid")){
				
				return "credittranspool";
			}else{
				
				return "transpool";
			}
			
		}
	}

	private String getKey(int logic,String superadmin, String admin, String username) {
		
		if(logic==1){
			
			return "~"+superadmin+"~"+admin+"~"+username+"~";
		}else if(logic==2){
		
			return "~"+superadmin+"~"+admin+"~~";

		}else if(logic==3){
		
			return "~"+superadmin+"~~~";

		}else{
			
			return "";
		}
	}
	
}
