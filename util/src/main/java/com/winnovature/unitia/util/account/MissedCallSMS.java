package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class MissedCallSMS {

	private static final String SQL = "create table missedcall_sms(id INT PRIMARY KEY AUTO_INCREMENT,vmn decimal(15,0) unique key,sms text,senderid varchar(20))";

	private static MissedCallSMS obj=null;
	
	Map<String,Map<String,String>> vmnsms=new HashMap<String,Map<String,String>>();

	
	private MissedCallSMS(){
		
		init();
		
		reload();
	}
	
	private void reload() {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset=null;
		Map<String,Map<String,String>> temp=new HashMap<String,Map<String,String>>();
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select * from missedcall_sms");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String vmn=resultset.getString("vmn");

				String sms=resultset.getString("sms");
				
				String senderid=resultset.getString("senderid");

				Map<String,String> data=new HashMap<String,String>();
				data.put("sms", sms);
				data.put("senderid", senderid);

				temp.put(vmn, data);
			}
		} catch (Exception e) {

			temp=null;
			e.printStackTrace();

		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(temp!=null){
			
			vmnsms=temp;
		}
	}

	private void init() {



		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "missedcall_sms")){
				
					if (table.create(connection, SQL, false)) {

						table.create(connection, "insert into missedcall_sms(vmn,sms,senderid) values('919487660738','Thanks for Missed Call','WECARE')", false);
					}
			
			}
			
			

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		
	}

	
	public Map<String,String> getSMS(String vmn){
		
		return vmnsms.get(vmn);
		
	}
	public static MissedCallSMS getInstance(){
		
		if(obj==null){
			
			obj=new MissedCallSMS();
		}
		
		return obj;
	}
}
