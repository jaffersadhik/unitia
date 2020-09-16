package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.ResultSetToHashMapConverter;

public class DNSuccessMasking {

	private static String SQL="create table dnsuccess_masking(username varchar(16) primary key,success_percentage numeric(2,0))";
	
	private static DNSuccessMasking obj=null;
	
	private Map<String,String> users=new HashMap<String,String>();
	
	private DNSuccessMasking(){
		
		init();
		
		reload();
	}
	
	public void reload() {
		
		Map<String,String> map=loadUser();
		
		if(map!=null){
			
			users=map;
		}
		
	}

	public static DNSuccessMasking getInstance(){
		
		if(obj==null){
			
			obj=new DNSuccessMasking();
		}

		return obj;
	}
	
	private void init() {

		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "dnsuccess_masking")){
		
				if (table.create(connection, SQL, false)) {

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	}
	
	
	public boolean isExsistingUser(String username){
		
		return users.containsKey(username);
		
	}
	
	public double getParcentage(String username){
		
		double result=0;
		
		try{
			result=Integer.parseInt(users.get(username));
		}catch(Exception e){
			
		}
		
		return result;
		
	}
	
	private Map<String,String> loadUser() 
	{
		Map<String,String> usermap = new HashMap<String,String>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
				
			String sql = "select username,success_percentage from dnsuccess_masking ";
		
			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			ResultSetToHashMapConverter rsConverter = new ResultSetToHashMapConverter();
			
			while(resultSet.next()) 
			{
				
				usermap.put(resultSet.getString("username").toLowerCase(),resultSet.getString("success_percentage"));	
				
			}
			
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		} 
		finally 
		{
			Close.close(resultSet);
			Close.close(statement);
			Close.close(connection);
		}//end of finally
		
		return usermap;
		
	}


}
