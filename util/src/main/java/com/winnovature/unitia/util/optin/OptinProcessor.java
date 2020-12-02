package com.winnovature.unitia.util.optin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.OptinDBConnection;

public class OptinProcessor {

	public boolean isOptin(String username,String mobile){
		
		if(OptinAccount.getInstance().isAvailable(username)){
			
			Connection connection =null;
			PreparedStatement statement=null;
			ResultSet resultset=null;
			try{
				
				connection =OptinDBConnection.getInstance().getConnection();
				statement=connection.prepareStatement("select * from optin.optin_"+username+" where mobile = ?");
				statement.setLong(1, Long.parseLong(mobile));
				resultset=statement.executeQuery();
				if(resultset.next()){
					
					return  true;
				}
				
			}catch(Exception e){
				
			}finally{
				
				Close.close(resultset);
				Close.close(statement);
				Close.close(connection);
			}
			
			return  false;
		}else{
			
			return false;
		}
	}
}
