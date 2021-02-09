package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;

public class TeleMarketerId {

	private static TeleMarketerId obj=null;
	
	private Map<String,String> carriermap=null;
	
	private TeleMarketerId(){
	
		reload();
	}
	
	public static TeleMarketerId getInstance(){
		
		if(obj==null){
			
			obj=new TeleMarketerId();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,String> temp=getMap();
		
		if(temp!=null){
			
			carriermap=temp;
		}
		
		
	}

	private Map<String, String> getMap() {
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> result=new HashMap<String,String>();
		try{
			
			connection=RouteDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select carrier,telemarketerid from carrier");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("carrier"),resultset.getString("telemarketerid") );
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
			
		return result;
	}
	
	
	public String getTeleMarketerId(String carrier){
		
		return carriermap.get(carrier);
	}
}
