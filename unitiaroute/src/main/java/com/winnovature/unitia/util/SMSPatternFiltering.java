package com.winnovature.unitia.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class SMSPatternFiltering {

	private static boolean isTableAvailable=false;

	private static SMSPatternFiltering obj=null;
	
	private Map<String,Set<String>> smspatternfilteringset=new HashMap<String,Set<String>>();
	private Map<String,String> smspatternidmap=new HashMap<String,String>();

	
	private SMSPatternFiltering(){
	
		reload();
	}
	
	public static SMSPatternFiltering getInstance(){
		
		if(obj==null){
			
			obj=new SMSPatternFiltering();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Set<String>> patternset=new HashMap<String,Set<String>>();
		Map<String,String> patternidmap=new HashMap<String,String>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "filtering_smspattern")){
					
					if(table.create(connection, " create table filtering_smspattern(pattern_id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),smspattern varchar(700) ,unique(smspattern))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select pattern_id,username,smspattern from filtering_smspattern");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String username=resultset.getString("username");
				
				Set<String> patternmap=patternset.get(username);
				
				if(patternmap==null){
					
					patternmap=new HashSet<String>();
					patternset.put(username, patternmap);
				}
				
				patternidmap.put(resultset.getString("smspattern").toLowerCase(), resultset.getString("pattern_id"));
				patternmap.add(resultset.getString("smspattern").toLowerCase());
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		smspatternfilteringset=patternset;
		smspatternidmap=patternidmap;
	}
	
	
	public Set<String> getFilteringPaternSet(String username){
		
		return smspatternfilteringset.get(username);
	}
	
	
	public String getPatternId(String pattern){
		
		return smspatternidmap.get(pattern);
	}
	
}
