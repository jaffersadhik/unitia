package com.winnovature.unitia.util.dngen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class ErrorCodeType {

	private static ErrorCodeType obj=null;
	
	private Map<String,String> codetype=new HashMap<String,String>();
	
	private boolean isTableExsists=false;
	
	private ErrorCodeType(){
		
		reload();
	}
	
	public void reload() {

		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			
			if(!isTableExsists){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "dngenerrorcode")){
					
					if(table.create(connection,"create table dngenerrorcode(username varchar(16),type varchar(1))", false)){
						
						isTableExsists=true;

					}
				}else{
					
					isTableExsists=true;
				}
			}
			
			
			loadCodeType(connection);
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
	}

	private void loadCodeType(Connection connection) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		try{
			statement =connection.prepareStatement("select username,type from dngenerrorcode");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("username"), resultset.getString("type"));
				
			}
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
		}
		
		this.codetype=result;
	}

	public void insert(String username,String type){
		
		Connection connection=null;
		PreparedStatement statement=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("insert into dngenerrorcode(username,type) values(?,?)");
			statement.setString(1, username);
			statement.setString(2, type);
			statement.execute();
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}
	public static  ErrorCodeType getInstance(){
		
		if(obj==null){
			
			obj=new ErrorCodeType();
		}
		
		return obj;
	}
	
	public boolean isDnRetry(String username){
		
		if(codetype.containsKey(username)){
			
			return codetype.get(username).equals("2");
		}
		return false;
	}
	
	public boolean isOTPRetry(String username){
		
		if(codetype.containsKey(username)){
			
			return codetype.get(username).equals("1");
		}
		return false;
	}
	
	public boolean isSuccessMasking(String username){
		
		if(codetype.containsKey(username)){
			
			return codetype.get(username).equals("3");
		}
		return false;
	}
	
	
	public boolean isLatencyAdjustment(String username){
		
		if(codetype.containsKey(username)){
			
			return codetype.get(username).equals("4");
		}
		return false;
	}
}

