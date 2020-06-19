package com.winnovature.unitia.util.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class InternationalSenderidSwapping {

	private static InternationalSenderidSwapping obj=null;
	
	private Map<String,Map<String,String>> senderidreswapping=new HashMap<String,Map<String,String>>();

	private Map<String,Map<String,String>> senderidswapping=new HashMap<String,Map<String,String>>();
	
	private boolean isTableAvailable=false;
	
	private InternationalSenderidSwapping(){
	
		reload();
	}
	
	public static InternationalSenderidSwapping getInstance(){
		
		if(obj==null){
			
			obj=new InternationalSenderidSwapping();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> senderidswap=new HashMap<String,Map<String,String>>();
		Map<String,Map<String,String>> senderidreswap=new HashMap<String,Map<String,String>>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "international_senderid_swapping")){
					
					if(table.create(connection, " create table international_senderid_swapping(id INT PRIMARY KEY AUTO_INCREMENT ,countrycode varchar(10),senderid varchar(15) , senderid_swap varchar(15))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select countrycode,senderid,senderid_swap from international_senderid_swapping");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			
				String countrycode =resultset.getString("countrycode");
				if(countrycode==null){
					countrycode="";
				}
			
				
				String key=countrycode.trim();
				
				Map<String,String> map1=senderidswap.get(key);
				
				if(map1==null){
					
					map1=new HashMap<String,String>();
					senderidswap.put(key, map1);
				}
				map1.put(resultset.getString("senderid"), resultset.getString("senderid_swapping"));
			
				Map<String,String> map2=senderidreswap.get(key);
				
				if(map2==null){
					
					map2=new HashMap<String,String>();
					senderidreswap.put(key, map2);
				}
				map2.put(resultset.getString("senderid_swapping"), resultset.getString("senderid"));
			
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		this.senderidswapping=senderidswap;
		this.senderidreswapping=senderidreswap;
	}
	
	
	public String getSwapingSenderid(String key,String senderid){
		
		if(senderidswapping.containsKey(key)){
			return senderidswapping.get(key).get(senderid);
		
			}else{
				
				return null; 
			}
		}
	
	
	public String getReSwapingSenderid(String key,String senderid){
		
		if(senderidreswapping.containsKey(key)){
		return senderidreswapping.get(key).get(senderid);
	
		}else{
			
			return null; 
		}
		}
	
	
}
