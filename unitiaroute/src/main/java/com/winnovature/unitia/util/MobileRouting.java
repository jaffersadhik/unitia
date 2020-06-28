package com.winnovature.unitia.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class MobileRouting {

	private static MobileRouting obj=null;
	
	private Map<String,String> mobilerouting=new HashMap<String,String>();
	
	private boolean isTableAvailable=false;
	
	private MobileRouting(){
	
		reload();
	}
	
	public static MobileRouting getInstance(){
		
		if(obj==null){
			
			obj=new MobileRouting();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,String> mobilemap=null;
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "mobile_routing")){
					
					if(table.create(connection, " create table mobile_routing(mobile numeric(21,0) primary key,routegroup varchar(25))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select mobile,routegroup from mobile_routing");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				if(mobilemap==null){
					
					mobilemap=new HashMap<String,String>();
					
				}
				
				mobilemap.put(resultset.getString("mobile"),resultset.getString("routegroup"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(mobilemap!=null){
			
			this.mobilerouting=mobilemap;
		}
		
	}
	
	
	public String getRouteGroup(String mobile){
		
		return mobilerouting.get(mobile);
	}
	
	
	
}
