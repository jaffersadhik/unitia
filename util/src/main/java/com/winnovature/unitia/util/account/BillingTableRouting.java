package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.MapKeys;

public class BillingTableRouting {

	private static final String BILLINGTABLE_ROUTING = "create table billingtable_routing(id INT PRIMARY KEY AUTO_INCREMENT,superadmin varchar(16),admin varchar(16),username varchar(16),submission_tablename varchar(50) not null ,delivery_tablename varchar(50) not null,dnpost_tablename varchar(50) not null,itime timestamp default CURRENT_TIMESTAMP)";

	private static BillingTableRouting obj=null;
	
	Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();


	private BillingTableRouting(){
	
		init();
		reload();
	}
	
	private void init() {
		
		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			loadbillingroutingtable(connection, table);


		} catch (Exception e) {

			obj=null;
			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	}

	public static BillingTableRouting getInstance(){
		
		if(obj==null){
			
			obj=new BillingTableRouting();
		}
		
		return obj;
	}
	
	private void loadbillingroutingtable(Connection connection, TableExsists table) {
		
		if (!table.isExsists(connection, table.getTableName(BILLINGTABLE_ROUTING))) {
			if (table.create(connection, BILLINGTABLE_ROUTING, false)) {

			
			}
		}
	}

	public void reload(){
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();
		
		try{
			Map<String,String> data=new HashMap<String,String>();
			data.put("submission", "submission");
			data.put("delivery", "delivery");
			data.put("delivery_post", "delivery_post");

			result.put("~~", data);
			
			connection =CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select superadmin,admin,username,submission_tablename,delivery_tablename,dnpost_tablename from billingtable_routing ");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				String superadmin=resultset.getString("superadmin");
				String admin=resultset.getString("admin");
				String username=resultset.getString("username");
				
				String submission_tablename=resultset.getString("submission_tablename");
				String delivery_tablename=resultset.getString("delivery_tablename");
				String dnpost_tablename=resultset.getString("dnpost_tablename");

				if(superadmin==null){
					
					superadmin="";
				}
				
				if(admin==null){
					
					admin="";
				}
				
				if(username==null){
					
					username="";
				}
				
				Map<String,String> data1=new HashMap<String,String>();
				data1.put("submission",submission_tablename);
				data1.put("delivery", delivery_tablename);
				data1.put("delivery_post", dnpost_tablename);

				result.put(superadmin.toLowerCase().trim()+"~"+admin.toLowerCase().trim()+"~"+username.toLowerCase().trim(), data1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		this.result=result;
	}
	
	
	public String getDNTableName(String username){
		
		Map<String,String> data=getMap(username);


		if(data==null){
			
			return "delivery";
		}else{
			
			return data.get("delivery");
		}
	}

	public String getDNPostTableName(String username){
		
		Map<String,String> data=getMap(username);


		if(data==null){
			
			return "delivery_post";
		}else{
			
			return data.get("delivery_post");
		}
	}
	
	
	
	public String getSubmissionTableName(String username){
		
		Map<String,String> data=getMap(username);


		if(data==null){
			
			return "submission";
		}else{
			
			return data.get("submission");
		}
	}
	private Map<String, String> getMap(String username) {
		
		String superadmin=PushAccount.instance().getPushAccount(username).get(MapKeys.SUPERADMIN);
		String admin=PushAccount.instance().getPushAccount(username).get(MapKeys.ADMIN);
		String key=superadmin+"~"+admin+"~"+username;
		
		Map<String,String> data=result.get(key);
		
		if(data==null){
			
			key=superadmin+"~"+admin+"~"+"";
			data=result.get(key);
			if(data==null){
				
				key=superadmin+"~~";
				data=result.get(key);
				
				if(data==null){
					
					key="~~";
					data=result.get(key);
				}
			}
		}
		
		return data;
	}
	
}
