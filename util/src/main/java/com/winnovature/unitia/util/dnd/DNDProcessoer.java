package com.winnovature.unitia.util.dnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.DNDDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.MapKeys;

public class DNDProcessoer {

	private static String SQL="create table dnd.dnd (mobile numeric(21,0))";
	
	private static boolean isTableAvailable=false;

	public boolean isDND(String mobile){
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			
			connection=DNDDBConnection.getInstance().getConnection();
		
				
				if(!isTableAvailable){
					
					TableExsists table=new TableExsists();			
					
					if(!table.isExsists(connection, "dnd.dnd")){
						
						if(table.create(connection, SQL, false)){
							
							isTableAvailable=true;

						}
					}else{
						isTableAvailable=true;
					}
				}
						
			statement =connection.prepareStatement("select * from dnd.dnd where mobile = ?");
			statement.setLong(1,Long.parseLong(mobile));
			resultset=statement.executeQuery();
			if(resultset.next()){
				
				return true;
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return false;
	}
	
	
	
}
