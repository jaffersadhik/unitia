package com.winnovature.unitia.util.mobileblacklist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class MobileBlackList {

	private static MobileBlackList obj=null;
	
	private Set<String> mobileblacklistset=new HashSet<String>();
	
	private boolean isTableAvailable=false;
	
	private MobileBlackList(){
	
		reload();
	}
	
	public static MobileBlackList getInstance(){
		
		if(obj==null){
			
			obj=new MobileBlackList();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Set<String> mobileset=null;
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "blacklist_mobile")){
					
					if(table.create(connection, " create table blacklist_mobile(mobile numeric(21,0) primary key)", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select mobile from blacklist_mobile");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				if(mobileset==null){
					
					mobileset=new HashSet<String>();
					
				}
				
				mobileset.add(resultset.getString("mobile"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(mobileset!=null){
			
			mobileblacklistset=mobileset;
		}
		
	}
	
	
	public boolean isBalckList(String mobile){
		
		return mobileblacklistset.contains(mobile);
	}
	
	
	
}
