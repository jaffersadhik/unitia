package com.winnovature.unitia.util.optout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.winnovature.unitia.util.db.OptoutDBConnection;

public class OptoutProcessor {

	public boolean isOptout(String userid,String mobile){
		
		if(OptoutAccount.getInstance().isAvailable(userid)){
			
			Connection connection =null;
			PreparedStatement statement=null;
			ResultSet resultset=null;
			try{
				
				connection =OptoutDBConnection.getInstance().getConnection();
				statement=connection.prepareStatement("select * from optout_"+userid+" where mobile = ?");
				statement.setLong(1, Long.parseLong(mobile));
				resultset=statement.executeQuery();
				if(resultset.next()){
					
					return  true;
				}
				
			}catch(Exception e){
				
			}finally{
				
			}
			
			return  false;
		}else{
			
			return false;
		}
	}
}
