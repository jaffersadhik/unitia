package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class Carrier {

	private static Carrier obj=null;
	
	private Map<String,String> carriermap=null;
	
	private Carrier(){
	
		init();
		reload();
	}
	
	private void init() {
		


		Connection connection =null;
		
		try{
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "carrier")){
				
				if(table.create(connection, "create table carrier(id INT PRIMARY KEY AUTO_INCREMENT,carrier varchar(45),mode varchar(50) default 'production')", false)){
				
					table.create(connection, "insert into carrier(carrier) values('unitia')", false);
				
				}
			}
			
			if(!table.isExsists(connection, "carrier_smscid_mapping")){
				
				if(table.create(connection, "create table carrier_smscid_mapping(id INT PRIMARY KEY AUTO_INCREMENT,carrier varchar(45),smscid varchar(50)) ENGINE=InnoDB DEFAULT CHARSET=utf8", false)){
					
					table.create(connection, "insert into carrier_smscid_mapping(carrier,smscid) values('unitia','unitia')", false);
					table.create(connection, "insert into carrier_smscid_mapping(carrier,smscid) values('unitia','apps')", false);
					table.create(connection, "insert into carrier_smscid_mapping(carrier,smscid) values('unitia','reapps')", false);
						
				}
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	
		
	}

	public static Carrier getInstance(){
		
		if(obj==null){
			
			obj=new Carrier();
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
			statement=connection.prepareStatement("select smscid,carrier from carrier_smscid_mapping");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("smscid"),resultset.getString("carrier") );
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
	
	
	public String getCarrier(String smscid){
		
		return carriermap.get(smscid);
	}
}
