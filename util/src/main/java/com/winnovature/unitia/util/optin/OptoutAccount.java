package com.winnovature.unitia.util.optin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.OptoutDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class OptoutAccount {

	private static String SQL="create table {0}(mobile numeric(21))";
	
	private static Set<String> availabletable=new HashSet();

	private static OptoutAccount obj=null;
	
	private OptoutAccount(){
		
		reload();
	}
	
	public static OptoutAccount getInstance(){
		
		if(obj==null){
			
			obj=new OptoutAccount();
		}
		
		return obj;
	}
	
	public void reload(){
	
		Set<String> optoutuserid=getUserId(); 
		
		if(optoutuserid!=null&&optoutuserid.size()>0){
			
			ensureTable(optoutuserid);
		}
		
	}

	private void ensureTable(Set<String> optoutuserid){
		
		Connection connection=null;
		
		try{
			
			connection=OptoutDBConnection.getInstance().getConnection();
		
			Iterator itr=optoutuserid.iterator();
			
			TableExsists table =new TableExsists();
			while(itr.hasNext()){
				
				String username=itr.next().toString();
				
				if(!availabletable.contains("optout.optout_"+username)){
					
					
					availabletable.add("optout.optout_"+username);
					
					if(!table.isExsists(connection, "optout.optout_"+username)){
						
						if(table.create(connection, getQuery("optout.optout_"+username), false)){
							
							availabletable.add("optout.optout_"+username);

						}
					}else{
						availabletable.add("optout.optout_"+username);
					}
				}
						
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}
	

	private String getQuery(String tablename) {
	
		String params[]={tablename};
		
		return MessageFormat.format(SQL, params);
	}

	private Set<String> getUserId() {
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;		
		Set<String> useridset=new HashSet<String>();
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select username from users where optin_type='2'");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				useridset.add(resultset.getString("username"));
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);

		}
		
		return useridset;
	}

	public boolean isAvailable(String username) {
		
		return availabletable.contains("optout.optout_"+username);
		
	}
	
	
}
