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
	
	
	public boolean isAllowed(String smscid,Map<String,Object> map){
		
		try{
			
			long max=100;
			
			try{
				max=Long.parseLong(maxtps.get(smscid));
			}catch(Exception e){
				
			}
			

			
			int totalmsgcount=1;
			try{
				totalmsgcount=Integer.parseInt(map.get(MapKeys.TOTAL_MSG_COUNT).toString());
			}catch(Exception e){
				
			}
			long currenttps=new TPSProcessor().getTPS(smscid,totalmsgcount);
			map.put("currenttps", ""+currenttps);
			if((max+1)>currenttps){
				
				return true;
			}
		}catch(Exception e){
			System.err.println("smscid " +smscid);
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
