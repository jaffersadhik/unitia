package com.winnovature.unitia.util.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class ReRouting {

	private static ReRouting obj=null;
	
	private Map<String,Map<String,String>> rerouting=new HashMap<String,Map<String,String>>();
	
	private boolean isTableAvailable=false;
	
	private ReRouting(){
	
		reload();
	}
	
	public static ReRouting getInstance(){
		
		if(obj==null){
			
			obj=new ReRouting();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> reroutemap=new HashMap<String,Map<String,String>>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "rerouting")){
					
					if(table.create(connection, " create table rerouting(username varchar(21),smscid varchar(10),reroute_smscid varchar(10)) "
							+ "", false)){
					
						table.insertReroute(connection,"apps","reapps");
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select username,smscid,reroute_smscid from rerouting");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String username=resultset.getString("username");
				
				Map<String,String> map=reroutemap.get(username);
				
				if(map==null){
					map=new HashMap<String,String>();
				}
				
				map.put(resultset.getString("smscid"),resultset.getString("reroute_smscid"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
			
			this.rerouting=reroutemap;
	
	}
	
	
	public String getReRouteSmscid(String username,String smscid){
		
		Map<String,String> map=rerouting.get(username);
		
		if(map==null){
			
			map=rerouting.get("");
		}
		
		if(map==null){
			
			return null;
		}
		
		return map.get(smscid);
	}
	
	
	
}
