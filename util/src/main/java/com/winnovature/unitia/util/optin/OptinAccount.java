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
import com.winnovature.unitia.util.db.OptinDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class OptinAccount {

	private static String SQL="create table {0}(mobile numeric(21))";
	
	private static Set<String> availabletable=new HashSet();

	private static OptinAccount obj=null;
	
	private OptinAccount(){
		
		reload();
	}
	
	public static OptinAccount getInstance(){
		
		if(obj==null){
			
			obj=new OptinAccount();
		}
		
		return obj;
	}
	
	public void reload(){
	
		Set<String> optinuserid=getUserId(); 
		
		if(optinuserid!=null&&optinuserid.size()>0){
			
			ensureTable(optinuserid);
		}
		
	}

	private void ensureTable(Set<String> optinuserid){
		
		Connection connection=null;
		
		try{
			
			connection=OptinDBConnection.getInstance().getConnection();
		
			Iterator itr=optinuserid.iterator();
			TableExsists table=new TableExsists();	
			while(itr.hasNext()){
				
				String userid=itr.next().toString();
				
				if(!availabletable.contains("optin_"+userid)){
					
					
					availabletable.add("optin_"+userid);
					
					if(!table.isExsists(connection, "optin_"+userid)){
						
						if(table.create(connection, getQuery("optin_"+userid), false)){
							
							availabletable.add("optin_"+userid);

						}
					}else{
						availabletable.add("optin_"+userid);
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
			statement=connection.prepareStatement("select userid from users where optin_type='1'");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				useridset.add(resultset.getString("userid"));
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

	public boolean isAvailable(String userid) {
		
		return availabletable.contains("optin_"+userid);
		
	}
	
	
}
