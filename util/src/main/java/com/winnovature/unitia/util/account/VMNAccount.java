package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class VMNAccount {

	private static final String SQL = "create table users_vmn(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),vmn decimal(15,0) unique key)";

	private static VMNAccount obj=null;
	
	
	private Map<String,String> vmnuser=new HashMap<String,String>();
	
	private VMNAccount(){
		
		init();
		
		reload();
	}
	
	public void reload() {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset=null;
		Map<String,String> temp=new HashMap<String,String>();
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select * from users_vmn");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String vmn=resultset.getString("vmn");

				String username=resultset.getString("username");
				temp.put(vmn, username);
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
			
			vmnuser=temp;
		}
	}

	private void init() {



		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "users_vmn")){
				
					if (table.create(connection, SQL, false)) {

						table.create(connection, "insert into users_vmn(username,vmn) values('unitia','919487660738')", false);
					}
			
			}
			
			

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		
	}

	
	public String getUsername(String vmn){
		
		return vmnuser.get(vmn);
	}
	public static VMNAccount getInstance(){
		
		if(obj==null){
			
			obj=new VMNAccount();
		}
		
		return obj;
	}
}
