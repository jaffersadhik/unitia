package com.winnovature.unitia.util.duplicate;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.DuplicateDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.MapKeys;

public class DuplicateCheck {

	private boolean istableAvailable=false;
	
	private String SQL="create table duplicate.duplicate(duplicate_key varchar(750) primary key,expiry numeric(13,0)) ENGINE=InnoDB DEFAULT CHARSET=latin1";
	
	private String INSERT="insert into duplicate.duplicate(duplicate_key,expiry) values(?,?)";
	
	private String DELETE="delete from duplicate.duplicate where expiry < ? ";
	
	
	public boolean isDuplicate(String username,String mobile){
		
		Connection connection=null;
		PreparedStatement statement=null;
		try{
			
			connection=DuplicateDBConnection.getInstance().getConnection();
			
			if(!istableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(table.isExsists(connection, "duplicate.duplicate")){
					
					istableAvailable=true;
					
				}else{
					
					if(table.create(connection, SQL, false)){
						
						istableAvailable=true;
					}
				}
			}
				statement=connection.prepareStatement(INSERT);
				
				statement.setString(1, username+"~"+mobile);
				statement.setLong(2, Long.parseLong(PushAccount.instance().getPushAccount(username).get(MapKeys.DUPLICATE_LIFE_TIME))+System.currentTimeMillis());
				statement.execute();
				
				return false;
				
			
		}catch(Exception e){
			
			return true;
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}
	
	
	
	public boolean isDuplicate(String username,String mobile,String message){
		
		Connection connection=null;
		PreparedStatement statement=null;
		try{
			
			connection=DuplicateDBConnection.getInstance().getConnection();
			
			if(!istableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(table.isExsists(connection, "duplicate")){
					
					istableAvailable=true;
					
				}else{
					
					if(table.create(connection, SQL, false)){
						
						istableAvailable=true;
					}
				}
			}
			
			if(message!=null){
				if(message.trim().length()>700){
					message=message.substring(0,700);
				}
				statement=connection.prepareStatement(INSERT);
				
				statement.setString(1, username+"~"+mobile+"~"+message);
				statement.setLong(2, Long.parseLong(PushAccount.instance().getPushAccount(username).get(MapKeys.DUPLICATE_LIFE_TIME))+System.currentTimeMillis());
				statement.execute();
				
			}
				return false;
				
				
			
		}catch(Exception e){
			
			return true;
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}

	public void flushDuplicate(){
		
		Connection connection=null;
		PreparedStatement statement=null;
		try{
			
			connection=DuplicateDBConnection.getInstance().getConnection();
			
			if(!istableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(table.isExsists(connection, "duplicate.duplicate")){
					
					istableAvailable=true;
					
				}else{
					
					if(table.create(connection, SQL, false)){
						
						istableAvailable=true;
					}
				}
			}
				statement=connection.prepareStatement(DELETE);
				
				statement.setLong(1,System.currentTimeMillis());
				statement.execute();
				
				
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}

}
