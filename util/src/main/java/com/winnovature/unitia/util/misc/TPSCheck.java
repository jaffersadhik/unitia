package com.winnovature.unitia.util.misc;

import com.winnovature.unitia.util.db.RouteDBConnection;

import com.winnovature.unitia.util.db.Close;

import java.util.*;
import java.sql.*;

public class TPSCheck {

	private static TPSCheck obj=new TPSCheck();
	
	private Map<String,String> maxtps=new HashMap<String,String>();
	
	private TPSCheck(){
		
		reload();
	}
	
	
	public static TPSCheck getInstance(){
		
		if(obj==null){
			
			obj=new TPSCheck();
		}
		
		return obj;
	}
	
	
	public boolean isAllowed(String smscid){
		
		try{
			long max=Long.parseLong(maxtps.get(smscid));
			
			if(max<=0){
				
				return true;
			}
			long currenttps=new TPSProcessor().getTPS(smscid);
			
			if((max+1)>currenttps){
				
				return true;
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return false;
	}
	
	public void reload(){

		Map<String,String> temp=getData();
		
		if(temp!=null){
			maxtps=temp;
		}
		
	}
	
	public void clearCounter(){
	
		Iterator itr=maxtps.keySet().iterator();
		
		while(itr.hasNext()){
			
			new TPSProcessor().delete(itr.next().toString());
		}
	}
	public Map<String,String> getData(){
		
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		try{
			connection=RouteDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select smscid,tps from kannel_config");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("smscid"),resultset.getString("tps"));
			}
			
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
